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
import org.yakindu.sct.arduino.generator.cpp.extensions.ArchitecturesExtension;
import org.yakindu.sct.arduino.generator.cpp.extensions.TimerElement;
import org.yakindu.sct.generator.core.library.AbstractDefaultFeatureValueProvider;
import org.yakindu.sct.model.sgen.FeatureParameterValue;
import org.yakindu.sct.model.sgen.FeatureType;
import org.yakindu.sct.model.sgen.FeatureTypeLibrary;

public class ArduinoDefaultFeatureValueProvider extends AbstractDefaultFeatureValueProvider {

	/**
	 * @see org.yakindu.sct.generator.core.features.IDefaultFeatureValueProvider#isProviderFor(org.yakindu.sct.model.sgen.FeatureTypeLibrary)
	 */
	@Override
	public boolean isProviderFor(FeatureTypeLibrary library) {
		return IArduinoFeatureConstants.LIBRARY_NAME.equals(library.getName());
	}

	/**
	 * @see org.yakindu.sct.generator.core.features.AbstractDefaultFeatureValueProvider#setDefaultValue(org.yakindu.sct.model.sgen.FeatureType,
	 *      org.yakindu.sct.model.sgen.FeatureParameterValue, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void setDefaultValue(FeatureType featureType, FeatureParameterValue parameterValue,
			EObject contextElement) {
		final String parameterName = parameterValue.getParameter().getName();
		if (IArduinoFeatureConstants.PARAM_USER_SRC_FOLDER.equals(parameterName)) {
			parameterValue.setValue(IArduinoFeatureConstants.USER_SRC_FOLDER_DEFAULT);
		} else if (IArduinoFeatureConstants.PARAM_TIMER.equals(parameterName)) {
			parameterValue.setValue("org.yakindu.sct.arduino.generator.cpp.architecture.software.counter");
		} else if (IArduinoFeatureConstants.PARAM_CYCLE_PERIOD.equals(parameterName)) {
			parameterValue.setValue(Integer.toString(IArduinoFeatureConstants.CYCLE_PERIOD_DEFAULT));
		}
	}

	/**
	 * @see org.yakindu.sct.generator.core.features.IDefaultFeatureValueProvider#validateParameterValue(org.yakindu.sct.model.sgen.FeatureParameterValue)
	 */
	@Override
	public IStatus validateParameterValue(FeatureParameterValue parameterValue) {
		final String parameterName = parameterValue.getParameter().getName();

		if (IArduinoFeatureConstants.PARAM_TIMER.equals(parameterName)) {
			if (ArchitecturesExtension.getTimer(parameterValue.getStringValue()) == null) {
				return error(String.format(Messages.ArduinoDefaultFeatureValueProvider_timerInvalid,
						parameterValue.getExpression()));
			}
		} else if (IArduinoFeatureConstants.PARAM_CYCLE_PERIOD.equals(parameterName)) {
			final FeatureParameterValue timerParamValue = parameterValue.getFeatureConfiguration()
					.getParameterValue(IArduinoFeatureConstants.PARAM_TIMER);
			final TimerElement timer = ArchitecturesExtension.getTimer(timerParamValue.getStringValue());

			try {
				final int cyclePeriod = Integer.parseInt(parameterValue.getStringValue());
				if ((timer != null) && !timer.isValidCyclePeriod(cyclePeriod)) {
					return error(String.format(Messages.ArduinoDefaultFeatureValueProvider_cyclePeriodNotInInterval,
							timer.getName(), timer.getMinCyclePeriod(), timer.getMaxCyclePeriod()));
				}
			} catch (final NumberFormatException exception) {
				return error(Messages.ArduinoDefaultFeatureValueProvider_cyclePeriodInvalid);
			}
		}

		return Status.OK_STATUS;
	}

}
