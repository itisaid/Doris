package com.alibaba.doris.dataserver.store.innodb.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.doris.common.data.CompareStatus;
import com.alibaba.doris.common.data.Key;
import com.alibaba.doris.common.data.Pair;
import com.alibaba.doris.common.data.Value;
import com.alibaba.doris.common.data.ValueFactory;
import com.alibaba.doris.common.data.impl.PairImpl;
import com.alibaba.doris.dataserver.store.ClosableIterator;
import com.alibaba.doris.dataserver.store.StorageType;
import com.alibaba.doris.dataserver.store.exception.VersionConflictException;
import com.g414.haildb.Cursor;
import com.g414.haildb.Cursor.LockMode;
import com.g414.haildb.Cursor.SearchMode;
import com.g414.haildb.Database;
import com.g414.haildb.TableDef;
import com.g414.haildb.Transaction;
import com.g414.haildb.Transaction.TransactionLevel;
import com.g414.haildb.Transaction.TransactionState;
import com.g414.haildb.Tuple;

/**
 * @author long.mal long.mal@alibaba-inc.com
 */
public class InnoDBDataBase extends InnoDBBase {

    public InnoDBDataBase(InnoDBBuilder dbBuilder, String databaseName) {
        this.dbBuilder = dbBuilder;
        this.database = dbBuilder.getDatabase();
        this.databaseName = databaseName;
    }

    public void close() {
        // ingore;
    }

    public void open() {
        tableDef = dbBuilder.buildTable(databaseName);
    }

    public String getName() {
        return this.databaseName;
    }
    
    public int getNodeIndex() {
    	return Integer.parseInt(this.databaseName);
    }
    
    public TableDef getTableDef() {
        return this.tableDef;
    }

    public boolean delete(Key key) {
        return delete(key, null);
    }

    public boolean delete(List<Integer> vnodeList) {
        throw new UnsupportedOperationException("The operation of 'delete' is not supported!", null);
    }

    public boolean delete(final Key key, final Value value) {
        final byte[] keyBytes = serializerFactory.encode(key).copyBytes();
        
        Transaction txn = null;
        Cursor c = null;
        Tuple toDelete = null;
        Tuple toFind = null;
        try {
            txn = database.beginTransaction(TransactionLevel.REPEATABLE_READ);
            
            c = txn.openTable(tableDef);
            c.setLockMode(LockMode.INTENTION_EXCLUSIVE);
            c.lock(LockMode.LOCK_EXCLUSIVE);

            final Map<String, Object> searchKey = new HashMap<String, Object>();
            searchKey.put("key_", keyBytes);
            toFind = c.createClusteredIndexSearchTuple(searchKey);
            c.find(toFind, SearchMode.GE);
            
            if (c != null && c.isPositioned() && c.hasNext()) {
            	toDelete = c.createClusteredIndexReadTuple();
        		c.readRow(toDelete);
        		Map<String, Object> found = toDelete.valueMap();
              
            	if (getKeyStringFromResult(found).equals(new String(keyBytes))) {
                	Value oldValue = ValueFactory.createValue(null, getVersionFromResult(found));
                    if (value != null) {
                        CompareStatus status = oldValue.compareVersion(value);
                        if (status == CompareStatus.AFTER) {
                            throw new VersionConflictException("Key " + key.getKey() + " Namespace " + key.getNamespace() + " "
                                                               + value.getTimestamp()
                                                               + " is obsolete, it is no greater than the current version of "
                                                               + oldValue.getTimestamp() + ".");
                        }
                    }
                    
                    c.deleteRow();
            		return true;
            	}
            }
            
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (toDelete != null) {
            	toDelete.clear();
            	toDelete.delete();
            }
            
            if (toFind != null) {
            	toFind.clear();
            	toFind.delete();
            }

            if (c != null) {
                c.close();
            }
            
            if (txn != null) {
                if (txn.getState().equals(TransactionState.NOT_STARTED)) {
                    txn.release();
                } else {
                    txn.commit();
                }
            }
        }
    }

