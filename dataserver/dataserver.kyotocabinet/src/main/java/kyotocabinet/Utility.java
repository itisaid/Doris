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
 * Utility functions.
 */
public class Utility {
  //----------------------------------------------------------------
  // static initializer
  //----------------------------------------------------------------
  static {
    Loader.load();
  }
  //----------------------------------------------------------------
  // public constants
  //----------------------------------------------------------------
  /** The version information. */
  public static final String VERSION = version();
  //----------------------------------------------------------------
  // public static methods
  //----------------------------------------------------------------
  /**
   * Convert a string with a metric prefix to an integer.
   * @param str the string.
   * @return the integer.  If the string does not contain numeric expression, 0 is returned.
   */
  public static native long atoi(String str);
  /**
   * Convert a string with a metric prefix to an integer.
   * @param str the string, which can be trailed by a binary metric prefix.  "K", "M", "G", "T",
   * "P", and "E" are supported.  They are case-insensitive.
   * @return the integer.  If the string does not contain numeric expression, 0 is returned.  If
   * the integer overflows the domain, Long.MAX_VALUE or Long.MIN_VALUE is returned according to
   * the sign.
   */
  public static native long atoix(String str);
  /**
   * Convert a string to a real number.
   * @param str specifies the string.
   * @return the real number.  If the string does not contain numeric expression, 0.0 is
   * returned.
   */
  public static native double atof(String str);
  /**
   * Get the hash value of a byte array by MurMur hashing.
   * @param data the byte array.
   * @return the hash value.
   */
  public static native long hash_murmur(byte[] data);
  /**
   * Get the hash value of a byte array by FNV hashing.
   * @param data the byte array.
   * @return the hash value.
   */
  public static native long hash_fnv(byte[] data);
  /**
   * Get the current time.
   * @return the current time from the epoch in seconds.
   */
  public static double time() {
    return System.currentTimeMillis() / 1000.0;
  }
  /**
   * Remove a file or a directory recursively.
   * @param path the path of a file or a directory.
   * @return true on success, or false on failure.
   */
  public static native boolean remove_files_recursively(String path);
  //----------------------------------------------------------------
  // package static methods
  //----------------------------------------------------------------
  /**
   * Get the string containing the version information.
   * @return the string containing the version information.
   */
  static native String version();
  /**
   * Create the magic data of NOP of Visitor.
   * @return the magic data of NOP of Visitor.
   */
  static native byte[] init_visitor_NOP();
  /**
   * Create the magic data of REMOVE of Visitor.
   * @return the magic data of REMOVE of Visitor.
   */
  static native byte[] init_visitor_REMOVE();
  //----------------------------------------------------------------
  // private methods
  //----------------------------------------------------------------
  /**
   * Dummy constructor.
   */
  private Utility() {}
}



// END OF FILE
