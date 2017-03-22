/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.editor.sgen.extensions;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.yakindu.sct.editor.sgen.GeneratorEntryFormPage;
import org.yakindu.sct.model.sgen.FeatureType;

public interface IFeatureConfigurationSection {

	static final char ASTERISK = '\u002A';

	void initialize(GeneratorEntryFormPage formPage, FeatureType featureType);

	Section createSection(final FormToolkit toolkit, final Composite parent);

	void modelChanged(IXtextDocument xtextDocument);

	void dispose();

}