    public Value get(final Key key) {
        final byte[] keyBytes = serializerFactory.encode(key).copyBytes();
        final Map<String, Object> searchKey = new HashMap<String, Object>();
        searchKey.put("key_", keyBytes);

        Transaction txn = null;
        Cursor c0 = null;
        Tuple tuple = null;
        try {
            txn = database.beginTransaction(TransactionLevel.READ_COMMITTED);
            c0 = txn.openTable(tableDef);
            tuple = c0.createClusteredIndexSearchTuple(searchKey);
            c0.find(tuple, SearchMode.GE);
            
            if (c0 != null && c0.isPositioned() && c0.hasNext()) {
            	Tuple read = c0.createClusteredIndexReadTuple();
            	try {
            		c0.readRow(read);
            		Map<String, Object> row = read.valueMap();
            		
            		if (getKeyStringFromResult(row).equals(new String(keyBytes)))
            			return getValueFromResult(row);
            	} catch (Exception e) {
            		
            	} finally {
            		read.delete();
            		if (c0 != null)
            			c0.next();
            	}
            }
            
          	return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (c0 != null) {
                c0.close();
                c0 = null;
            }
            
            if (tuple != null) {
            	tuple.delete();
            }
        	
            if (txn != null) {
                if (txn.getState().equals(TransactionState.NOT_STARTED)) {
                    txn.release();
                } else {
                    txn.commit();
                }
            }
        }
    }

    public void set(final Key key, final Value value) {
        this.set(key, value, false);
    }
    
    public void put(final Key key, final Value value) {
    	this.set(key, value);
    }

    public void set(final Key key, final Value value, final boolean isSetWithCompareVersion) {
        final byte[] keyBytes = serializerFactory.encode(key).copyBytes();
        final byte[] valueBytes = serializerFactory.encode(value).copyBytes();
        
        Transaction txn = null;
        Cursor c = null;
        Tuple toInsert = null;
        Tuple toUpdate = null;
        Tuple toFind = null;
        
        try {
            txn = database.beginTransaction(TransactionLevel.READ_COMMITTED);
        	Map<String, Object> newRow = new LinkedHashMap<String, Object>();
        	
            newRow.put(InnoDBBuilder.FIELD_KEY, keyBytes);
            newRow.put(InnoDBBuilder.FIELD_VALUE, valueBytes);
            newRow.put(InnoDBBuilder.FIELD_VERSION, value.getTimestamp());

            c = txn.openTable(tableDef);
            
            c.setLockMode(LockMode.INTENTION_EXCLUSIVE);
            c.lock(LockMode.LOCK_EXCLUSIVE);

            final Map<String, Object> searchKey = new HashMap<String, Object>();
            searchKey.put(InnoDBBuilder.FIELD_KEY, keyBytes);
            toFind = c.createClusteredIndexSearchTuple(searchKey);
            c.find(toFind, SearchMode.GE);
            
            if (c != null && c.isPositioned() && c.hasNext()) {
            	toUpdate = c.createClusteredIndexReadTuple();
        		c.readRow(toUpdate);
        		Map<String, Object> found = toUpdate.valueMap();

            	if (getKeyStringFromResult(found).equals(new String(keyBytes))) {
                    Value oldValue = ValueFactory.createValue(null, getVersionFromResult(found));
                    CompareStatus status = oldValue.compareVersion(value);
                    if (status == CompareStatus.AFTER) {
                        throw new VersionConflictException("Key " + key.getKey() + " Namespace " + key.getNamespace() + " "
                                                           + value.getTimestamp()
                                                           + " is obsolete, it is no greater than the current version of "
                                                           + oldValue.getTimestamp() + ".");
                    }
                    
        			c.updateRow(toUpdate, newRow);
            		return;
            	}
            }
            
            toInsert = c.createClusteredIndexReadTuple();
           	c.insertRow(toInsert, newRow);
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (toInsert != null) {
                toInsert.delete();
            }
            
            if (toUpdate != null) {
            	toUpdate.delete();
            }
            
            if (toFind != null) {
            	toFind.delete();
            }

            if (c != null) {
                c.close();
            }
            
            if (txn != null) {
                if (txn.getState().equals(TransactionState.NOT_STARTED)) {
                    txn.release();
                } else {
                    txn.commit();
                }
            }
        }
    }

