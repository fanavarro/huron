package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.RDFUtils;

public class ObjectPropertiesWithNoSynonymMetric extends AnnotationsPerEntityAbstractMetric {

	public ObjectPropertiesWithNoSynonymMetric() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ObjectPropertiesWithNoSynonymMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/** The Constant NAME. */
	private static final String METRIC_NAME = "ObjectProperties with no synonym";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tObjectProperty\tWithNoSynonym\n");
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfObjectPropertiesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty owlObjectProperty : super.getOntology().objectPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}				
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlObjectProperty);
			if (localNumberOfSynonyms == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlObjectProperty.toStringID(), true));
				this.notifyExporterListeners(ontologyIRI, owlObjectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), Boolean.valueOf(true), timestamp);
				// TODO: Create issue here?
				// RDFUtils.createIssue(rdfModel, metricProperty, owlObjectProperty, String.format("The entity %s does not have any synonym.", owlObjectProperty.getIRI().toQuotedString()));
				numberOfObjectPropertiesWithNoSynonym++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlObjectProperty.toStringID(), false));
				this.notifyExporterListeners(ontologyIRI, owlObjectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), Boolean.valueOf(false), timestamp);
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfObjectPropertiesWithNoSynonym)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp);
		
		return new MetricResult(metricValue, rdfModel);	
	}

	@Override
	public String getName() {
		return METRIC_NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "ObjectPropertiesWithNoSynonymMetric";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.SYNONYMS;
	}
}
