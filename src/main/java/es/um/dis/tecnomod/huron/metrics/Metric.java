package es.um.dis.tecnomod.huron.metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.result_model.ResultModelInterface;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.huron.services.URIUtils;


/**
 * The Class Metric.
 */
public abstract class Metric {
	
	/** The config */
	private Config config;
	
	/** The ontology. */
	private OWLOntology ontology;
	
	/** The ontology path. */
	private String ontologyPath;
	
	
	/**
	 * Instantiates a new metric.
	 */
	public Metric() {
		super();
		this.config = new Config();
	}
	
	/**
	 * Instantiates a new metric.
	 *
	 * @param config the config
	 */
	public Metric(Config config) {
		this.config = config;
	}
	
	/**
	 * Calculate the value of the metric and the RDF with information
	 * @return
	 * @throws OWLOntologyCreationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 */
	public abstract MetricResult calculate()
			throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception;

	/**
	 * Calculate.
	 *
	 * @return the double
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws Exception the exception
	 */
	public double calculateValue()
			throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		return this.calculate().getMetricValue();
	}
	


	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public abstract String getName();
	
	
	/**
	 * Gets the IRI.
	 * @return the IRI
	 */
	public abstract String getIRI();

	/**
	 * Load ontology.
	 *
	 * @param ontologyPath the ontology path
	 * @return the OWL ontology
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 */
	protected OWLOntology loadOntology(String ontologyPath) throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(ontologyPath));
		return ontology;
	}

	/**
	 * Sets the ontology path.
	 *
	 * @param path the new ontology path
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 */
	public void setOntologyPath(String path) throws OWLOntologyCreationException {
		this.ontologyPath = path;
		this.ontology = loadOntology(ontologyPath);
	}

	/**
	 * Sets the ontology.
	 *
	 * @param ontology the new ontology
	 */
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	/**
	 * Gets the ontology.
	 *
	 * @return the ontology
	 */
	public OWLOntology getOntology() {
		return ontology;
	}

	/**
	 * Gets the ontology path.
	 *
	 * @return the ontology path
	 */
	public String getOntologyPath() {
		return ontologyPath;
	}
	
	/**
	 * Get the IRI of the property observed by the metric
	 * @return
	 */
	public abstract String getObservablePropertyIRI();
	
	/**
	 * Get the IRI of the instrument used to compute the metric
	 * @return
	 */
	public String getInstrumentIRI() {
		return RDFUtils.HURON;
	}
	
	/**
	 * Gets the scale IRI.
	 *
	 * @return the scale IRI
	 */
	public String getScaleIRI() {
		String prefix = URIUtils.getNamespaceFromURI(this.getIRI());
		String metricName = URIUtils.getNameFromURI(this.getIRI());
		String scaleName = Character.toLowerCase(metricName.charAt(0)) + metricName.substring(1) + "Scale";
		return prefix + scaleName;
	}
	
	public String getScaleTypeIRI() {
		return RDFUtils.RAW_SCALE;
	}
	
	public abstract String getRankingFunctionIRI();
	
	/**
	 * Get the unit of measure used by the metric
	 * @return
	 */
	public String getUnitOfMeasureIRI() {
		return null;
	}

	/**
	 * Gets the config.
	 *
	 * @return the config
	 */
	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
	
	public void notifyExporterListeners(String sourceDocumentIRI, String featureOfInterestIRI, String featureOfInterestTypeIRI, Object value, Calendar timestamp) {
		ObservationInfoDTO observationInfo = getObservationInfo(sourceDocumentIRI, featureOfInterestIRI, featureOfInterestTypeIRI, value, timestamp);
		for (ResultModelInterface resultModel : this.getConfig().getResultModels()) {
			resultModel.addObservation(observationInfo);
		}
	}
	
	protected ObservationInfoDTO getObservationInfo(String sourceDocumentIRI, String featureOfInterestIRI, String featureOfInterestTypeIRI, Object value, Calendar timestamp) {
		ObservationInfoDTO observationInfo = new ObservationInfoDTO();
		observationInfo.setSourceDocumentIRI(sourceDocumentIRI);
		observationInfo.setFeatureOfInterestIRI(featureOfInterestIRI);
		observationInfo.setFeatureOfInterestTypeIRI(featureOfInterestTypeIRI);
		observationInfo.setMetricUsedIRI(this.getIRI());
		observationInfo.setValue(value);
		observationInfo.setTimestamp(timestamp);
		observationInfo.setObservablePropertyIRI(this.getObservablePropertyIRI());
		observationInfo.setInstrumentIRI(this.getInstrumentIRI());
		observationInfo.setUnitOfMeasureIRI(this.getUnitOfMeasureIRI());
		observationInfo.setScaleIRI(this.getScaleIRI());
		observationInfo.setScaleTypeIRI(this.getScaleTypeIRI());
		observationInfo.setRankingFunctionIRI(this.getRankingFunctionIRI());
		return observationInfo;
	}

}
