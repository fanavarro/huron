package es.um.dis.tecnomod.huron.metrics;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class NumberOfLexicalRegularityClassesMetricTest {

	@Test
	public void test() throws FileNotFoundException, IOException, Exception {
		// STEP 1: create the object
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example2.owl"));

		NumberOfLexicalRegularityClassesMetric m1 = new NumberOfLexicalRegularityClassesMetric();
		m1.setOntology(ontology);
		// STEP 2: calculate the metric
		double res = m1.calculateValue();
		assertEquals(2.0, res, 0.01);
		System.out.println(m1.getName()+ ": " + res);
	}

}
