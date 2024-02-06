package es.um.dis.tecnomod.huron.result_model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class WideTSVResultModel extends LongTSVResultModel{
	private Table<Integer, String, Object> longTable;
	private Table<Integer, String, Object> wideTable;
	private List<String> longHeader;

	public WideTSVResultModel(File outputFile) {
		super(outputFile);
		this.longTable = this.getTable();
		this.longHeader = this.getHeader();
		this.wideTable = null;
	}
	
	@Override
	public void export() throws IOException {
		if (this.wideTable == null) {
			this.wideTable = this.getWideFormat(longTable, METRIC_COLUMN, VALUE_COLUMN);
		}
		this.setTable(wideTable);
		this.setHeader(getWideHeader(METRIC_COLUMN, VALUE_COLUMN));
		super.export();
		this.setHeader(longHeader);
		this.setTable(longTable);
	}
	
	private Table<Integer, String, Object> getWideFormat(Table<Integer, String, Object> longTable, String keyColumn, String valueColumn) {
		Table<Integer, String, Object> wideTable = HashBasedTable.create();
		Map<String, Integer> ontologyMap = new HashMap<>();
		int ontologyCount = 0;
		for (int row : this.getTable().rowKeySet()) {
			String ontologyID = this.getTable().get(row, ONTOLOGY_COLUMN).toString();
			if (!ontologyMap.containsKey(ontologyID)) {
				ontologyMap.put(ontologyID, ontologyCount);
				ontologyCount++;
			}
			
			for (String column : getHeader()) {
				if (valueColumn.equals(column)) {
					continue;
				}
				if (!keyColumn.equals(column)) {
					wideTable.put(ontologyMap.get(ontologyID), column, this.getTable().get(row, column));
				} else {
					String keyValue = this.getTable().get(row, column).toString();
					Object value = this.getTable().get(row, valueColumn);
					wideTable.put(ontologyMap.get(ontologyID), keyValue, value);
				}
			}
		}
		return wideTable;
		
	}
	
	private List<String> getWideHeader(String keyColumn, String valueColumn) {
		List<String> wideHeader = new ArrayList<>(this.longHeader);
		wideHeader.remove(valueColumn);
		wideHeader.remove(keyColumn);
		for (String column : this.wideTable.columnKeySet()) {
			if (!wideHeader.contains(column)) {
				wideHeader.add(column);
			}
		}
		//wideHeader.addAll(this.wideTable.columnKeySet());
		return wideHeader;
	}

}
