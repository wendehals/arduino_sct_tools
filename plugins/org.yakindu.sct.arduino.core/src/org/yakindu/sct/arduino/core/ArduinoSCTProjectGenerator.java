package org.yakindu.sct.arduino.core;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.build.CBuilder;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.tools.templates.freemarker.FMProjectGenerator;
import org.osgi.framework.Bundle;
import org.yakindu.base.base.BasePackage;
import org.yakindu.sct.arduino.generator.cpp.features.Timer;
import org.yakindu.sct.builder.nature.SCTNature;
import org.yakindu.sct.model.sgraph.SGraphPackage;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.ui.editor.DiagramActivator;
import org.yakindu.sct.ui.editor.factories.FactoryUtils;

public class ArduinoSCTProjectGenerator extends FMProjectGenerator {

	private static final String UTF_8 = "UTF-8"; //$NON-NLS-1$

	public static final String ARDUINO_PROJECT_NATURE_ID = "org.eclipse.cdt.arduino.core.arduinoNature"; //$NON-NLS-1$

	private String statechartName;

	private String srcFolderName;

	private String srcGenFolderName;

	private Timer timer;

	public ArduinoSCTProjectGenerator(String manifestPath, String projectName) {
		super(manifestPath);
		setProjectName(projectName);
	}

	public void setStatechartName(String statechartName) {
		this.statechartName = statechartName;
	}

	public void setSrcFolder(String srcFolderName) {
		this.srcFolderName = srcFolderName;
	}

	public void setSrcGenFolder(String srcGenFolderName) {
		this.srcGenFolderName = srcGenFolderName;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	@Override
	protected Bundle getSourceBundle() {
		return SCTArduinoPlugin.getDefault().getBundle();
	}

	@Override
	protected void initProjectDescription(IProjectDescription description) throws CoreException {
		description.setNatureIds(new String[] { CProjectNature.C_NATURE_ID, CCProjectNature.CC_NATURE_ID,
				ARDUINO_PROJECT_NATURE_ID, SCTNature.NATURE_ID });
		final ICommand command = description.newCommand();
		CBuilder.setupBuilder(command);
		description.setBuildSpec(new ICommand[] { command });
	}

	@Override
	public void generate(Map<String, Object> model, IProgressMonitor monitor) throws CoreException {
		model.put("statechartName", this.statechartName); //$NON-NLS-1$
		model.put("srcFolder", this.srcFolderName); //$NON-NLS-1$
		model.put("srcGenFolder", this.srcGenFolderName); //$NON-NLS-1$
		model.put("timer", this.timer.literal); //$NON-NLS-1$

		final SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		super.generate(model, subMonitor.split(80));
		createDiagram(subMonitor.split(20));
	}

	protected void createDiagram(IProgressMonitor progressMonitor) {
		final TransactionalEditingDomain editingDomain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();
		progressMonitor.beginTask(Messages.ArduinoSCTProjectGenerator_creatingDiagram, 3);

		final Resource resource = editingDomain.getResourceSet().createResource(getModelURI());

		final AbstractTransactionalCommand command = new AbstractTransactionalCommand(editingDomain,
				Messages.ArduinoSCTProjectGenerator_creatingDiagram, Collections.EMPTY_LIST) {
			@Override
			protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {

				FactoryUtils.createStatechartModel(resource, DiagramActivator.DIAGRAM_PREFERENCES_HINT);
				final Statechart statechart = (Statechart) EcoreUtil.getObjectByType(resource.getContents(),
						SGraphPackage.Literals.STATECHART);
				statechart.setDomainID(getDomainID());

				try {
					resource.save(getSaveOptions());
				} catch (final IOException exception) {
					return CommandResult.newErrorCommandResult(exception);
				}
				return CommandResult.newOKCommandResult();
			}

		};

		try {
			command.execute(progressMonitor, null);
		} catch (final ExecutionException e) {
			e.printStackTrace();
		}

		try {
			WorkspaceSynchronizer.getFile(resource).setCharset(UTF_8, new NullProgressMonitor());
		} catch (final CoreException e) {
			e.printStackTrace();
		}

		editingDomain.dispose();
	}

	protected URI getModelURI() {
		final IPath path = getProject().getFullPath().append("/model/" + this.statechartName + ".sct"); //$NON-NLS-1$//$NON-NLS-2$
		return URI.createPlatformResourceURI(path.toString(), false);
	}

	protected Map<String, String> getSaveOptions() {
		final Map<String, String> saveOptions = new HashMap<String, String>();
		saveOptions.put(XMLResource.OPTION_ENCODING, UTF_8);
		saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

		return saveOptions;
	}

	protected String getDomainID() {
		return BasePackage.Literals.DOMAIN_ELEMENT__DOMAIN_ID.getDefaultValueLiteral();
	}

}
