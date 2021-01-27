package kafvam.rcp.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.handlers.HandlerUtil;

import kafvam.kafka.KafkaInit;
import kafvam.kafka.controller.KafAdminClient;
import kafvam.kafka.entity.KafkaDetails;
import kafvam.rcp.view.BrokerDetails;

public class HandlerBrokerDetails extends AbstractHandler {
	public final static String ID = "hd.brkr.details";

	private KafAdminClient kac = KafkaInit.getKafAdminClient();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		BrokerDetails bDetailsView = (BrokerDetails) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
				.findView(BrokerDetails.ID);
		if (bDetailsView != null) {
			KafkaDetails kd = kac.getKafkaDetails();
			setLabelVal(bDetailsView.getBootstrapServer(), kd.getBootstrapServer());
			setLabelVal(bDetailsView.getTotPartition(), kd.getTotalPartitions());
			setLabelVal(bDetailsView.getTotTopics(), kd.getTotalTopics());
			setLabelVal(bDetailsView.getTotCgs(), kd.getNumOfCgs());

		}
		return null;
	}

	private void setLabelVal(Label l, String val) {
		l.setText(val);
	}

	private void setLabelVal(Label l, int val) {
		l.setText(String.valueOf(val));
	}

}
