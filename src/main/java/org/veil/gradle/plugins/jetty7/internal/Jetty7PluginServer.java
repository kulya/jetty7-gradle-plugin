package org.veil.gradle.plugins.jetty7.internal;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jetty7PluginServer implements JettyPluginServer {
	
	private static final Logger log = LoggerFactory.getLogger(Jetty7PluginServer.class);
	
	public static final int DEFAULT_MAX_IDLE_TIME = 30000;
	
	private final Server server;
	
	private RequestLog requestLog;
	
	private DefaultHandler defaultHandler;
	private RequestLogHandler requestLogHandler;
	
	private ContextHandlerCollection contexts;
	private HandlerCollection handlers;
	
	public Jetty7PluginServer() {
		this.server = new Server();
		this.server.setStopAtShutdown(true);
		Resource.setDefaultUseCaches(false);
	}

	@Override
	public Object getProxiedObject() {
		return this.server;
	}

	@Override
	public void setRequestLog(Object requestLog) {
		this.requestLog = (RequestLog)requestLog;
	}

	@Override
	public Object getRequestLog() {
		return this.requestLog;
	}

	@Override
	public void setConnectors(Object[] connectors) throws Exception {
		if (connectors == null || connectors.length < 1) {
			return;
		}
		
		for (Object conn : connectors) {
			Connector connector = (Connector)conn;
			log.debug("Setting Connector: " + connector.getClass().getName() + " on port " + connector.getPort());
			this.server.addConnector(connector);
		}
	}

	@Override
	public Object[] getConnectors() {
		return this.server.getConnectors();
	}

	@Override
	public void configureHandlers() throws Exception {
		this.defaultHandler = new DefaultHandler();
		this.requestLogHandler = new RequestLogHandler();
		if (this.requestLog != null) {
			requestLogHandler.setRequestLog(requestLog);
		}

		this.contexts = (ContextHandlerCollection) server.getChildHandlerByClass(ContextHandlerCollection.class);
		if (this.contexts == null) {
			this.contexts = new ContextHandlerCollection();
			this.handlers = (HandlerCollection) server.getChildHandlerByClass(HandlerCollection.class);
			if (this.handlers ==null) {
				this.handlers = new HandlerCollection();
				this.server.setHandler(handlers);
				this.handlers.setHandlers(new Handler[] {this.contexts, this.defaultHandler, this.requestLogHandler});
			} else {
				this.handlers.addHandler(this.contexts);
			}
		}
		
	}

	@Override
	public void addWebApplication(WebAppContext webapp) throws Exception {
		this.contexts.addHandler(webapp);
	}

	@Override
	public void start() throws Exception {
		log.debug("Starting jetty " + this.server.getClass().getPackage().getImplementationVersion() + "...");
		this.server.start();
	}

	@Override
	public Object createDefaultConnector(int port) throws Exception {
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		connector.setMaxIdleTime(DEFAULT_MAX_IDLE_TIME);
		
		return connector;
	}

	@Override
	public void join() throws Exception {
		this.server.getThreadPool().join();
	}

}
