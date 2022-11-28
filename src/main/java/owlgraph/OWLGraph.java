package owlgraph;

import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import com.github.fanavarro.graphlib.AbstractGraph;

/**
 * Class representing an owl-based graph. The nodes of this kind of graph are owl classes,
 * and the edges can be customised by specialising this class.
 * @author fabad
 *
 * @param <E> The edge type
 */
public abstract class OWLGraph<E> extends AbstractGraph<OWLClass, E>{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1990901811979413043L;
	
	/** The ontology. */
	private OWLOntology ontology;
	
	/** The reasoner. */
	private OWLReasoner reasoner;
	
	/** The include imports closure. */
	private boolean includeImportsClosure;
	
	/**
	 * Instantiates a new OWL graph.
	 *
	 * @param ontology the ontology
	 * @param reasonerFactory the reasoner factory
	 */
	public OWLGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory){
		this(ontology, reasonerFactory, false);
	}
	
	/**
	 * Instantiates a new OWL graph.
	 *
	 * @param ontology the ontology
	 * @param reasonerFactory the reasoner factory
	 * @param includeImportsClosure the include imports closure
	 */
	public OWLGraph(OWLOntology ontology, OWLReasonerFactory reasonerFactory, boolean includeImportsClosure){
		this(reasonerFactory.createNonBufferingReasoner(ontology), includeImportsClosure);
	}
	
	/**
	 * Instantiates a new OWL graph.
	 *
	 * @param reasoner the reasoner
	 */
	public OWLGraph(OWLReasoner reasoner){
		this(reasoner, false);
	}
	
	/**
	 * Instantiates a new OWL graph.
	 *
	 * @param reasoner the reasoner
	 * @param includeImportsClosure the include imports closure
	 */
	public OWLGraph(OWLReasoner reasoner, boolean includeImportsClosure){
		this.ontology = reasoner.getRootOntology();
		this.includeImportsClosure = includeImportsClosure;
		this.reasoner = reasoner;
	}
	
	/* (non-Javadoc)
	 * @see com.github.fanavarro.graphlib.Graph#getNodes()
	 */
	@Override
	public Set<OWLClass> getNodes() {
		return ontology.classesInSignature(Imports.fromBoolean(includeImportsClosure)).collect(Collectors.toSet());
	}
	
	/**
	 * Gets the ontology.
	 *
	 * @return the ontology
	 */
	public OWLOntology getOntology(){
		return ontology;
	}
	
	/**
	 * Gets the reasoner.
	 *
	 * @return the reasoner
	 */
	public OWLReasoner getReasoner(){
		return reasoner;
	}
	
	/**
	 * Gets the include imports closure.
	 *
	 * @return the include imports closure
	 */
	public boolean getIncludeImportsClosure(){
		return includeImportsClosure;
	}

}
