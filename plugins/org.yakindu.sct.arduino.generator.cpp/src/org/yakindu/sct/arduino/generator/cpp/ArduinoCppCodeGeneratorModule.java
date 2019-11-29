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
import org.yakindu.sct.generator.c.IncludeProvider;
import org.yakindu.sct.generator.c.ScTypesIncludeProvider;
import org.yakindu.sct.generator.c.extensions.GenmodelEntries;
import org.yakindu.sct.generator.c.extensions.Naming;
import org.yakindu.sct.generator.c.types.CTypeSystemAccess;
import org.yakindu.sct.generator.core.IExecutionFlowGenerator;
import org.yakindu.sct.generator.core.IGeneratorModule;
import org.yakindu.sct.generator.core.extensions.AnnotationExtensions;
import org.yakindu.sct.generator.core.submodules.lifecycle.Enter;
import org.yakindu.sct.generator.core.submodules.lifecycle.Exit;
import org.yakindu.sct.generator.core.submodules.lifecycle.Init;
import org.yakindu.sct.generator.core.submodules.lifecycle.IsActive;
import org.yakindu.sct.generator.core.submodules.lifecycle.IsFinal;
import org.yakindu.sct.generator.core.submodules.lifecycle.IsStateActive;
import org.yakindu.sct.generator.core.submodules.lifecycle.RunCycle;
import org.yakindu.sct.generator.core.types.ICodegenTypeSystemAccess;
import org.yakindu.sct.generator.cpp.CppExpressionsGenerator;
import org.yakindu.sct.generator.cpp.CppInterfaceIncludeProvider;
import org.yakindu.sct.generator.cpp.CppNaming;
import org.yakindu.sct.generator.cpp.CppNamingService;
import org.yakindu.sct.generator.cpp.files.StatemachineHeader;
import org.yakindu.sct.generator.cpp.files.StatemachineImplementation;
import org.yakindu.sct.generator.cpp.providers.ConstantsProvider;
import org.yakindu.sct.generator.cpp.providers.ConstructorProvider;
import org.yakindu.sct.generator.cpp.providers.DefaultFunctionProvider;
import org.yakindu.sct.generator.cpp.providers.ISourceFragment;
import org.yakindu.sct.generator.cpp.providers.OCBDestructorProvider;
import org.yakindu.sct.generator.cpp.providers.StatemachineClassDeclaration;
import org.yakindu.sct.generator.cpp.providers.StatevectorDefineProvider;
import org.yakindu.sct.generator.cpp.providers.classdecl.InnerClassesProvider;
import org.yakindu.sct.generator.cpp.providers.classdecl.PublicClassMemberProvider;
import org.yakindu.sct.generator.cpp.providers.eventdriven.EventDrivenConstructorProvider;
import org.yakindu.sct.generator.cpp.providers.eventdriven.StatechartEventImpl;
import org.yakindu.sct.generator.cpp.providers.eventdriven.StatechartEvents;
import org.yakindu.sct.generator.cpp.providers.eventdriven.UsingNamespaceProvider;
import org.yakindu.sct.generator.cpp.providers.eventdriven.classdecl.QueueMemberProvider;
import org.yakindu.sct.generator.cpp.submodules.lifecycle.LifecycleFunctions;
import org.yakindu.sct.model.sexec.naming.INamingService;
import org.yakindu.sct.model.sexec.transformation.BehaviorMapping;
import org.yakindu.sct.model.sexec.transformation.IModelSequencer;
import org.yakindu.sct.model.sexec.transformation.ng.ModelSequencer;
import org.yakindu.sct.model.sgen.GeneratorEntry;
import org.yakindu.sct.model.stext.inferrer.STextTypeInferrer;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public class ArduinoCppCodeGeneratorModule implements IGeneratorModule {

	public static final String CLASS_PUBLIC_TARGET = "Header.Class.Public";
	public static final String CLASS_PROTECTED_TARGET = "Header.Class.Protected";
	public static final String CLASS_PRIVATE_TARGET = "Header.Class.Private";
	public static final String CLASS_INNER_TARGET = "Header.Class.InnerClasses";

	public static final String STATEMACHINE_IMPL_TARGET = StatemachineImplementation.class.getSimpleName();
	public static final String STATEMACHINE_HEADER_TARGET = StatemachineHeader.class.getSimpleName();

	/**
	 * @see org.yakindu.sct.generator.core.GeneratorModule#configure(org.yakindu.sct.model.sgen.GeneratorEntry,
	 *      com.google.inject.Binder)
	 */
	@Override
	public void configure(final GeneratorEntry entry, final Binder binder) {
		bindFragments(binder, entry);

		binder.bind(IModelSequencer.class).to(ModelSequencer.class);
		binder.bind(BehaviorMapping.class).to(org.yakindu.sct.model.sexec.transformation.ng.BehaviorMapping.class);
		binder.bind(GeneratorEntry.class).toInstance(entry);
		binder.bind(String.class).annotatedWith(Names.named("Separator")).toInstance(getSeparator(entry));
		binder.bind(IExecutionFlowGenerator.class).to(ArduinoCodeGenerator.class);
		binder.bind(ICodegenTypeSystemAccess.class).to(CTypeSystemAccess.class);
		binder.bind(INamingService.class).to(CppNamingService.class);
		binder.bind(ITypeSystemInferrer.class).to(STextTypeInferrer.class);
		binder.bind(Naming.class).to(CppNaming.class);
		binder.bind(CExpressionsGenerator.class).to(CppExpressionsGenerator.class);

		bindCycleBasedClasses(binder);
		bindDefaultClasses(binder);

		addIncludeProvider(binder, ScTypesIncludeProvider.class);
		addIncludeProvider(binder, CppInterfaceIncludeProvider.class);
	}

	protected void bindFragments(final Binder binder, final GeneratorEntry entry) {
		bindFragment(binder, ConstantsProvider.class, STATEMACHINE_IMPL_TARGET);
		bindFragment(binder, getConstructorProvider(entry), STATEMACHINE_IMPL_TARGET);

		if ((new AnnotationExtensions()).isEventDriven(entry)) {
			bindFragment(binder, UsingNamespaceProvider.class, STATEMACHINE_IMPL_TARGET);
			bindFragment(binder, QueueMemberProvider.class, CLASS_PRIVATE_TARGET);
			bindFragment(binder, StatechartEventImpl.class, STATEMACHINE_IMPL_TARGET);
			bindFragment(binder, StatechartEvents.class, STATEMACHINE_HEADER_TARGET);
		}
		bindFragment(binder, DefaultFunctionProvider.class, STATEMACHINE_IMPL_TARGET);
		bindFragment(binder, StatevectorDefineProvider.class, STATEMACHINE_HEADER_TARGET);
		bindFragment(binder, StatemachineClassDeclaration.class, STATEMACHINE_HEADER_TARGET);
		bindFragment(binder, OCBDestructorProvider.class, STATEMACHINE_HEADER_TARGET);
		bindFragment(binder, InnerClassesProvider.class, CLASS_INNER_TARGET);
		bindFragment(binder, PublicClassMemberProvider.class, CLASS_PUBLIC_TARGET);
	}

	protected void bindFragment(final Binder binder, final Class<? extends ISourceFragment> cls, final String target) {
		final Multibinder<ISourceFragment> fragmentProvider = Multibinder.newSetBinder(binder, ISourceFragment.class,
				Names.named(target));
		fragmentProvider.addBinding().to(cls);
	}

	/** Only for cycle based case */
	protected void bindCycleBasedClasses(final Binder binder) {
		binder.bind(RunCycle.class).to(LifecycleFunctions.class);
	}

	/** Needed for cycle based AND event driven */
	protected void bindDefaultClasses(final Binder binder) {
		binder.bind(Init.class).to(LifecycleFunctions.class);
		binder.bind(Enter.class).to(LifecycleFunctions.class);
		binder.bind(Exit.class).to(LifecycleFunctions.class);
		binder.bind(IsActive.class).to(LifecycleFunctions.class);
		binder.bind(IsFinal.class).to(LifecycleFunctions.class);
		binder.bind(IsStateActive.class).to(LifecycleFunctions.class);
	}

	protected Class<? extends ConstructorProvider> getConstructorProvider(final GeneratorEntry entry) {
		if ((new AnnotationExtensions()).isEventDriven(entry)) {
			return EventDrivenConstructorProvider.class;
		} else {
			return ConstructorProvider.class;
		}
	}

	protected void addIncludeProvider(final Binder binder, final Class<? extends IncludeProvider> provider) {
		final Multibinder<IncludeProvider> includeBinder = Multibinder.newSetBinder(binder, IncludeProvider.class);
		includeBinder.addBinding().to(provider);
	}

	protected String getSeparator(final GeneratorEntry entry) {
		final GenmodelEntries entries = new GenmodelEntries();
		final String separator = entries.getSeparator(entry);
		if (separator == null) {
			return "_";
		} else {
			return separator;
		}
	}

}
