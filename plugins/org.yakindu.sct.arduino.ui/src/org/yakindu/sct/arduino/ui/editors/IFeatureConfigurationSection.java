/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

public interface IFeatureConfigurationSection {

	Section createSection(final FormToolkit toolkit, final Composite parent);

	void initialize(IXtextDocument xtextDocument);

	void modelChanged(IXtextDocument xtextDocument);

	void dispose();

}
