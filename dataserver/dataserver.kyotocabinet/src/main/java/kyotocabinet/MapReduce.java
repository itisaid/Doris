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
 * MapReduce framework.
 */
public abstract class MapReduce {
  //----------------------------------------------------------------
  // static initializer
  //----------------------------------------------------------------
  static {
    Loader.load();
  }
  //----------------------------------------------------------------
  // public constants
  //----------------------------------------------------------------
  /** execution option: avoid locking against update operations */
  public static final int XNOLOCK = 1 << 0;
  /** execution option: avoid compression of temporary databases */
  public static final int XNOCOMP = 1 << 8;
  //----------------------------------------------------------------
  // public methods
  //---------------------------------------------------------------
  /**
   * Map a record data.
   * @param key specifies the key.
   * @param value specifies the value.
   * @return true on success, or false on failure.
   * @note This method can call the MapReduce::emit method to emit a record.  To avoid
   * deadlock, any explicit database operation must not be performed in this method.
   */
  public abstract boolean map(byte[] key, byte[] value);
  /**
   * Reduce a record data.
   * @param key specifies the key.
   * @param iter the iterator to get the values.
   * @return true on success, or false on failure.
   * @note To avoid deadlock, any explicit database operation must not be performed in this
   * method.
   */
  public abstract boolean reduce(byte[] key, ValueIterator iter);
  /**
   * Preprocess the map operations.
   * @return true on success, or false on failure.
   * @note This method can call the MapReduce::emit method to emit a record.  To avoid
   * deadlock, any explicit database operation must not be performed in this method.
   */
  public boolean preprocess() {
    return true;
  }
  /**
   * Mediate between the map and the reduce phases.
   * @return true on success, or false on failure.
   * @note This method can call the MapReduce::emit method to emit a record.  To avoid
   * deadlock, any explicit database operation must not be performed in this method.
   */
  public boolean midprocess() {
    return true;
  }
  /**
   * Postprocess the reduce operations.
   * @return true on success, or false on failure.
   * @note To avoid deadlock, any explicit database operation must not be performed in this
   * method.
   */
  public boolean postprocess() {
    return true;
  }
  /**
   * Process a log message.
   * @param name the name of the event.
   * @param message a supplement message.
   * @return true on success, or false on failure.
   */
  public boolean log(String name, String message) {
    return true;
  }
  /**
   * Execute the MapReduce process about a database.
   * @param db the source database.
   * @param tmppath the path of a directory for the temporary data storage.  If it is an empty
   * string, temporary data are handled on memory.
   * @param opts the optional features by bitwise-or: MapReduce::XNOLOCK to avoid locking
   * against update operations by other threads, MapReduce::XNOCOMP to avoid compression of
   * temporary databases.
   * @return true on success, or false on failure.
   */
  public native boolean execute(DB db, String tmppath, int opts);
  //----------------------------------------------------------------
  // protected methods
  //---------------------------------------------------------------
  /**
   * Emit a record from the mapper.
   * @param key specifies the key.
   * @param value specifies the value.
   * @return true on success, or false on failure.
   */
  protected native boolean emit(byte[] key, byte[] value);
  //----------------------------------------------------------------
  // private fields
  //----------------------------------------------------------------
  /** The pointer to the native object */
  private long ptr_ = 0;
}



// END OF FILE
