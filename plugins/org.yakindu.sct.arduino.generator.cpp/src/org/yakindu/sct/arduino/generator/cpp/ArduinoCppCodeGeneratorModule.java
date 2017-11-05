/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.generator.cpp;

import org.yakindu.base.types.inferrer.ITypeSystemInferrer;
import org.yakindu.sct.generator.c.CExpressionsGenerator;
import org.yakindu.sct.generator.c.extensions.Naming;
import org.yakindu.sct.generator.c.types.CTypeSystemAccess;
import org.yakindu.sct.generator.core.IExecutionFlowGenerator;
import org.yakindu.sct.generator.core.IGeneratorModule;
import org.yakindu.sct.generator.core.types.ICodegenTypeSystemAccess;
import org.yakindu.sct.generator.cpp.CppExpressionsGenerator;
import org.yakindu.sct.generator.cpp.CppNaming;
import org.yakindu.sct.generator.cpp.CppNamingService;
import org.yakindu.sct.model.sexec.naming.INamingService;
import org.yakindu.sct.model.sgen.GeneratorEntry;
import org.yakindu.sct.model.stext.inferrer.STextTypeInferrer;

import com.google.inject.Binder;

public class ArduinoCppCodeGeneratorModule implements IGeneratorModule {

	/**
	 * @see org.yakindu.sct.generator.core.GeneratorModule#configure(org.yakindu.sct.model.sgen.GeneratorEntry,
	 *      com.google.inject.Binder)
	 */
	@Override
	public void configure(final GeneratorEntry entry, final Binder binder) {
		binder.bind(GeneratorEntry.class).toInstance(entry);
		binder.bind(IExecutionFlowGenerator.class).to(ArduinoCodeGenerator.class);
		binder.bind(ICodegenTypeSystemAccess.class).to(CTypeSystemAccess.class);
		binder.bind(INamingService.class).to(CppNamingService.class);
		binder.bind(ITypeSystemInferrer.class).to(STextTypeInferrer.class);
		binder.bind(Naming.class).to(CppNaming.class);
		binder.bind(CExpressionsGenerator.class).to(CppExpressionsGenerator.class);
	}

}
