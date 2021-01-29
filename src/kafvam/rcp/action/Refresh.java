package kafvam.rcp.action;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import kafvam.kafka.KafkaInit;
import kafvam.kafka.controller.KafAdminClient;

public class Refresh extends Action implements IWorkbenchAction {
	private Logger logger = LogManager.getLogger(getClass());

	private static final String ID = "tec-rcp.refresh";
	private TableViewer viewer;
	private KafAdminClient kac = KafkaInit.getKafAdminClient();

	public Refresh(TableViewer viewer) {
		setId(ID);
		this.viewer = viewer;
	}

	@Override
	public void run() {
		logger.info("Refresh Called!");
		viewer.setInput(kac.getAllTopics(true));
		viewer.refresh();
	}

	@Override
	public void dispose() {
	}

}
