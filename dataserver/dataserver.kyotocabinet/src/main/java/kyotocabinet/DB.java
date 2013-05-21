/*************************************************************************************************
 * Java binding of Kyoto Cabinet.
 *                                                               Copyright (C) 2009-2011 FAL Labs
 * This file is part of Kyoto Cabinet.
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version
 * 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *************************************************************************************************/


package kyotocabinet;

import java.util.*;
import java.io.*;
import java.net.*;


/**
 * Interface of database abstraction.
 */
public class DB {
  //----------------------------------------------------------------
  // static initializer
  //----------------------------------------------------------------
  static {
    Loader.load();
  }
  //----------------------------------------------------------------
  // public constants
  //----------------------------------------------------------------
  /** generic mode: exceptional mode */
  public static final int GEXCEPTIONAL = 1 << 0;
  /** open mode: open as a reader */
  public static final int OREADER = 1 << 0;
  /** open mode: open as a writer */
  public static final int OWRITER = 1 << 1;
  /** open mode: writer creating */
  public static final int OCREATE = 1 << 2;
  /** open mode: writer truncating */
  public static final int OTRUNCATE = 1 << 3;
  /** open mode: auto transaction */
  public static final int OAUTOTRAN = 1 << 4;
  /** open mode: auto synchronization */
  public static final int OAUTOSYNC = 1 << 5;
  /** open mode: open without locking */
  public static final int ONOLOCK = 1 << 6;
  /** open mode: lock without blocking */
  public static final int OTRYLOCK = 1 << 7;
  /** open mode: open without auto repair */
  public static final int ONOREPAIR = 1 << 8;
  /** merge mode: overwrite the existing value */
  public static final int MSET = 0;
  /** merge mode: keep the existing value */
  public static final int MADD = 1;
  /** merge mode: modify the existing record only */
  public static final int MREPLACE = 2;
  /** merge mode: append the new value */
  public static final int MAPPEND = 3;
  //----------------------------------------------------------------
  // constructors and finalizer
  //----------------------------------------------------------------
  /**
   * Create an instance.
   */
  public DB() {
    initialize(0);
  }
  /**
   * Create an instance with options.
   * @param opts the optional features by bitwise-or: DB.GEXCEPTIONAL for the exceptional mode.
   * @note The exceptional mode means that fatal errors caused by methods are reported by
   * exceptions thrown.
   */
  public DB(int opts) {
    initialize(opts);
  }
  /**
   * Release resources.
   */
  protected void finalize() {
    destruct();
  }
  //----------------------------------------------------------------
  // public methods
  //---------------------------------------------------------------
  /**
   * Get the last happened error.
   * @return the last happened error.
   */
  public native Error error();
  /**
   * Open a database file.
   * @param path the path of a database file.  If it is "-", the database will be a prototype
   * hash database.  If it is "+", the database will be a prototype tree database.  If it is ":",
   * the database will be a stash database.  If it is "*", the database will be a cache hash
   * database.  If it is "%", the database will be a cache tree database.  If its suffix is
   * ".kch", the database will be a file hash database.  If its suffix is ".kct", the database
   * will be a file tree database.  If its suffix is ".kcd", the database will be a directory
   * hash database.  If its suffix is ".kcf", the database will be a directory tree database.
   * Otherwise, this method fails.  Tuning parameters can trail the name, separated by "#".
   * Each parameter is composed of the name and the value, separated by "=".  If the "type"
   * parameter is specified, the database type is determined by the value in "-", "+", ":", "*",
   * "%", "kch", "kct", "kcd", and "kcf".  All database types support the logging parameters of
   * "log", "logkinds", and "logpx".  The prototype hash database and the prototype tree
   * database do not support any other tuning parameter.  The stash database supports "bnum".
   * The cache hash database supports "opts", "bnum", "zcomp", "capcnt", "capsiz", and "zkey".
   * The cache tree database supports all parameters of the cache hash database except for
   * capacity limitation, and supports "psiz", "rcomp", "pccap" in addition.  The file hash
   * database supports "apow", "fpow", "opts", "bnum", "msiz", "dfunit", "zcomp", and "zkey".
   * The file tree database supports all parameters of the file hash database and "psiz",
   * "rcomp", "pccap" in addition.  The directory hash database supports "opts", "zcomp", and
   * "zkey".  The directory tree database supports all parameters of the directory hash database
   * and "psiz", "rcomp", "pccap" in addition.
   * @param mode the connection mode.  DB.OWRITER as a writer, DB.OREADER as a
   * reader.  The following may be added to the writer mode by bitwise-or: DB.OCREATE,
   * which means it creates a new database if the file does not exist, DB.OTRUNCATE, which
   * means it creates a new database regardless if the file exists, DB.OAUTOTRAN, which
   * means each updating operation is performed in implicit transaction, DB.OAUTOSYNC,
   * which means each updating operation is followed by implicit synchronization with the file
   * system.  The following may be added to both of the reader mode and the writer mode by
   * bitwise-or: DB.ONOLOCK, which means it opens the database file without file locking,
   * DB.OTRYLOCK, which means locking is performed without blocking, DB.ONOREPAIR,
   * which means the database file is not repaired implicitly even if file destruction is
   * detected.
   * @return true on success, or false on failure.
   * @note The tuning parameter "log" is for the original "tune_logger" and the value specifies
   * the path of the log file, or "-" for the standard output, or "+" for the standard error.
   * "logkinds" specifies kinds of logged messages and the value can be "debug", "info", "warn",
   * or "error".  "logpx" specifies the prefix of each log message.  "opts" is for "tune_options"
   * and the value can contain "s" for the small option, "l" for the linear option, and "c" for
   * the compress option.  "bnum" corresponds to "tune_bucket".  "zcomp" is for "tune_compressor"
   * and the value can be "zlib" for the ZLIB raw compressor, "def" for the ZLIB deflate
   * compressor, "gz" for the ZLIB gzip compressor, "lzo" for the LZO compressor, "lzma" for the
   * LZMA compressor, or "arc" for the Arcfour cipher.  "zkey" specifies the cipher key of the
   * compressor.  "capcnt" is for "cap_count".  "capsiz" is for "cap_size".  "psiz" is for
   * "tune_page".  "rcomp" is for "tune_comparator" and the value can be "lex" for the lexical
   * comparator, "dec" for the decimal comparator, "lexdesc" for the lexical descending
   * comparator, or "decdesc" for the decimal descending comparator.  "pccap" is for
   * "tune_page_cache".  "apow" is for "tune_alignment".  "fpow" is for "tune_fbp".  "msiz" is
   * for "tune_map".  "dfunit" is for "tune_defrag".  Every opened database must be closed by
   * the DB.close method when it is no longer in use.  It is not allowed for two or more
   * database objects in the same process to keep their connections to the same database file at
   * the same time.
   */
  public native boolean open(String path, int mode);
  /**
   * Close the database file.
   * @return true on success, or false on failure.
   */
  public native boolean close();
  /**
   * Accept a visitor to a record.
   * @param key the key.
   * @param visitor a visitor object which implements the Visitor interface.
   * @param writable true for writable operation, or false for read-only operation.
   * @return true on success, or false on failure.
   * @note The operation for each record is performed atomically and other threads accessing the
   * same record are blocked.  To avoid deadlock, any explicit database operation must not be
   * performed in this method.
   */
  public native boolean accept(byte[] key, Visitor visitor, boolean writable);
  /**
   * Accept a visitor to multiple records at once.
   * @param keys specifies an array of the keys.
   * @param visitor a visitor object.
   * @param writable true for writable operation, or false for read-only operation.
   * @return true on success, or false on failure.
   * @note The operations for specified records are performed atomically and other threads
   * accessing the same records are blocked.  To avoid deadlock, any explicit database operation
   * must not be performed in this method.
   */
  public native boolean accept_bulk(byte[][] keys, Visitor visitor, boolean writable);
  /**
   * Iterate to accept a visitor for each record.
   * @param visitor a visitor object which implements the Visitor interface.
   * @param writable true for writable operation, or false for read-only operation.
   * @return true on success, or false on failure.
   * @note The whole iteration is performed atomically and other threads are blocked.  To avoid
   * deadlock, any explicit database operation must not be performed in this method.
   */
  public native boolean iterate(Visitor visitor, boolean writable);
  /**
   * Set the value of a record.
   * @param key the key.
   * @param value the value.
   * @return true on success, or false on failure.
   * @note If no record corresponds to the key, a new record is created.  If the corresponding
   * record exists, the value is overwritten.
   */
  public native boolean set(byte[] key, byte[] value);
  /**
   * Set the value of a record.
   * @note Equal to the original DB.set method except that the parameters are String.
   * @see #set(byte[], byte[])
   */
  public boolean set(String key, String value) {
    return set(str_to_ary(key), str_to_ary(value));
  }
  /**
   * Add a record.
   * @param key the key.
   * @param value the value.
   * @return true on success, or false on failure.
   * @note If no record corresponds to the key, a new record is created.  If the corresponding
   * record exists, the record is not modified and false is returned.
   */
  public native boolean add(byte[] key, byte[] value);
  /**
   * Add a record.
   * @note Equal to the original DB.add method except that the parameters are String.
   * @see #add(byte[], byte[])
   */
  public boolean add(String key, String value) {
    return add(str_to_ary(key), str_to_ary(value));
  }
  /**
   * Replace the value of a record.
   * @param key the key.
   * @param value the value.
   * @return true on success, or false on failure.
   * @note If no record corresponds to the key, no new record is created and false is returned.
   * If the corresponding record exists, the value is modified.
   */
  public native boolean replace(byte[] key, byte[] value);
  /**
   * Replace the value of a record.
   * @note Equal to the original DB.replace method except that the parameters are String.
   * @see #add(byte[], byte[])
   */
  public boolean replace(String key, String value) {
    return replace(str_to_ary(key), str_to_ary(value));
  }
  /**
   * Append the value of a record.
   * @param key the key.
   * @param value the value.
   * @return true on success, or false on failure.
   * @note If no record corresponds to the key, a new record is created.  If the corresponding
   * record exists, the given value is appended at the end of the existing value.
   */
  public native boolean append(byte[] key, byte[] value);
  /**
   * Append the value of a record.
   * @note Equal to the original DB.append method except that the parameters are String.
   * @see #append(byte[], byte[])
   */
  public boolean append(String key, String value) {
    return append(str_to_ary(key), str_to_ary(value));
  }
  /**
   * Add a number to the numeric integer value of a record.
   * @param key the key.
   * @param num the additional number.
   * @param orig the origin number if no record corresponds to the key.  If it is Long.MIN_VALUE
   * and no record corresponds, this method fails.  If it is Long.MAX_VALUE, the value is set as
   * the additional number regardless of the current value.
   * @return the result value, or Long.MIN_VALUE on failure.
   * @note The value is serialized as an 8-byte binary integer in big-endian order, not a decimal
   * string.  If existing value is not 8-byte, this method fails.
   */
  public native long increment(byte[] key, long num, long orig);
  /**
   * Add a number to the numeric integer value of a record.
   * @note Equal to the original DB.increment method except that the parameter is String.
   * @see #increment(byte[], long, long)
   */
  public long increment(String key, long num, long orig) {
    return increment(str_to_ary(key), num, orig);
  }
  /**
   * Add a number to the numeric double value of a record.
   * @param key the key.
   * @param num the additional number.
   * @param orig the origin number if no record corresponds to the key.  If it is negative
   * infinity and no record corresponds, this method fails.  If it is positive infinity, the
   * value is set as the additional number regardless of the current value.
   * @return the result value, or Not-a-number on failure.
   */
  public native double increment_double(byte[] key, double num, double orig);
  /**
   * Add a number to the numeric double value of a record.
   * @note Equal to the original DB.increment method except that the parameter is String.
   * @see #increment_double(byte[], double, double)
   */
  public double increment_double(String key, double num, double orig) {
    return increment_double(str_to_ary(key), num, orig);
  }
  /**
   * Perform compare-and-swap.
   * @param key the key.
   * @param oval the old value.  null means that no record corresponds.
   * @param nval the new value.  null means that the record is removed.
   * @return true on success, or false on failure.
   */
  public native boolean cas(byte[] key, byte[] oval, byte[] nval);
  /**
   * Perform compare-and-swap.
   * @note Equal to the original DB.cas method except that the parameters are String.
   * @see #cas(byte[], byte[], byte[])
   */
  public boolean cas(String key, String oval, String nval) {
    byte[] oary = oval != null ? str_to_ary(oval) : null;
    byte[] nary = oval != null ? str_to_ary(nval) : null;
    return cas(str_to_ary(key), oary, nary);
  }
  /**
   * Remove a record.
   * @param key the key.
   * @return true on success, or false on failure.
   * @note If no record corresponds to the key, false is returned.
   */
  public native boolean remove(byte[] key);
  /**
   * @note Equal to the original DB.remove method except that the parameter is String.
   * @see #remove(byte[])
   */
  public boolean remove(String key) {
    return remove(str_to_ary(key));
  }
  /**
   * Retrieve the value of a record.
   * @param key the key.
   * @return the value of the corresponding record, or null on failure.
   */
  public native byte[] get(byte[] key);
  /**
   * Retrieve the value of a record.
   * @note Equal to the original DB.get method except that the parameter and the return value
   * are String.
   * @see #get(byte[])
   */
  public String get(String key) {
    return ary_to_str(get(str_to_ary(key)));
  }
  /**
   * Retrieve the value of a record and remove it atomically.
   * @param key the key.
   * @return the value of the corresponding record, or null on failure.
   */
  public native byte[] seize(byte[] key);
  /**
   * Retrieve the value of a record and remove it atomically.
   * @note Equal to the original DB.seize method except that the parameter and the return value
   * are String.
   * @see #seize(byte[])
   */
  public String seize(String key) {
    return ary_to_str(seize(str_to_ary(key)));
  }
  /**
   * Store records at once.
   * @param recs the records to store.  Each key and each value must be placed alternately.
   * @param atomic true to perform all operations atomically, or false for non-atomic operations.
   * @return the number of stored records, or -1 on failure.
   */
  public native long set_bulk(byte[][] recs, boolean atomic);
  /**
   * Store records at once.
   * @note Equal to the original DB.set_bulk method except that the parameter is Map.
   * @see #set_bulk(byte[][], boolean)
   */
  public long set_bulk(Map<String, String> recs, boolean atomic) {
    byte[][] recary = new byte[recs.size()*2][];
    int ridx = 0;
    for (Map.Entry<String, String> rec : recs.entrySet()) {
      recary[ridx++] = ((String)rec.getKey()).getBytes();
      recary[ridx++] = ((String)rec.getValue()).getBytes();
    }
    return set_bulk(recary, atomic);
  }
  /**
   * Remove records at once.
   * @param keys the keys of the records to remove.
   * @param atomic true to perform all operations atomically, or false for non-atomic operations.
   * @return the number of removed records, or -1 on failure.
   */
  public native long remove_bulk(byte[][] keys, boolean atomic);
  /**
   * Remove records at once.
   * @note Equal to the original DB.remove_bulk method except that the parameter is List.
   * @see #remove_bulk(byte[][], boolean)
   */
  public long remove_bulk(List<String> keys, boolean atomic) {
    byte[][] keyary = new byte[keys.size()][];
    int kidx = 0;
    for (String key : keys) {
      keyary[kidx++] = key.getBytes();
    }
    return remove_bulk(keyary, atomic);
  }
  /**
   * Retrieve records at once.
   * @param keys the keys of the records to retrieve.
   * @param atomic true to perform all operations atomically, or false for non-atomic operations.
   * @return an array of retrieved records, or null on failure.  Each key and each value is
   * placed alternately.
   */
  public native byte[][] get_bulk(byte[][] keys, boolean atomic);
  /**
   * Retrieve records at once.
   * @note Equal to the original DB.get_bulk method except that the parameter is List and the
   * return value is Map.
   * @see #get_bulk(byte[][], boolean)
   */
  public Map<String, String> get_bulk(List<String> keys, boolean atomic) {
    byte[][] keyary = new byte[keys.size()][];
    int kidx = 0;
    for (String key : keys) {
      keyary[kidx++] = key.getBytes();
    }
    byte[][] recary = get_bulk(keyary, atomic);
    Map<String, String> recs = new HashMap<String, String>();
    for (int i = 0; i + 1 < recary.length; i += 2) {
      recs.put(new String(recary[i]), new String(recary[i+1]));
    }
    return recs;
  }
  /**
   * Remove all records.
   * @return true on success, or false on failure.
   */
  public native boolean clear();
  /**
   * Synchronize updated contents with the file and the device.
   * @param hard true for physical synchronization with the device, or false for logical
   * synchronization with the file system.
   * @param proc a postprocessor object which implements the FileProcessor interface.  If it is
   * null, no postprocessing is performed.
   * @return true on success, or false on failure.
   * @note The operation of the postprocessor is performed atomically and other threads accessing
   * the same record are blocked.  To avoid deadlock, any explicit database operation must not
   * be performed in this method.
   */
  public native boolean synchronize(boolean hard, FileProcessor proc);
  /**
   * Occupy database by locking and do something meanwhile.
   * @param writable true to use writer lock, or false to use reader lock.
   * @param proc a processor object which implements the FileProcessor interface.  If it is null,
   * no processing is performed.
   * @return true on success, or false on failure.
   * @note The operation of the processor is performed atomically and other threads accessing the
   * same record are blocked.  To avoid deadlock, any explicit database operation must not be
   * performed in this method.
   */
  public native boolean occupy(boolean writable, FileProcessor proc);
  /**
   * Create a copy of the database file.
   * @param dest the path of the destination file.
   * @return true on success, or false on failure.
   */
  public native boolean copy(String dest);
  /**
   * Begin transaction.
   * @param hard true for physical synchronization with the device, or false for logical
   * synchronization with the file system.
   * @return true on success, or false on failure.
   */
  public native boolean begin_transaction(boolean hard);
  /**
   * End transaction.
   * @param commit true to commit the transaction, or false to abort the transaction.
   * @return true on success, or false on failure.
   */
  public native boolean end_transaction(boolean commit);
  /**
   * Dump records into a snapshot file.
   * @param dest the name of the destination file.
   * @return true on success, or false on failure.
   */
  public native boolean dump_snapshot(String dest);
  /**
   * Load records from a snapshot file.
   * @param src the name of the source file.
   * @return true on success, or false on failure.
   */
  public native boolean load_snapshot(String src);
  /**
   * Get the number of records.
   * @return the number of records, or -1 on failure.
   */
  public native long count();
  /**
   * Get the size of the database file.
   * @return the size of the database file in bytes, or -1 on failure.
   */
  public native long size();
  /**
   * Get the path of the database file.
   * @return the path of the database file, or null on failure.
   */
  public native String path();
  /**
   * Get the miscellaneous status information.
   * @return a map object of the status information, or null on failure.
   */
  public native Map<String, String> status();
  /**
   * Get keys matching a prefix string.
   * @param prefix the prefix string.
   * @param max the maximum number to retrieve.  If it is negative, no limit is specified.
   * @return a list object of matching keys, or null on failure.
   */
  public native List<String> match_prefix(String prefix, long max);
  /**
   * Get keys matching a regular expression string.
   * @param regex the regular expression string.
   * @param max the maximum number to retrieve.  If it is negative, no limit is specified.
   * @return a list object of matching keys, or null on failure.
   */
  public native List<String> match_regex(String regex, long max);
  /**
   * Merge records from other databases.
   * @param srcary an array of the source detabase objects.
   * @param mode the merge mode.  DB.MSET to overwrite the existing value, DB.MADD to keep the
   * existing value, DB.MAPPEND to append the new value.
   * @return true on success, or false on failure.
   */
  public native boolean merge(DB[] srcary, int mode);
  /**
   * Create a cursor object.
   * @return the return value is the created cursor object.  Each cursor should be disabled
   * with the Cursor#disable method when it is no longer in use.
   */
  public native Cursor cursor();
  /**
   * Set the rule about throwing exception.
   * @param codes an array of error codes.  If each method occurs an error corresponding to one
   * of the specified codes, the error is thrown as an exception.
   * @return true on success, or false on failure.
   */
  public boolean tune_exception_rule(int[] codes) {
    int exbits = 0;
    for (int i = 0; i < codes.length; i++) {
      int code = codes[i];
      if (code <= Error.MISC) exbits |= 1 << code;
    }
    exbits_ = exbits;
    return true;
  }
  /**
   * Set the encoding of external strings.
   * @param encname the name of the encoding.
   * @note The default encoding of external strings is UTF-8.
   * @return true on success, or false on failure.
   */
  public boolean tune_encoding(String encname) {
    try {
      Utility.VERSION.getBytes(encname);
    } catch (UnsupportedEncodingException e) {
      return false;
    }
    encname_ = encname;
    return true;
  }
  /**
   * Get the string expression.
   * @return the string expression.
   */
  public String toString() {
    String tpath = path();
    if (tpath == null) tpath = "(null)";
    return tpath + ": " + count() + ": " + size();
  }
  //----------------------------------------------------------------
  // package methods
  //----------------------------------------------------------------
  /**
   * Get a UTF-8 byte array of a string.
   * @param str the string.
   * @return the UTF-8 byte array.
   */
  byte[] str_to_ary(String str) {
    if (str == null) return null;
    try {
      return str.getBytes(encname_);
    } catch (UnsupportedEncodingException e) {
      return str.getBytes();
    }
  }
  /**
   * Get a string from a UTF-8 byte array.
   * @param ary the UTF-8 byte array.
   * @return the string.
   */
  String ary_to_str(byte[] ary) {
    if (ary == null) return null;
    try {
      return new String(ary, encname_);
    } catch (UnsupportedEncodingException e) {
      return new String(ary);
    }
  }
  //----------------------------------------------------------------
  // private methods
  //----------------------------------------------------------------
  /**
   * Initialize the object.
   */
  private native void initialize(int opts);
  /**
   * Release resources.
   */
  private native void destruct();
  //----------------------------------------------------------------
  // package fields
  //----------------------------------------------------------------
  /** The default encoding. */
  String encname_ = "UTF-8";
  //----------------------------------------------------------------
  // private fields
  //----------------------------------------------------------------
  /** The pointer to the native object */
  private long ptr_ = 0;
  /** The bitfields for exceptional errors. */
  private int exbits_ = 0;
}



// END OF FILE
