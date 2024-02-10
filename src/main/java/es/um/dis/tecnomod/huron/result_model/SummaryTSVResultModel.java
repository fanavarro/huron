package es.um.dis.tecnomod.huron.result_model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import es.um.dis.tecnomod.huron.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.huron.services.URIUtils;


public abstract class SummaryTSVResultModel extends TSVResultModel implements ResultModelInterface {
	
	protected static final String ONTOLOGY_COLUMN = "ontology";
	protected static final String METRIC_COLUMN = "metric";
	protected static final String VALUE_COLUMN = "value";
	
	private File outputFile;
	private Table<Integer, String, Object> table;
	private int rowCount;
	private List<String> header;
	
	public SummaryTSVResultModel(File outputFile) {
		this.outputFile = outputFile;
		this.table = HashBasedTable.create();
		this.rowCount = 0;
		this.header = Arrays.asList(ONTOLOGY_COLUMN, METRIC_COLUMN, VALUE_COLUMN);
	}
	
	@Override
	public synchronized void addObservation(ObservationInfoDTO observationInfo) {
		
		String featureOfInterestIRI = observationInfo.getFeatureOfInterestIRI();
		String sourceDocumentIRI = observationInfo.getSourceDocumentIRI();
		String metricUsedIRI = observationInfo.getMetricUsedIRI();
		Object value = observationInfo.getValue();
		/* Here we only register ontology values */
		if (featureOfInterestIRI.equals(sourceDocumentIRI)) {
			this.table.put(rowCount, ONTOLOGY_COLUMN, featureOfInterestIRI);
			this.table.put(rowCount, METRIC_COLUMN, URIUtils.getNameFromURI(metricUsedIRI));
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
