/**
 * Copyright (c) 2017 by Lothar Wendehals.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.yakindu.sct.editor.sgen.extensions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
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
import org.yakindu.sct.editor.sgen.AbstractFeatureConfigurationSection;
import org.yakindu.sct.editor.sgen.SGenModelUtil;
import org.yakindu.sct.model.sgen.FeatureConfiguration;
import org.yakindu.sct.model.sgen.FeatureParameter;
import org.yakindu.sct.model.sgen.FeatureType;

public class LicenseHeaderFeatureConfigurationSection extends AbstractFeatureConfigurationSection
		implements FocusListener {

	protected Section section;

	protected Text licenseText;

	private boolean mutex;

	/**
	 * @see org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection#createSection(org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
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

		final FeatureParameter parameter = SGenModelUtil.getFeatureParameter(getFeatureType(), "licenseText"); //$NON-NLS-1$

		final Label label = toolkit.createLabel(composite,
				convertCamelCaseName(parameter.getName(), !parameter.isOptional()) + ':');
		label.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		this.licenseText = toolkit.createText(composite, "", SWT.SINGLE | SWT.MULTI | SWT.WRAP | SWT.BORDER); //$NON-NLS-1$
		final TableWrapData layoutData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL);
		layoutData.heightHint = 5 * this.licenseText.getLineHeight();
		this.licenseText.setLayoutData(layoutData);
		this.licenseText.addFocusListener(this);
		this.licenseText.setData(parameter);

		this.section.setClient(composite);

		return this.section;
	}

	/**
	 * @see org.yakindu.sct.editor.sgen.extensions.IFeatureConfigurationSection#modelChanged(org.eclipse.xtext.ui.editor.model.IXtextDocument)
	 */
	@Override
	public void modelChanged(final IXtextDocument xtextDocument) {
		if (!this.mutex) {
			this.mutex = true;

			final String statechartName = getFormPage().getStatechartName();
			final FeatureType featureType = getFeatureType();

			xtextDocument.readOnly(new IUnitOfWork.Void<XtextResource>() {
				/**
				 * @see org.eclipse.xtext.util.concurrent.IUnitOfWork.Void#process(java.lang.Object)
				 */
				@Override
				public void process(final XtextResource resource) throws Exception {
					final FeatureConfiguration featureConfiguration = SGenModelUtil.getFeatureConfiguration(resource,
							statechartName, featureType);
					if (featureConfiguration == null) {
						LicenseHeaderFeatureConfigurationSection.this.section.setExpanded(false);

						LicenseHeaderFeatureConfigurationSection.this.licenseText.setText(""); //$NON-NLS-1$
					} else {
						LicenseHeaderFeatureConfigurationSection.this.section.setExpanded(true);

						final String value = SGenModelUtil.getStringParameterValue(resource, statechartName,
								(FeatureParameter) LicenseHeaderFeatureConfigurationSection.this.licenseText.getData());
						LicenseHeaderFeatureConfigurationSection.this.licenseText.setText(value);
					}
				}
			});

			this.mutex = false;
		}
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
			this.mutex = true;

			final Text text = (Text) event.getSource();
			final FeatureParameter parameter = (FeatureParameter) text.getData();
			getFormPage().updateModel(parameter, text.getText());

			this.mutex = false;
		}
	}

}
