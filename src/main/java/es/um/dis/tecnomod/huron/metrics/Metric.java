package es.um.dis.tecnomod.huron.metrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.jena.rdf.model.Model;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;


/**
 * The Class Metric.
 */
public abstract class Metric {
	
	/** The ontology. */
	private OWLOntology ontology;
	
	/** The ontology path. */
	private String ontologyPath;
	
	/** The detailed output file. */
	private File detailedOutputFile;
	
	/** The print writer. */
	private PrintWriter printWriter;
	
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
	 * Calculate the metric and return RDF statements with the metric values and issues found.
	 * @return Model including RDF statements with the metric values and the issues found.
	 * @throws Exception 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws OWLOntologyCreationException 
	 */
	public Model calculateRDF() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		return this.calculate().getRdf();
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
	 * Gets the detailed output file.
	 *
	 * @return the detailed output file
	 */
	public File getDetailedOutputFile() {
		return detailedOutputFile;
	}

	/**
	 * Open detailed output file.
	 *
	 * @param detailedOutputFile the detailed output file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void openDetailedOutputFile(File detailedOutputFile) throws IOException {
		this.detailedOutputFile = detailedOutputFile;
		printWriter = new PrintWriter(new FileWriter(detailedOutputFile));
	}
	
	/**
	 * Write to detailed output file.
	 *
	 * @param text the text
	 */
	public void writeToDetailedOutputFile(String text){
		if(printWriter != null){
			printWriter.write(text);
		}
	}
	
	/**
	 * Checks if is open detailed output file.
	 *
	 * @return true, if is open detailed output file
	 */
	public boolean isOpenDetailedOutputFile(){
		return printWriter != null;
	}
	
	/**
	 * Close detailed output file.
	 */
	public void closeDetailedOutputFile(){
		this.printWriter.close();
		this.printWriter = null;
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
		return RDFConstants.HURON;
	}
	
	/**
	 * Get the unit of measure used by the metric
	 * @return
	 */
	public String getUnitOfMeasureIRI() {
		return null;
	}

}
