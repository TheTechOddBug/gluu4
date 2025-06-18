/*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.gluu.persist.model;

/**
 * DB Password Attribute
 *
 * @author Yuriy Movchan Date: 0612/2025
 */
public class PasswordAttributeData extends AttributeData {

	private boolean skipHashed;

	public PasswordAttributeData(AttributeData attributeData, boolean skipHashed) {
		super(attributeData.getName(), attributeData.getValues(), attributeData.getMultiValued());
		this.skipHashed = skipHashed;
	}

	public boolean isSkipHashed() {
		return skipHashed;
	}

	public void setSkipHashed(boolean skipHashed) {
		this.skipHashed = skipHashed;
	}

}