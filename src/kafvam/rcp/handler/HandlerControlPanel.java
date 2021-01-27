package kafvam.rcp.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import kafvam.kafka.KafkaInit;
import kafvam.kafka.controller.KafConsumer;
import kafvam.rcp.RCPManager;
import kafvam.rcp.common.RCPConstants;
import kafvam.rcp.common.WidgetFactory;
import kafvam.rcp.events.AddLogEventImpl;
import kafvam.rcp.events.UiChangedEvent;
import kafvam.rcp.view.ControlPanel;
import kafvam.rcp.view.TopicMessages;

public class HandlerControlPanel {
	private Logger logger = LogManager.getLogger(getClass());
	private KafConsumer kafConsumer = new KafConsumer();
	private boolean isPollRunning = false;
	private ControlPanel ctrlView;

	public HandlerControlPanel(ControlPanel controlPanel) {
		ctrlView = controlPanel;
	}

	public void startPollHandler() {
		if (isPollRunning)
			stopPollHandler();

		IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(TopicMessages.ID);
		TopicMessages tpMsgView = (TopicMessages) viewPart;
		HandlerPollMessages pollMsgHndlr = tpMsgView.getPollMsgHandler();

		RCPManager.getInstance().getUiChangedEventCaster().notifyUiChangedEvent(new UiChangedEvent() {

			@Override
			public EVENT_TYPE getEventType() {
				return EVENT_TYPE.NOTIFY_CLEAR_LOG;
			}
		});
		kafConsumer.pollMsgs(KafkaInit.getBrokerUrl(), pollMsgHndlr.getTopic(),
				pollMsgHndlr.getSearchMsgs(), false);
		toggleButton(false, ctrlView.getbSstartPoll(), ctrlView.getbStartPollFromBegin());
		toggleButton(true, ctrlView.getbStopPoll());
		isPollRunning = true;
	}

	public void startPollBeginHandler() {
		if (isPollRunning)
			stopPollHandler();
		IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(TopicMessages.ID);
		TopicMessages tpMsgView = (TopicMessages) viewPart;
		HandlerPollMessages pollMsgHndlr = tpMsgView.getPollMsgHandler();

		RCPManager.getInstance().getUiChangedEventCaster().notifyUiChangedEvent(new UiChangedEvent() {

			@Override
			public EVENT_TYPE getEventType() {
				return EVENT_TYPE.NOTIFY_CLEAR_LOG;
			}
		});
		kafConsumer.pollMsgs(KafkaInit.getBrokerUrl(), pollMsgHndlr.getTopic(), pollMsgHndlr.getSearchMsgs(), true);
		toggleButton(false, ctrlView.getbSstartPoll(), ctrlView.getbStartPollFromBegin());
		toggleButton(true, ctrlView.getbStopPoll());
		isPollRunning = true;
	}

	public void stopPollHandler() {
		isPollRunning = false;
		kafConsumer.stopPoll();
		IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(TopicMessages.ID);
		TopicMessages details = (TopicMessages) viewPart;
		toggleButton(false, ctrlView.getbStopPoll());
		if (details.getTabFolder().getSelectionIndex() == 1)
			toggleButton(true, ctrlView.getbSstartPoll(), ctrlView.getbStartPollFromBegin());
		else
			toggleButton(true, ctrlView.getbViewMsg());

	}

	public void viewMsgHandler(Composite parent) {
		logger.info("viewSelectionHandler");
		RCPManager.getInstance().getUiChangedEventCaster().notifyUiChangedEvent(new UiChangedEvent() {
			@Override
			public EVENT_TYPE getEventType() {
				logger.info("Notify Clear Log Triggered");
				return EVENT_TYPE.NOTIFY_CLEAR_LOG;
			}
		});

		IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(TopicMessages.ID);
		TopicMessages tpMsgView = (TopicMessages) viewPart;
		HandlerViewMessages viewMsgHndlr = tpMsgView.getViewMsgHandler();
		String offset = viewMsgHndlr.getOffset();
		String snumOfMsgs = viewMsgHndlr.getNumOfMsgs();
		long iOffset = -1;
		long numOfmsgs = -1;
		if (!offset.isEmpty())
			iOffset = Long.parseLong(offset);
		if (!snumOfMsgs.isEmpty())
			numOfmsgs = Long.parseLong(snumOfMsgs);
		int partition = viewMsgHndlr.getPartition();
		if (partition == -1 && numOfmsgs == -1) {
			MessageBox dialog = WidgetFactory.createmessage(parent.getShell());
			dialog.setText("Message Alert");
			dialog.setMessage("NumofMsgs cant be empty!");
			dialog.open();
			return;

		}
		if (partition != -1 && (numOfmsgs == -1 || iOffset == -1)) {
			MessageBox dialog = WidgetFactory.createmessage(parent.getShell());
			dialog.setText("Message Alert");
			dialog.setMessage("Offset or NumofMsgs cant be empty!");
			dialog.open();
			return;

		}
		if (numOfmsgs > RCPConstants.MSG_LIMIT) {
			MessageBox dialog = WidgetFactory.createmessage(parent.getShell());
			dialog.setText("Message Alert");
			dialog.setMessage("Messages cant be greater than " + RCPConstants.MSG_LIMIT);
			dialog.open();
			return;

		}
		List<String> msgs = new ArrayList<>();
		if (partition == -1)
			msgs = kafConsumer.viewMsgs(KafkaInit.getBrokerUrl(), viewMsgHndlr.getTopic(), viewMsgHndlr.getSearchMsgs(),
					true, -1, -1, numOfmsgs, viewMsgHndlr.getLastOffset());
		else
			msgs = kafConsumer.viewMsgs(KafkaInit.getBrokerUrl(), viewMsgHndlr.getTopic(), viewMsgHndlr.getSearchMsgs(),
					false, partition, iOffset, numOfmsgs, viewMsgHndlr.getLastOffset());

		RCPManager.getInstance().getUiChangedEventCaster().notifyAppendLogEvent(
				new AddLogEventImpl(UiChangedEvent.EVENT_TYPE.NOTIFY_ADD_LOG, "Kafka Message", msgs));
	}

	public void tabSelectionEvent(UiChangedEvent e) {

		if (e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_VIEWMSG
				|| e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_POLLMSG) {

			if (isPollRunning) {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						ctrlView.getbStopPoll().setEnabled(true);

					}
				});
				return;
			}
		}
		if (e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_VIEWMSG)

		{
			logger.info("Notify View Msg Triggered");
			setEnabled(ctrlView.getbViewMsg());
		} else if (e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_POLLMSG) {
			logger.info("Notify Poll Msg Triggered");
			setEnabled(ctrlView.getbSstartPoll(), ctrlView.getbStartPollFromBegin());

		}

	}

	private void toggleButton(boolean value, Button... bs) {
		for (Button b : bs) {
			b.setEnabled(value);
			logger.info("Button:" + b.getText() + " value set to:" + value);
		}
	}

	private void setEnabled(Button... bs) {
		for (Button b : ctrlView.getButtons()) {
			if (chkIfButtonPresent(bs, b)) {
				logger.info("Button Enabled:" + b.getText());
				b.setEnabled(true);
			} else {
				logger.info("Button Disabled:" + b.getText());
				b.setEnabled(false);
			}

		}
	}

	private boolean chkIfButtonPresent(Button[] bs, Button b) {
		return Arrays.asList(bs).contains(b);
	}

	public void shutdown() {
		kafConsumer.stopPoll();
	}

}
