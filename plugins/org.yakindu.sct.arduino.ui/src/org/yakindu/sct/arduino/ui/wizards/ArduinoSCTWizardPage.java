/**
 * Copyright (c) 2016 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.arduino.ui.wizards;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.yakindu.sct.arduino.generator.cpp.extensions.AbstractNamedElement;
import org.yakindu.sct.arduino.generator.cpp.extensions.ArchitectureElement;
import org.yakindu.sct.arduino.generator.cpp.extensions.ArchitecturesExtension;
import org.yakindu.sct.arduino.generator.cpp.extensions.TimerElement;

public class ArduinoSCTWizardPage extends WizardPage implements ModifyListener, ISelectionChangedListener {

	private Text statechartNameText;

	private Text srcFolderText;

	private Text srcGenFolderText;

	private Text cyclePeriodText;

	private Text timerImplDescText;

	private ComboViewer architectureViewer;

	private ComboViewer timerViewer;

	public ArduinoSCTWizardPage() {
		super("arduinoSCTWizardPage"); //$NON-NLS-1$
		setDescription(Messages.ArduinoSCTWizardPage_description);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.ArduinoSCTWizardPage_statechartLabel);

		this.statechartNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.statechartNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.statechartNameText.addModifyListener(this);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.ArduinoSCTWizardPage_srcFolderLabel);

		this.srcFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.srcFolderText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.srcFolderText.addModifyListener(this);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.ArduinoSCTWizardPage_genSrcFolderLabel);

		this.srcGenFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.srcGenFolderText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.srcGenFolderText.addModifyListener(this);

		label = new Label(composite, SWT.NONE);
		label.setText(Messages.ArduinoSCTWizardPage_cyclePeriodLabel);
		label.setToolTipText(Messages.ArduinoSCTWizardPage_cyclePeriodToolTip);

		this.cyclePeriodText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.cyclePeriodText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.cyclePeriodText.addModifyListener(this);
		this.cyclePeriodText.setToolTipText(Messages.ArduinoSCTWizardPage_cyclePeriodToolTip);

		final Group group = new Group(composite, SWT.SHADOW_NONE);
		group.setText(Messages.ArduinoSCTWizardPage_timerImplLabel);
		group.setLayout(new GridLayout(2, false));

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		group.setLayoutData(gridData);

		label = new Label(group, SWT.NONE);
		label.setText(Messages.ArduinoSCTWizardPage_architectureLabel);

		this.architectureViewer = new ComboViewer(group, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.architectureViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.architectureViewer.addSelectionChangedListener(this);

		NamedExtensionElementsProvider provider = new NamedExtensionElementsProvider();
		this.architectureViewer.setContentProvider(provider);
		this.architectureViewer.setLabelProvider(provider);

		this.architectureViewer.setInput(ArchitecturesExtension.getArchitectures());
		this.architectureViewer.getCombo().select(0);

		label = new Label(group, SWT.NONE);
		label.setText(Messages.ArduinoSCTWizardPage_timerLabel);

		this.timerViewer = new ComboViewer(group, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.timerViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.timerViewer.addSelectionChangedListener(this);

		provider = new NamedExtensionElementsProvider();
		this.timerViewer.setContentProvider(provider);
		this.timerViewer.setLabelProvider(provider);

		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;

		this.timerImplDescText = new Text(group, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP);
		this.timerImplDescText.setLayoutData(gridData);

		setControl(composite);

		initialize();
		checkPageComplete();
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	@Override
	public void modifyText(ModifyEvent event) {
		checkPageComplete();
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource() == this.architectureViewer) {
			updateArchitecture();
		} else if (event.getSource() == this.timerViewer) {
			updateTimer();
		}
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		this.statechartNameText.setText(((WizardNewProjectCreationPage) getPreviousPage()).getProjectName());
	}

	public String getStatechartName() {
		return this.statechartNameText.getText().trim();
	}

	public String getSrcFolderName() {
		return this.srcFolderText.getText().trim();
	}

	public String getSrcGenFolderName() {
		return this.srcGenFolderText.getText().trim();
	}

	public int getCyclePeriod() {
		return Integer.parseInt(this.cyclePeriodText.getText());
	}

	public TimerElement getTimer() {
		return (TimerElement) this.timerViewer.getStructuredSelection().getFirstElement();
	}

	private void initialize() {
		this.srcFolderText.setText("src"); //$NON-NLS-1$
		this.srcGenFolderText.setText("src-gen"); //$NON-NLS-1$
		this.cyclePeriodText.setText("10"); //$NON-NLS-1$

		updateArchitecture();
	}

	private void updateArchitecture() {
		final ArchitectureElement architecture = (ArchitectureElement) this.architectureViewer.getStructuredSelection()
				.getFirstElement();
		this.timerViewer.setInput(architecture.getTimers());
		this.timerViewer.getCombo().select(0);

		updateTimer();
	}

	private void updateTimer() {
		this.timerImplDescText.setText(getTimer().getDescription());
		checkPageComplete();
	}

	private void checkPageComplete() {
		setPageComplete(false);
		if (getStatechartName().isEmpty()) {
			setErrorMessage(Messages.ArduinoSCTWizardPage_missingStatechartNameMessage);
			return;
		}

		try {
			final int cyclePeriod = Integer.parseInt(this.cyclePeriodText.getText());
			final TimerElement timer = getTimer();
			if ((cyclePeriod < timer.getMinCyclePeriod()) || (cyclePeriod > timer.getMaxCyclePeriod())) {
				setErrorMessage(String.format(Messages.ArduinoSCTWizardPage_cyclePeriodNotInIntervalMessage,
						timer.getMinCyclePeriod(), timer.getMaxCyclePeriod()));
				return;
			}
		} catch (final NumberFormatException exception) {
			setErrorMessage(Messages.ArduinoSCTWizardPage_invalidCyclePeriodMessage);
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	protected static class NamedExtensionElementsProvider extends LabelProvider implements IStructuredContentProvider {

		private final Collection<AbstractNamedElement> namedExtensionElement = new TreeSet<>(
				new Comparator<AbstractNamedElement>() {

					/**
					 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
					 */
					@Override
					public int compare(AbstractNamedElement element1, AbstractNamedElement element2) {
						return element1.getName().compareTo(element2.getName());
					}
				});

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		@Override
		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.namedExtensionElement.clear();
			this.namedExtensionElement.addAll((Collection<AbstractNamedElement>) newInput);
		}

		/**
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			return ((AbstractNamedElement) element).getName();
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement) {
			return this.namedExtensionElement.toArray();
		}

	}

}
