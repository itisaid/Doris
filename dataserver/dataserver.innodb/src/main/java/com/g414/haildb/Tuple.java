package com.g414.haildb;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.g414.haildb.impl.jna.HailDB;
import com.sun.jna.Pointer;

public class Tuple {
    protected final List<ColumnDef> columns;
    protected Pointer tupl;
    private volatile boolean deleted = false;

    public Tuple(Pointer tupl, List<ColumnDef> columns) {
        this.tupl = tupl;
        this.columns = columns;
    }

    public Map<String, Object> valueMap() {
        if (deleted) {
            throw new IllegalStateException("tuple already deleted!");
        }

        Map<String, Object> values = new LinkedHashMap<String, Object>(
                this.columns.size());

        for (ColumnDef def : this.columns) {
            values.put(def.getName(), this.getValue(def));
        }

        return Collections.unmodifiableMap(values);
    }

    public void clear() {
        if (deleted) {
            throw new IllegalStateException("tuple is deleted!");
        }

        tupl = HailDB.ib_tuple_clear(tupl);
    }

    public void delete() {
        if (deleted) {
            throw new IllegalStateException("tuple is deleted!");
        }

        if (tupl != null && !tupl.equals(Pointer.NULL)) {
            deleted = true;
            HailDB.ib_tuple_delete(tupl);
        }
    }

    private Object getValue(ColumnDef def) {
        switch (def.getType()) {
        case BINARY:
        case VARBINARY:
        case BLOB:
            return TupleStorage.loadBytes(this, def.getIndex());
        case CHAR:
        case CHAR_ANYCHARSET:
        case VARCHAR:
        case VARCHAR_ANYCHARSET:
            return TupleStorage.loadString(this, def.getIndex());
        case INT:
            return TupleStorage.loadInteger(this, def.getIndex(),
                    def.getLength(),
                    !def.getAttrs().contains(ColumnAttribute.UNSIGNED));
        default:
            throw new IllegalArgumentException("unsupported datatype: "
                    + def.getType());
        }
    }
}
