package org.yakindu.sct.arduino.ui.wizards;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.yakindu.sct.arduino.generator.cpp.features.Timer;

public class ArduinoSCTWizardPage extends WizardPage implements ModifyListener, ISelectionChangedListener {

	private Text statechartNameText;

	private Text srcFolderText;

	private Text srcGenFolderText;

	private Text timerImplDescText;

	private ComboViewer timerViewer;

	public ArduinoSCTWizardPage() {
		super("arduinoSCTWizardPage"); //$NON-NLS-1$
		setDescription("Specify properties of Arduino SCT project.");
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Statechart name:");

		this.statechartNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.statechartNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.statechartNameText.addModifyListener(this);

		label = new Label(composite, SWT.NONE);
		label.setText("Source folder:");

		this.srcFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.srcFolderText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.srcFolderText.addModifyListener(this);

		label = new Label(composite, SWT.NONE);
		label.setText("Generated Source folder:");

		this.srcGenFolderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		this.srcGenFolderText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.srcGenFolderText.addModifyListener(this);

		final Group group = new Group(composite, SWT.SHADOW_NONE);
		group.setText("Arduino Timer Implementation:");
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout());

		this.timerViewer = new ComboViewer(group, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.timerViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.timerViewer.addSelectionChangedListener(this);

		final TimerProvider provider = new TimerProvider();
		this.timerViewer.setContentProvider(provider);
		this.timerViewer.setLabelProvider(provider);

		this.timerViewer.setInput(Timer.values());
		this.timerViewer.getCombo().select(0);

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
		updateTimerDescription();
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		this.statechartNameText.setText(((WizardNewProjectCreationPage) getPreviousPage()).getProjectName());
		updateTimerDescription();
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

	public Timer getTimer() {
		return (Timer) this.timerViewer.getStructuredSelection().getFirstElement();
	}

	private void initialize() {
		this.srcFolderText.setText("src"); //$NON-NLS-1$
		this.srcGenFolderText.setText("src-gen"); //$NON-NLS-1$
	}

	private void updateTimerDescription() {
		this.timerImplDescText.setText(getTimer().description);
	}

	private void checkPageComplete() {
		setPageComplete(false);
		if (getStatechartName().isEmpty()) {
			setErrorMessage("Please provide a name for the statechart.");
			return;
		}

		setErrorMessage(null);
		setPageComplete(true);
	}

	private static class TimerProvider extends LabelProvider implements IStructuredContentProvider {

		@Override
		public String getText(Object element) {
			return ((Timer) element).title;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return Timer.values();
		}

	}

}
