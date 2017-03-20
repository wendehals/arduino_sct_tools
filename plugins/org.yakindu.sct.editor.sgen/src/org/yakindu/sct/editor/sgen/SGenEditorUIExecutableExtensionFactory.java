/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.editor.sgen;

import org.osgi.framework.Bundle;
import org.yakindu.sct.generator.genmodel.ui.SGenExecutableExtensionFactory;

public class SGenEditorUIExecutableExtensionFactory extends SGenExecutableExtensionFactory {

	/**
	 * @see org.yakindu.sct.generator.genmodel.ui.SGenExecutableExtensionFactory#getBundle()
	 */
	@Override
	protected Bundle getBundle() {
		return SGenEditorUIPlugin.getDefault().getBundle();
	}

}
