package es.um.dis.tecnomod.huron.result_model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import es.um.dis.tecnomod.huron.services.RDFUtils;

public class RDFResultModel implements ResultModelInterface {
	
	private File outputFile;
	private Model rdfModel;
	
	public RDFResultModel(File outputFile) {
		super();
		this.outputFile = outputFile;
		this.rdfModel = ModelFactory.createDefaultModel();
	}

	@Override
	public synchronized void addObservation(String sourceDocumentIRI, String featureOfInterestIRI, String featureOfInterestTypeIRI,
			String observablePropertyIRI, String metricUsedIRI, String instrumentIRI, String unitIRI, Object value,
			Calendar timestamp) {
		RDFUtils.createObservation(rdfModel, sourceDocumentIRI, featureOfInterestIRI, featureOfInterestTypeIRI, observablePropertyIRI, metricUsedIRI, instrumentIRI, unitIRI, value, timestamp);
	}

	@Override
	public void export() throws IOException {
		this.rdfModel.write(new FileOutputStream(this.outputFile), "N-TRIPLES");
	}

}
