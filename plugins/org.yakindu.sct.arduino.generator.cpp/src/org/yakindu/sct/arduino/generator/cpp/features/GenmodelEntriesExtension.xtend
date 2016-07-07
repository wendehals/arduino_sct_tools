package org.yakindu.sct.arduino.generator.cpp.features

import org.yakindu.sct.generator.c.GenmodelEntries
import org.yakindu.sct.model.sgen.GeneratorEntry

class GenmodelEntriesExtension extends GenmodelEntries {

	def private getGeneratorOptionsFeature(GeneratorEntry it) {
		getFeatureConfiguration(IArduinoFeatureConstants::FEATURE_NAME)
	}

	def getUserSrcFolder(GeneratorEntry it) {
		generatorOptionsFeature?.getParameterValue(IArduinoFeatureConstants::PARAM_USER_SRC_FOLDER).stringValue
	}

	def boolean isSoftwareTimer(GeneratorEntry it){
		Timer::SOFTWARE.literal.equals(getTimer)
	}

	def boolean isATmega168_328Timer(GeneratorEntry it){
		Timer::ATMEGA168328.literal.equals(getTimer)
	}
	
	def getTimer(GeneratorEntry it) {
		generatorOptionsFeature?.getParameterValue(IArduinoFeatureConstants::PARAM_TIMER).stringValue
	}

}
