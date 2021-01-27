package kafvam.rcp.view;

import static kafvam.rcp.common.RCPConstants.BEGIN_POLL;
import static kafvam.rcp.common.RCPConstants.LATEST_POLL;
import static kafvam.rcp.common.RCPConstants.STOP_POLL;
import static kafvam.rcp.common.RCPConstants.VIEW_MESSAGES;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import kafvam.rcp.RCPManager;
import kafvam.rcp.common.WidgetFactory;
import kafvam.rcp.events.UiChangedEvent;
import kafvam.rcp.events.UiChangedListener;
import kafvam.rcp.handler.HandlerControlPanel;

public class ControlPanel extends ViewPart implements UiChangedListener {
	private Logger logger = LogManager.getLogger(getClass());
	public static final String ID = "KafVam.controlpanel";
	private List<Button> buttons = new ArrayList<>();
	private Composite composite;
	private Button bViewMsg;
	private Button bSstartPoll;
	private Button bStopPoll;
	private Button bStartPollFromBegin;
	private HandlerControlPanel ctrlPanelHndlr;

	@Override
	public void createPartControl(Composite parent) {
		RCPManager.getInstance().addUiChangedListener(this);

		buildButtonPanel(parent);
		bViewMsg = WidgetFactory.createButton(composite);
		bViewMsg.setText(VIEW_MESSAGES);
		bViewMsg.setEnabled(false);
		buttons.add(bViewMsg);
		bSstartPoll = WidgetFactory.createButton(composite);
		bSstartPoll.setText(LATEST_POLL);
		bSstartPoll.setEnabled(false);
		buttons.add(bSstartPoll);
		bStartPollFromBegin = WidgetFactory.createButton(composite);
		bStartPollFromBegin.setText(BEGIN_POLL);
		bStartPollFromBegin.setEnabled(false);
		buttons.add(bStartPollFromBegin);
		bStopPoll = WidgetFactory.createButton(composite);
		bStopPoll.setText(STOP_POLL);
		bStopPoll.setEnabled(false);
		buttons.add(bStopPoll);

		bViewMsg.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				logger.info("View Message Selected");
				ctrlPanelHndlr.viewMsgHandler(parent);
			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		bSstartPoll.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				logger.info("Start Poll from Latest Selected");
				ctrlPanelHndlr.startPollHandler();

			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		bStartPollFromBegin.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				logger.info("Start Poll from Beginning Selected");
				ctrlPanelHndlr.startPollBeginHandler();

			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		bStopPoll.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				logger.info("Stop Poll Selected");
				ctrlPanelHndlr.stopPollHandler();

			}

			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		composite.layout();
		ctrlPanelHndlr = new HandlerControlPanel(this);
	}

	@Override
	public void setFocus() {
		bViewMsg.setFocus();
	}

	private void buildButtonPanel(Composite parent) {
		composite = parent;
		GridLayout gridLayoutObj = new GridLayout(1, true);
		gridLayoutObj.horizontalSpacing = 15;
		FillLayout layout = new FillLayout();
		layout.type = SWT.VERTICAL;
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		layout.spacing = 2;

		composite.setLayout(layout);

	}

	@Override
	public void handleChangeEvent(UiChangedEvent e) {
		ctrlPanelHndlr.tabSelectionEvent(e);
	}

	@Override
	public void dispose() {
		RCPManager.getInstance().removeUiChangedListener(this);
		ctrlPanelHndlr.shutdown();
	}

	public Button getbViewMsg() {
		return bViewMsg;
	}

	public Button getbSstartPoll() {
		return bSstartPoll;
	}

	public Button getbStopPoll() {
		return bStopPoll;
	}

	public Button getbStartPollFromBegin() {
		return bStartPollFromBegin;
	}

	public List<Button> getButtons() {
		return buttons;
	}

}
