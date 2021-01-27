package kafvam.rcp.common;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import kafvam.rcp.Activator;

public class CustomProgressMonitorDialog extends ProgressMonitorDialog {

	private String title;

	public CustomProgressMonitorDialog(Shell parent, String title) {
		super(parent);
		this.title = title;

	}

	@Override
	protected Image getImage() {
		return Activator.getImageDescriptor("icons/info.gif").createImage();
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(title); //$NON-NLS-1$
	}

}
