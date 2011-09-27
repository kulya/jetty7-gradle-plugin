package org.veil.gradle.plugins.jetty7.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jetty.plus.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyConfiguration extends Configuration {

	private List classpathFiles;
	
	private File webXmlFile;
	
	@Override
	public void preConfigure(WebAppContext context) throws Exception {
		super.preConfigure(context);
		
		configureServerClasspath(context);
		
	}
	
	private void configureWebXML(WebAppContext context) {
		
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
