/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.wizards;

import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tools.templates.core.IGenerator;
import org.eclipse.tools.templates.ui.TemplateWizard;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.part.FileEditorInput;
import org.yakindu.sct.arduino.core.ArduinoSCTProjectGenerator;
import org.yakindu.sct.arduino.ui.SCTArduinoUIPlugin;
import org.yakindu.sct.ui.perspectives.IYakinduSctPerspectives;

public class NewArduinoSCTProjectWizard extends TemplateWizard {

	private WizardNewProjectCreationPage projectCreationPage;

	private ArduinoSCTWizardPage arduinoSCTWizardPage;

	private ArduinoSCTProjectGenerator generator;

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		this.projectCreationPage = new WizardNewProjectCreationPage("basicNewProjectPage") { //$NON-NLS-1$
			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				createWorkingSetGroup((Composite) getControl(), getSelection(),
						new String[] { "org.eclipse.ui.resourceWorkingSetPage" }); //$NON-NLS-1$
				Dialog.applyDialogFont(getControl());
			}
		};
		this.projectCreationPage.setTitle(Messages.NewArduinoSCTProjectWizard_title);
		this.projectCreationPage.setDescription(Messages.NewArduinoSCTProjectWizard_description);

		this.arduinoSCTWizardPage = new ArduinoSCTWizardPage();
		this.arduinoSCTWizardPage.setTitle(Messages.NewArduinoSCTProjectWizard_title);

		addPage(this.projectCreationPage);
		addPage(this.arduinoSCTWizardPage);
	}

	/**
	 * @see org.eclipse.tools.templates.ui.TemplateWizard#getGenerator()
	 */
	@Override
	protected IGenerator getGenerator() {
		this.generator = new ArduinoSCTProjectGenerator("templates/arduino_sgen/manifest.xml", //$NON-NLS-1$
				this.projectCreationPage.getProjectName());
		this.generator.setStatechartName(this.arduinoSCTWizardPage.getStatechartName());
		this.generator.setSrcFolder(this.arduinoSCTWizardPage.getSrcFolderName());
		this.generator.setSrcGenFolder(this.arduinoSCTWizardPage.getSrcGenFolderName());
		this.generator.setTimer(this.arduinoSCTWizardPage.getTimer());

		if (!this.projectCreationPage.useDefaults()) {
			this.generator.setLocationURI(this.projectCreationPage.getLocationURI());
		}

		return this.generator;
	}

	/**
	 * @see org.eclipse.tools.templates.ui.TemplateWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final boolean finished = super.performFinish();

		if (finished) {
			final IWorkbench workbench = getWorkbench();
			final IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
			final IWorkbenchPage page = workbenchWindow.getActivePage();
			final IFile file = this.generator.getDiagramFile();
			final IEditorDescriptor desc = workbench.getEditorRegistry().getDefaultEditor(file.getName());

			try {
				page.openEditor(new FileEditorInput(file), desc.getId());
			} catch (final PartInitException exception) {
				SCTArduinoUIPlugin.logError(exception);
			}

			try {
				workbench.showPerspective(CUIPlugin.ID_CPERSPECTIVE, workbenchWindow);
				workbench.showPerspective(IYakinduSctPerspectives.ID_PERSPECTIVE_SCT_MODELING, workbenchWindow);
			} catch (final WorkbenchException exception) {
				SCTArduinoUIPlugin.logError(exception);
			}
		}

		return finished;
	}

}
