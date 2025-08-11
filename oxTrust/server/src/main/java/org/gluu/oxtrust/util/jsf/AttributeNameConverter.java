package org.gluu.oxtrust.util.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.gluu.model.GluuAttribute;
import org.gluu.service.AttributeService;

@FacesConverter("attributeNameConverter")
public class AttributeNameConverter implements Converter {

	@Inject
	private AttributeService attributeService;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}

		return attributeService.getAttributeByName(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String string = null;
		if (value instanceof GluuAttribute) {
			string = ((GluuAttribute) value).getName();
		}

		return string;
	}

}
