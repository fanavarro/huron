package es.um.dis.tecnomod.huron.metrics;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import es.um.dis.tecnomod.huron.main.Config;
import es.um.dis.tecnomod.huron.main.Prefixes;
import es.um.dis.tecnomod.huron.services.OntologyUtils;

/**
 * The Class AnnotationsPerEntityAbstractMetric.
 */
public abstract class AnnotationsPerEntityAbstractMetric extends Metric {
	public AnnotationsPerEntityAbstractMetric() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AnnotationsPerEntityAbstractMetric(Config config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	private static final boolean INCLUDE_IMPORTED_ANNOTATIONS = true;
	
	/** The Constant SKOS_PREFERRED_LABEL. */
	/* Annotation properties referring names */
	private static final IRI SKOS_PREFERRED_LABEL = IRI.create(Prefixes.SKOS + "prefLabel");
	
	/** The Constant RDFS_LABEL. */
	private static final IRI RDFS_LABEL = IRI.create(Prefixes.RDFS + "label");
	
	/** The Constant SCHEMA_NAME. */
	private static final IRI SCHEMA_NAME = IRI.create(Prefixes.SCHEMA + "name");
	
	/** The Constant NCIT_PREFERRED_NAME. */
	private static final IRI NCIT_PREFERRED_NAME = IRI.create(Prefixes.NCIT + "P108");
	
	/** The Constant IAO_OBO_FOUNDRY_UNIQUE_NAME. */
	private static final IRI IAO_OBO_FOUNDRY_UNIQUE_NAME = IRI.create(Prefixes.IAO + "0000589");
	
	/** The Constant FOAF_NAME. */
	private static final IRI FOAF_NAME = IRI.create(Prefixes.FOAF + "name");


	/* Annotation properties referring synonyms */
	/** The Constant SKOS_ALT_LABEL. */
	private static final IRI SKOS_ALT_LABEL = IRI.create(Prefixes.SKOS + "altLabel");
	
	/** The Constant OBO_HAS_EXACT_SYNONYM. */
	private static final IRI OBO_HAS_EXACT_SYNONYM = IRI.create(Prefixes.OBO_IN_OWL + "hasExactSynonym");
	
	/** The Constant OBO_HAS_RELATED_SYNONYM. */
	private static final IRI OBO_HAS_RELATED_SYNONYM = IRI.create(Prefixes.OBO_IN_OWL + "hasRelatedSynonym");
	
	/** The Constant OBO_HAS_BROAD_SYNONYM. */
	private static final IRI OBO_HAS_BROAD_SYNONYM = IRI.create(Prefixes.OBO_IN_OWL + "hasBroadSynonym");
	
	/** The Constant OBO_HAS_NARROW_SYNONYM. */
	private static final IRI OBO_HAS_NARROW_SYNONYM = IRI.create(Prefixes.OBO_IN_OWL + "hasNarrowSynonym");
	
	/** The Constant NCIT_FULLY_QUALIFIED_SYNONYM. */
	private static final IRI NCIT_FULLY_QUALIFIED_SYNONYM = IRI.create(Prefixes.NCIT + "P90");
	
	/** The Constant IAO_ALTERNATIVE_NAME. */
	private static final IRI IAO_ALTERNATIVE_NAME = IRI.create(Prefixes.IAO + "0000118");
	
	/** The Constant OBI_FGED_ALT_NAME */
	private static final IRI OBI_FGED_ALT_NAME = IRI.create(Prefixes.OBI + "9991119");
	
	/** The Constant OBI_IEDB_ALT_NAME */
	private static final IRI OBI_IEDB_ALT_NAME = IRI.create(Prefixes.OBI + "9991118");
	
	/** The Constant OBI_ISA_ALT_NAME */
	private static final IRI OBI_ISA_ALT_NAME = IRI.create(Prefixes.OBI + "0001847");
	
	/** The Constant OBI_NIAID_ALT_NAME */
	private static final IRI OBI_NIAID_ALT_NAME = IRI.create(Prefixes.OBI + "0001886");

	/* Annotation properties referring definitions, comments or explanations */
	/** The Constant IAO_OFFICIAL_DEFINITION. */
	private static final IRI IAO_OFFICIAL_DEFINITION = IRI.create(Prefixes.IAO + "0000115");
	
	/** The Constant SKOS_DEFINITION. */
	private static final IRI SKOS_DEFINITION = IRI.create(Prefixes.SKOS + "definition");
	
	/** The Constant RDFS_COMMENT. */
	private static final IRI RDFS_COMMENT = IRI.create(Prefixes.RDFS + "comment");
	
	/** The Constant DC_DESCRIPTION. */
	private static final IRI DC_DESCRIPTION = IRI.create(Prefixes.DC + "description");
	
	/** The Constant NCIT_DEFINITION. */
	private static final IRI NCIT_DEFINITION = IRI.create(Prefixes.NCIT + "P97");

	/** The Constant NAME_PROPERTIES. */
	protected static final List<IRI> NAME_PROPERTIES = Arrays.asList(SKOS_PREFERRED_LABEL, RDFS_LABEL, SCHEMA_NAME,
			NCIT_PREFERRED_NAME, IAO_OBO_FOUNDRY_UNIQUE_NAME, FOAF_NAME);

	/** The Constant SYNONYM_PROPERTIES. */
	protected static final List<IRI> SYNONYM_PROPERTIES = Arrays.asList(SKOS_ALT_LABEL, OBO_HAS_EXACT_SYNONYM,
			OBO_HAS_RELATED_SYNONYM, OBO_HAS_BROAD_SYNONYM, OBO_HAS_NARROW_SYNONYM, NCIT_FULLY_QUALIFIED_SYNONYM,
			IAO_ALTERNATIVE_NAME, OBI_FGED_ALT_NAME, OBI_IEDB_ALT_NAME, OBI_ISA_ALT_NAME, OBI_NIAID_ALT_NAME);

	/** The Constant DESCRIPTION_PROPERTIES. */
	protected static final List<IRI> DESCRIPTION_PROPERTIES = Arrays.asList(IAO_OFFICIAL_DEFINITION, SKOS_DEFINITION,
			RDFS_COMMENT, DC_DESCRIPTION, NCIT_DEFINITION);

	/**
	 * Gets the usage in classes.
	 *
	 * @param annotationIRI the annotation IRI
	 * @return the usage in classes
	 */
	protected int getUsageInClasses(IRI annotationIRI) {
		int usage = 0;
		if (getOntology().containsAnnotationPropertyInSignature(annotationIRI, this.getConfig().getImports())) {
			OWLAnnotationProperty annotationProperty = getOntology().getOWLOntologyManager().getOWLDataFactory()
					.getOWLAnnotationProperty(annotationIRI);
			
			for (OWLAxiom axiom : getOntology().referencingAxioms(annotationProperty, this.getConfig().getImports()).collect(Collectors.toList())) {
				if (axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)) {
					OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) axiom).getSubject();
					if (subject instanceof IRI && getOntology().containsClassInSignature((IRI) subject, this.getConfig().getImports())) {
						usage = usage + 1;
					}
				}
			}
		}
		return usage;
	}

	/**
	 * Gets the usage in data properties.
	 *
	 * @param annotationIRI the annotation IRI
	 * @return the usage in data properties
	 */
	protected int getUsageInDataProperties(IRI annotationIRI) {
		int usage = 0;
		if (getOntology().containsAnnotationPropertyInSignature(annotationIRI, this.getConfig().getImports())) {
			OWLAnnotationProperty annotationProperty = getOntology().getOWLOntologyManager().getOWLDataFactory()
					.getOWLAnnotationProperty(annotationIRI);
			for (OWLAxiom axiom : getOntology().referencingAxioms(annotationProperty, this.getConfig().getImports()).collect(Collectors.toList())) {
				if (axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)) {
					OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) axiom).getSubject();
					if (subject instanceof IRI && getOntology().containsDataPropertyInSignature((IRI) subject, this.getConfig().getImports())) {
						usage = usage + 1;
					}
				}
			}
		}
		return usage;
	}

