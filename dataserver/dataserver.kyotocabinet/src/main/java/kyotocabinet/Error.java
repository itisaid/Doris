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
 * Error data.
 */
public class Error extends RuntimeException {
  //----------------------------------------------------------------
  // static initializer
  //----------------------------------------------------------------
  static {
    Loader.load();
  }
  //----------------------------------------------------------------
  // public inner classes
  //----------------------------------------------------------------
  /**
   * Exception for the success code.
   */
  public static class XSUCCESS extends Error {
    XSUCCESS(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the not implemented code.
   */
  public static class XNOIMPL extends Error {
    XNOIMPL(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the invalid operation code.
   */
  public static class XINVALID extends Error {
    XINVALID(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the no repository code.
   */
  public static class XNOREPOS extends Error {
    XNOREPOS(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the no permission code.
   */
  public static class XNOPERM extends Error {
    public XNOPERM(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the broken file code.
   */
  public static class XBROKEN extends Error {
    XBROKEN(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the record duplication code.
   */
  public static class XDUPREC extends Error {
    XDUPREC(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the no record code.
   */
  public static class XNOREC extends Error {
    XNOREC(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the logical inconsistency code.
   */
  public static class XLOGIC extends Error {
    XLOGIC(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the system error code.
   */
  public static class XSYSTEM extends Error {
    XSYSTEM(String expr) {
      super(expr);
    }
  }
  /**
   * Exception for the miscellaneous error code.
   */
  public static class XMISC extends Error {
    XMISC(String expr) {
      super(expr);
    }
  }
  //----------------------------------------------------------------
  // public constants
  //----------------------------------------------------------------
  /** error code: success */
  public static final int SUCCESS = 0;
  /** error code: not implemented */
  public static final int NOIMPL = 1;
  /** error code: invalid operation */
  public static final int INVALID = 2;
  /** error code: no repository. */
  public static final int NOREPOS = 3;
  /** error code: no permission */
  public static final int NOPERM = 4;
  /** error code: broken file */
  public static final int BROKEN = 5;
  /** error code: record duplication */
  public static final int DUPREC = 6;
  /** error code: no record */
  public static final int NOREC = 7;
  /** error code: logical inconsistency */
  public static final int LOGIC = 8;
  /** error code: system error */
  public static final int SYSTEM = 9;
  /** error code: miscellaneous error */
  public static final int MISC = 15;
  //----------------------------------------------------------------
  // constructors and finalizer
  //----------------------------------------------------------------
  /**
   * Create an instance.
   */
  public Error() {
    code_ = SUCCESS;
    message_ = "no error";
  }
  /**
   * Create an instance.
   * @param code the error code.
   * @param message the supplement message.
   */
  public Error(int code, String message) {
    code_ = code;
    message_ = message;
  }
  //----------------------------------------------------------------
  // public methods
  //---------------------------------------------------------------
  /**
   * Set the error information.
   * @param code the error code.
   * @param message the supplement message.
   */
  public void set(int code, String message) {
    code_ = code;
    message_ = message;
  }
  /**
   * Get the error code.
   * @return the error code.
   */
  public int code() {
    return code_;
  }
  /**
   * Get the readable string of the code.
   * @return the readable string of the code.
   */
  public String name() {
    return codename(code_);
  }
  /**
   * Get the supplement message.
   * @return the supplement message.
   */
  public String message() {
    return message_;
  }
  /**
   * Get the string expression.
   * @return the string expression.
   */
  public String toString() {
    return name() + ": " + message();
  }
  /**
   * Check equality.
   * @param right an error object.
   * @return true for the both operands are equal, or false if not.
   */
  public boolean equals(Error right) {
    return right.code_ == code_;
  }
  //----------------------------------------------------------------
  // package methods
  //----------------------------------------------------------------
  /**
   * Create an instance.
   * @param expr an expression of the error.
   */
  Error(String expr) {
    super(expr);
    code_ = (int)Utility.atoi(expr);
    int idx = expr.indexOf(':');
    if (idx >= 0) {
      idx++;
      int len = expr.length();
      while (idx < len && expr.charAt(idx) == ' ') {
        idx++;
      }
      expr = expr.substring(idx);
    }
    message_ = expr;
  }
  //----------------------------------------------------------------
  // private static methods
  //----------------------------------------------------------------
  /**
   * Get the readable string of an error code.
   * @param code the error code.
   * @return the readable string of the error code.
   */
  private static native String codename(int code);
  //----------------------------------------------------------------
  // private fields
  //----------------------------------------------------------------
  /** The error code. */
  private int code_;
  /** The supplement message. */
  private String message_;
}



// END OF FILE
