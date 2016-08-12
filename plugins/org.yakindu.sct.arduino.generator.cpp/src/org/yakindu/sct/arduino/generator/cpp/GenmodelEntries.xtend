/**
 * Copyright (c) 2016 by Lothar Wendehals.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp

import com.google.inject.Inject
import org.yakindu.sct.arduino.generator.cpp.features.IArduinoFeatureConstants
import org.yakindu.sct.arduino.generator.cpp.timers.Architectures
import org.yakindu.sct.arduino.generator.cpp.timers.Timer
import org.yakindu.sct.generator.core.library.IOutletFeatureHelper
import org.yakindu.sct.model.sgen.GeneratorEntry

class GenmodelEntries extends org.yakindu.sct.generator.c.GenmodelEntries {

	@Inject extension IOutletFeatureHelper outletFeatureHelper

	def private getGeneratorOptionsFeature(GeneratorEntry it) {
		getFeatureConfiguration(IArduinoFeatureConstants::FEATURE_NAME)
	}

	def getUserSrcFolder(GeneratorEntry it) {
		generatorOptionsFeature?.getParameterValue(IArduinoFeatureConstants::PARAM_USER_SRC_FOLDER).stringValue
	}

	def getSrcGenFolder(GeneratorEntry it) {
		outletFeatureHelper.getTargetFolderValue(it).stringValue
	}

	def Timer getTimer(GeneratorEntry it) {
		val timerId = generatorOptionsFeature?.getParameterValue(IArduinoFeatureConstants::PARAM_TIMER).stringValue
		Architectures.getTimer(timerId)
	}

	def cyclePeriod(GeneratorEntry it) {
		val paramValue = generatorOptionsFeature?.getParameterValue(IArduinoFeatureConstants::PARAM_CYCLE_PERIOD)
		if (paramValue != null) {
			try {
				return Integer.parseInt(paramValue.stringValue);
			} catch (NumberFormatException exception) {
			}
		}

		return IArduinoFeatureConstants::CYCLE_PERIOD_DEFAULT
	}

	def srcGenFolderRelativeToUserSrc(GeneratorEntry it) {
		folderRelativeToOther(srcGenFolder, userSrcFolder)
	}

	def userSrcFolderRelativeToSrcGen(GeneratorEntry it) {
		folderRelativeToOther(userSrcFolder, srcGenFolder)
	}

	def folderRelativeToOther(String folder, String otherFolder) {
		if (otherFolder.equals(folder)) {
			""
		} else if (otherFolder.empty) {
			folder + "/"
		} else if (folder.empty) {
			"../"
		} else {
			"../" + folder + "/"
		}
	}

}
