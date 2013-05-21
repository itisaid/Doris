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
 * Interface to access a record.
 */
public interface Visitor {
  //----------------------------------------------------------------
  // public constants
  //----------------------------------------------------------------
  /** magic data: no operation */
  public static final byte[] NOP = Utility.init_visitor_NOP();
  /** magic data: remove the record */
  public static final byte[] REMOVE = Utility.init_visitor_REMOVE();
  //----------------------------------------------------------------
  // public methods
  //----------------------------------------------------------------
  /**
   * Visit a record.
   * @param key the key.
   * @param value the value.
   * @return If it is a string, the value is replaced by the content.  If it is Visitor.NOP,
   * nothing is modified.  If it is Visitor.REMOVE, the record is removed.
   */
  public byte[] visit_full(byte[] key, byte[] value);
  /**
   * Visit a empty record space.
   * @param key the key.
   * @return If it is a string, the value is replaced by the content.  If it is Visitor.NOP or
   * Visitor.REMOVE, nothing is modified.
   */
  public byte[] visit_empty(byte[] key);
}



// END OF FILE
