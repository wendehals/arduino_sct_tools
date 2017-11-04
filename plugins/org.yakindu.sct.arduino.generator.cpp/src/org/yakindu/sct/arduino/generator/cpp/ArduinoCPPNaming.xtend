/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp

import org.yakindu.sct.generator.cpp.CppNaming

class ArduinoCPPNaming extends CppNaming {

	def arduinoMain() {
		"ArduinoMain"
	}

	def timeEvent() {
		"TimeEvent"
	}

	def hardwareConnector() {
		"Hardware".connector
	}

	def connector(String it) {
		it + "Connector"
	}

}
