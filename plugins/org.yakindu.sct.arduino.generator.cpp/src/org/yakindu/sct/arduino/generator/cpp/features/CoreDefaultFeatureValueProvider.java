/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.features;

import org.eclipse.emf.ecore.EObject;
import org.yakindu.sct.generator.core.library.ICoreLibraryConstants;
import org.yakindu.sct.generator.core.library.impl.CoreLibraryDefaultFeatureValueProvider;
import org.yakindu.sct.model.sgen.FeatureParameterValue;
import org.yakindu.sct.model.sgen.FeatureType;

public class CoreDefaultFeatureValueProvider extends CoreLibraryDefaultFeatureValueProvider {

	/**
	 * @see org.yakindu.sct.generator.core.features.impl.CoreLibraryDefaultFeatureValueProvider#setDefaultValue(org.yakindu.sct.model.sgen.FeatureType,
	 *      org.yakindu.sct.model.sgen.FeatureParameterValue, org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected void setDefaultValue(FeatureType featureType, FeatureParameterValue parameterValue,
			EObject contextElement) {
		super.setDefaultValue(featureType, parameterValue, contextElement);

		final String parameterName = parameterValue.getParameter().getName();
		if (ICoreLibraryConstants.OUTLET_FEATURE_TARGET_FOLDER.equals(parameterName)) {
			parameterValue.setValue("src-gen");
		} else if (ICoreLibraryConstants.OUTLET_FEATURE_LIBRARY_TARGET_FOLDER.equals(parameterName)) {
			parameterValue.setValue("");
		}
	}

}
