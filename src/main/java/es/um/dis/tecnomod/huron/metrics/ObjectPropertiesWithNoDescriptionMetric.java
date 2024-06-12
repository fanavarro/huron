package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.issues.IssueTypes;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

public class ObjectPropertiesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric {

	public ObjectPropertiesWithNoDescriptionMetric() {
		super();
	}


	public ObjectPropertiesWithNoDescriptionMetric(Config config) {
		super(config);
	}


	/** The Constant NAME. */
	private static final String METRIC_NAME = "ObjectProperties with no description";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfObjectPropertiesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty owlObjectProperty : super.getOntology().objectPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}			
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlObjectProperty);
			if (localNumberOfDescriptions == 0) {
				IssueInfoDTO issue = new IssueInfoDTO(IssueTypes.OBJECT_PROPERTY_WITH_NO_DESCRIPTION_ISSUE, String.format("The object property %s does not have any description.", owlObjectProperty.getIRI().toQuotedString()));
				this.notifyExporterListeners(ontologyIRI, owlObjectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), Integer.valueOf(1), timestamp, issue);
				numberOfObjectPropertiesWithNoDescription++;
			}else {
				
				this.notifyExporterListeners(ontologyIRI, owlObjectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), Integer.valueOf(0), timestamp, Collections.emptyList());
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfObjectPropertiesWithNoDescription)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());
			
		return new MetricResult(metricValue);	
	}


	@Override
	public String getName() {
		return METRIC_NAME;
	}


	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "ObjectPropertiesWithNoDescriptionMetric";
	}


	@Override
	public String getObservablePropertyIRI() {
		return RDFUtils.DESCRIPTIONS;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_LOWER_BEST;
	}
}
