package org.yakindu.sct.arduino.generator.cpp;

import org.yakindu.sct.generator.c.GenArtifactConfigurations;
import org.yakindu.sct.generator.c.IGenArtifactConfigurations;
import org.yakindu.sct.generator.c.types.CTypeSystemAccess;
import org.yakindu.sct.generator.core.impl.GenericJavaBasedGenerator;
import org.yakindu.sct.generator.core.types.ICodegenTypeSystemAccess;
import org.yakindu.sct.generator.cpp.CppNamingService;
import org.yakindu.sct.model.sexec.naming.INamingService;
import org.yakindu.sct.model.sgen.GeneratorEntry;
import org.yakindu.sct.model.sgraph.Statechart;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;

public class ArduinoCppCodeGenerator extends GenericJavaBasedGenerator {

	@Inject
	private ArduinoCodeGenerator delegate;

	@Override
	public void runGenerator(Statechart statechart, GeneratorEntry entry) {
		this.delegate.generate(createExecutionFlow(statechart, entry), entry, this.sctFsa.getIFileSystemAccess());
	}

	@Override
	public Module getOverridesModule(final GeneratorEntry entry) {
		final Module module = super.getOverridesModule(entry);
		return Modules.override(module).with(new Module() {
			@Override
			public void configure(Binder binder) {
				binder.bind(ICodegenTypeSystemAccess.class).to(CTypeSystemAccess.class);
				binder.bind(INamingService.class).to(CppNamingService.class);
				binder.bind(GeneratorEntry.class).toInstance(entry);
				binder.bind(IGenArtifactConfigurations.class)
						.annotatedWith(Names.named(IGenArtifactConfigurations.DEFAULT))
						.toInstance(GenArtifactConfigurations.DEFAULT);
			}
		});
	}
}
