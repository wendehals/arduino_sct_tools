/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp.features;

public interface IArduinoFeatureConstants {
	String LIBRARY_NAME = "Arduino C++ Generator"; //$NON-NLS-1$
	String FEATURE_NAME = "Arduino"; //$NON-NLS-1$
	String PARAM_USER_SRC_FOLDER = "userSrcFolder"; //$NON-NLS-1$
	String PARAM_TIMER = "timer"; //$NON-NLS-1$
	String PARAM_CYCLE_PERIOD = "cyclePeriod"; //$NON-NLS-1$

	String USER_SRC_FOLDER_DEFAULT = "src"; //$NON-NLS-1$
	int CYCLE_PERIOD_DEFAULT = 10;
}
