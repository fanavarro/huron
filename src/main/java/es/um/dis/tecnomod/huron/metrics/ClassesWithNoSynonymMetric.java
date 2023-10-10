package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

public class ClassesWithNoSynonymMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "Classes with no synonym";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tClass\tWithNoSynonym\n");
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		int numberOfClassesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().classesInSignature().collect(Collectors.toList())){
			if (owlClass.isOWLNothing() || owlClass.isOWLThing() || OntologyUtils.isObsolete(owlClass, getOntology())) {
				continue;
			}				
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlClass);
			if (localNumberOfSynonyms == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlClass.toStringID(), true));
				RDFUtils.createObservation(rdfModel, owlClass.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(true), timestamp);
				numberOfClassesWithNoSynonym++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlClass.toStringID(), false));
				RDFUtils.createObservation(rdfModel, owlClass.getIRI().toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(false), timestamp);
			}
			numberOfEntities ++;
		}
		double metricValue = ((double) (numberOfClassesWithNoSynonym)) / numberOfEntities;
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			rdfModel.createResource(ontologyIRI.toString()).addLiteral(metricProperty, metricValue);
			RDFUtils.createObservation(rdfModel, ontologyIRI.toString(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), timestamp);
		});
		return new MetricResult(metricValue, rdfModel);	
	}
	

	@Override
	public String getName() {
		return METRIC_NAME;
	}


	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "ClassesWithNoSynonymMetric";
	}


	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.SYNONYMS;
	}
}
