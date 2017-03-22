/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.editor.sgen;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.yakindu.sct.model.sgen.FeatureConfiguration;
import org.yakindu.sct.model.sgen.FeatureParameter;

public class GenericFeatureConfigurationSection extends AbstractFeatureConfigurationSection
		implements FocusListener, SelectionListener {

	protected final Map<FeatureParameter, Control> controls = new HashMap<>();

	protected Section section;

	private boolean mutex;

	/**
	 * @see org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection#getSection()
	 */
	@Override
	public Section createSection(final FormToolkit toolkit, final Composite parent) {
		this.section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.section.setText(convertCamelCaseName(getFeatureType().getName(), !getFeatureType().isOptional()));
		this.section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

		final Composite composite = toolkit.createComposite(this.section);
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		for (final FeatureParameter parameter : getFeatureType().getParameters()) {
			final Control control = createParameterControl(toolkit, composite, parameter);
			this.controls.put(parameter, control);
		}

		this.section.setClient(composite);

		return this.section;
	}

	private Control createParameterControl(final FormToolkit toolkit, final Composite composite,
			final FeatureParameter parameter) {
		final StringBuilder builder = new StringBuilder();
		builder.append(convertCamelCaseName(parameter.getName(), !parameter.isOptional()));
		builder.append(':');

		final Label label = toolkit.createLabel(composite, builder.toString());
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		Control control;
		switch (parameter.getParameterType()) {
			case BOOLEAN:
				final Button button = toolkit.createButton(composite, "", SWT.CHECK); //$NON-NLS-1$
				button.addSelectionListener(this);
				control = button;
				break;
			case INTEGER:
			case FLOAT:
			case STRING:
			default:
				final Text text = toolkit.createText(composite, "", SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
				text.addFocusListener(this);
				control = text;
				break;
		}

		control.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));
		control.setData(parameter);

		return control;

	}

	/**
	 * @see org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection#modelChanged(org.eclipse.xtext.ui.editor.model.IXtextDocument)
	 */
	@Override
	public void modelChanged(final IXtextDocument xtextDocument) {
		this.mutex = true;

		xtextDocument.readOnly(new IUnitOfWork.Void<XtextResource>() {
			/**
			 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork.Void#process(java.lang.Object)
			 */
			@Override
			public void process(final XtextResource resource) throws Exception {
				final FeatureConfiguration featureConfiguration = SGenModelUtil.getFeatureConfiguration(resource,
						getFormPage().getStatechartName(), getFeatureType());
				if (featureConfiguration == null) {
					GenericFeatureConfigurationSection.this.section.setExpanded(false);

					for (final Control control : GenericFeatureConfigurationSection.this.controls.values()) {
						if (control instanceof Text) {
							((Text) control).setText(""); //$NON-NLS-1$
						} else if (control instanceof Button) {
							((Button) control).setSelection(false);
						}
					}
				} else {
					GenericFeatureConfigurationSection.this.section.setExpanded(true);

					for (final FeatureParameter parameter : getFeatureType().getParameters()) {
						final Control control = GenericFeatureConfigurationSection.this.controls.get(parameter);
						if (control instanceof Text) {
							final String value = SGenModelUtil.getStringParameterValue(resource,
									getFormPage().getStatechartName(), parameter);
							((Text) control).setText(value);
						} else if (control instanceof Button) {
							final boolean value = SGenModelUtil.getBooleanParameterValue(resource,
									getFormPage().getStatechartName(), parameter);
							((Button) control).setSelection(value);
						}
					}
				}
			}
		});

		this.mutex = false;
	}

	/**
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusGained(final FocusEvent e) {
		// nothing to do
	}

	/**
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusLost(final FocusEvent event) {
		if (!this.mutex) {
			final Text text = (Text) event.getSource();
			final FeatureParameter parameter = (FeatureParameter) text.getData();

			getFormPage().updateModel(parameter, text.getText());
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (!this.mutex) {
			final Button button = (Button) event.getSource();
			final FeatureParameter parameter = (FeatureParameter) button.getData();

			getFormPage().updateModel(parameter, button.getSelection());
		}
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing to do
	}

}
