package es.um.dis.tecnomod.huron.result_model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.service.InstanceCreator;

public class RDFResultModel implements ResultModelInterface {
	
	private File outputFile;
	private Model rdfModel;
	
	public RDFResultModel(File outputFile) {
		super();
		this.outputFile = outputFile;
		this.rdfModel = ModelFactory.createDefaultModel();
	}

	@Override
	public synchronized void addObservation(ObservationInfoDTO observationInfo) {
		InstanceCreator.createObservation(rdfModel, observationInfo);
	}

	@Override
	public void export() throws IOException {
		this.rdfModel.write(new FileOutputStream(this.outputFile), "N-TRIPLES");
	}

}
