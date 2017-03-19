/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.resource.XtextResource;
import org.yakindu.sct.generator.core.extensions.IGeneratorDescriptor;
import org.yakindu.sct.generator.core.extensions.ILibraryDescriptor;
import org.yakindu.sct.generator.core.extensions.LibraryExtensions;
import org.yakindu.sct.model.sgen.FeatureConfiguration;
import org.yakindu.sct.model.sgen.FeatureParameter;
import org.yakindu.sct.model.sgen.FeatureParameterValue;
import org.yakindu.sct.model.sgen.FeatureType;
import org.yakindu.sct.model.sgen.FeatureTypeLibrary;
import org.yakindu.sct.model.sgen.GeneratorEntry;
import org.yakindu.sct.model.sgen.GeneratorModel;
import org.yakindu.sct.model.sgen.SGenFactory;
import org.yakindu.sct.model.sgraph.Statechart;

import com.google.common.collect.Lists;

public class SGenModelUtil {

	public static List<FeatureType> getFeatureTypes(final IGeneratorDescriptor descriptor) {
		final ArrayList<FeatureType> featureTypes = Lists.newArrayList();
		final Iterable<ILibraryDescriptor> libraryDescriptors = LibraryExtensions
				.getLibraryDescriptors(descriptor.getLibraryIDs());
		for (final ILibraryDescriptor libraryDescriptor : libraryDescriptors) {
			final ResourceSet set = new ResourceSetImpl();
			final Resource resource = set.getResource(libraryDescriptor.getURI(), true);
			final FeatureTypeLibrary featureTypeLibrary = (FeatureTypeLibrary) resource.getContents().get(0);
			featureTypes.addAll(featureTypeLibrary.getTypes());
		}

		return featureTypes;
	}

	public static GeneratorEntry getGeneratorEntry(final XtextResource resource, final String name) {
		final GeneratorModel generatorModel = (GeneratorModel) resource.getContents().get(0);
		for (final GeneratorEntry generatorEntry : generatorModel.getEntries()) {
			if (generatorEntry.getElementRef() instanceof Statechart) {
				final Statechart statechart = (Statechart) generatorEntry.getElementRef();
				if (name.equals(statechart.getName())) {
					return generatorEntry;
				}
			}
		}

		return null;
	}

	public static FeatureConfiguration getFeatureConfiguration(final XtextResource resource,
			final String statechartName, final FeatureType featureType) {
		final GeneratorEntry generatorEntry = getGeneratorEntry(resource, statechartName);
		if (generatorEntry != null) {
			return generatorEntry.getFeatureConfiguration(featureType.getName());
		}

		return null;
	}

	public static FeatureConfiguration createFeatureConfiguration(final XtextResource resource,
			final String statechartName, final FeatureType featureType) {
		final GeneratorEntry generatorEntry = getGeneratorEntry(resource, statechartName);
		if (generatorEntry != null) {
			final FeatureConfiguration featureConfiguration = SGenFactory.eINSTANCE.createFeatureConfiguration();
			featureConfiguration.setType(featureType);
			generatorEntry.getFeatures().add(featureConfiguration);

			return featureConfiguration;
		}

		return null;
	}

	public static Collection<FeatureConfiguration> getFeatureConfigurations(final XtextResource resource,
			final String statechartName) {
		final GeneratorEntry generatorEntry = getGeneratorEntry(resource, statechartName);
		if (generatorEntry != null) {
			return generatorEntry.getFeatures();
		}

		return Collections.emptyList();
	}

	public static FeatureParameterValue createFeatureParameterValue(final XtextResource resource,
			final FeatureConfiguration featureConfiguration, final FeatureParameter parameter) {
		final FeatureParameterValue parameterValue = SGenFactory.eINSTANCE.createFeatureParameterValue();
		parameterValue.setParameter(parameter);
		featureConfiguration.getParameterValues().add(parameterValue);

		return parameterValue;
	}

	public static String getStringParameterValue(final XtextResource resource, final String statechartName,
			final FeatureParameter parameter) {
		final FeatureConfiguration featureConfiguration = getFeatureConfiguration(resource, statechartName,
				parameter.getFeatureType());
		if (featureConfiguration != null) {
			final FeatureParameterValue parameterValue = featureConfiguration.getParameterValue(parameter.getName());
			if (parameterValue != null) {
				return parameterValue.getStringValue();
			}
		}

		return ""; //$NON-NLS-1$
	}

	public static boolean getBooleanParameterValue(final XtextResource resource, final String statechartName,
			final FeatureParameter parameter) {
		final FeatureConfiguration featureConfiguration = getFeatureConfiguration(resource, statechartName,
				parameter.getFeatureType());
		if (featureConfiguration != null) {
			final FeatureParameterValue parameterValue = featureConfiguration.getParameterValue(parameter.getName());
			if (parameterValue != null) {
				return Boolean.valueOf(parameterValue.getBooleanValue());
			}
		}

		return Boolean.FALSE;
	}

}
