/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jst.web.validation;

import java.util.List;
import java.util.StringTokenizer;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Viacheslav Kabanovich
 */
public class CheckResource extends Check {
	boolean acceptEmpty = true;
	String extensions = null;
	String extensionMessage = null;

	boolean canBeServlet = false;

	public CheckResource(ValidationErrorManager manager, String preference, String attr) {
		super(manager, preference, attr);
	}

	public CheckResource(ValidationErrorManager manager, String preference, String attr, boolean acceptEmpty, String extensions, String extensionMessage) {
		super(manager, preference, attr);
		this.acceptEmpty = acceptEmpty;
		this.extensions = extensions;
		this.extensionMessage = extensionMessage;
	}		

	public CheckResource acceptServlet() {
		canBeServlet = true;
		return this;
	}

	public void check(XModelObject object) {
		String value = object.getAttributeValue(attr);
		String actualValue = value;
		XModel model = object.getModel();
		XModelObject webRoot = model == null ? null : FileSystemsHelper.getWebRoot(model);
		if(webRoot == null) return;

		if(canBeServlet) {
			if(value != null && value.indexOf("?") > 0) { //$NON-NLS-1$
				value = value.substring(0, value.indexOf("?")); //$NON-NLS-1$
			}
			if(isMappedToServlet(object, value)) {
				return;
			}
		}
		
		List<Object> list = WebPromptingProvider.getInstance().getList(model, IWebPromptingProvider.JSF_CONVERT_URL_TO_PATH, value, null);
		if(list != null && !list.isEmpty()) {
			value = list.get(0).toString();
		}
		
		if(value == null || value.trim().length() == 0) {
			if(!acceptEmpty) {
				fireEmpty(object, preference, attr);
			}
			return;
		}

		XModelObject o = null;
		for (Object v: list) {
			String valuei = v.toString();
			String value2 = valuei.startsWith("/") ? valuei.substring(1) : valuei; //$NON-NLS-1$
			o = webRoot.getChildByPath(value2);
			if(o != null) {
				value = "/" + value2; //$NON-NLS-1$
				break;
			}
		}
		if(o == null) {
			String v = value;
			if(list.size() > 1) {
				v = actualValue;
			}
			fireExists(object, preference, attr, v);
		} else {
			boolean ok = checkExtensions(value);
			if(!ok && list != null) {
				for (Object v: list) {
					ok = checkExtensions(v.toString());
					if(ok) break;
				}				
			}
			if(!ok) {
				fireExtension(object, preference, attr, value);
			}
		}			
	}

	boolean checkExtensions(String value) {
		value = value.toLowerCase();
		if(extensions == null || extensions.trim().length() == 0) return true;
		StringTokenizer st = new StringTokenizer(extensions, " "); //$NON-NLS-1$
		while(st.hasMoreTokens()) {
			String ext = st.nextToken();
			if(value.endsWith(ext)) return true;
		}
		return false;
	}

	boolean isMappedToServlet(XModelObject o, String value) {
		XModelObject webxml = FileSystemsHelper.getFile(o);
		if(webxml == null) return false;
		XModelObject[] ms = WebAppHelper.getServletMappings(webxml);
		if(ms != null) for (XModelObject m: ms) {
			String url = m.getAttributeValue("url-pattern"); //$NON-NLS-1$
			if(value != null && value.equals(url)) return true;
		}
		return false;
	}

	protected void fireEmpty(XModelObject object, String id, String attr) {
		fireMessage(object, WebXMLValidatorMessages.PATH_EMPTY, attr);
	}
	protected void fireExists(XModelObject object, String id, String attr, String value) {
		fireMessage(object, WebXMLValidatorMessages.PATH_NOT_EXISTS, attr, value);
	}
	protected void fireExtension(XModelObject object, String id, String attr, String value) {
		fireMessage(object, extensionMessage, attr, value);
	}
}
