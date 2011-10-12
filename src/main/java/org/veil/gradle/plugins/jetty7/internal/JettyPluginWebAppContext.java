/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veil.gradle.plugins.jetty7.internal;

import org.eclipse.jetty.webapp.StandardDescriptorProcessor;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Jetty7PluginWebAppContext
 */
public class JettyPluginWebAppContext extends WebAppContext {
    private List classpathFiles;
    private File jettyEnvXmlFile;
    private File webXmlFile;

    public JettyPluginWebAppContext() {
        super();
    }

    public void setClassPathFiles(List classpathFiles) {
        this.classpathFiles = classpathFiles;
    }

    public List getClassPathFiles() {
        return this.classpathFiles;
    }

    public void setWebXmlFile(File webXmlFile) {
        this.webXmlFile = webXmlFile;
    }

    public File getWebXmlFile() {
        return this.webXmlFile;
    }

    public void setJettyEnvXmlFile(File jettyEnvXmlFile) {
        this.jettyEnvXmlFile = jettyEnvXmlFile;
    }

    public File getJettyEnvXmlFile() {
        return this.jettyEnvXmlFile;
    }

    public void configure() throws Exception{
        if (this.jettyEnvXmlFile != null) {
            setJettyEnvXmlFile(this.jettyEnvXmlFile);
        }

        if (webXmlFile != null) {
            setDescriptor(webXmlFile.getCanonicalPath());
        }

        super.configure();
    }
    public void preConfigure() throws Exception{
        super.preConfigure();
        configureWebAppClasspath();
//        getMetaData().addDescriptorProcessor(new StandardDescriptorProcessor());

    }

    private void configureWebAppClasspath() throws IOException {
        if (classpathFiles != null && !classpathFiles.isEmpty()) {
            for (Object file : classpathFiles) {
                ((WebAppClassLoader)getClassLoader()).addClassPath(((File)file).getCanonicalPath());
            }
        }
    }

    public void doStart() throws Exception {
        setShutdown(false);
        super.doStart();
    }

    public void doStop() throws Exception {
        setShutdown(true);
        //just wait a little while to ensure no requests are still being processed
        Thread.sleep(500L);
        super.doStop();
    }
}
