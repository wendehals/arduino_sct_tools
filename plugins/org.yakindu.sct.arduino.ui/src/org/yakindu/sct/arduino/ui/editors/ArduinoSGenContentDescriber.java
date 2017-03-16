package org.yakindu.sct.arduino.ui.editors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

public class ArduinoSGenContentDescriber implements ITextContentDescriber {

	/**
	 * @see org.eclipse.core.runtime.content.IContentDescriber#getSupportedOptions()
	 */
	@Override
	public QualifiedName[] getSupportedOptions() {
		return new QualifiedName[0];
	}

	/**
	 * @see org.eclipse.core.runtime.content.IContentDescriber#describe(java.io.InputStream,
	 *      org.eclipse.core.runtime.content.IContentDescription)
	 */
	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		try (InputStreamReader inputStreamReader = new InputStreamReader(contents)) {
			return describe(inputStreamReader, description);
		}
	}

	/**
	 * @see org.eclipse.core.runtime.content.ITextContentDescriber#describe(java.io.Reader,
	 *      org.eclipse.core.runtime.content.IContentDescription)
	 */
	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(contents)) {
			final int i = 0;
			String line;
			while ((i < 4) && ((line = bufferedReader.readLine()) != null)) {
				if (line.contains("yakindu::arduino_cpp")) { //$NON-NLS-1$
					return IContentDescriber.VALID;
				}
			}
		}

		return IContentDescriber.INVALID;
	}

}
