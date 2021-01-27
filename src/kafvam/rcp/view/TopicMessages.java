package kafvam.rcp.view;

import static kafvam.rcp.common.RCPConstants.POLL_MESSAGES;
import static kafvam.rcp.common.RCPConstants.VIEW_MESSAGES;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import kafvam.rcp.Activator;
import kafvam.rcp.RCPManager;
import kafvam.rcp.common.WidgetFactory;
import kafvam.rcp.events.CTabItemListener;
import kafvam.rcp.events.UiChangedEvent;
import kafvam.rcp.events.UiChangedEvent.EVENT_TYPE;
import kafvam.rcp.handler.HandlerPollMessages;
import kafvam.rcp.handler.HandlerViewMessages;

public class TopicMessages extends ViewPart {
	private Logger logger = LogManager.getLogger(getClass());
	public static final String ID = "KafVam.topicmessages";
	private FormToolkit toolkit;
	private Composite parent;
	private Composite topicMsgsViewComposite;
	private Composite topicMsgsPollComposite;
	private CTabFolder tabFolder;
	private HandlerViewMessages viewMsgHandler;
	private HandlerPollMessages pollMsgHandler;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		toolkit = new FormToolkit(parent.getDisplay());
		GridLayout gridLayout = new GridLayout(1, true);
		parent.setBackground(WidgetFactory.WHITE_COLOR);

		tabFolder = new CTabFolder(parent, SWT.BORDER);
		tabFolder.setSimple(false);
		tabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		CTabItem viewMsgs = new CTabItem(tabFolder, SWT.NONE);
		viewMsgs.setText(VIEW_MESSAGES);
		viewMsgs.setImage(Activator.getImageDescriptor("icons/types.gif").createImage());
		CTabItem pollMsgs = new CTabItem(tabFolder, SWT.NONE);
		pollMsgs.setText(POLL_MESSAGES);
		pollMsgs.setImage(Activator.getImageDescriptor("icons/types.gif").createImage());
		topicMsgsViewComposite = new Composite(tabFolder, SWT.BORDER);
		viewMsgs.setControl(topicMsgsViewComposite);
		topicMsgsViewComposite.setLayout(gridLayout);
		topicMsgsPollComposite = new Composite(tabFolder, SWT.NONE);
		pollMsgs.setControl(topicMsgsPollComposite);
		topicMsgsPollComposite.setLayout(gridLayout);
		addCTabItemListener(viewMsgs, UiChangedEvent.EVENT_TYPE.NOTIFY_VIEWMSG);
		addCTabItemListener(pollMsgs, UiChangedEvent.EVENT_TYPE.NOTIFY_POLLMSG);
		addtabFolderListener();
		tabFolder.setSelection(0);
		parent.layout();
		this.setFocus();
		logger.info("Topic Messages View created!");
		viewMsgHandler = new HandlerViewMessages(this, toolkit);
		pollMsgHandler = new HandlerPollMessages(this, toolkit);
		viewMsgHandler.execute();
		pollMsgHandler.execute();
	}

	private void addCTabItemListener(CTabItem tabItem, EVENT_TYPE eventType) {
		tabItem.addListener(SWT.SELECTED, new Listener() {

			@Override
			public void handleEvent(Event event) {
				logger.info("TabItemSelected:" + tabItem.getText());
				RCPManager.getInstance().getUiChangedEventCaster()
						.notifyUiChangedEvent(new CTabItemListener(eventType));

			}
		});
	}

	protected void addtabFolderListener() {
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tabFolder.getSelection().notifyListeners(SWT.SELECTED, new Event());
			}
		});
	}

	public Composite getTopicMsgsViewComposite() {
		return topicMsgsViewComposite;
	}

	public Composite getTopicMsgsPollComposite() {
		return topicMsgsPollComposite;
	}

	public CTabFolder getTabFolder() {
		return tabFolder;
	}

	public HandlerViewMessages getViewMsgHandler() {
		return viewMsgHandler;
	}

	public HandlerPollMessages getPollMsgHandler() {
		return pollMsgHandler;
	}

	@Override
	public void setFocus() {
		parent.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
