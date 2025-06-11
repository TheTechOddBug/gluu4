/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.oxauth.service.net;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gluu.oxauth.model.configuration.AppConfiguration;
import org.gluu.oxauth.model.configuration.ConnectionServiceConfiguration;
import org.slf4j.Logger;

/**
 * Provides operations with http/https requests
 *
 * @author Yuriy Movchan Date: 04/10/2023
 */
@ApplicationScoped
public class HttpService2 extends org.gluu.net.HttpServiceUtility implements Serializable {

	@Inject
	private Logger log;
	
	@Inject
	private AppConfiguration appConfiguration;

	@PostConstruct
	public void init() {
		super.init();
	}

	@PreDestroy
	public void destroy() {
		super.destroy();
	}

	@Override
	public Logger getLogger() {
		return log;
	}

	public Map<String, Integer> getApplicationConnectionProperties() {
		ConnectionServiceConfiguration connectionServiceConfiguration = appConfiguration.getConnectionServiceConfiguration();
		if (connectionServiceConfiguration == null) {
			return null;
		}
		
		Map<String, Integer> conf = new HashMap<String, Integer>();
		conf.put(HTTPCLIENT_MAX_TOTAL, connectionServiceConfiguration.getMaxTotal());
		conf.put(HTTPCLIENT_MAX_PER_ROUTE, connectionServiceConfiguration.getMaxPerRoute());
		conf.put(HTTPCLIENT_VALIDATE_AFTER_INACTIVITY, connectionServiceConfiguration.getValidateAfterInactivity());

		return conf;
    }
}
