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
 * Test cases.
 */
public class Test {
  //----------------------------------------------------------------
  // public static methods
  //----------------------------------------------------------------
  /** main routine of the test command */
  public static void main(String[] args) {
    if (args.length < 1) usage();
    int rv = 0;
    if (args[0].equals("order")) {
      rv = runorder(args);
    } else if (args[0].equals("wicked")) {
      rv = runwicked(args);
    } else if (args[0].equals("misc")) {
      rv = runmisc(args);
    } else if (args[0].equals("memsize")) {
      rv = runmemsize(args);
    } else {
      usage();
    }
    System.gc();
    System.exit(rv);
  }
  //----------------------------------------------------------------
  // private static methods
  //----------------------------------------------------------------
  /** print the usage and exit */
  private static void usage() {
    STDERR.printf("test cases of the Java binding\n");
    STDERR.printf("\n");
    STDERR.printf("synopsis:\n");
    STDERR.printf("  java %s arguments...\n", Test.class.getName());
    STDERR.printf("\n");
    STDERR.printf("arguments:\n");
    STDERR.printf("  order [-th num] [-rnd] [-etc] path rnum\n");
    STDERR.printf("  wicked [-th num] [-it num] path rnum\n");
    STDERR.printf("  misc path\n");
    STDERR.printf("  memsize [rnum [path]]\n");
    STDERR.printf("\n");
    System.exit(1);
  }
  /** print the error message of the database */
  private static void dberrprint(DB db, String func) {
    Error err = db.error();
    printf("%s: %s: %d: %s: %s\n",
                  Test.class.getName(), func, err.code(), err.name(), err.message());
  }
  /** print members of a database */
  private static void dbmetaprint(DB db, boolean verbose) {
    if (verbose) {
      Map<String, String> status = db.status();
      if (status != null) {
        for (Map.Entry<String, String> rec : status.entrySet()) {
          printf("%s: %s\n", rec.getKey(), rec.getValue());
        }
      }
    } else {
      printf("count: %d\n", db.count());
      printf("size: %d\n", db.size());
    }
  }
  /** parse arguments of order command */
  private static int runorder(String[] args) {
    String path = null;
    String rstr = null;
    int thnum = 1;
    boolean rnd = false;
    boolean etc = false;
    for (int i = 1; i < args.length; i++) {
      String arg = args[i];
      if (path == null && arg.startsWith("-")) {
        if (arg.equals("-th")) {
          if (++i >= args.length) usage();
          thnum = (int)Utility.atoix(args[i]);
        } else if (arg.equals("-rnd")) {
          rnd = true;
        } else if (arg.equals("-etc")) {
          etc = true;
        } else {
          usage();
        }
      } else if (path == null) {
        path = arg;
      } else if (rstr == null) {
        rstr = arg;
      } else {
        usage();
      }
    }
    if (path == null || rstr == null) usage();
    long rnum = Utility.atoix(rstr);
    if (rnum < 1 || thnum < 1) usage();
    int rv = procorder(path, rnum, thnum, rnd, etc);
    return rv;
  }
  /** parse arguments of wicked command */
  private static int runwicked(String[] args) {
    String path = null;
    String rstr = null;
    int thnum = 1;
    int itnum = 1;
    for (int i = 1; i < args.length; i++) {
      String arg = args[i];
      if (path == null && arg.startsWith("-")) {
        if (arg.equals("-th")) {
          if (++i >= args.length) usage();
          thnum = (int)Utility.atoix(args[i]);
        } else if (arg.equals("-it")) {
          if (++i >= args.length) usage();
          itnum = (int)Utility.atoix(args[i]);
        } else {
          usage();
        }
      } else if (path == null) {
        path = arg;
      } else if (rstr == null) {
        rstr = arg;
      } else {
        usage();
      }
    }
    if (path == null || rstr == null) usage();
    long rnum = Utility.atoix(rstr);
    if (rnum < 1 || thnum < 1 || itnum < 1) usage();
    int rv = procwicked(path, rnum, thnum, itnum);
    return rv;
  }
  /** parse arguments of misc command */
  private static int runmisc(String[] args) {
    String path = null;
    for (int i = 1; i < args.length; i++) {
      String arg = args[i];
      if (path == null && arg.startsWith("-")) {
        usage();
      } else if (path == null) {
        path = arg;
      } else {
        usage();
      }
    }
    if (path == null) usage();
    int rv = procmisc(path);
    return rv;
  }
  /** parse arguments of memsize command */
  private static int runmemsize(String[] args) {
    String rstr = null;
    String path = null;
    for (int i = 1; i < args.length; i++) {
      String arg = args[i];
      if (path == null && arg.startsWith("-")) {
        usage();
      } else if (rstr == null) {
        rstr = arg;
      } else if (path == null) {
        path = arg;
      } else {
        usage();
      }
    }
    long rnum = rstr != null ? Utility.atoix(rstr) : 1000000;
    if (rnum < 1) usage();
    int rv = procmemsize(rnum, path);
    return rv;
  }
  /** perform order command */
  private static int procorder(String path, long rnum, int thnum, boolean rnd, boolean etc) {
    printf("<In-order Test>\n  path=%s  rnum=%d  thnum=%d  rnd=%s  etc=%s\n\n",
           path, rnum, thnum, rnd, etc);
    boolean err = false;
    printf("calling utility functions:\n");
    Utility.atoi("123.456mikio");
    Utility.atoix("123.456mikio");
    Utility.atof("123.456mikio");
    Utility.hash_murmur(path.getBytes());
    Utility.hash_fnv(path.getBytes());
    DB db = new DB();
    int[] codes = { Error.SUCCESS, Error.NOIMPL, Error.MISC };
    db.tune_exception_rule(codes);
    db.tune_encoding("UTF-8");
    printf("opening the database:\n");
    double stime = Utility.time();
    if (!db.open(path, DB.OWRITER | DB.OCREATE | DB.OTRUNCATE)) {
      dberrprint(db, "DB::open");
      err = true;
    }
    double etime = Utility.time();
    printf("time: %.3f\n", etime - stime);
    printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    printf("setting records:\n");
    stime = Utility.time();
    class Setter extends Thread {
      public Setter(int id, long rnum, int thnum, boolean rnd, DB db) {
        id_ = id;
        rnum_ = rnum;
        thnum_ = thnum;
        rnd_ = rnd;
        db_ = db;
        err_ = false;
      }
      public boolean error() {
        return err_;
      }
      public void run() {
        long base = id_ * rnum_;
        long range = rnum_ * thnum_;
        for (long i = 1; !err_ && i <= rnum_; i++) {
          String key = String.format("%08d", rnd_ ? rand(range) + 1 : base + i);
          if (!db_.set(key, key)) {
            dberrprint(db_, "DB::set");
            err_ = true;
          }
          if (id_ < 1 && rnum_ > 250 && i % (rnum_ / 250) == 0) {
            printf(".");
            if (i == rnum_ || i % (rnum_ / 10) == 0) printf(" (%08d)\n", i);
          }
        }
      }
      private int id_;
      private long rnum_;
      private int thnum_;
      private boolean rnd_;
      private DB db_;
      private boolean err_;
    }
    Setter[] setters = new Setter[thnum];
    for (int i = 0; i < thnum; i++) {
      setters[i] = new Setter(i, rnum, thnum, rnd, db);
      setters[i].setDefaultUncaughtExceptionHandler(EXH);
      setters[i].start();
    }
    for (int i = 0; i < thnum; i++) {
      try {
        setters[i].join();
      } catch (java.lang.InterruptedException e) {
        e.printStackTrace();
        err = true;
      }
      if (setters[i].error()) err = true;
    }
    etime = Utility.time();
    dbmetaprint(db, false);
    printf("time: %.3f\n", etime - stime);
    printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    if (etc) {
      printf("adding records:\n");
      stime = Utility.time();
      class Adder extends Thread {
        public Adder(int id, long rnum, int thnum, boolean rnd, DB db) {
          id_ = id;
          rnum_ = rnum;
          thnum_ = thnum;
          rnd_ = rnd;
          db_ = db;
          err_ = false;
        }
        public boolean error() {
          return err_;
        }
        public void run() {
          long base = id_ * rnum_;
          long range = rnum_ * thnum_;
          for (long i = 1; !err_ && i <= rnum_; i++) {
            String key = String.format("%08d", rnd_ ? rand(range) + 1 : base + i);
            if (!db_.add(key, key) && db_.error().code() != Error.DUPREC) {
              dberrprint(db_, "DB::add");
              err_ = true;
            }
            if (id_ < 1 && rnum_ > 250 && i % (rnum_ / 250) == 0) {
              printf(".");
              if (i == rnum_ || i % (rnum_ / 10) == 0) printf(" (%08d)\n", i);
            }
          }
        }
        private int id_;
        private long rnum_;
        private int thnum_;
        private boolean rnd_;
        private DB db_;
        private boolean err_;
      }
      Adder[] adders = new Adder[thnum];
      for (int i = 0; i < thnum; i++) {
        adders[i] = new Adder(i, rnum, thnum, rnd, db);
        adders[i].setDefaultUncaughtExceptionHandler(EXH);
        adders[i].start();
      }
      for (int i = 0; i < thnum; i++) {
        try {
          adders[i].join();
        } catch (java.lang.InterruptedException e) {
          e.printStackTrace();
          err = true;
        }
        if (adders[i].error()) err = true;
      }
      etime = Utility.time();
      dbmetaprint(db, false);
      printf("time: %.3f\n", etime - stime);
      printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    }
    if (etc) {
      printf("appending records:\n");
      stime = Utility.time();
      class Appender extends Thread {
        public Appender(int id, long rnum, int thnum, boolean rnd, DB db) {
          id_ = id;
          rnum_ = rnum;
          thnum_ = thnum;
          rnd_ = rnd;
          db_ = db;
          err_ = false;
        }
        public boolean error() {
          return err_;
        }
        public void run() {
          long base = id_ * rnum_;
          long range = rnum_ * thnum_;
          for (long i = 1; !err_ && i <= rnum_; i++) {
            String key = String.format("%08d", rnd_ ? rand(range) + 1 : base + i);
            if (!db_.append(key, key)) {
              dberrprint(db_, "DB::append");
              err_ = true;
            }
            if (id_ < 1 && rnum_ > 250 && i % (rnum_ / 250) == 0) {
              printf(".");
              if (i == rnum_ || i % (rnum_ / 10) == 0) printf(" (%08d)\n", i);
            }
          }
        }
        private int id_;
        private long rnum_;
        private int thnum_;
        private boolean rnd_;
        private DB db_;
        private boolean err_;
      }
      Appender[] appenders = new Appender[thnum];
      for (int i = 0; i < thnum; i++) {
        appenders[i] = new Appender(i, rnum, thnum, rnd, db);
        appenders[i].setDefaultUncaughtExceptionHandler(EXH);
        appenders[i].start();
      }
      for (int i = 0; i < thnum; i++) {
        try {
          appenders[i].join();
        } catch (java.lang.InterruptedException e) {
          e.printStackTrace();
          err = true;
        }
        if (appenders[i].error()) err = true;
      }
      etime = Utility.time();
      dbmetaprint(db, false);
      printf("time: %.3f\n", etime - stime);
      printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    }
    if (etc) {
      printf("accepting records:\n");
      stime = Utility.time();
      class Accepter extends Thread {
        public Accepter(int id, long rnum, int thnum, boolean rnd, DB db) {
          id_ = id;
          rnum_ = rnum;
          thnum_ = thnum;
          rnd_ = rnd;
          db_ = db;
          err_ = false;
        }
        public boolean error() {
          return err_;
        }
        public void run() {
          class VisitorImpl implements Visitor {
            public VisitorImpl(boolean rnd) {
              rnd_ = rnd;
              cnt_ = 0;
            }
            public byte[] visit_full(byte[] key, byte[] value) {
              cnt_++;
              if (cnt_ % 100 == 0) Thread.yield();
              byte[] rv = NOP;
              if (rnd_) {
                switch ((int)rand(7)) {
                  case 0: {
                    rv = String.format("%d", cnt_).getBytes();
                    break;
                  }
                  case 1: {
                    rv = REMOVE;
                    break;
                  }
                }
              }
              return rv;
            }
            public byte[] visit_empty(byte[] key) {
              return visit_full(key, key);
            }
            private boolean rnd_;
            private long cnt_;
          }
          Visitor visitor = new VisitorImpl(rnd_);
          long base = id_ * rnum_;
          long range = rnum_ * thnum_;
          for (long i = 1; !err_ && i <= rnum_; i++) {
            String key = String.format("%08d", rnd_ ? rand(range) + 1 : base + i);
            if (!db_.accept(key.getBytes(), visitor, rnd_)) {
              dberrprint(db_, "DB::accept");
              err_ = true;
            }
            if (id_ < 1 && rnum_ > 250 && i % (rnum_ / 250) == 0) {
              printf(".");
              if (i == rnum_ || i % (rnum_ / 10) == 0) printf(" (%08d)\n", i);
            }
          }
        }
        private int id_;
        private long rnum_;
        private int thnum_;
        private boolean rnd_;
        private DB db_;
        private boolean err_;
      }
      Accepter[] accepters = new Accepter[thnum];
      for (int i = 0; i < thnum; i++) {
        accepters[i] = new Accepter(i, rnum, thnum, rnd, db);
        accepters[i].setDefaultUncaughtExceptionHandler(EXH);
        accepters[i].start();
      }
      for (int i = 0; i < thnum; i++) {
        try {
          accepters[i].join();
        } catch (java.lang.InterruptedException e) {
          e.printStackTrace();
          err = true;
        }
        if (accepters[i].error()) err = true;
      }
      etime = Utility.time();
      dbmetaprint(db, false);
      printf("time: %.3f\n", etime - stime);
      printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    }
    printf("getting records:\n");
    stime = Utility.time();
    class Getter extends Thread {
      public Getter(int id, long rnum, int thnum, boolean rnd, DB db) {
        id_ = id;
        rnum_ = rnum;
        thnum_ = thnum;
        rnd_ = rnd;
        db_ = db;
        err_ = false;
      }
      public boolean error() {
        return err_;
      }
      public void run() {
        long base = id_ * rnum_;
        long range = rnum_ * thnum_;
        for (long i = 1; !err_ && i <= rnum_; i++) {
          String key = String.format("%08d", rnd_ ? rand(range) + 1 : base + i);
          if (db_.get(key) == null && db_.error().code() != Error.NOREC) {
            dberrprint(db_, "DB::get");
            err_ = true;
          }
          if (id_ < 1 && rnum_ > 250 && i % (rnum_ / 250) == 0) {
            printf(".");
            if (i == rnum_ || i % (rnum_ / 10) == 0) printf(" (%08d)\n", i);
          }
        }
      }
      private int id_;
      private long rnum_;
      private int thnum_;
      private boolean rnd_;
      private DB db_;
      private boolean err_;
    }
    Getter[] getters = new Getter[thnum];
    for (int i = 0; i < thnum; i++) {
      getters[i] = new Getter(i, rnum, thnum, rnd, db);
      getters[i].setDefaultUncaughtExceptionHandler(EXH);
      getters[i].start();
    }
    for (int i = 0; i < thnum; i++) {
      try {
        getters[i].join();
      } catch (java.lang.InterruptedException e) {
        e.printStackTrace();
        err = true;
      }
      if (getters[i].error()) err = true;
    }
    etime = Utility.time();
    dbmetaprint(db, false);
    printf("time: %.3f\n", etime - stime);
    printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    if (etc) {
      printf("traversing the database by the inner iterator:\n");
      stime = Utility.time();
      class InnerTraverser extends Thread {
        public InnerTraverser(int id, long rnum, int thnum, boolean rnd, DB db) {
          id_ = id;
          rnum_ = rnum;
          thnum_ = thnum;
          rnd_ = rnd;
          db_ = db;
          err_ = false;
        }
        public boolean error() {
          return err_;
        }
        public void run() {
          class VisitorImpl implements Visitor {
            public VisitorImpl(int id, boolean rnd) {
              id_ = id;
              rnd_ = rnd;
              cnt_ = 0;
            }
            public byte[] visit_full(byte[] key, byte[] value) {
              cnt_++;
              if (cnt_ % 100 == 0) Thread.yield();
              byte[] rv = NOP;
              if (rnd_) {
                switch ((int)rand(7)) {
                  case 0: {
                    rv = String.format("%d%d", cnt_, cnt_).getBytes();
                    break;
                  }
                  case 1: {
                    rv = REMOVE;
                    break;
                  }
                }
              }
              if (id_ < 1 && rnum_ > 250 && cnt_ % (rnum_ / 250) == 0) {
                printf(".");
                if (cnt_ == rnum_ || cnt_ % (rnum_ / 10) == 0) printf(" (%08d)\n", cnt_);
              }
              return rv;
            }
            public byte[] visit_empty(byte[] key) {
              return NOP;
            }
            private int id_;
            private boolean rnd_;
            private long cnt_;
          }
          Visitor visitor = new VisitorImpl(id_, rnd_);
          if (!db_.iterate(visitor, rnd_)) {
            dberrprint(db_, "DB::iterate");
            err_ = true;
          }
        }
        private int id_;
        private long rnum_;
        private int thnum_;
        private boolean rnd_;
        private DB db_;
        private boolean err_;
      }
      InnerTraverser[] itraversers = new InnerTraverser[thnum];
      for (int i = 0; i < thnum; i++) {
        itraversers[i] = new InnerTraverser(i, rnum, thnum, rnd, db);
        itraversers[i].setDefaultUncaughtExceptionHandler(EXH);
        itraversers[i].start();
      }
      for (int i = 0; i < thnum; i++) {
        try {
          itraversers[i].join();
        } catch (java.lang.InterruptedException e) {
          e.printStackTrace();
          err = true;
        }
        if (itraversers[i].error()) err = true;
      }
      if (rnd) printf(" (end)\n");
      etime = Utility.time();
      dbmetaprint(db, false);
      printf("time: %.3f\n", etime - stime);
      printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    }
    if (etc) {
      printf("traversing the database by the outer cursor:\n");
      stime = Utility.time();
      class OuterTraverser extends Thread {
        public OuterTraverser(int id, long rnum, int thnum, boolean rnd, DB db) {
          id_ = id;
          rnum_ = rnum;
          thnum_ = thnum;
          rnd_ = rnd;
          db_ = db;
          err_ = false;
        }
        public boolean error() {
          return err_;
        }
        public void run() {
          class VisitorImpl implements Visitor {
            public VisitorImpl(int id, long rnum, boolean rnd) {
              id_ = id;
              rnum_ = rnum;
              rnd_ = rnd;
              cnt_ = 0;
            }
            public byte[] visit_full(byte[] key, byte[] value) {
              cnt_++;
              if (cnt_ % 100 == 0) Thread.yield();
              byte[] rv = NOP;
              if (rnd_) {
                switch ((int)rand(7)) {
                  case 0: {
                    rv = String.format("%d%d", cnt_, cnt_).getBytes();
                    break;
                  }
                  case 1: {
                    rv = REMOVE;
                    break;
                  }
                }
              }
              if (id_ < 1 && rnum_ > 250 && cnt_ % (rnum_ / 250) == 0) {
                printf(".");
                if (cnt_ == rnum_ || cnt_ % (rnum_ / 10) == 0) printf(" (%08d)\n", cnt_);
              }
              return rv;
            }
            public byte[] visit_empty(byte[] key) {
              return NOP;
            }
            private int id_;
            private long rnum_;
            private boolean rnd_;
            private long cnt_;
          }
          Visitor visitor = new VisitorImpl(id_, rnum_, rnd_);
          Cursor cur = db_.cursor();
          if (!cur.jump() && db_.error().code() != Error.NOREC) {
            dberrprint(db_, "Cursor::jump");
            err_ = true;
          }
          while (cur.accept(visitor, rnd_, false)) {
            if (!cur.step() && db_.error().code() != Error.NOREC) {
              dberrprint(db_, "Cursor::step");
              err_ = true;
            }
          }
          if (db_.error().code() != Error.NOREC) {
            dberrprint(db_, "Cursor::jump");
            err_ = true;
          }
          if (!rnd_ || rand(2) == 0) cur.disable();
        }
        private int id_;
        private long rnum_;
        private int thnum_;
        private boolean rnd_;
        private DB db_;
        private boolean err_;
      }
      OuterTraverser[] otraversers = new OuterTraverser[thnum];
      for (int i = 0; i < thnum; i++) {
        otraversers[i] = new OuterTraverser(i, rnum, thnum, rnd, db);
        otraversers[i].setDefaultUncaughtExceptionHandler(EXH);
        otraversers[i].start();
      }
      for (int i = 0; i < thnum; i++) {
        try {
          otraversers[i].join();
        } catch (java.lang.InterruptedException e) {
          e.printStackTrace();
          err = true;
        }
        if (otraversers[i].error()) err = true;
      }
      printf(" (end)\n");
      etime = Utility.time();
      dbmetaprint(db, false);
      printf("time: %.3f\n", etime - stime);
      printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    }
    printf("removing records:\n");
    stime = Utility.time();
    class Remover extends Thread {
      public Remover(int id, long rnum, int thnum, boolean rnd, DB db) {
        id_ = id;
        rnum_ = rnum;
        thnum_ = thnum;
        rnd_ = rnd;
        db_ = db;
        err_ = false;
      }
      public boolean error() {
        return err_;
      }
      public void run() {
        long base = id_ * rnum_;
        long range = rnum_ * thnum_;
        for (long i = 1; !err_ && i <= rnum_; i++) {
          String key = String.format("%08d", rnd_ ? rand(range) + 1 : base + i);
          if (!db_.remove(key) && db_.error().code() != Error.NOREC) {
            dberrprint(db_, "DB::remove");
            err_ = true;
          }
          if (id_ < 1 && rnum_ > 250 && i % (rnum_ / 250) == 0) {
            printf(".");
            if (i == rnum_ || i % (rnum_ / 10) == 0) printf(" (%08d)\n", i);
          }
        }
      }
      private int id_;
      private long rnum_;
      private int thnum_;
      private boolean rnd_;
      private DB db_;
      private boolean err_;
    }
    Remover[] removers = new Remover[thnum];
    for (int i = 0; i < thnum; i++) {
      removers[i] = new Remover(i, rnum, thnum, rnd, db);
      removers[i].setDefaultUncaughtExceptionHandler(EXH);
      removers[i].start();
    }
    for (int i = 0; i < thnum; i++) {
      try {
        removers[i].join();
      } catch (java.lang.InterruptedException e) {
        e.printStackTrace();
        err = true;
      }
      if (removers[i].error()) err = true;
    }
    etime = Utility.time();
    dbmetaprint(db, true);
    printf("time: %.3f\n", etime - stime);
    printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    printf("closing the database:\n");
    stime = Utility.time();
    if (!db.close()) {
      dberrprint(db, "DB::close");
      err = true;
    }
    etime = Utility.time();
    printf("time: %.3f\n", etime - stime);
    printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    printf("%s\n\n", err ? "error" : "ok");
    return err ? 1 : 0;
  }
  /** perform wicked command */
  private static int procwicked(String path, long rnum, int thnum, int itnum) {
    printf("<Wicked Test>\n  path=%s  rnum=%d  thnum=%d  itnum=%d\n\n",
           path, rnum, thnum, itnum);
    boolean err = false;
    DB db = new DB();
    int[] codes = { Error.SUCCESS, Error.NOIMPL, Error.MISC };
    db.tune_exception_rule(codes);
    db.tune_encoding("UTF-8");
    for (int itcnt = 1; itcnt <= itnum; itcnt++) {
      if (itnum > 1) printf("iteration %d:\n", itcnt);
      double stime = Utility.time();
      int omode = DB.OWRITER | DB.OCREATE;
      if (itcnt == 1) omode |= DB.OTRUNCATE;
      if (!db.open(path, omode)) {
        dberrprint(db, "DB::open");
        err = true;
      }
      class Operator extends Thread {
        public Operator(int id, long rnum, int thnum, DB db) {
          id_ = id;
          rnum_ = rnum;
          thnum_ = thnum;
          db_ = db;
          err_ = false;
        }
        public boolean error() {
          return err_;
        }
        public void run() {
          class VisitorImpl implements Visitor {
            public VisitorImpl() {
              cnt_ = 0;
            }
            public byte[] visit_full(byte[] key, byte[] value) {
              cnt_++;
              if (cnt_ % 100 == 0) Thread.yield();
              byte[] rv = NOP;
              switch ((int)rand(7)) {
                case 0: {
                  rv = String.format("%d", cnt_).getBytes();
                  break;
                }
                case 1: {
                  rv = REMOVE;
                  break;
                }
              }
              return rv;
            }
            public byte[] visit_empty(byte[] key) {
              return visit_full(key, key);
            }
            private long cnt_;
          }
          Visitor visitor = new VisitorImpl();
          Cursor cur = db_.cursor();
          long range = rnum_ * thnum_;
          for (long i = 1; !err_ && i <= rnum_; i++) {
            boolean tran = rand(100) == 0;
            if (tran && !db_.begin_transaction(rand(rnum_) == 0)) {
              dberrprint(db_, "DB::begin_transaction");
              tran = false;
              err_ = true;
            }
            String key = String.format("%08d", rand(range) + 1);
            switch ((int)rand(12)) {
              case 0: {
                if (!db_.set(key, key)) {
                  dberrprint(db_, "DB::set");
                  err_ = true;
                }
                break;
              }
              case 1: {
                if (!db_.add(key, key) && db_.error().code() != Error.DUPREC) {
                  dberrprint(db_, "DB::add");
                  err_ = true;
                }
                break;
              }
              case 2: {
                if (!db_.replace(key, key) && db_.error().code() != Error.NOREC) {
                  dberrprint(db_, "DB::replace");
                  err_ = true;
                }
                break;
              }
              case 3: {
                if (!db_.append(key, key)) {
                  dberrprint(db_, "DB::append");
                  err_ = true;
                }
                break;
              }
              case 4: {
                if (rand(2) == 0) {
                  if (db_.increment(key, rand(10), 0) == Long.MIN_VALUE &&
                      db_.error().code() != Error.LOGIC) {
                    dberrprint(db_, "DB::increment");
                    err_ = true;
                  }
                } else {
                  if (Double.isNaN(db_.increment_double(key, rand(1000) / 100.0, 0)) &&
                      db_.error().code() != Error.LOGIC) {
                    dberrprint(db_, "DB::increment_double");
                    err_ = true;
                  }
                }
                break;
              }
              case 5: {
                if (!db_.cas(key, key, key) && db_.error().code() != Error.LOGIC) {
                  dberrprint(db_, "DB::cas");
                  err_ = true;
                }
                break;
              }
              case 6: {
                if (!db_.remove(key) && db_.error().code() != Error.NOREC) {
                  dberrprint(db_, "DB::remove");
                  err_ = true;
                }
                break;
              }
              case 7: {
                if (!db_.accept(key.getBytes(), visitor, true)) {
                  dberrprint(db_, "DB::accept");
                  err_ = true;
                }
                break;
              }
              case 8: {
                if (rand(10) == 0) {
                  if (rand(4) == 0) {
                    try {
                      if (!cur.jump_back(key) && db_.error().code() != Error.NOREC) {
                        dberrprint(db_, "Cursor::jump_back");
                        err_ = true;
                      }
                    } catch (Error.XNOIMPL e) {}
                  } else {
                    if (!cur.jump(key) && db_.error().code() != Error.NOREC) {
                      dberrprint(db_, "Cursor::jump");
                      err_ = true;
                    }
                  }
                } else {
                  switch ((int)rand(6)) {
                    case 0: {
                      if (cur.get_key_str(false) == null &&
                          db_.error().code() != Error.NOREC) {
                        dberrprint(db_, "Cursor::get_key");
                        err_ = true;
                      }
                      break;
                    }
                    case 1: {
                      if (cur.get_value_str(false) == null &&
                          db_.error().code() != Error.NOREC) {
                        dberrprint(db_, "Cursor::get_value");
                        err_ = true;
                      }
                      break;
                    }
                    case 2: {
                      if (cur.get_str(false) == null &&
                          db_.error().code() != Error.NOREC) {
                        dberrprint(db_, "Cursor::get");
                        err_ = true;
                      }
                      break;
                    }
                    case 3: {
                      if (!cur.remove() && db_.error().code() != Error.NOREC) {
                        dberrprint(db_, "Cursor::remove");
                        err_ = true;
                      }
                      break;
                    }
                    default: {
                      if (!cur.accept(visitor, true, rand(2) == 0) &&
                          db_.error().code() != Error.NOREC) {
                        dberrprint(db_, "Cursor::accept");
                        err_ = true;
                      }
                      break;
                    }
                  }
                }
                if (rand(2) == 0) {
                  if (!cur.step() && db_.error().code() != Error.NOREC) {
                    dberrprint(db_, "Cursor::step");
                    err_ = true;
                  }
                }
                if (rand(rnum_ / 50 + 1) == 0) {
                  String prefix = key.substring(0, key.length() - 1);
                  if (db_.match_prefix(prefix, rand(10)) == null) {
                    dberrprint(db_, "DB::match_prefix");
                    err_ = true;
                  }
                }
                if (rand(rnum_ / 50 + 1) == 0) {
                  String regex = key.substring(0, key.length() - 1);
                  if (db_.match_regex(regex, rand(10)) == null &&
                      db_.error().code() != Error.LOGIC) {
                    dberrprint(db_, "DB::match_regex");
                    err_ = true;
                  }
                }
                if (rand(10) == 0) {
                  Cursor paracur = db_.cursor();
                  paracur.jump(key);
                  if (!paracur.accept(visitor, true, rand(2) == 0) &&
                      db_.error().code() != Error.NOREC) {
                    dberrprint(db_, "Cursor::accept");
                    err_ = true;
                  }
                  paracur.disable();
                }
                break;
              }
              default: {
                if (db_.get(key) == null && db_.error().code() != Error.NOREC) {
                  dberrprint(db_, "DB::get");
                  err_ = true;
                }
                break;
              }
            }
            if (tran) {
              if (rand(10) == 0) Thread.yield();
              if (!db_.end_transaction(rand(10) > 0)) {
                dberrprint(db_, "DB::end_transaction");
                err_ = true;
              }
            }
            if (id_ < 1 && rnum_ > 250 && i % (rnum_ / 250) == 0) {
              printf(".");
              if (i == rnum_ || i % (rnum_ / 10) == 0) printf(" (%08d)\n", i);
            }
          }
          if (rand(2) == 0) cur.disable();
        }
        private int id_;
        private long rnum_;
        private int thnum_;
        private DB db_;
        private boolean err_;
      }
      Operator[] operators = new Operator[thnum];
      for (int i = 0; i < thnum; i++) {
        operators[i] = new Operator(i, rnum, thnum, db);
        operators[i].setDefaultUncaughtExceptionHandler(EXH);
        operators[i].start();
      }
      for (int i = 0; i < thnum; i++) {
        try {
          operators[i].join();
        } catch (java.lang.InterruptedException e) {
          e.printStackTrace();
          err = true;
        }
        if (operators[i].error()) err = true;
      }
      dbmetaprint(db, itcnt == itnum);
      if (!db.close()) {
        dberrprint(db, "DB::close");
        err = true;
      }
      double etime = Utility.time();
      printf("time: %.3f\n", etime - stime);
      printf("memory: %.3f\n", memusage() / 1024.0 / 1024.0);
    }
    printf("%s\n\n", err ? "error" : "ok");
    return err ? 1 : 0;
  }
  /** perform order command */
  private static int procmisc(String path) {
    printf("<Miscellaneous Test>\n  path=%s\n\n", path);
    boolean err = false;
    Utility.atoi("123.456abc");
    Utility.atoix("123.456abc");
    Utility.atof("123.456abc");
    Utility.hash_murmur(path.getBytes());
    Utility.hash_fnv(path.getBytes());
    List<Cursor> dcurs = new ArrayList<Cursor>();
    printf("opening the database:\n");
    DB db = new DB();
    try {
      if (!db.open(path, DB.OWRITER | DB.OCREATE | DB.OTRUNCATE)) {
        dberrprint(db, "DB::open");
        err = true;
      }
      int[] codes = { Error.SUCCESS, Error.NOIMPL, Error.MISC };
      db.tune_exception_rule(codes);
      db.tune_encoding("UTF-8");
      db.toString();
      long rnum = 10000;
      printf("setting records:\n");
      for (long i = 0; i < rnum; i++) {
        String key = String.format("%d", i);
        db.set(key, key);
      }
      if (db.count() != rnum) {
        dberrprint(db, "DB::count");
        err = true;
      }
      printf("deploying cursors:\n");
      for (int i = 1; i < 100; i++) {
        Cursor cur = db.cursor();
        String key = String.format("%d", i);
        if (!cur.jump(key)) {
          dberrprint(db, "Cursor::jump");
          err = true;
        }
        switch (i % 3) {
          case 0: {
            dcurs.add(cur);
            break;
          }
          case 1: {
            cur.disable();
            break;
          }
        }
        cur.toString();
      }
      printf("getting records:\n");
      for (Cursor cur : dcurs) {
        if (cur.get_key(false) == null) {
          dberrprint(db, "Cursor::get_key");
          err = true;
        }
      }
      printf("accepting visitor:\n");
      class VisitorImpl implements Visitor {
        public byte[] visit_full(byte[] key, byte[] value) {
          byte[] rv = NOP;
          String kstr = new String(key);
          switch (Integer.parseInt(kstr) % 3) {
            case 0: {
              rv = String.format("full:%s", kstr).getBytes();
              break;
            }
            case 1: {
              rv = REMOVE;
              break;
            }
          }
          return rv;
        }
        public byte[] visit_empty(byte[] key) {
          byte[] rv = NOP;
          String kstr = new String(key);
          switch (Integer.parseInt(kstr) % 3) {
            case 0: {
              rv = String.format("empty:%s", kstr).getBytes();
              break;
            }
            case 1: {
              rv = REMOVE;
              break;
            }
          }
          return rv;
        }
      }
      VisitorImpl visitor = new VisitorImpl();
      long end = rnum * 2;
      for (long i = 0; i < end; i++) {
        byte[] key = String.format("%d", i).getBytes();
        if (!db.accept(key, visitor, true)) {
          dberrprint(db, "DB::accept");
          err = true;
        }
      }
      printf("accepting visitor with a cursor:\n");
      Cursor cur = db.cursor();
      try {
        if (!cur.jump_back()) {
          dberrprint(db, "Cursor::jump");
          err = true;
        }
        while (cur.accept(visitor, true, false)) {
          cur.step_back();
        }
      } catch (Error.XNOIMPL e) {
        if (!cur.jump()) {
          dberrprint(db, "Cursor::jump");
          err = true;
        }
        while (cur.accept(visitor, true, false)) {
          cur.step();
        }
      }
      printf("accepting visitor in bulk:\n");
      byte[][] keys = new byte[10][];
      for (int i = 0; i < keys.length; i++) {
        keys[i] = String.format("%d", i).getBytes();
      }
      if (!db.accept_bulk(keys, visitor, true)) {
        dberrprint(db, "DB::accept_bulk");
        err = true;
      }
      byte[][] recs = new byte[20][];
      for (int i = 0; i < recs.length; i++) {
        recs[i] = String.format("%d", i).getBytes();
      }
      if (db.set_bulk(recs, true) != recs.length / 2) {
        dberrprint(db, "DB::set_bulk");
        err = true;
      }
      if (db.get_bulk(keys, true) == null) {
        dberrprint(db, "DB::get_bulk");
        err = true;
      }
      if (db.remove_bulk(keys, true) < 0) {
        dberrprint(db, "DB::remove_bulk");
        err = true;
      }
      Map<String, String> recmap = new HashMap<String, String>();
      recmap.put("one", "first");
      recmap.put("two", "second");
      if (db.set_bulk(recmap, false) != recmap.size()) {
        dberrprint(db, "DB::set_bulk");
        err = true;
      }
      List<String> keylist = new ArrayList<String>();
      keylist.add("one");
      keylist.add("two");
      recmap = db.get_bulk(keylist, false);
      if (recmap == null || recmap.size() != keylist.size()) {
        dberrprint(db, "DB::get_bulk");
        err = true;
      }
      if (db.remove_bulk(keylist, false) != keylist.size()) {
        dberrprint(db, "DB::remove_bulk");
        err = true;
      }
      printf("synchronizing the database:\n");
      class Informer implements FileProcessor {
        public boolean process(String path, long count, long size) {
          return true;
        }
      }
      Informer informer = new Informer();
      if (!db.synchronize(false, informer)) {
        dberrprint(db, "DB::synchronize");
        err = true;
      }
      if (!db.occupy(false, informer)) {
        dberrprint(db, "DB::occupy");
        err = true;
      }
      printf("performing transaction:\n");
      if (db.begin_transaction(false)) {
        if (!db.set("tako", "ika")) {
          dberrprint(db, "DB::set");
          err = true;
        }
        if (!db.end_transaction(true)) {
          dberrprint(db, "DB::end_transaction");
          err = true;
        }
      } else {
        dberrprint(db, "DB::begin_transaction");
        err = true;
      }
      String value = db.get("tako");
      if (value == null || !value.equals("ika")) {
        dberrprint(db, "DB::get");
        err = true;
      }
      if (!db.remove("tako")) {
        dberrprint(db, "DB::remove");
        err = true;
      }
      long cnt = db.count();
      if (db.begin_transaction(false)) {
        if (!db.set("tako", "ika") || !db.set("kani", "ebi")) {
          dberrprint(db, "DB::set");
          err = true;
        }
        if (!db.end_transaction(false)) {
          dberrprint(db, "DB::end_transaction");
          err = true;
        }
      } else {
        dberrprint(db, "DB::begin_transaction");
        err = true;
      }
      if (db.get("tako") != null || db.get("kani") != null || db.count() != cnt) {
        dberrprint(db, "DB::transaction");
        err = true;
      }
      String corepath = db.path();
      String suffix = null;
      if (corepath.endsWith(".kch")) {
        suffix = ".kch";
      } else if (corepath.endsWith(".kct")) {
        suffix = ".kct";
      } else if (corepath.endsWith(".kcd")) {
        suffix = ".kcd";
      } else if (corepath.endsWith(".kcf")) {
        suffix = ".kcf";
      }
      if (suffix != null) {
        printf("performing copy and merge:\n");
        String[] copypaths = new String[2];
        for (int i = 0; i < copypaths.length; i++) {
          copypaths[i] = String.format("%s.%d%s", corepath, i + 1, suffix);
        }
        DB[] srcary = new DB[copypaths.length];
        for (int i = 0; i < copypaths.length; i++) {
          if (!db.copy(copypaths[i])) {
            dberrprint(db, "DB::copy");
            err = true;
          }
          srcary[i] = new DB();
          if (!srcary[i].open(copypaths[i], DB.OREADER)) {
            dberrprint(srcary[i], "DB::open");
            err = true;
          }
        }
        if (!db.merge(srcary, DB.MAPPEND)) {
          dberrprint(db, "DB::merge");
          err = true;
        }
        for (int i = 0; i < copypaths.length; i++) {
          if (!srcary[i].close()) {
            dberrprint(srcary[i], "DB::close");
            err = true;
          }
          Utility.remove_files_recursively(copypaths[i]);
        }
      }
      printf("executing mapreduce process:\n");
      class MapReduceImpl extends MapReduce {
        public boolean map(byte[] key, byte[] value) {
          mapcnt_++;
          return emit(value, key);
        }
        public boolean reduce(byte[] key, ValueIterator iter) {
          byte[] value;
          while ((value = iter.next()) != null) {
            redcnt_++;
          }
          return true;
        }
        public boolean preprocess() {
          emit("pre".getBytes(), "process".getBytes());
          emit("PROCESS".getBytes(), "PRE".getBytes());
          return true;
        }
        public boolean midprocess() {
          emit("mid".getBytes(), "process".getBytes());
          emit("PROCESS".getBytes(), "MID".getBytes());
          return true;
        }
        public boolean postprocess() {
          return true;
        }
        public long mapcnt() {
          return mapcnt_;
        }
        public long redcnt() {
          return redcnt_;
        }
        private long mapcnt_ = 0;
        private long redcnt_ = 0;
      }
      rnum = db.count();
      MapReduceImpl mr = new MapReduceImpl();
      if (!mr.execute(db, null, 0)) {
        dberrprint(db, "MapReduce::execute");
        err = true;
      }
      if (mr.mapcnt() != rnum || mr.redcnt() != rnum + 4) {
        dberrprint(db, "MapReduce::execute");
        err = true;
      }
    } finally {
      printf("closing the database:\n");
      if (!db.close()) {
        dberrprint(db, "DB::close");
        err = true;
      }
    }
    printf("checking the exceptional mode:\n");
    db = new DB(DB.GEXCEPTIONAL);
    try {
      db.open("hoge", DB.OREADER);
      dberrprint(db, "DB::open");
      err = true;
    } catch (Error.XINVALID e) {
      if (e.code() != Error.INVALID) {
        dberrprint(db, "DB::open");
        err = true;
      }
    }
    printf("%s\n\n", err ? "error" : "ok");
    return err ? 1 : 0;
  }
  /** perform memsize command */
  private static int procmemsize(long rnum, String path) {
    System.gc();
    long musage = memusagerss();
    double stime = Utility.time();
    long count = -1;
    if (path == null) {
      int bnum = rnum < Integer.MAX_VALUE ? (int)rnum : Integer.MAX_VALUE;
      Map<String, String> map = new HashMap<String, String>(bnum, 100);
      for (long i = 0; i < rnum; i++) {
        String key = String.format("%08d", i);
        String value = String.format("%08d", i);
        map.put(key, value);
      }
      count = map.size();
    } else {
      DB db = new DB();
      if (db.open(path, DB.OWRITER | DB.OCREATE | DB.OTRUNCATE)) {
        for (long i = 0; i < rnum; i++) {
          String key = String.format("%08d", i);
          String value = String.format("%08d", i);
          db.set(key, value);
        }
        count = db.count();
        db.close();
      }
    }
    printf("count: %d\n", count);
    double etime = Utility.time();
    printf("time: %.3f\n", etime - stime);
    System.gc();
    printf("usage: %.3f MB\n", (memusagerss() - musage) / 1024.0 / 1024.0);
    return 0;
  }
  /** print formatted information string and flush the buffer */
  private static void printf(String format, Object... args) {
    STDOUT.printf(format, args);
    STDOUT.flush();
  }
  /** get a random number */
  private static long rand(long range) {
    long num = (long)(RND.nextDouble() * range);
    return num < range ? num : 0;
  }
  /** get the memory usage */
  private static long memusage() {
    Runtime rt = Runtime.getRuntime();
    return rt.totalMemory() - rt.freeMemory();
  }
  /** get the memory usage of RSS */
  private static long memusagerss() {
    long rss = -1;
    try {
      Reader fr = new FileReader("/proc/self/status");
      LineNumberReader lnr = new LineNumberReader(fr);
      String line;
      while ((line = lnr.readLine()) != null) {
        int idx = line.indexOf(':');
        if (idx < 0) continue;
        String name = line.substring(0, idx);
        idx++;
        while (idx < line.length() && Character.isWhitespace(line.charAt(idx))) idx++;
        String value = line.substring(idx, line.length());
        if (name.equals("VmRSS")) {
          rss = Utility.atoix(value);
          break;
        }
      }
      fr.close();
    } catch (Exception e) {
    }
    return rss;
  }
  //----------------------------------------------------------------
  // private static inner classes
  //----------------------------------------------------------------
  private static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread th, Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  //----------------------------------------------------------------
  // private constants
  //----------------------------------------------------------------
  /** The standard output stream. */
  private static final PrintStream STDOUT = System.out;
  /** The standard error stream. */
  private static final PrintStream STDERR = System.err;
  /** The rondom generator. */
  private static final Random RND = new Random();
  /** The exception handler. */
  private static final ExceptionHandler EXH = new ExceptionHandler();
  //----------------------------------------------------------------
  // private methods
  //----------------------------------------------------------------
  /**
   * Dummy constructor.
   */
  private Test() {}
}



// END OF FILE
