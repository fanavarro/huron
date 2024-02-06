package es.um.dis.tecnomod.huron.result_model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public abstract class TSVResultModel implements ResultModelInterface {
	protected static final String QUOTE_STRING = "\"";
	protected static final String NEW_LINE_STRING = "\n";
	protected static final String COLUMN_SEPARATOR_STRING = "\t";
	
	protected String getStringValue(Object object) {
		if (object instanceof String) {
			return QUOTE_STRING + object.toString() + QUOTE_STRING;
		}
		if (object instanceof Number) {
			return new DecimalFormat("###.###", new DecimalFormatSymbols(Locale.ROOT)).format(object);
					
		}
		return object.toString();
	}
}
