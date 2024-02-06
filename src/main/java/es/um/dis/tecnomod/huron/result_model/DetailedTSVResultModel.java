package es.um.dis.tecnomod.huron.result_model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import es.um.dis.tecnomod.huron.services.URIUtils;

public class DetailedTSVResultModel extends TSVResultModel implements ResultModelInterface {
	
	private final static Logger LOGGER = Logger.getLogger(DetailedTSVResultModel.class.getName());
	private static final String METRIC_COLUMN = "metric";
	private static final String ENTITY_COLUMN = "entity";
	private static final String VALUE_COLUMN = "value";
	private static final List<String> HEADER = Arrays.asList(METRIC_COLUMN, ENTITY_COLUMN, VALUE_COLUMN);
	
	private File outputFolder;
	
	private Map<String, Map<String, Table<Integer, String, Object>>> tablePerMetricOntologyPair;
	private Map<String, Map<String, Integer>> rowCountPerMetricOntologyPair;

	public DetailedTSVResultModel(File outputFolder) {
		this.outputFolder = outputFolder;
		this.tablePerMetricOntologyPair = new HashMap<>();
		this.rowCountPerMetricOntologyPair = new HashMap<>();
	}

	@Override
	public synchronized void addObservation(String sourceDocumentIRI, String featureOfInterestIRI, String featureOfInterestTypeIRI,
			String observablePropertyIRI, String metricUsedIRI, String instrumentIRI, String unitIRI, Object value,
			Calendar timestamp) {
		/* Here we only record information about ontology entities, not the ontology itself*/
		if (!featureOfInterestIRI.equals(sourceDocumentIRI)) {
			this.rowCountPerMetricOntologyPair.putIfAbsent(sourceDocumentIRI, new HashMap<>());
			this.rowCountPerMetricOntologyPair.get(sourceDocumentIRI).putIfAbsent(metricUsedIRI, Integer.valueOf(0));
			
			this.tablePerMetricOntologyPair.putIfAbsent(sourceDocumentIRI, new HashMap<>());
			this.tablePerMetricOntologyPair.get(sourceDocumentIRI).putIfAbsent(metricUsedIRI, HashBasedTable.create());
			
			Table<Integer, String, Object> table = this.tablePerMetricOntologyPair.get(sourceDocumentIRI).get(metricUsedIRI);
			int row = this.rowCountPerMetricOntologyPair.get(sourceDocumentIRI).get(metricUsedIRI);
			
			/* Meter datos */
			table.put(row, ENTITY_COLUMN, featureOfInterestIRI);
			table.put(row, METRIC_COLUMN, URIUtils.getNameFromURI(metricUsedIRI));
			table.put(row, VALUE_COLUMN, value);
			
			this.rowCountPerMetricOntologyPair.get(sourceDocumentIRI).put(metricUsedIRI, Integer.valueOf(row + 1));
		}
	}

	@Override
	public void export() throws IOException {
		if(!outputFolder.exists() || !outputFolder.isDirectory()){
			try {
				Files.createDirectories(outputFolder.toPath());
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, "Error creating folder for detailed files. Ignoring detailed files...", e);
				return;
			}
		}
	
		for(String ontology : tablePerMetricOntologyPair.keySet()) {
			for(String metric : tablePerMetricOntologyPair.get(ontology).keySet()) {
				Table<Integer, String, Object> table = tablePerMetricOntologyPair.get(ontology).get(metric);
				this.exportTable(table, ontology, metric);
			}
		}
		
	}

	private void exportTable(Table<Integer, String, Object> table, String ontology, String metric) throws IOException {
		String ontologyName = URIUtils.getNameFromURI(ontology);
		String metricName = URIUtils.getNameFromURI(metric);
		String outputFilename = ontologyName + "_" + metricName + ".tsv";
		File outputFile = Paths.get(this.outputFolder.getAbsolutePath(), outputFilename).toFile();
		
		PrintWriter printer = new PrintWriter(new FileWriter(outputFile));
		printer.write(String.join(COLUMN_SEPARATOR_STRING, HEADER) + NEW_LINE_STRING);
		for (int row : table.rowKeySet()) {
			StringBuilder line = new StringBuilder();
			for (String column : HEADER) {
				String value = getStringValue(table.get(row, column));
				line.append(value).append(COLUMN_SEPARATOR_STRING);
			}
			printer.print(line.substring(0, line.lastIndexOf(COLUMN_SEPARATOR_STRING)) + NEW_LINE_STRING);
		}
		printer.close();
	}
	
	
	

}
