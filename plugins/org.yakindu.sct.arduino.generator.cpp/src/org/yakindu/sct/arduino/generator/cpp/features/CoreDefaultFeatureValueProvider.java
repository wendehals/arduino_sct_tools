package org.yakindu.sct.arduino.generator.cpp.features;

import static org.yakindu.sct.generator.core.features.ICoreFeatureConstants.OUTLET_FEATURE_LIBRARY_TARGET_FOLDER;
import static org.yakindu.sct.generator.core.features.ICoreFeatureConstants.OUTLET_FEATURE_TARGET_FOLDER;

import org.eclipse.emf.ecore.EObject;
import org.yakindu.sct.generator.core.features.impl.CoreLibraryDefaultFeatureValueProvider;
import org.yakindu.sct.model.sgen.FeatureParameterValue;
import org.yakindu.sct.model.sgen.FeatureType;

public class CoreDefaultFeatureValueProvider extends CoreLibraryDefaultFeatureValueProvider {

	@Override
	protected void setDefaultValue(FeatureType featureType, FeatureParameterValue parameterValue,
			EObject contextElement) {
		super.setDefaultValue(featureType, parameterValue, contextElement);

		final String parameterName = parameterValue.getParameter().getName();
		if (OUTLET_FEATURE_TARGET_FOLDER.equals(parameterName)) {
			parameterValue.setValue("src-gen");
		} else if (OUTLET_FEATURE_LIBRARY_TARGET_FOLDER.equals(parameterName)) {
			parameterValue.setValue("");
		}
	}

}
