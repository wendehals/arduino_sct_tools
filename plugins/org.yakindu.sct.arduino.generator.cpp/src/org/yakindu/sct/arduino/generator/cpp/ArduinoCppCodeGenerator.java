/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp;

import org.eclipse.xtext.generator.OutputConfiguration;
import org.yakindu.sct.arduino.generator.cpp.features.IArduinoFeatureConstants;
import org.yakindu.sct.generator.core.execution.SExecGeneratorEntryExecutor;
import org.yakindu.sct.generator.core.filesystem.ISCTFileSystemAccess;
import org.yakindu.sct.model.sgen.FeatureConfiguration;
import org.yakindu.sct.model.sgen.GeneratorEntry;

public class ArduinoCppCodeGenerator extends SExecGeneratorEntryExecutor {

	/**
	 * @see org.yakindu.sct.generator.core.execution.AbstractGeneratorEntryExecutor#execute(org.yakindu.sct.model.sgen.GeneratorEntry)
	 */
	@Override
	public void execute(GeneratorEntry entry) {
		execute(getFileSystemAccess(entry), entry);
	}

	private ISCTFileSystemAccess getFileSystemAccess(GeneratorEntry entry) {
		final ISCTFileSystemAccess fileSystemAccess = this.factory.create(entry);

		final FeatureConfiguration featureConfiguration = entry
				.getFeatureConfiguration(IArduinoFeatureConstants.FEATURE_NAME);
		if (featureConfiguration != null) {
			final String userSrcFolder = featureConfiguration
					.getParameterValue(IArduinoFeatureConstants.PARAM_USER_SRC_FOLDER).getStringValue();
			fileSystemAccess.setOutputPath(IArduinoFeatureConstants.PARAM_USER_SRC_FOLDER, userSrcFolder);

			final OutputConfiguration outputConfiguration = fileSystemAccess.getOutputConfigurations()
					.get(IArduinoFeatureConstants.PARAM_USER_SRC_FOLDER);
			outputConfiguration.setCreateOutputDirectory(true);
			outputConfiguration.setOverrideExistingResources(false);
			outputConfiguration.setSetDerivedProperty(false);
		}

		return fileSystemAccess;
	}

}
