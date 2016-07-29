/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.timers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.yakindu.sct.arduino.generator.cpp.ArduinoGeneratorPlugin;

public class Architectures {

	private static final String ARCHITECTURES_EXTENSION = "architectures"; //$NON-NLS-1$

	private static final String ARCHITECTURE_ELEMENT = "architecture"; //$NON-NLS-1$

	private static final String TIMER_ELEMENT = "timer"; //$NON-NLS-1$

	private static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$

	private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

	private static final String MIN_CYCLE_PERIOD_ATTRIBUTE = "minCyclePeriod"; //$NON-NLS-1$

	private static final String MAX_CYCLE_PERIOD_ATTRIBUTE = "maxCyclePeriod"; //$NON-NLS-1$

	private static final String CPP_CODE_GENERATOR_ATTRIBUTE = "cppCodeGenerator"; //$NON-NLS-1$

	private static final String DESCRIPTION_ATTRIBUTE = "description"; //$NON-NLS-1$

	private static List<Architecture> architectures;

	/**
	 * @return a collection of model traversals
	 */
	public static Collection<Architecture> getArchitectures() {
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
							final Architecture architecture = new Architecture(id, name);

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

									final AbstractTimerCodeGenerator codeGenerator = (AbstractTimerCodeGenerator) timerElement
											.createExecutableExtension(CPP_CODE_GENERATOR_ATTRIBUTE);

									architecture.addTimer(new Timer(timerId, timerName, timerDescription,
											minCyclePeriod, maxCyclePeriod, codeGenerator));
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

	public static Timer getTimer(String id) {
		for (final Architecture architecture : getArchitectures()) {
			for (final Timer timer : architecture.timers) {
				if (timer.getId().equals(id)) {
					return timer;
				}
			}
		}

		return null;
	}

}
