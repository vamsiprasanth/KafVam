package kafvam.rcp.handler;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import kafvam.kafka.controller.KafConsumer;
import kafvam.kafka.entity.KafkaTopic;
import kafvam.kafka.entity.KafkaTopicPartition;
import kafvam.rcp.view.Topic;

/**
 * @author Vamsi Prasanth
 *
 */
public class HanderTopicViewer extends AbstractHandler {
	public final static String ID = "hd.tp.viewer";

	private Logger logger = LogManager.getLogger(getClass());
	private KafConsumer kc = new KafConsumer();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection) selection).getFirstElement();
			if (o instanceof KafkaTopic) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						KafkaTopic data = (KafkaTopic) o;
						if (data != null) {
							String topicSelected = data.getName();
							ConcurrentHashMap<Integer, KafkaTopicPartition> tpMap = kc.getPartitionSize(topicSelected);
							data.setPartitions(tpMap);
							Topic tpView = (Topic) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
									.findView(Topic.ID);
							tpView.getViewer().update(o, new String[] { data.getName(),
									String.valueOf(data.getTotalPartitions()), String.valueOf(data.getTotalSize()) });
							logger.info("Partition metrics updated");
						}
					}
				});
			}

		}
		return null;
	}

}
