/*
 * oxTrust is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.oxtrust.action;

import java.util.List;

import javax.inject.Named;

import org.gluu.model.custom.script.CustomScriptType;
import org.gluu.model.custom.script.model.CustomScript;

/**
 * Add/Modify custom script configurations
 * 
 * @author Yuriy Movchan Date: 12/29/2014
 */
@Named("manageAuthScriptAction")
public class ManageAuthScriptAction extends ManageOtherCustomScriptAction {

	private static final long serialVersionUID = 8010364513892715321L;

	public CustomScriptType[] getScriptType() {
		return new CustomScriptType[] { CustomScriptType.PERSON_AUTHENTICATION };
	}

	public List<CustomScript> getAuthCustomScripts() {
		return customScriptsByTypes.get(CustomScriptType.PERSON_AUTHENTICATION );
	}

	public CustomScriptType getAuthScriptType() {
		return CustomScriptType.PERSON_AUTHENTICATION;
	}
	
	public void addAuthCustomScript() {
		addCustomScript(CustomScriptType.PERSON_AUTHENTICATION);
	}
}
