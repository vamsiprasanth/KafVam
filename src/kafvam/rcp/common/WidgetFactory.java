package kafvam.rcp.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Factory for widgets
 * 
 * @author Andy
 * 
 */
public class WidgetFactory {
	public static final Color WHITE_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	public static final Font TAHOMA_FONT = new Font(Display.getDefault(), "Tahoma", 10, SWT.NORMAL);
	public static final Font TAHOMA_BOLD_FONT_8 = new Font(Display.getDefault(), "Tahoma", 8, SWT.BOLD);
	public static final Font SUBHEADER_FONT = new Font(null, "Arial", 10, SWT.BOLD); //$NON-NLS-1$
	public static final Color SILVER_COLOR = new Color(null, 192, 192, 192);
	public static final Color LIGHT_VIOLET_COLOR = new Color(Display.getDefault(), 224, 224, 235);
	public static final Font BOLD_FONT_10 = new Font(Display.getDefault(), RCPConstants.KAFVAM_FONT, 10, SWT.BOLD);
	public static final Font BOLD_FONT_8 = new Font(Display.getDefault(), RCPConstants.KAFVAM_FONT, 8, SWT.BOLD);

	// public static final Color ORANGE_COLOR = new Color(null, 245, 143, 0);
	public static final Color GREY_COLOR = new Color(null, 236, 233, 226);
	public static final Color HEADING_COLOR = new Color(null, 102, 102, 102);
	private static FormColors formColors;

	public static Section createSection(FormToolkit toolkit, Composite composite) {
		return toolkit.createSection(composite, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR);
	}

	public static Section createTitleOnlySection(FormToolkit toolkit, Composite composite) {
		return toolkit.createSection(composite, Section.TITLE_BAR);
	}

	public static Composite createComposite(FormToolkit toolkit, Composite composite) {
		return toolkit.createComposite(composite, SWT.NONE);
	}

	public static Label createLabel(FormToolkit toolkit, Composite composite, String text) {
		return toolkit.createLabel(composite, text);
	}

	public static Label createWrapLabel(FormToolkit toolkit, Composite composite, String text) {
		return toolkit.createLabel(composite, text,SWT.WRAP);
	}

	public static Label createLabel(Composite composite) {
		return new Label(composite, SWT.WRAP);
	}

	public static Button createButton(Composite composite) {
		return new Button(composite, SWT.PUSH);
	}

	public static Text createText(Composite composite) {
		return new Text(composite, SWT.BORDER);
	}

	public static Text createNoBorderText(Composite composite) {
		return new Text(composite, SWT.NONE);
	}

	public static Text createMultiText(Composite composite) {
		return new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	}

	public static MessageBox createmessage(Composite composite) {
		return new MessageBox(composite.getShell(), SWT.ICON_QUESTION);
	}

	public static Combo buildCombo(Composite composite) {
		Combo combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		return combo;
	}

	public static FormColors FORM_COLOR(final Display display) {
		if (formColors == null) {
			formColors = new FormColors(display);
			formColors.createColor(IFormColors.H_GRADIENT_START, SILVER_COLOR.getRGB());
			formColors.createColor(IFormColors.H_GRADIENT_END, GREY_COLOR.getRGB());
			formColors.createColor(IFormColors.H_BOTTOM_KEYLINE1, GREY_COLOR.getRGB());
			formColors.createColor(IFormColors.H_BOTTOM_KEYLINE2, SILVER_COLOR.getRGB());
			formColors.createColor(IFormColors.TITLE, HEADING_COLOR.getRGB());
		}
		return formColors;
	}

	/**
	 * Builds a tab folder with a fancy look and feel
	 * 
	 * @param parent
	 *            parent
	 * @return tab folder
	 */

	/**
	 * Builds a basic tab folder
	 * 
	 * @param parent
	 *            parent
	 * @return tab folder
	 */
	public static TabFolder buildBasicTabFolder(Composite parent) {
		return new TabFolder(parent, SWT.FLAT | SWT.BORDER);
	}

	public static Section createNoTitleOnlySection(FormToolkit toolkit, Composite composite) {
		return toolkit.createSection(composite, Section.NO_TITLE);
	}

	public static boolean isValidInput(Text text, String val) {
		if (text.getText().isEmpty()) {
			MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_WARNING);
			messageBox.setText("Message Alert");
			messageBox.setMessage(val + " Cant be empty");
			messageBox.open();
			return false;
		}
		return true;
	}
}
