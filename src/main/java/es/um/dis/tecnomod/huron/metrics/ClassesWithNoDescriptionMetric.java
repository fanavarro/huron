package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

public class ClassesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric {

	public ClassesWithNoDescriptionMetric() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ClassesWithNoDescriptionMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}


	/** The Constant NAME. */
	private static final String METRIC_NAME = "Classes with no description";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tClass\tWithNoDescription\n");
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfClassesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().classesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if (owlClass.isOWLNothing() || owlClass.isOWLThing() || OntologyUtils.isObsolete(owlClass, getOntology(), this.getConfig().getImports())) {
				continue;
			}				
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlClass);
			if (localNumberOfDescriptions == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlClass.toStringID(), true));
				this.notifyExporterListeners(ontologyIRI, owlClass.getIRI().toString(), OWL.Class.getURI(), Boolean.valueOf(true), timestamp);
				
				numberOfClassesWithNoDescription++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlClass.toStringID(), false));
				this.notifyExporterListeners(ontologyIRI, owlClass.getIRI().toString(), OWL.Class.getURI(), Boolean.valueOf(false), timestamp);
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfClassesWithNoDescription)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp);
		return new MetricResult(metricValue, rdfModel);			
	}


	@Override
	public String getName() {
		return METRIC_NAME;
	}


	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "ClassesWithNoDescriptionMetric";
	}


	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.DESCRIPTIONS;
	}


}
