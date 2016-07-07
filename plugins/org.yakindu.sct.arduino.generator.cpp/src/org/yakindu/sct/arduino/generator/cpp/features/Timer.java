package org.yakindu.sct.arduino.generator.cpp.features;

public enum Timer {

	SOFTWARE("software", "Software", "Software Timer that uses millies() to run the cycle."),

	ATMEGA168328("atmega168_328", "ATmega 168/328",
			"Hardware Timer of ATmega 168/328 microprocessor, uses timer 1 of the microprocessor and puts it to sleep beetween cycles.");

	public final String literal;
	public final String title;
	public final String description;

	private Timer(String literal, String title, String description) {
		this.literal = literal;
		this.title = title;
		this.description = description;
	}

	public static Timer getTimer(String literal) {
		for (final Timer timer : values()) {
			if (timer.literal.equals(literal)) {
				return timer;
			}
		}
		return null;
	}

}
