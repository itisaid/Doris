package com.g414.haildb;

import java.util.List;
import java.util.Map;

public class IndexDef {
    private final String name;
    private final boolean clustered;
    private final boolean unique;
    private final List<ColumnDef> columns;
    private final Map<String, Integer> prefixLenOverrides;

    public IndexDef(String name, List<ColumnDef> columns,
            Map<String, Integer> prefixLenOverrides, boolean clustered,
            boolean unique) {
        this.name = name;
        this.columns = columns;
        this.prefixLenOverrides = prefixLenOverrides;
        this.clustered = clustered;
        this.unique = unique;
    }

    public String getName() {
        return name;
    }

    public List<ColumnDef> getColumns() {
        return columns;
    }

    public Map<String, Integer> getPrefixLenOverrides() {
        return prefixLenOverrides;
    }

    public boolean isClustered() {
        return clustered;
    }

    public boolean isUnique() {
        return unique;
    }
}