    public StorageType getType() {
        return null;
    }
    
    public void traverse() throws Exception {
    	Transaction txn = null;
    	Cursor cursor = null;
    	
    	try {
			txn = database.beginTransaction(TransactionLevel.READ_COMMITTED);
			cursor = txn.openTable(tableDef);
			cursor.first();
			
			while (cursor != null && cursor.isPositioned() && cursor.hasNext()) {
				Tuple read = cursor.createClusteredIndexReadTuple();
				try {
					cursor.readRow(read);
//					Map<String, Object> row = read.valueMap();
//					System.out.println(row.get("key_"));
				} finally {
					read.delete();
					cursor.next();
				}
			}
		} catch (Exception e) {
            if (txn != null) {
                if (txn.getState().equals(TransactionState.NOT_STARTED)) {
                    txn.release();
                } else {
                    txn.rollback();
                }
                txn = null;
            }
			
			throw new RuntimeException(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			
            if (txn != null) {
                if (txn.getState().equals(TransactionState.NOT_STARTED)) {
                    txn.release();
                } else {
                    txn.commit();
                }
            }
		}
    }

    public Iterator<Pair> iterator() {
    	Transaction txn = null;
    	Cursor cursor = null;
    	
    	try {
			txn = database.beginTransaction(TransactionLevel.READ_COMMITTED);
			cursor = txn.openTable(tableDef);
			cursor.first();
			
			return new InnoDBCursorIterator<Pair>(txn, cursor);
		} catch (Exception e) {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			
            if (txn != null) {
                if (txn.getState().equals(TransactionState.NOT_STARTED)) {
                    txn.release();
                } else {
                    txn.rollback();
                }
                txn = null;
            }
			
			throw new RuntimeException(e);
		} finally {
		}
    }

    public Iterator<Pair> iterator(List<Integer> vnodeList) {
        return iterator();
    }

    private static class InnoDBCursorIterator<T> implements ClosableIterator<T> {

        final Transaction txn;
        private Cursor cursor;
        private boolean closed = false;

        public InnoDBCursorIterator(Transaction txn, Cursor c) {
            this.txn = txn;
            this.cursor = c;
        }

        public boolean hasNext() {
        	boolean hasNext = (cursor != null && cursor.isPositioned() && cursor.hasNext());
        	if (!hasNext) {
        		this.close();
        	}
            return hasNext;
        }

        public T next() {
        	if (cursor == null) {
        		return null;
        	}
        	
			Tuple read = cursor.createClusteredIndexReadTuple();
			try {
				cursor.readRow(read);
				Map<String, Object> row = read.valueMap();
                Key key = serializerFactory.decodeKey((byte[]) row.get(InnoDBBuilder.FIELD_KEY));
                Value value = serializerFactory.decodeValue((byte[]) row.get(InnoDBBuilder.FIELD_VALUE));
                return (T) new PairImpl(key, value);
			} finally {
				read.delete();
				cursor.next();
			}
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void close() {
        	if (closed) {
        		return;
        	}
        	
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			
            if (txn != null) {
                if (txn.getState().equals(TransactionState.NOT_STARTED)) {
                    txn.release();
                } else {
                    txn.commit();
                }
            }
            
            closed = true;
        }
    }

    private InnoDBBuilder    dbBuilder;
    private TableDef         tableDef;
    private Database         database;
    private String           databaseName;

}
