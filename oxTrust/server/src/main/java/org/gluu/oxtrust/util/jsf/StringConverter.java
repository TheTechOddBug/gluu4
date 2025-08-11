/*
 * oxTrust is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2025, Gluu
 */

package org.gluu.oxtrust.util.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("stringConverter")
public class StringConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String val) {
		return val;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object val) {
		return (String) val;
	}

}
