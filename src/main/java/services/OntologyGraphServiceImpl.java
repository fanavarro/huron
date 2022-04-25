package services;

import java.util.Collection;
import java.util.HashSet;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import owlgraph.OWLAxiomaticGraph;
import owlgraph.OWLTaxonomicGraph;

/**
 * The Class OntologyGraphServiceImpl.
 */
public class OntologyGraphServiceImpl implements OntologyGraphService {
	
	private static final int NO_MAX_DEPTH_LIMIT = -1;

	/* (non-Javadoc)
	 * @see services.OntologyGraphService#isRelated(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass, java.util.Collection, java.lang.Integer)
	 */
	@Override
	public boolean isRelated(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms, int maxDepth) {
		return existPath(reasoner, a, b, ignoredAxioms, maxDepth) || existPath(reasoner, b, a, ignoredAxioms, maxDepth);
	}

	
	/* (non-Javadoc)
	 * @see services.OntologyGraphService#isRelated(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass, java.util.Collection)
	 */
	@Override
	public boolean isRelated(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms) {
		return this.isRelated(reasoner, a, b, ignoredAxioms, NO_MAX_DEPTH_LIMIT);
	}
	

	/* (non-Javadoc)
	 * @see services.OntologyGraphService#isRelated(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public boolean isRelated(OWLReasoner reasoner, OWLClass a, OWLClass b) {
		return isRelated(reasoner, a, b, new HashSet<AxiomType<?>>(), NO_MAX_DEPTH_LIMIT);
	}
	
	/* (non-Javadoc)
	 * @see services.OntologyGraphService#existPath(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass, java.util.Collection, java.lang.Integer)
	 */
	@Override
	public boolean existPath(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms, int maxDepth) {
		if (a.getIRI() != b.getIRI()) {
			if (this.isHierarchichallyRelated(reasoner, a, b)) {
				return true;
			}

			if(this.isAxiomaticallyRelated(reasoner, a, b, ignoredAxioms, maxDepth)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see services.OntologyGraphService#existPath(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass, java.util.Collection)
	 */
	@Override
	public boolean existPath(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms) {
		return this.existPath(reasoner, a, b, ignoredAxioms, NO_MAX_DEPTH_LIMIT);
	}
	

	/* (non-Javadoc)
	 * @see services.OntologyGraphService#existPath(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public boolean existPath(OWLReasoner reasoner, OWLClass a, OWLClass b) {
		return existPath(reasoner, a, b, new HashSet<AxiomType<?>>(), NO_MAX_DEPTH_LIMIT);
	}

	/* (non-Javadoc)
	 * @see services.OntologyGraphService#isHierarchichallyRelated(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public boolean isHierarchichallyRelated(OWLReasoner reasoner, OWLClass a, OWLClass b) {
		return reasoner.getSubClasses(a, false).containsEntity(b) || reasoner.getSubClasses(b, false).containsEntity(a);
	}

	/* (non-Javadoc)
	 * @see services.OntologyGraphService#isAxiomaticallyRelated(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass, java.util.Collection)
	 */
	@Override
	public boolean isAxiomaticallyRelated(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms) {
		return this.isAxiomaticallyRelated(reasoner, a, b, ignoredAxioms, NO_MAX_DEPTH_LIMIT);
	}
	
	/* (non-Javadoc)
	 * @see services.OntologyGraphService#isAxiomaticallyRelated(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass, org.semanticweb.owlapi.model.OWLClass, java.util.Collection, java.lang.Integer)
	 */
	@Override
	public boolean isAxiomaticallyRelated(OWLReasoner reasoner, OWLClass a, OWLClass b, Collection<AxiomType<?>> ignoredAxioms, int maxDepth) {
		OWLAxiomaticGraph graph = new OWLAxiomaticGraph(reasoner, false, ignoredAxioms, maxDepth);
		return graph.existsPath(a, b);
	}
	
	/* (non-Javadoc)
	 * @see services.OntologyGraphService#getClassDepth(org.semanticweb.owlapi.reasoner.OWLReasoner, org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public int getClassDepth(OWLReasoner reasoner, OWLClass owlClass){
		OWLTaxonomicGraph taxonomicGraph = new OWLTaxonomicGraph(reasoner);
		return taxonomicGraph.getDepth(owlClass);
	}

}
