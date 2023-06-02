package es.um.dis.tecnomod.huron.services;

import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.model.OWLEntity;

import es.um.dis.tecnomod.huron.namespaces.Namespaces;

public class RDFUtils {
	private static final String ISSUE_CLASS_IRI = Namespaces.OQUO_NS + "Issue";
	private static final String HAS_ISSUE_IRI = Namespaces.OQUO_NS + "hasIssue";
	private static final String WAS_GENERATED_BY = Namespaces.PROV_NS + "wasGeneratedBy";
	
	public static void createIssue(Model rdfModel, Property metricProperty, OWLEntity owlEntity,
			String message) {
		String issueID = Namespaces.OQUO_NS + UUID.randomUUID().toString();
		Resource issue = rdfModel.createResource(issueID, rdfModel.createResource(ISSUE_CLASS_IRI));
		
		issue.addLiteral(RDFS.comment, message);		
		issue.addProperty(rdfModel.createProperty(WAS_GENERATED_BY), metricProperty);
		

		rdfModel.createResource(owlEntity.getIRI().toString()).addProperty(rdfModel.createProperty(HAS_ISSUE_IRI), issue);
	}
}
