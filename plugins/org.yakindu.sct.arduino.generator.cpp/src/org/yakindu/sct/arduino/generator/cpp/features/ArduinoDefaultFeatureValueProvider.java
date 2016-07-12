/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.features;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.yakindu.sct.generator.core.features.AbstractDefaultFeatureValueProvider;
import org.yakindu.sct.model.sgen.FeatureParameterValue;
import org.yakindu.sct.model.sgen.FeatureType;
import org.yakindu.sct.model.sgen.FeatureTypeLibrary;

public class ArduinoDefaultFeatureValueProvider extends AbstractDefaultFeatureValueProvider {

	@Override
	public boolean isProviderFor(FeatureTypeLibrary library) {
		return IArduinoFeatureConstants.LIBRARY_NAME.equals(library.getName());
	}

	@Override
	protected void setDefaultValue(FeatureType featureType, FeatureParameterValue parameterValue,
			EObject contextElement) {
		final String parameterName = parameterValue.getParameter().getName();
		if (IArduinoFeatureConstants.PARAM_USER_SRC_FOLDER.equals(parameterName)) {
			parameterValue.setValue("src");
		} else if (IArduinoFeatureConstants.PARAM_TIMER.equals(parameterName)) {
			parameterValue.setValue(Timer.ATMEGA168328.literal);
		}
	}

	@Override
	public IStatus validateParameterValue(FeatureParameterValue parameterValue) {
		final String parameterName = parameterValue.getParameter().getName();
		if (IArduinoFeatureConstants.PARAM_TIMER.equals(parameterName)) {
			if (Timer.getTimer(parameterValue.getStringValue()) == null) {
				return error(String.format("The timer %s does not exist.", parameterValue.getExpression()));
			}
		}

		return Status.OK_STATUS;
	}

}
