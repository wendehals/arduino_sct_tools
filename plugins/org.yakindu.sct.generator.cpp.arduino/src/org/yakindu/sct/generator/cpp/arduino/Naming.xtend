package org.yakindu.sct.generator.cpp.arduino

class Naming extends org.yakindu.sct.generator.cpp.Naming {

	def main() {
		"Main"
	}

	def timeEvent() {
		"TimeEvent"
	}

	def abstractTimer() {
		"AbstractTimer"
	}

	def atMega168_328Timer() {
		"ATmega168_328Timer"
	}

	def hardwareConnector() {
		"Hardware".connector
	}

	def connector(String it) {
		it + "Connector"
	}
}
