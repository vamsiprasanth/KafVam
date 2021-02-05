package kafvam.rcp;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import kafvam.rcp.dialog.CustomProgressMonitorDialog;

/**
 * @author Vamsi Prasanth
 *
 */
public class ShutdownHook extends Thread {
	private Logger logger = LogManager.getLogger(getClass());

	private boolean exitSuccessful = false;

	@Override
	public void run() {

		try {
			IRunnableWithProgress progressObj = new RCPThread(100);
			CustomProgressMonitorDialog dialog = new CustomProgressMonitorDialog(new Shell(), "Shutting Down KafkaVam...");
			dialog.run(true, true, progressObj);
		} catch (Exception ex) {
			logger.error("Shutdown Hook exception:" + exitSuccessful);
			ex.printStackTrace();
			if (!exitSuccessful) {
				logger.error("Shutdown Hook exception again");
			}
		}

	}

	class RCPThread implements IRunnableWithProgress {
		private int workload;

		public RCPThread(int workload) {
			this.workload = workload;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Closing Kafvam...", workload);
			for (int i = 0; i < workload; i += 10) {
				if (i < 30) {
					monitor.subTask("Removing Listeners...");
					IWorkbenchWindow window;
					try {
						window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					} catch (Exception e) {
						return;
					}
					if (window != null) {
						IViewReference[] ref = window.getActivePage().getViewReferences();
						if (ref != null) {
							for (IViewReference viewRef : ref) {
								viewRef.getView(false).dispose();
							}
						}
					}
				}
				if (i == 30) {
					monitor.subTask("Disposing resource objects and Images...");
				}

				if (i == 60) {
					monitor.subTask("Cleaning up Threads...");

				}
				if (i == 90) {
					monitor.subTask("Closing Workbench...");
					Thread.sleep(5000);
				}

				monitor.worked(10);
				if (monitor.isCanceled()) {
					monitor.done();
					return;
				}
			}

			monitor.done();
			return;
		}

	}
}
