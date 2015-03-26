package com.gee5.tools.shellraptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ManifestVersionReader {

	public ManifestVersionReader() { }

	public String fetchShellRaptorManifestVersion() {
	    try {		        
	    	URL url = (URL)this.getClass().getResource("/META-INF/MANIFEST.MF");
		    InputStream is = url.openStream();
		    if (is != null) {
		    	Manifest manifest = new Manifest(is);
		        Attributes mainAttribs = manifest.getMainAttributes();
		        String version = mainAttribs.getValue("Implementation-Version");
		        if(version != null) {
		        	return version;
		        }//end if version-null
		    }//end if stream null
	    } catch (IOException e1) {
	        // Silently ignore wrong manifests on classpath?
	    }
	    return "Unknown"; 
	}	  
	
}
