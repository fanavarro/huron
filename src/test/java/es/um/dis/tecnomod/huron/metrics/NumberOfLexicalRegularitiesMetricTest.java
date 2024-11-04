package es.um.dis.tecnomod.huron.metrics;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class NumberOfLexicalRegularitiesMetricTest {

	@Test
	public void test() throws FileNotFoundException, IOException, Exception {
		// STEP 1: create the object
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(getClass().getResourceAsStream("/example2.owl"));
		

		// STEP 2: calculate the metric
		NumberOfLexicalRegularitiesMetric m1 = new NumberOfLexicalRegularitiesMetric();
		m1.setOntology(ontology);
		double res = m1.calculateValue();
		assertEquals(3.0, res, 0.01);
		System.out.println(m1.getName()+ ": " + res);
	}

}
