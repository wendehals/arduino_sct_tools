/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.core;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.build.CBuilder;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IPathEntry;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
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
import org.yakindu.sct.arduino.generator.cpp.extensions.TimerElement;
import org.yakindu.sct.generator.builder.nature.SCTNature;
import org.yakindu.sct.model.sgraph.SGraphPackage;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.ui.editor.DiagramActivator;
import org.yakindu.sct.ui.editor.wizards.IModelCreator;

public class ArduinoSCTProjectGenerator extends FMProjectGenerator {

	private static final String UTF_8 = "UTF-8"; //$NON-NLS-1$

	public static final String ARDUINO_PROJECT_NATURE_ID = "org.eclipse.cdt.arduino.core.arduinoNature"; //$NON-NLS-1$

	private String statechartName;

	private String srcFolderName;

	private String srcGenFolderName;

	private int cyclePeriod = 10;

	private TimerElement timer;

	private IFile diagramFile;

	public ArduinoSCTProjectGenerator(final String manifestPath, final String projectName) {
		super(manifestPath);
		setProjectName(projectName);
	}

	public void setStatechartName(final String statechartName) {
		this.statechartName = statechartName;
	}

	public void setSrcFolder(final String srcFolderName) {
		this.srcFolderName = srcFolderName;
	}

	public void setSrcGenFolder(final String srcGenFolderName) {
		this.srcGenFolderName = srcGenFolderName;
	}

	public void setCyclePeriod(final int cyclePeriod) {
		this.cyclePeriod = cyclePeriod;
	}

	protected int getCyclePeriod() {
		return this.cyclePeriod;
	}

	public void setTimer(final TimerElement timer) {
		this.timer = timer;
	}

	/**
	 * @see org.eclipse.tools.templates.freemarker.FMProjectGenerator#generate(java.util.Map,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void generate(final Map<String, Object> model, final IProgressMonitor monitor) throws CoreException {
		model.put("statechartName", this.statechartName); //$NON-NLS-1$
		model.put("srcFolder", this.srcFolderName); //$NON-NLS-1$
		model.put("srcGenFolder", this.srcGenFolderName); //$NON-NLS-1$
		model.put("timer", this.timer.getId()); //$NON-NLS-1$
		model.put("cyclePeriod", Integer.toString(this.cyclePeriod)); //$NON-NLS-1$

		final SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		super.generate(model, subMonitor.split(70));

		final IProject project = getProject();
		CoreModel.getDefault().create(project).setRawPathEntries(
				new IPathEntry[] { CoreModel.newSourceEntry(project.getFullPath()) }, subMonitor.split(10));

		createDiagram(subMonitor.split(20));
	}

	/**
	 * @see org.eclipse.tools.templates.freemarker.FMGenerator#getSourceBundle()
	 */
	@Override
	protected Bundle getSourceBundle() {
		return SCTArduinoPlugin.getDefault().getBundle();
	}

	/**
	 * @see org.eclipse.tools.templates.freemarker.FMProjectGenerator#initProjectDescription(org.eclipse.core.resources.IProjectDescription)
	 */
	@Override
	protected void initProjectDescription(final IProjectDescription description) throws CoreException {
		description.setNatureIds(new String[] { CProjectNature.C_NATURE_ID, CCProjectNature.CC_NATURE_ID,
				ARDUINO_PROJECT_NATURE_ID, SCTNature.NATURE_ID });
		final ICommand command = description.newCommand();
		CBuilder.setupBuilder(command);
		description.setBuildSpec(new ICommand[] { command });
	}

	protected void createDiagram(final IProgressMonitor progressMonitor) {
		final TransactionalEditingDomain editingDomain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();
		progressMonitor.beginTask(Messages.ArduinoSCTProjectGenerator_creatingDiagram, 3);

		this.diagramFile = getProject().getFile("/model/" + this.statechartName + ".sct"); //$NON-NLS-1$ //$NON-NLS-2$
		final URI uri = URI.createPlatformResourceURI(this.diagramFile.getFullPath().toString(), false);
		final Resource resource = editingDomain.getResourceSet().createResource(uri);

		final AbstractTransactionalCommand command = new AbstractTransactionalCommand(editingDomain,
				Messages.ArduinoSCTProjectGenerator_creatingDiagram, Collections.EMPTY_LIST) {
			/**
			 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor,
			 *      org.eclipse.core.runtime.IAdaptable)
			 */
			@Override
			protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info)
					throws ExecutionException {
				final IModelCreator modelCreator = new ArduinoModelCreator(getCyclePeriod());
				modelCreator.createStatechartModel(resource, DiagramActivator.DIAGRAM_PREFERENCES_HINT);
				final Statechart statechart = (Statechart) EcoreUtil.getObjectByType(resource.getContents(),
						SGraphPackage.Literals.STATECHART);
				statechart.setDomainID(BasePackage.Literals.DOMAIN_ELEMENT__DOMAIN_ID.getDefaultValueLiteral());

				final Map<String, String> saveOptions = new HashMap<String, String>();
				saveOptions.put(XMLResource.OPTION_ENCODING, UTF_8);
				saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED,
						Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);

				try {
					resource.save(saveOptions);
				} catch (final IOException exception) {
					return CommandResult.newErrorCommandResult(exception);
				}

				return CommandResult.newOKCommandResult();
			}
		};

		try {
			command.execute(progressMonitor, null);
		} catch (final ExecutionException exception) {
			SCTArduinoPlugin.logError(exception);
		}

		try {
			WorkspaceSynchronizer.getFile(resource).setCharset(UTF_8, new NullProgressMonitor());
		} catch (final CoreException exception) {
			SCTArduinoPlugin.logError(exception);
		}

		editingDomain.dispose();
	}

	public IFile getDiagramFile() {
		return this.diagramFile;
	}

}
