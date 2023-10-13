package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.ontoenrich.beans.Label;
import org.ontoenrich.core.LexicalEnvironment;
import org.ontoenrich.core.LexicalRegularity;
import org.ontoenrich.filters.RemoveNoClasses;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.OntologyGraphService;
import es.um.dis.tecnomod.huron.services.OntologyGraphServiceImpl;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * The Class SystematicNamingMetric.
 */
public class SystematicNamingMetric extends OntoenrichMetric {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(SystematicNamingMetric.class.getName());
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Systematic naming";
	
	/** The reasoner. */
	private OWLReasoner reasoner;
	
	/** The ontology graph service. */
	private OntologyGraphService ontologyGraphService;
	
	/**
	 * Instantiates a new systematic naming metric.
	 */
	public SystematicNamingMetric(){
		this.ontologyGraphService = new OntologyGraphServiceImpl();
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		/* Write header for detailed output file */
		super.writeToDetailedOutputFile("Metric\tClass\tClass depth\tLR\tPositive Cases\tPositive cases average depth\tPositive cases average distance to LR class\tNegative Cases\tNegative cases average depth\tNegative cases average distance to LR class\tMetric Value\n" );
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		
		// STEP 1: create the lexical environment
		LexicalEnvironment lexicalEnvironment = this.getLexicalEnvironment();

		// INIT owlReasoner
		reasoner = createReasoner(getOntology());

		// STEP 2: Perform lexical analysis with threshold
		int numberOfClassesThreshold = this.getNumberOfClassesThreshold();
		List<LexicalRegularity> lexicalRegularities = lexicalEnvironment.searchAllPatterns(numberOfClassesThreshold /* Minimum Coverage (in labels) */);
				

		// STEP 3: apply a filter to get just LRs that are classes
		RemoveNoClasses.execute(lexicalRegularities);

		// STEP 4: calculate the metric
		int positiveCasesCount = 0;
		int negativeCasesCount = 0;
		for (LexicalRegularity lexicalRegularity : lexicalRegularities) {
			for (OWLClass owlClassA : this.getRepresentingClasses(lexicalEnvironment, lexicalRegularity)) {
				int owlClassADepth = -1;
				if (OntologyUtils.isObsolete(owlClassA, getOntology())) {
					continue;
				}
				String classALabel = lexicalEnvironment.getLabelById(owlClassA.getIRI().toString()).getStrLabel();
				Set<OWLClass> subClassesOfA = reasoner.getSubClasses(owlClassA, false).entities().collect(Collectors.toSet());
				subClassesOfA.remove(getOntology().getOWLOntologyManager().getOWLDataFactory().getOWLNothing());
				Set<OWLClass> classeslexicallyRelatedWithA = this.getClassesWithPattern(lexicalRegularity);
				classeslexicallyRelatedWithA.remove(owlClassA);
				
				Set<OWLClass> localPositiveCases = SetUtils.intersection(subClassesOfA, classeslexicallyRelatedWithA);
				int localPositiveCasesCount = localPositiveCases.size();
				
				Set<OWLClass> localNegativeCases = SetUtils.difference(subClassesOfA, classeslexicallyRelatedWithA);
				int localNegativeCasesCount = localNegativeCases.size();
				
				double localMetricResult = (double) localPositiveCasesCount / (localPositiveCasesCount + localNegativeCasesCount);
				RDFUtils.createObservation(rdfModel, ontologyIRI, owlClassA.getIRI().toString(), OWL.Class.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(localMetricResult), timestamp);
				if(super.isOpenDetailedOutputFile()){
					if (owlClassADepth == -1){
						owlClassADepth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClassA);
					}
					double averageDepthLocalPositiveCases = this.getAverageDepth(localPositiveCases);
					double averageDepthLocalNegativeCases = this.getAverageDepth(localNegativeCases);
					double averageDistanceToLRClassLocalPositiveCases = this.getAverageDistanceToDepth(localPositiveCases, owlClassADepth);
					double averageDistanceToLRClassLocalNegativeCases = this.getAverageDistanceToDepth(localNegativeCases, owlClassADepth);
					super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\t%s\t%d\t%.3f\t%.3f\t%d\t%.3f\t%.3f\t%.3f\n", this.getName(), owlClassA.toStringID(), owlClassADepth, lexicalRegularity.getStrPattern(), localPositiveCasesCount, averageDepthLocalPositiveCases,averageDistanceToLRClassLocalPositiveCases, localNegativeCasesCount, averageDepthLocalNegativeCases, averageDistanceToLRClassLocalNegativeCases, localMetricResult));
				}
				positiveCasesCount += localPositiveCasesCount;
				negativeCasesCount += localNegativeCasesCount;
				for(OWLClass c : localNegativeCases){
					String cLabel = lexicalEnvironment.getLabelById(c.getIRI().toString()).getStrLabel();
					LOGGER.log(Level.INFO, String.format("The class %s is subclass of %s but there are no lexical regularities in common.", c.toStringID(), owlClassA.toStringID()));
					// TODO: create issue here?
					// RDFUtils.createIssue(rdfModel, metricProperty, owlClassA, String.format("Class %s ('%s') is subclass of %s ('%s') but there are no lexical regularities in common.", c.toStringID(), cLabel, owlClassA.toStringID(), classALabel));
				}
			}
		}
		reasoner.flush();
		reasoner.dispose();
		// STEP 5: return the calculated value
		double metricValue = (double) positiveCasesCount / (positiveCasesCount + negativeCasesCount);
		RDFUtils.createObservation(rdfModel, ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), timestamp);
		
		return new MetricResult(metricValue, rdfModel);

	}
	
	/**
	 * Gets the average distance to depth.
	 *
	 * @param owlClasses the owl classes
	 * @param parentDepth the parent depth
	 * @return the average distance to depth
	 */
	private double getAverageDistanceToDepth(Set<OWLClass> owlClasses, int parentDepth) {
		int distanceToParentSum = 0;
		int totalClasses = 0;
		for(OWLClass owlClass : owlClasses){
			int depth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClass);
			if(depth != -1){
				distanceToParentSum += Math.abs(depth - parentDepth);
				totalClasses++;
			}
		}
		return (double)distanceToParentSum/totalClasses;
	}
	
	/**
	 * Gets the average depth.
	 *
	 * @param owlClasses the owl classes
	 * @return the average depth
	 */
	private double getAverageDepth(Set<OWLClass> owlClasses) {
		int depthSum = 0;
		int totalClasses = 0;
		for(OWLClass owlClass : owlClasses){
			int depth = this.ontologyGraphService.getClassDepth(this.reasoner, owlClass);
			if(depth != -1){
				depthSum += depth;
				totalClasses++;
			}
		}
		return (double)depthSum/totalClasses;
	}
	
	/**
	 * Gets the classes with pattern.
	 *
	 * @param lexicalRegularity the lexical regularity
	 * @return the classes with pattern
	 */
	private Set<OWLClass> getClassesWithPattern(LexicalRegularity lexicalRegularity) {
		Set<OWLClass> classesWithPattern = new HashSet<OWLClass>();
		for (Label l : lexicalRegularity.getIdLabelsWhereItAppears()) {
			OWLClass classWithPattern = getOntology().getOWLOntologyManager().getOWLDataFactory()
					.getOWLClass(IRI.create(l.getIdLabel()));
			if (!OntologyUtils.isObsolete(classWithPattern, getOntology())) {
				classesWithPattern.add(classWithPattern);
			}
			
		}
		return classesWithPattern;
	}

	/**
	 * Gets the representing classes.
	 *
	 * @param la the la
	 * @param p the p
	 * @return the representing classes
	 */
	private Set<OWLClass> getRepresentingClasses(LexicalEnvironment lexicalEnvironment, LexicalRegularity lexicalRegularity) {
		
		Set<Label> labels = lexicalEnvironment.getLabel(lexicalRegularity);
		Set<OWLClass> classes = new HashSet<OWLClass>();
		for (Label label : labels) {
			label.getIdLabel();
			OWLClass owlClass = lexicalEnvironment.owlApiOntology.getOWLOntologyManager().getOWLDataFactory()
					.getOWLClass(IRI.create(label.getIdLabel()));
			classes.add(owlClass);
		}
		return classes;
	}
	
	/**
	 * Creates the reasoner.
	 *
	 * @param ontology the ontology
	 * @return the OWL reasoner
	 */
	private OWLReasoner createReasoner(OWLOntology ontology) {
		OWLReasonerFactory factory = new ElkReasonerFactory();
		OWLReasoner reasoner =  factory.createReasoner(ontology);
		try {
			reasoner.isConsistent();
		} catch (Exception e){
			LOGGER.warning("ELK reasoner not supported, using structural reasoner.");
			factory = new StructuralReasonerFactory();
			reasoner = factory.createReasoner(ontology);
		}
		return reasoner;
	}
	
	


	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "SystematicNaming";
	}

	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.SYSTEMATIC_NAMING_PRINCIPLE;
	}

}
