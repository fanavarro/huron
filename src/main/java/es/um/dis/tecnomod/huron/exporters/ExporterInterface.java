package es.um.dis.tecnomod.huron.exporters;

import java.io.IOException;
import java.util.Calendar;

/**
 * The Interface ExporterInterface.
 */
public interface ExporterInterface {

	/**
	 * Adds the observation.
	 *
	 * @param sourceDocumentIRI the source document IRI
	 * @param featureOfInterestIRI the feature of interest IRI
	 * @param featureOfInterestTypeIRI the feature of interest type IRI
	 * @param observablePropertyIRI the observable property IRI
	 * @param metricUsedIRI the metric used IRI
	 * @param instrumentIRI the instrument IRI
	 * @param unitIRI the unit IRI
	 * @param value the value
	 * @param timestamp the timestamp
	 */
	public void addObservation (String sourceDocumentIRI, String featureOfInterestIRI,
			String featureOfInterestTypeIRI, String observablePropertyIRI, String metricUsedIRI, String instrumentIRI, String unitIRI, Object value, Calendar timestamp);
	
	
	/**
	 * Export.
	 * @throws IOException 
	 */
	public void export() throws IOException;
}
