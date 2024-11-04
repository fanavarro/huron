package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.issues.IssueTypes;
import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;
import es.um.dis.tecnomod.oquo.dto.IssueInfoDTO;
import es.um.dis.tecnomod.oquo.utils.Namespaces;
import es.um.dis.tecnomod.oquo.utils.RankingFunctionTypes;

public class ClassesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric {

	public ClassesWithNoDescriptionMetric() {
		super();
	}


	public ClassesWithNoDescriptionMetric(Config config) {
		super(config);
	}


	/** The Constant NAME. */
	private static final String METRIC_NAME = "Classes with no description";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		
		String ontologyIRI = RDFUtils.getOntologyIdentifier(getOntology());
		Calendar timestamp = Calendar.getInstance();
		int numberOfClassesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().classesInSignature(this.getConfig().getImports()).collect(Collectors.toList())){
			if (owlClass.isOWLNothing() || owlClass.isOWLThing() || OntologyUtils.isObsolete(owlClass, getOntology(), this.getConfig().getImports())) {
				continue;
			}				
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlClass);
			if (localNumberOfDescriptions == 0) {
				IssueInfoDTO issue = new IssueInfoDTO(IssueTypes.CLASS_WITH_NO_DESCRIPTION_ISSUE, owlClass.getIRI().toString(), String.format("The class %s does not have any description.", owlClass.getIRI().toQuotedString()));
				this.notifyExporterListeners(ontologyIRI, owlClass.getIRI().toString(), OWL.Class.getURI(), Integer.valueOf(1), timestamp, issue);
				
				numberOfClassesWithNoDescription++;
			}else {
				
				this.notifyExporterListeners(ontologyIRI, owlClass.getIRI().toString(), OWL.Class.getURI(), Integer.valueOf(0), timestamp, Collections.emptyList());
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfClassesWithNoDescription)) / numberOfEntities;
		this.notifyExporterListeners(ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), Double.valueOf(metricValue), timestamp, Collections.emptyList());
		return new MetricResult(metricValue);			
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
		return RDFUtils.DESCRIPTIONS;
	}

	@Override
	public String getRankingFunctionIRI() {
		return RankingFunctionTypes.RANKING_FUNCTION_LOWER_BEST;
	}

}
