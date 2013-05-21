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
 * Interface of cursor to indicate a record.
 */
public class Cursor {
  //----------------------------------------------------------------
  // static initializer
  //----------------------------------------------------------------
  static {
    Loader.load();
  }
  //----------------------------------------------------------------
  // constructors and finalizer
  //----------------------------------------------------------------
  /**
   * Create an instance.
   */
  public Cursor(DB db) {
    initialize(db);
  }
  /**
   * Release resources.
   */
  protected void finalize() {
    destruct();
  }
  //----------------------------------------------------------------
  // public methods
  //----------------------------------------------------------------
  /**
   * Disable the cursor.
   * @note This method should be called explicitly when the cursor is no longer in use.
   */
  public native void disable();
  /**
   * Accept a visitor to the current record.
   * @param visitor a visitor object which implements the Visitor interface.
   * @param writable true for writable operation, or false for read-only operation.
   * @param step true to move the cursor to the next record, or false for no move.
   * @return true on success, or false on failure.
   * @note The operation for each record is performed atomically and other threads accessing the
   * same record are blocked.  To avoid deadlock, any explicit database operation must not be
   * performed in this method.
   */
  public native boolean accept(Visitor visitor, boolean writable, boolean step);
  /**
   * Set the value of the current record.
   * @param value the value.
   * @param step true to move the cursor to the next record, or false for no move.
   * @return true on success, or false on failure.
   */
  public native boolean set_value(byte[] value, boolean step);
  /**
   * Set the value of the current record.
   * Equal to the original Cursor.set_value method except that the parameter is String.
   * @see #set_value(byte[], boolean)
   */
  public boolean set_value(String value, boolean step) {
    DB db = db();
    return set_value(db.str_to_ary(value), step);
  }
  /**
   * Remove the current record.
   * @return true on success, or false on failure.
   * @note If no record corresponds to the key, false is returned.  The cursor is moved to the
   * next record implicitly.
   */
  public native boolean remove();
  /**
   * Get the key of the current record.
   * @param step true to move the cursor to the next record, or false for no move.
   * @return the key of the current record, or null on failure.
   * @note If the cursor is invalidated, null is returned.
   */
  public native byte[] get_key(boolean step);
  /**
   * Get the key of the current record.
   * Equal to the original Cursor.get_key method except that the return value is String.
   * @see #get_key(boolean)
   */
  public String get_key_str(boolean step) {
    DB db = db();
    byte[] key = get_key(step);
    if (key == null) return null;
    return db.ary_to_str(key);
  }
  /**
   * Get the value of the current record.
   * @param step true to move the cursor to the next record, or false for no move.
   * @return the value of the current record, or null on failure.
   * @note If the cursor is invalidated, null is returned.
   */
  public native byte[] get_value(boolean step);
  /**
   * Get the value of the current record.
   * Equal to the original Cursor.get_value method except that the return value is String.
   * @see #get_value(boolean)
   */
  public String get_value_str(boolean step) {
    DB db = db();
    byte[] value = get_value(step);
    if (value == null) return null;
    return db.ary_to_str(value);
  }
  /**
   * Get a pair of the key and the value of the current record.
   * @param step true to move the cursor to the next record, or false for no move.
   * @return a pair of the key and the value of the current record, or null on failure.
   * @note If the cursor is invalidated, null is returned.
   */
  public native byte[][] get(boolean step);
  /**
   * Get a pair of the key and the value of the current record.
   * Equal to the original Cursor.get method except that the return value is String.
   * @see #get(boolean)
   */
  public String[] get_str(boolean step) {
    DB db = db();
    byte[][] rec = get(step);
    if (rec == null) return null;
    String[] strrec = new String[2];
    strrec[0] = db.ary_to_str(rec[0]);
    strrec[1] = db.ary_to_str(rec[1]);
    return strrec;
  }
  /**
   * Get a pair of the key and the value of the current record and remove it atomically.
   * @param step true to move the cursor to the next record, or false for no move.
   * @return a pair of the key and the value of the current record, or null on failure.
   * @note If the cursor is invalidated, null is returned.  The cursor is moved to the
   * next record implicitly.
   */
  public native byte[][] seize();
  /**
   * Get a pair of the key and the value of the current record and remove it atomically.
   * Equal to the original Cursor.get method except that the return value is String.
   * @see #get(boolean)
   */
  public String[] seize_str() {
    DB db = db();
    byte[][] rec = seize();
    if (rec == null) return null;
    String[] strrec = new String[2];
    strrec[0] = db.ary_to_str(rec[0]);
    strrec[1] = db.ary_to_str(rec[1]);
    return strrec;
  }
  /**
   * Jump the cursor to the first record for forward scan.
   * @return true on success, or false on failure.
   */
  public native boolean jump();
  /**
   * Jump the cursor to a record for forward scan.
   * @param key the key of the destination record.
   * @return true on success, or false on failure.
   */
  public native boolean jump(byte[] key);
  /**
   * Jump the cursor to a record for forward scan.
   * Equal to the original Cursor.jump method except that the parameter is String.
   * @see #jump(byte[])
   */
  public boolean jump(String key) {
    DB db = db();
    return jump(db.str_to_ary(key));
  }
  /**
   * Jump the cursor to the last record for backward scan.
   * @return true on success, or false on failure.
   * @note This method is dedicated to tree databases.  Some database types, especially hash
   * databases, may provide a dummy implementation.
   */
  public native boolean jump_back();
  /**
   * Jump the cursor to a record for backward scan.
   * @param key the key of the destination record.
   * @return true on success, or false on failure.
   * @note This method is dedicated to tree databases.  Some database types, especially hash
   * databases, may provide a dummy implementation.
   */
  public native boolean jump_back(byte[] key);
  /**
   * Jump the cursor to a record for backward scan.
   * Equal to the original Cursor.jump_back method except that the parameter is String.
   * @see #jump_back(byte[])
   */
  public boolean jump_back(String key) {
    DB db = db();
    return jump_back(db.str_to_ary(key));
  }
  /**
   * Step the cursor to the next record.
   * @return true on success, or false on failure.
   */
  public native boolean step();
  /**
   * Step the cursor to the previous record.
   * @return true on success, or false on failure.
   * @note This method is dedicated to tree databases.  Some database types, especially hash
   * databases, may provide a dummy implementation.
   */
  public native boolean step_back();
  /**
   * Get the database object.
   * @return the database object.
   */
  public native DB db();
  /**
   * Get the last happened error.
   * @return the last happened error.
   */
  public native Error error();
  /**
   * Get the string expression.
   * @return the string expression.
   */
  public String toString() {
    DB db = db();
    String path = db.path();
    if (path.length() < 1) path = "(null)";
    byte[] key = get_key(false);
    String kstr = (key == null) ? "(null)" : new String(key);
    return path + ": " + kstr;
  }
  //----------------------------------------------------------------
  // private methods
  //----------------------------------------------------------------
  /**
   * Initialize the object.
   */
  private native void initialize(DB db);
  /**
   * Release resources.
   */
  private native void destruct();
  //----------------------------------------------------------------
  // private fields
  //----------------------------------------------------------------
  /** The pointer to the native object */
  private long ptr_ = 0;
  /** The inner database. */
  private DB db_ = null;
}



// END OF FILE
