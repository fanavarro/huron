package es.um.dis.tecnomod.huron.result_model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;

import es.um.dis.tecnomod.oquo.dto.ObservationInfoDTO;
import es.um.dis.tecnomod.oquo.service.InstanceCreator;
import es.um.dis.tecnomod.oquo.service.RDFValidator;
import es.um.dis.tecnomod.oquo.utils.Namespaces;

public class RDFResultModel implements ResultModelInterface {
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(RDFResultModel.class.getName());
	
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
		ValidationReport validationReport = RDFValidator.validate(this.rdfModel);
		if (!validationReport.conforms()) {
			logValidationReport(validationReport);
		}
		this.rdfModel.setNsPrefixes(Namespaces.getPrefixMap());
		this.rdfModel.write(new FileOutputStream(this.outputFile), "TTL");
	}

	private void logValidationReport(ValidationReport validationReport) throws IOException {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream();){
			ShLib.printReport(out, validationReport);
			LOGGER.warning(String.format("RDF generated model is not compliant with oquo ontology:\n%s", out.toString()));
		}
	}

}
