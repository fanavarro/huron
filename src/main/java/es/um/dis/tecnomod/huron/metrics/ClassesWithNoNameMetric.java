package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * This class calculates the ratio of number of classes with no names to the number of all classes.
 * 
 * @author fjredondo
 */
public class ClassesWithNoNameMetric extends AnnotationsPerEntityAbstractMetric {
	
	public ClassesWithNoNameMetric() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ClassesWithNoNameMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Classes with no name";
	
	/**
	 * Get the entities from the ontology and obtain the annotations number of each entity to calculate the metric ratio.
	 * Also, saves the totalEntities and numberOfEntitiesWithNoAnnotation.
	 * Writes the following fields: Metric, Class (IRI) and WithNoName (boolean), in a file.
	 * @return The metric ratio
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfClassesWithNoName = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().classesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if (owlClass.isOWLNothing() || owlClass.isOWLThing() || OntologyUtils.isObsolete(owlClass, getOntology(), this.getConfig().getImports())) {
				continue;
			}			
			int localNumberOfNames = getNumberOfNames(owlClass);
			if (localNumberOfNames == 0) {
				
				this.notifyExporterListeners(ontologyIRI, owlClass.getIRI().toString(), OWL.Class.getURI(), Boolean.valueOf(true), timestamp);
				numberOfClassesWithNoName++;
			}else {
				
				this.notifyExporterListeners(ontologyIRI, owlClass.getIRI().toString(), OWL.Class.getURI(), Boolean.valueOf(false), timestamp);
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfClassesWithNoName)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp);

		return new MetricResult(metricValue);	
	}


	/**
	 * Get the name of the metric
	 * @return String
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}


	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "ClassesWithNoNameMetric";
	}
	
	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.NAMES;
	}
}
