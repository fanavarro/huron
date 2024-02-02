package es.um.dis.tecnomod.huron.exporters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


public abstract class TableExporter implements ExporterInterface {
	private static final String QUOTE_STRING = "\"";
	protected static final String ONTOLOGY_COLUMN = "ontology";
	protected static final String METRIC_COLUMN = "metric";
	protected static final String VALUE_COLUMN = "value";
	
	private File outputFile;
	private Table<Integer, String, Object> table;
	private int rowCount;
	private List<String> header;
	
	public TableExporter(File outputFile) {
		this.outputFile = outputFile;
		this.table = HashBasedTable.create();
		this.rowCount = 0;
		this.header = Arrays.asList(ONTOLOGY_COLUMN, METRIC_COLUMN, VALUE_COLUMN);
	}
	
	@Override
	public synchronized void addObservation(String sourceDocumentIRI, String featureOfInterestIRI, String featureOfInterestTypeIRI,
			String observablePropertyIRI, String metricUsedIRI, String instrumentIRI, String unitIRI, Object value,
			Calendar timestamp) {
		
		/* Here we only register ontology values */
		if (featureOfInterestIRI.equals(sourceDocumentIRI)) {
			this.table.put(rowCount, ONTOLOGY_COLUMN, featureOfInterestIRI);
			this.table.put(rowCount, METRIC_COLUMN, metricUsedIRI);
			this.table.put(rowCount, VALUE_COLUMN, value);
			rowCount++;
		}		
	}

	@Override
	public void export() throws IOException {
		PrintWriter printer = new PrintWriter(new FileWriter(this.outputFile));
		
		printer.write(String.join(getColumnSeparator(), getHeader()) + getRowSeparator());
		
		for (int row : table.rowKeySet()) {
			StringBuilder line = new StringBuilder();
			for (String column : getHeader()) {
				String value = getStringValue(table.get(row, column));
				line.append(value).append(getColumnSeparator());
			}
			printer.print(line.substring(0, line.lastIndexOf(getColumnSeparator())) + getRowSeparator());
		}
		printer.close();
	}

	protected String getStringValue(Object object) {
		if (object instanceof String) {
			return QUOTE_STRING + object.toString() + QUOTE_STRING;
		}
		if (object instanceof Number) {
			return new DecimalFormat("###.###", new DecimalFormatSymbols(Locale.ROOT)).format(object);
					
		}
		return object.toString();
	}
	
	protected void setHeader(List<String> header) {
		this.header = header;
	}

	protected List<String> getHeader() {
		return this.header;
	}
	
	
	
	protected Table<Integer, String, Object> getTable() {
		return this.table;
	}

	protected void setTable(Table<Integer, String, Object> table) {
		this.table = table;
	}

	protected abstract String getQuoteString();
	protected abstract String getColumnSeparator();
	protected abstract String getRowSeparator();
	

}
