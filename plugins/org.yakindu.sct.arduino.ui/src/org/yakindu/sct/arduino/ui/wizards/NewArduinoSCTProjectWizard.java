/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tools.templates.core.IGenerator;
import org.eclipse.tools.templates.ui.TemplateWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.yakindu.sct.arduino.core.ArduinoSCTProjectGenerator;

public class NewArduinoSCTProjectWizard extends TemplateWizard {

	private WizardNewProjectCreationPage projectCreationPage;

	private ArduinoSCTWizardPage arduinoSCTWizardPage;

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
		final ArduinoSCTProjectGenerator generator = new ArduinoSCTProjectGenerator(
				"templates/arduino_sgen/manifest.xml", this.projectCreationPage.getProjectName()); //$NON-NLS-1$
		generator.setStatechartName(this.arduinoSCTWizardPage.getStatechartName());
		generator.setSrcFolder(this.arduinoSCTWizardPage.getSrcFolderName());
		generator.setSrcGenFolder(this.arduinoSCTWizardPage.getSrcGenFolderName());
		generator.setTimer(this.arduinoSCTWizardPage.getTimer());

		if (!this.projectCreationPage.useDefaults()) {
			generator.setLocationURI(this.projectCreationPage.getLocationURI());
		}

		return generator;
	}

}
