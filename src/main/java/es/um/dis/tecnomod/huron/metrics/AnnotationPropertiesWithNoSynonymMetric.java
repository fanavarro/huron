package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.issues.IssueTypes;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;


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
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfAnnotationPropertiesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().annotationPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){	
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlAnnotationProperty);
			if (localNumberOfSynonyms == 0) {
				IssueInfoDTO issue = new IssueInfoDTO(IssueTypes.ANNOTATION_PROPERTY_WITH_NO_SYNONYMS_ISSUE, owlAnnotationProperty.getIRI().toString(), String.format("The annotation property %s does not have any synonym.", owlAnnotationProperty.getIRI().toQuotedString()));
				this.notifyExporterListeners(ontologyIRI, owlAnnotationProperty.getIRI().toString(), OWL.AnnotationProperty.getURI(), Integer.valueOf(1), timestamp, issue);
				numberOfAnnotationPropertiesWithNoSynonym++;
			}else {
				
				this.notifyExporterListeners(ontologyIRI, owlAnnotationProperty.getIRI().toString(), OWL.AnnotationProperty.getURI(), Integer.valueOf(0), timestamp, Collections.emptyList());
			}
			numberOfEntities ++;
		}
		double metricValue = ((double) (numberOfAnnotationPropertiesWithNoSynonym)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());
		return new MetricResult(metricValue);		
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
		return RDFUtils.SYNONYMS;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_LOWER_BEST;
	}
}
