package org.yakindu.sct.arduino.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tools.templates.core.IGenerator;
import org.eclipse.tools.templates.ui.TemplateWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.yakindu.sct.arduino.core.ArduinoSCTProjectGenerator;

public class NewArduinoSCTProjectWizard extends TemplateWizard {

	private WizardNewProjectCreationPage mainPage;

	@Override
	public void addPages() {
		this.mainPage = new WizardNewProjectCreationPage("basicNewProjectPage") { //$NON-NLS-1$
			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				createWorkingSetGroup((Composite) getControl(), getSelection(),
						new String[] { "org.eclipse.ui.resourceWorkingSetPage" }); //$NON-NLS-1$
				Dialog.applyDialogFont(getControl());
			}
		};
		this.mainPage.setTitle("New Arduino SCT Project"); //$NON-NLS-1$
		this.mainPage.setDescription("Specify properties of new Arduino SCT project."); //$NON-NLS-1$

		addPage(this.mainPage);
	}

	@Override
	protected IGenerator getGenerator() {
		final ArduinoSCTProjectGenerator generator = new ArduinoSCTProjectGenerator(
				"templates/arduino_sgen/manifest.xml", this.mainPage.getProjectName()); //$NON-NLS-1$
		if (!this.mainPage.useDefaults()) {
			generator.setLocationURI(this.mainPage.getLocationURI());
		}

		return generator;
	}

}
