package es.um.dis.tecnomod.huron.result_model;

import java.io.File;

public class LongTSVResultModel extends SummaryTSVResultModel {


	public LongTSVResultModel(File outputFile) {
		super(outputFile);
	}

	@Override
	protected String getQuoteString() {
		return "\"";
	}

	@Override
	protected String getColumnSeparator() {
		return "\t";
	}

	@Override
	protected String getRowSeparator() {
		return "\n";
	}
}
