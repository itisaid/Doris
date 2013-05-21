package com.g414.haildb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TableBuilder {
    private final String name;
    private final Map<String, ColumnDef> columns;
    private final Map<String, List<IndexPart>> indexes;
    private volatile AtomicInteger index = new AtomicInteger();

    public TableBuilder(String name) {
        String[] nameParts = name.split("/");
        if (nameParts.length != 2) {
            throw new IllegalArgumentException(
                    "Malformed table name - must include database name followed by slash");
        }

        this.name = name;
        this.columns = new LinkedHashMap<String, ColumnDef>();
        this.indexes = new LinkedHashMap<String, List<IndexPart>>();
    }

    public TableBuilder addColumn(String name, ColumnType type, int length,
            ColumnAttribute... attrs) {
        ColumnDef def = new ColumnDef(index.getAndIncrement(), name, type,
                length, attrs);
        this.columns.put(def.getName(), def);

        return this;
    }

    public TableBuilder addIndex(String indexName, String column,
            int prefixLen, boolean clustered, boolean unique) {
        if (!this.indexes.containsKey(indexName)) {
            this.indexes.put(indexName, new ArrayList<IndexPart>());
        }

        this.indexes.get(indexName).add(
                new IndexPart(column, prefixLen, clustered, unique));

        return this;
    }

    public TableDef build() {
        String primary = getPrimaryIndex(this.indexes);
        Map<String, IndexDef> defs = createIndexDefMap(primary);

        return new TableDef(name, columns, defs, defs.get(primary));
    }

    private Map<String, IndexDef> createIndexDefMap(String primary) {
        Map<String, IndexDef> defs = new LinkedHashMap<String, IndexDef>();
        for (Map.Entry<String, List<IndexPart>> entry : indexes.entrySet()) {
            boolean clustered = false;
            boolean unique = false;

            Map<String, ColumnDef> indexColumns = new LinkedHashMap<String, ColumnDef>();

            Map<String, Integer> prefixLenOverrides = new LinkedHashMap<String, Integer>();

            for (IndexPart part : entry.getValue()) {
                clustered |= part.isClustered();
                unique |= part.isUnique();

                if (part.getPrefixLen() != 0) {
                    prefixLenOverrides.put(part.getColumn(), part
                            .getPrefixLen());
                }

                indexColumns.put(part.getColumn(), this.columns.get(part
                        .getColumn()));
            }

            for (IndexPart primaryPart : this.indexes.get(primary)) {
                String primaryColumn = primaryPart.getColumn();

                if (!indexColumns.containsKey(primaryColumn)) {
                    indexColumns.put(primaryColumn, this.columns
                            .get(primaryColumn));
                }
            }

            List<ColumnDef> indexColumnList = new ArrayList<ColumnDef>();
            indexColumnList.addAll(indexColumns.values());

            IndexDef idx = new IndexDef(entry.getKey(), Collections
                    .unmodifiableList(indexColumnList), Collections
                    .unmodifiableMap(prefixLenOverrides), clustered, unique);

            defs.put(entry.getKey(), idx);
        }

        return defs;
    }

    private static String getPrimaryIndex(Map<String, List<IndexPart>> defs) {
        for (Map.Entry<String, List<IndexPart>> def : defs.entrySet()) {
            if (def.getValue().get(0).isClustered()) {
                return def.getKey();
            }
        }

        return null;
    }

    private static class IndexPart {
        private final String column;
        private final int prefixLen;
        private final boolean clustered;
        private final boolean unique;

        public IndexPart(String column, int prefixLen, boolean clustered,
                boolean unique) {
            this.column = column;
            this.prefixLen = prefixLen;
            this.clustered = clustered;
            this.unique = unique;
        }

        public String getColumn() {
            return column;
        }

        public int getPrefixLen() {
            return prefixLen;
        }

        public boolean isClustered() {
            return clustered;
        }

        public boolean isUnique() {
            return unique;
        }
    }
}
