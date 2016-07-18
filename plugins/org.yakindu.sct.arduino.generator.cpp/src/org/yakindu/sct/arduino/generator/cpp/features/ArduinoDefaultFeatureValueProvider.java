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
	protected void setDefaultValue(FeatureType featureType, FeatureParameterValue parameterValue, EObject contextElement) {
		final String parameterName = parameterValue.getParameter().getName();
		if (IArduinoFeatureConstants.PARAM_USER_SRC_FOLDER.equals(parameterName)) {
			parameterValue.setValue(IArduinoFeatureConstants.USER_SRC_FOLDER_DEFAULT);
		} else if (IArduinoFeatureConstants.PARAM_TIMER.equals(parameterName)) {
			parameterValue.setValue(Timer.ATMEGA168328.literal);
		} else if (IArduinoFeatureConstants.PARAM_CYCLE_PERIOD.equals(parameterName)) {
			parameterValue.setValue(Integer.toString(IArduinoFeatureConstants.CYCLE_PERIOD_DEFAULT));
		}
	}

	@Override
	public IStatus validateParameterValue(FeatureParameterValue parameterValue) {
		final String parameterName = parameterValue.getParameter().getName();

		if (IArduinoFeatureConstants.PARAM_TIMER.equals(parameterName)) {
			if (Timer.getTimer(parameterValue.getStringValue()) == null) {
				return error(String.format(Messages.ArduinoDefaultFeatureValueProvider_timerInvalid,
						parameterValue.getExpression()));
			}
		} else if (IArduinoFeatureConstants.PARAM_CYCLE_PERIOD.equals(parameterName)) {
			final FeatureParameterValue timerParamValue = parameterValue.getFeatureConfiguration().getParameterValue(
					IArduinoFeatureConstants.PARAM_TIMER);
			final Timer timer = Timer.getTimer(timerParamValue.getStringValue());

			try {
				final int cyclePeriod = Integer.parseInt(parameterValue.getStringValue());
				if ((timer != null) && !timer.isValidCyclePeriod(cyclePeriod)) {
					return error(String.format(Messages.ArduinoDefaultFeatureValueProvider_cyclePeriodNotInInterval,
							timer.title, timer.min, timer.max));
				}
			} catch (final NumberFormatException exception) {
				return error(Messages.ArduinoDefaultFeatureValueProvider_cyclePeriodInvalid);
			}
		}

		return Status.OK_STATUS;
	}
}