//	/**
//	 * Gets the usage in object properties.
//	 *
//	 * @param annotationIRI the annotation IRI
//	 * @return the usage in object properties
//	 */
//	protected int getUsageInObjectProperties(IRI annotationIRI) {
//		int usage = 0;
//		if (getOntology().containsAnnotationPropertyInSignature(annotationIRI)) {
//			OWLAnnotationProperty annotationProperty = getOntology().getOWLOntologyManager().getOWLDataFactory()
//					.getOWLAnnotationProperty(annotationIRI);
//			for (OWLAxiom axiom : annotationProperty.getReferencingAxioms(getOntology())) {
//				if (axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)) {
//					OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) axiom).getSubject();
//					if (subject instanceof IRI && getOntology().containsObjectPropertyInSignature((IRI) subject)) {
//						usage = usage + 1;
//					}
//				}
//			}
//		}
//		return usage;
//	}

	/**
	 * Gets the usage in annotation properties.
	 *
	 * @param annotationIRI the annotation IRI
	 * @return the usage in annotation properties
	 */
	protected int getUsageInAnnotationProperties(IRI annotationIRI) {
		int usage = 0;
		if (getOntology().containsAnnotationPropertyInSignature(annotationIRI, this.getConfig().getImports())) {
			OWLAnnotationProperty annotationProperty = getOntology().getOWLOntologyManager().getOWLDataFactory()
					.getOWLAnnotationProperty(annotationIRI);
			for (OWLAxiom axiom : getOntology().referencingAxioms(annotationProperty, this.getConfig().getImports()).collect(Collectors.toList())) {
				if (axiom.isOfType(AxiomType.ANNOTATION_ASSERTION)) {
					OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) axiom).getSubject();
					if (subject instanceof IRI && getOntology().containsAnnotationPropertyInSignature((IRI) subject, this.getConfig().getImports())) {
						usage = usage + 1;
					}
				}
			}
		}
		return usage;
	}

	/**
	 * Gets the usage in properties.
	 *
	 * @param annotationIRI the annotation IRI
	 * @return the usage in properties
	 */
