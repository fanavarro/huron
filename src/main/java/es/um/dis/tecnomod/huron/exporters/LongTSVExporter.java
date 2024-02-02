package es.um.dis.tecnomod.huron.exporters;

import java.io.File;

public class LongTSVExporter extends TableExporter {


	public LongTSVExporter(File outputFile) {
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
