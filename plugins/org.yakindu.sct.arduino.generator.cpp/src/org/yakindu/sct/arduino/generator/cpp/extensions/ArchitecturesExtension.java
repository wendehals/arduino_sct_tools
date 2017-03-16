/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.extensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.yakindu.sct.arduino.generator.cpp.ArduinoGeneratorPlugin;
import org.yakindu.sct.arduino.generator.cpp.timers.AbstractTimer;

public class ArchitecturesExtension {

	private static final String ARCHITECTURES_EXTENSION = "architectures"; //$NON-NLS-1$

	private static final String ARCHITECTURE_ELEMENT = "architecture"; //$NON-NLS-1$

	private static final String TIMER_ELEMENT = "timer"; //$NON-NLS-1$

	private static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$

	private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

	private static final String KIND_ATTRIBUTE = "kind"; //$NON-NLS-1$

	private static final String MIN_CYCLE_PERIOD_ATTRIBUTE = "minCyclePeriod"; //$NON-NLS-1$

	private static final String MAX_CYCLE_PERIOD_ATTRIBUTE = "maxCyclePeriod"; //$NON-NLS-1$

	private static final String PRE_DEFINED_CYCLE_PERIODS_ATTRIBUTE = "preDefinedCyclePeriods"; //$NON-NLS-1$

	private static final String CPP_CODE_GENERATOR_ATTRIBUTE = "cppCodeGenerator"; //$NON-NLS-1$

	private static final String DESCRIPTION_ATTRIBUTE = "description"; //$NON-NLS-1$

	private static List<ArchitectureElement> architectures;

	/**
	 * @return a collection of model traversals
	 */
	public static Collection<ArchitectureElement> getArchitectures() {
		if (architectures == null) {
			architectures = new ArrayList<>();

			final IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
					.getExtensionPoint(ArduinoGeneratorPlugin.getPluginId(), ARCHITECTURES_EXTENSION);
			if (extensionPoint != null) {
				for (final IExtension extension : extensionPoint.getExtensions()) {
					for (final IConfigurationElement element : extension.getConfigurationElements()) {
						if (ARCHITECTURE_ELEMENT.equals(element.getName())) {
							final String id = element.getAttribute(ID_ATTRIBUTE);
							final String name = element.getAttribute(NAME_ATTRIBUTE);
							final String kind = element.getAttribute(KIND_ATTRIBUTE);
							final ArchitectureElement architecture = new ArchitectureElement(id, name, kind);

							for (final IConfigurationElement timerElement : element.getChildren(TIMER_ELEMENT)) {
								try {
									final String timerId = timerElement.getAttribute(ID_ATTRIBUTE);
									final String timerName = timerElement.getAttribute(NAME_ATTRIBUTE);
									final String timerDescription = timerElement.getAttribute(DESCRIPTION_ATTRIBUTE);
									final long minCyclePeriod = Long
											.parseLong(timerElement.getAttribute(MIN_CYCLE_PERIOD_ATTRIBUTE));

									long maxCyclePeriod = Long.MAX_VALUE;
									final String maxCyclePeriodString = timerElement
											.getAttribute(MAX_CYCLE_PERIOD_ATTRIBUTE);
									if (maxCyclePeriodString != null) {
										maxCyclePeriod = Long.parseLong(maxCyclePeriodString);
									}

									final String preDefinedCyclePeriodsString = timerElement
											.getAttribute(PRE_DEFINED_CYCLE_PERIODS_ATTRIBUTE);
									final HashSet<Integer> preDefinedCyclePeriods = new HashSet<>();
									if (preDefinedCyclePeriodsString != null) {
										try (final Scanner scanner = new Scanner(preDefinedCyclePeriodsString)
												.useDelimiter("\\s*,\\s*")) {
											while (scanner.hasNextInt()) {
												preDefinedCyclePeriods.add(scanner.nextInt());
											}
										} catch (final InputMismatchException exception) {
											ArduinoGeneratorPlugin.logError(exception);
										}
									}

									final AbstractTimer codeGenerator = (AbstractTimer) timerElement
											.createExecutableExtension(CPP_CODE_GENERATOR_ATTRIBUTE);

									architecture.addTimer(new TimerElement(architecture, timerId, timerName,
											timerDescription, minCyclePeriod, maxCyclePeriod, codeGenerator,
											preDefinedCyclePeriods));
								} catch (final CoreException | NumberFormatException exception) {
									ArduinoGeneratorPlugin.logError(exception);
								}
							}

							architectures.add(architecture);
						}
					}
				}
			}
		}

		return Collections.unmodifiableCollection(architectures);
	}

	public static TimerElement getTimer(String id) {
		for (final ArchitectureElement architecture : getArchitectures()) {
			for (final TimerElement timer : architecture.timers) {
				if (timer.getId().equals(id)) {
					return timer;
				}
			}
		}

		return null;
	}

}
