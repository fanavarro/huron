package es.um.dis.tecnomod.huron.metrics;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.huron.dto.MetricResult;

public class ClassesWithNoSynonymMetricTest {

	@Test
	public void test() throws FileNotFoundException, IOException, Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example1.owl"));
		Metric metric = new ClassesWithNoSynonymMetric();
		metric.setOntology(ontology);
		MetricResult metricResult = metric.calculate();
		

		assertEquals(13.0/15.0, metricResult.getMetricValue(), 0.01);
		
	}

}
