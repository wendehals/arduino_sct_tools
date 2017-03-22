/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.editor.sgen;

import org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection;
import org.yakindu.sct.model.sgen.FeatureType;

public abstract class AbstractFeatureConfigurationSection implements IFeatureConfigurationSection {

	protected static final String CAMEL_CASE_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"; //$NON-NLS-1$

	private GeneratorEntryFormPage formPage;

	private FeatureType featureType;

	/**
	 * @see org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection#initialize(org.yakindu.sct.editor.sgen.GeneratorEntryFormPage,
	 *      org.yakindu.sct.model.sgen.FeatureType)
	 */
	@Override
	public void initialize(final GeneratorEntryFormPage formPage, final FeatureType featureType) {
		this.formPage = formPage;
		this.featureType = featureType;
	}

	/**
	 * @see org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection#dispose()
	 */
	@Override
	public void dispose() {
		// nothing to do
	}

	protected GeneratorEntryFormPage getFormPage() {
		return this.formPage;
	}

	protected FeatureType getFeatureType() {
		return this.featureType;
	}

	protected static String convertCamelCaseName(final String name, final boolean mandatory) {
		final StringBuilder builder = new StringBuilder();
		final String[] splitName = name.split(CAMEL_CASE_PATTERN);

		builder.append(splitName[0].substring(0, 1).toUpperCase());
		if (splitName[0].length() > 1) {
			builder.append(splitName[0].substring(1));
		}

		for (int i = 1; i < splitName.length; i++) {
			builder.append(" ").append(splitName[i]); //$NON-NLS-1$
		}

		if (mandatory) {
			builder.append(ASTERISK);
		}

		return builder.toString();
	}

}
