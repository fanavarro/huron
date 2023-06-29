package es.um.dis.tecnomod.huron.metrics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class DOIDTest {
	private static final String ONTOLOGY_RESOURCE = "/example3.owl";

	@Test
	public void namesPerClassMetricTest() throws Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(ONTOLOGY_RESOURCE));
		Metric metric = new NamesPerClassMetric();
		metric.setOntology(ontology);
		double res = metric.calculate();
		
		assertEquals(1.0, res, 0.01);
	}
	
	@Test
	public void synonymsPerClassMetricTest() throws Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(ONTOLOGY_RESOURCE));
		Metric metric = new SynonymsPerClassMetric();
		metric.setOntology(ontology);
		double res = metric.calculate();
		
		assertEquals(2.0/3.0, res, 0.01);
	}
	
	@Test
	public void descriptionsPerClassMetricTest() throws Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(ONTOLOGY_RESOURCE));
		Metric metric = new DescriptionsPerClassMetric();
		metric.setOntology(ontology);
		double res = metric.calculate();
		
		assertEquals(1.0, res, 0.01);
	}
	
	@Test
	public void lsldMetricTest() throws Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(ONTOLOGY_RESOURCE));
		Metric metric = new LexicallySuggestLogicallyDefineMetric();
		metric.setOntology(ontology);
		double res = metric.calculate();
		
		assertEquals(1.0, res, 0.01);
	}
	
	@Test
	public void systematicNamingTest() throws Exception {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(this.getClass().getResourceAsStream(ONTOLOGY_RESOURCE));
		Metric metric = new SystematicNamingMetric();
		metric.setOntology(ontology);
		double res = metric.calculate();
		
		assertEquals(1.0/2.0, res, 0.01);
	}

}
