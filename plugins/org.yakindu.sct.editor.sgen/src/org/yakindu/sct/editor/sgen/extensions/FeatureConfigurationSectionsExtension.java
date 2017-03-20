/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.editor.sgen.extensions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.yakindu.sct.editor.sgen.SGenEditorUIPlugin;
import org.yakindu.sct.model.sgen.FeatureType;

public class FeatureConfigurationSectionsExtension {

	private static final String FEATURE_CONFIGURATION_SECTIONS_EXTENSION = "featureConfigurationSections"; //$NON-NLS-1$

	private static final String FEATURE_CONFIGURATION_SECTION_ELEMENT = "featureConfigurationSection"; //$NON-NLS-1$

	private static final String FEATURE_TYPE_ATTRIBUTE = "featureType"; //$NON-NLS-1$

	private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	private static Map<String, IFeatureConfigurationSection> featureConfigurationSections;

	/**
	 * @return a mapping of {@link FeatureType} names to {@link IFeatureConfigurationSection}s.
	 */
	public static Map<String, IFeatureConfigurationSection> getFeatureConfigurationSections() {
		if (featureConfigurationSections == null) {
			featureConfigurationSections = new HashMap<>();

			final IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(SGenEditorUIPlugin.getPluginId(), FEATURE_CONFIGURATION_SECTIONS_EXTENSION);
			if (extensionPoint != null) {
				for (final IExtension extension : extensionPoint.getExtensions()) {
					for (final IConfigurationElement element : extension.getConfigurationElements()) {
						if (FEATURE_CONFIGURATION_SECTION_ELEMENT.equals(element.getName())) {
							final String featureType = element.getAttribute(FEATURE_TYPE_ATTRIBUTE);

							try {
								final IFeatureConfigurationSection featureConfigurationSection = (IFeatureConfigurationSection) element
										.createExecutableExtension(CLASS_ATTRIBUTE);
								featureConfigurationSections.put(featureType, featureConfigurationSection);
							} catch (final CoreException exception) {
								SGenEditorUIPlugin.logError(exception);
							}
						}
					}
				}
			}
		}

		return Collections.unmodifiableMap(featureConfigurationSections);
	}

}
