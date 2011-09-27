package org.veil.gradle.plugins.jetty7.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.jetty.plus.webapp.Configuration;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlProcessor;

public class JettyConfiguration extends Configuration {

	private List classpathFiles;
	
	private File webXmlFile;
	
	@Override
	public void preConfigure(WebAppContext context) throws Exception {
		super.preConfigure(context);
		
		configureServerClasspath(context);
		configureWebXML(context);
		
	}
	
	private void configureWebXML(WebAppContext context) throws MalformedURLException, IOException, URISyntaxException, Exception {
		if (webXmlFile != null && webXmlFile.exists()) {
    		WebXmlProcessor webXmlProcessor = (WebXmlProcessor)context.getAttribute(WebXmlProcessor.WEB_PROCESSOR); 
            
    		if (webXmlProcessor == null) {
               throw new IllegalStateException ("No processor for web xml");
            }
    		webXmlProcessor.parseWebXml(new FileResource(webXmlFile.toURI().toURL()));
		}
	}
	
	private void configureServerClasspath(WebAppContext context) throws IOException {
		for (Object file : classpathFiles) {
			((WebAppClassLoader)context.getClassLoader()).addClassPath(((File)file).getCanonicalPath());
		}
	}

	public void setClassPathConfiguration(List classpathFiles) {
		this.classpathFiles = classpathFiles;
	}

	public void setWebXml(File webXmlFile) {
		this.webXmlFile = webXmlFile;
	}
	
	
}
