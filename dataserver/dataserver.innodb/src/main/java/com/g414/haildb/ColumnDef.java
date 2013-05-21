package com.g414.haildb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ColumnDef {
    private final Integer index;
    private final String name;
    private final ColumnType type;
    private final Set<ColumnAttribute> attrs;
    private final Integer length;

    public ColumnDef(Integer index, String name, ColumnType type,
            Integer length, ColumnAttribute... attrs) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.length = length;

        if (attrs != null && attrs.length > 0) {
            List<ColumnAttribute> newAttrs = new ArrayList<ColumnAttribute>();
            newAttrs.addAll(Arrays.asList(attrs));
            this.attrs = Collections.unmodifiableSet(EnumSet.copyOf(newAttrs));
        } else {
            this.attrs = Collections.emptySet();
        }
    }

    public Integer getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public ColumnType getType() {
        return type;
    }

    public Set<ColumnAttribute> getAttrs() {
        return attrs;
    }

    public Integer getLength() {
        return length;
    }

    public boolean is(ColumnAttribute attr) {
        return attrs.contains(attr);
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getType() + "(" + this.getLength()
                + ") " + attrs.toString();
    }
}
