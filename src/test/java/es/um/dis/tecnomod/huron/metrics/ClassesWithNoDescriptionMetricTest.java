package es.um.dis.tecnomod.huron.metrics;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import es.um.dis.tecnomod.huron.dto.MetricResult;

public class ClassesWithNoDescriptionMetricTest {

	@Test
	public void test() throws FileNotFoundException, IOException, Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream("/example1.owl"));
		Metric metric = new ClassesWithNoDescriptionMetric();
		metric.setOntology(ontology);
		MetricResult metricResult = metric.calculate();
		

		assertEquals(1.0, metricResult.getMetricValue(), 0.01);
		metricResult.getRdf().write(System.out, "Turtle");
		
	}

}