//	protected int getUsageInProperties(IRI annotationIRI) {
//		return this.getUsageInAnnotationProperties(annotationIRI) + this.getUsageInDataProperties(annotationIRI)
//				+ this.getUsageInObjectProperties(annotationIRI);
//	}
	
	/**
	 * Gets the number of annotations.
	 *
	 * @param entity the entity
	 * @param annotationsToCheck the annotations to check
	 * @return the number of annotations
	 */
	protected int getNumberOfAnnotations(OWLEntity entity, List<IRI> annotationsToCheck){
		int numberOfAnnotations = 0;
		Set<OWLAnnotationAssertionAxiom> annotationAssertionAxioms = OntologyUtils.getOWLAnnotationAssertionAxiom(entity, this.getOntology(), INCLUDE_IMPORTED_ANNOTATIONS);
		for(OWLAnnotationAssertionAxiom annotationAssertionAxiom : annotationAssertionAxioms){
			if(annotationsToCheck.contains(annotationAssertionAxiom.getProperty().getIRI())){
				numberOfAnnotations = numberOfAnnotations + 1;
			}
		}
		return numberOfAnnotations;
	}
	
	/**
	 * Gets the number of names.
	 *
	 * @param entity the entity
	 * @return the number of names
	 */
	protected int getNumberOfNames(OWLEntity entity){
		return getNumberOfAnnotations(entity, NAME_PROPERTIES);
	}
	
	/**
	 * Gets the number of synonyms.
	 *
	 * @param entity the entity
	 * @return the number of synonyms
	 */
	protected int getNumberOfSynonyms(OWLEntity entity){
		return getNumberOfAnnotations(entity, SYNONYM_PROPERTIES);
	}
	
	/**
	 * Gets the number of descriptions.
	 *
	 * @param entity the entity
	 * @return the number of descriptions
	 */
	protected int getNumberOfDescriptions(OWLEntity entity){
		return getNumberOfAnnotations(entity, DESCRIPTION_PROPERTIES);
	}
}
