package org.yakindu.sct.arduino.generator.cpp

import org.yakindu.sct.generator.c.GenmodelEntries
import org.yakindu.sct.model.sgen.GeneratorEntry
import org.yakindu.sct.arduino.generator.cpp.features.IArduinoFeatureConstants
import org.yakindu.sct.arduino.generator.cpp.features.Timer
import com.google.inject.Inject
import org.yakindu.sct.generator.core.library.IOutletFeatureHelper

class GenmodelEntriesExtension extends GenmodelEntries {

	@Inject extension IOutletFeatureHelper outletFeatureHelper

	def private getGeneratorOptionsFeature(GeneratorEntry it) {
		getFeatureConfiguration(IArduinoFeatureConstants::FEATURE_NAME)
	}

	def getUserSrcFolder(GeneratorEntry it) {
		generatorOptionsFeature?.getParameterValue(IArduinoFeatureConstants::PARAM_USER_SRC_FOLDER).stringValue
	}
	
	def getSrcGenFolder(GeneratorEntry it){
		outletFeatureHelper.getTargetFolderValue(it).stringValue
	}

	def boolean isSoftwareTimer(GeneratorEntry it) {
		Timer::SOFTWARE.literal.equals(getTimer)
	}

	def boolean isATmega168_328Timer(GeneratorEntry it) {
		Timer::ATMEGA168328.literal.equals(getTimer)
	}

	def getTimer(GeneratorEntry it) {
		generatorOptionsFeature?.getParameterValue(IArduinoFeatureConstants::PARAM_TIMER).stringValue
	}

	def srcGenFolderRelativeToUserSrc(GeneratorEntry it){
		folderRelativeToOther(srcGenFolder, userSrcFolder)
	}

	def userSrcFolderRelativeToSrcGen(GeneratorEntry it){
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
