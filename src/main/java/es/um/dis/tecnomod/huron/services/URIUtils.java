package es.um.dis.tecnomod.huron.services;

public class URIUtils {
	public static String getNameFromURI(String uri) {
		return uri.substring(Math.max(uri.lastIndexOf('/'), uri.lastIndexOf('#')) + 1);
		
	}
}
