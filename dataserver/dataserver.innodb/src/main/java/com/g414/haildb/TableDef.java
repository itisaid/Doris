package com.g414.haildb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableDef {
    private final String name;
    private final Map<String, ColumnDef> columnDefs;
    private final Map<String, IndexDef> indexDefs;
    private final List<ColumnDef> colDefs;
    private final IndexDef primaryIndex;

    public TableDef(String name, Map<String, ColumnDef> columnDefs,
            Map<String, IndexDef> indexDefs, IndexDef primaryIndex) {
        this.name = name;

        Map<String, ColumnDef> newCols = new LinkedHashMap<String, ColumnDef>();
        newCols.putAll(columnDefs);
        this.columnDefs = Collections.unmodifiableMap(newCols);

        List<ColumnDef> newColDefs = new ArrayList<ColumnDef>();
        newColDefs.addAll(newCols.values());
        this.colDefs = Collections.unmodifiableList(newColDefs);

        Map<String, IndexDef> newIdxs = new LinkedHashMap<String, IndexDef>();
        newIdxs.putAll(indexDefs);
        this.indexDefs = Collections.unmodifiableMap(newIdxs);

        this.primaryIndex = primaryIndex;
    }

    public String getName() {
        return name;
    }

    public Map<String, ColumnDef> getColumnDefs() {
        return columnDefs;
    }

    public List<ColumnDef> getColDefs() {
        return colDefs;
    }

    public Map<String, IndexDef> getIndexDefs() {
        return indexDefs;
    }

    public IndexDef getPrimaryIndex() {
        return primaryIndex;
    }

    public IndexDef getIndexDef(String index) {
        return indexDefs.get(index);
    }
}
