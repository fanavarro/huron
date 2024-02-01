package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.RDFUtils;


public class AnnotationPropertiesWithNoSynonymMetric extends AnnotationsPerEntityAbstractMetric {

	public AnnotationPropertiesWithNoSynonymMetric() {
		super();
		// TODO Auto-generated constructor stub
	}



	public AnnotationPropertiesWithNoSynonymMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}



	/** The Constant NAME. */
	private static final String METRIC_NAME = "AnnotationProperties with no synonym";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tAnnotationProperty\tWithNoSynonym\n");
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfAnnotationPropertiesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().annotationPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){	
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlAnnotationProperty);
			if (localNumberOfSynonyms == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlAnnotationProperty.toStringID(), true));
				this.notifyExporterListeners(ontologyIRI, owlAnnotationProperty.getIRI().toString(), OWL.AnnotationProperty.getURI(), Boolean.valueOf(true), timestamp);
				numberOfAnnotationPropertiesWithNoSynonym++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlAnnotationProperty.toStringID(), false));
				this.notifyExporterListeners(ontologyIRI, owlAnnotationProperty.getIRI().toString(), OWL.AnnotationProperty.getURI(), Boolean.valueOf(false), timestamp);
			}
			numberOfEntities ++;
		}
		double metricValue = ((double) (numberOfAnnotationPropertiesWithNoSynonym)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp);
		return new MetricResult(metricValue, rdfModel);		
	}



	@Override
	public String getName() {
		return METRIC_NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "AnnotationPropertiesWithNoSynonymMetric";
	}



	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.SYNONYMS;
	}
}
