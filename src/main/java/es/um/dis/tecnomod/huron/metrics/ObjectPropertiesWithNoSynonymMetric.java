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
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.utils.IssueTypes;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

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
		
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfObjectPropertiesWithNoSynonym = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty owlObjectProperty : super.getOntology().objectPropertiesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if(owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}				
			int localNumberOfSynonyms = this.getNumberOfSynonyms(owlObjectProperty);
			if (localNumberOfSynonyms == 0) {
				IssueInfoDTO issue = new IssueInfoDTO(IssueTypes.TRIVIAL_ISSUE, String.format("The object property %s does not have any synonym.", owlObjectProperty.getIRI().toQuotedString()));
				this.notifyExporterListeners(ontologyIRI, owlObjectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), Integer.valueOf(1), timestamp, issue);
				numberOfObjectPropertiesWithNoSynonym++;
			}else {
				
				this.notifyExporterListeners(ontologyIRI, owlObjectProperty.getIRI().toString(), OWL.ObjectProperty.getURI(), Integer.valueOf(0), timestamp, Collections.emptyList());
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfObjectPropertiesWithNoSynonym)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());
		
		return new MetricResult(metricValue);	
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
		return RDFUtils.SYNONYMS;
	}
	
	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_LOWER_BEST;
	}
}
