/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jst.web.kb.internal.taglib.jq;

import org.jboss.tools.jst.web.kb.internal.taglib.CustomTagLibAttribute;

/**
 * @author Alexey Kazakov, Daniel Azarov
 */
public class PageAttributeProvider extends JQueryMobileAttrProvider {

	private static final JQueryMobileAttribute[] requiredAttributes = new JQueryMobileAttribute[] {new DataRoleAttribute("", PAGE)};

	private static final JQueryMobileAttribute[] conditionalAttributes = new JQueryMobileAttribute[] {
		DATA_ADD_BACK_BTN_ATTRIBUTE,
		DATA_BACK_BTN_TEXT_ATTRIBUTE,
		DATA_BACK_BTN_THEME_ATTRIBUTE,
		DATA_CLOSE_BTN_TEXT_ATTRIBUTE,
		DATA_DOM_CACHE_ATTRIBUTE,
		DATA_OVERLAY_THEME_ATTRIBUTE,
		DATA_THEME_ATTRIBUTE,
		DATA_TITLE_ATTRIBUTE,
		DATA_URL_ATTRIBUTE};

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.taglib.attribute.AbstractAttributeProvider#checkComponent(org.jboss.tools.jst.web.kb.KbQuery)
	 */
	@Override
	protected boolean checkComponent() {
		return checkDataRole(PAGE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.taglib.attribute.AbstractAttributeProvider#getAttributes()
	 */
	@Override
	protected CustomTagLibAttribute[] getConditionalAttributes() {
		return conditionalAttributes;
	}

	@Override
	protected CustomTagLibAttribute[] getRequiredAttributes() {
		return requiredAttributes;
	}
}