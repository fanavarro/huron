package es.um.dis.tecnomod.huron.services;

import java.util.Collection;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * The Interface OntologyGraphService.
 */
public interface OntologyGraphService {
	
	/**
	 * True if there is a path between a and b or between b and a.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @param ignoredAxioms the ignored axioms
	 * @return true, if is related
	 */
	boolean isRelated(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection <AxiomType<?>> ignoredAxioms);
	
	/**
	 * True if there is a path between a and b or between b and a.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @param ignoredAxioms the ignored axioms
	 * @param maxDepth Maximum depth to consider when traversing the graph axiomatically.
	 * @return true, if is related
	 */
	boolean isRelated(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms, int maxDepth);
	
	/**
	 * True if there is a path between a and b or between b and a.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @return true, if is related
	 */
	boolean isRelated(OWLReasoner reasoner, OWLClass a, OWLClass b);
	
	
	/**
	 * True if a and b are related by subclass of relations.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @return true, if is hierarchichally related
	 */
	boolean isHierarchichallyRelated(OWLReasoner reasoner, OWLClass a, OWLClass b);
	
	/**
	 * True if a and b are related by any axiom of relations.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @param ignoredAxioms Axioms to ignore.
	 * @return true, if is axiomatically related
	 */
	boolean isAxiomaticallyRelated(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms);
	
	/**
	 * True if a and b are related by any axiom of relations.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @param ignoredAxioms Axioms to ignore.
	 * @param maxDepth Maximum depth to take into account when expanding nodes in the graph.
	 * @return true, if is axiomatically related
	 */
	boolean isAxiomaticallyRelated(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms, int maxDepth);
	
	/**
	 * True if there is a path between a and b.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @param ignoredAxioms the ignored axioms
	 * @return true, if successful
	 */
	boolean existPath(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection <AxiomType<?>> ignoredAxioms);
	
	/**
	 * True if there is a path between a and b.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @param ignoredAxioms the ignored axioms
	 * @param maxDepth Maximum depth to take into account when expanding nodes through axioms in the graph.
	 * @return true, if successful
	 */
	boolean existPath(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms, int maxDepth);
	
	/**
	 * True if there is a path between a and b.
	 *
	 * @param reasoner the reasoner
	 * @param a the a
	 * @param b the b
	 * @return true, if successful
	 */
	boolean existPath(OWLReasoner reasoner, OWLClass a, OWLClass b);

	/**
	 * Get the depth of a class in the ontology hierarchy.
	 *
	 * @param reasoner the reasoner
	 * @param owlClass the owl class
	 * @return the class depth
	 */
	int getClassDepth(OWLReasoner reasoner, OWLClass owlClass);
}
