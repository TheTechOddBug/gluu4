package org.gluu.oxauth.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Connection Service configuration
 *
 * @author Yuriy Movchan Date: 05/10/2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectionServiceConfiguration {

	private Integer maxTotal;
	private Integer maxPerRoute;
	private Integer validateAfterInactivity;

	public Integer getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
	}

	public Integer getMaxPerRoute() {
		return maxPerRoute;
	}

	public void setMaxPerRoute(Integer maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}

	public Integer getValidateAfterInactivity() {
		return validateAfterInactivity;
	}

	public void setValidateAfterInactivity(Integer validateAfterInactivity) {
		this.validateAfterInactivity = validateAfterInactivity;
	}

}
