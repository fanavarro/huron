package es.um.dis.tecnomod.huron.services;

public class URIUtils {
	public static String getNameFromURI(String uri) {
		return uri.substring(Math.max(uri.lastIndexOf('/'), uri.lastIndexOf('#')) + 1);
	}
	
	public static String getNamespaceFromURI(String uri) {
		return uri.substring(0, Math.max(uri.lastIndexOf('/'), uri.lastIndexOf('#')) + 1);
	}
}
