package kafvam.rcp.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import kafvam.kafka.entity.KafkaTopic;
import kafvam.kafka.entity.KafkaTopicPartition;
import kafvam.rcp.common.WidgetFactory;
import kafvam.rcp.view.TopicMessages;

public class HandlerPollMessages {
	private Logger logger = LogManager.getLogger(getClass());

	private TopicMessages tpMsgsView;
	private List<String> searchMsgs;
	private String sMsgTxt;
	private String topic;

	private FormToolkit toolkit;
	private Text pollMsgSearchTxt;

	public HandlerPollMessages(TopicMessages tpMsgsView, FormToolkit toolkit) {
		this.tpMsgsView = tpMsgsView;
		this.toolkit = toolkit;
	}

	public void execute() {
		sMsgTxt = "'|' as separator for multiple search strings '&&' as separator in search string.\n   Eg:Apple&&Machintosh|Apple&&Ambrosia|Orange\n   The above example matches Apple and Gala in the same Kafka message\n   or Apple and McIntosh or Orange ";

		tpMsgsView.getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this.outLineSelectionChangedListener);

	}

	private final ISelectionListener outLineSelectionChangedListener = new ISelectionListener() {

		@Override
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (sourcepart.getTitle().equals("Topic")) {
				if (selection instanceof IStructuredSelection) {
					Object o = ((IStructuredSelection) selection).getFirstElement();
					if (o instanceof KafkaTopic) {
						KafkaTopic data = (KafkaTopic) o;
						if (data != null) {
							tpMsgsView.getTabFolder().notifyListeners(SWT.Selection, new Event());
							Map<Integer, KafkaTopicPartition> tpMap = data.getPartitionMap();
							populatePollMsgs(data.getName(), tpMap);
						}
					}
				}
			}
		}
	};

	private void populatePollMsgs(String topic, Map<Integer, KafkaTopicPartition> tpMap) {
		Composite topicMsgsPollComposite = tpMsgsView.getTopicMsgsPollComposite();
		Control[] controls = topicMsgsPollComposite.getChildren();
		for (Control control : controls) {
			control.dispose();
		}
		this.topic = topic;
		Composite messagePollViewer = createSectionComposite(topicMsgsPollComposite, "Topic: " + topic, 6, true);
		Label sStr = WidgetFactory.createLabel(messagePollViewer);
		sStr.setFont(WidgetFactory.BOLD_FONT_8);
		sStr.setText("Search String");
		pollMsgSearchTxt = WidgetFactory.createMultiText(messagePollViewer);
		GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, false, 6, 1);
		gridData1.heightHint = 3 * pollMsgSearchTxt.getLineHeight();
		pollMsgSearchTxt.setLayoutData(gridData1);
		WidgetFactory.createLabel(messagePollViewer).setLayoutData(gridData1);
		Composite appendix = createSectionComposite(topicMsgsPollComposite, "Appendix", 6, true);
		Label header1 = WidgetFactory.createLabel(appendix);
		header1.setText("1. " + sMsgTxt);
		header1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));
		header1.setFont(WidgetFactory.BOLD_FONT_8);
		header1.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 1, 1));

		setParentLayout(topicMsgsPollComposite);
		logger.info("Poll Messages Tab Populated!");
	}

	public List<String> getSearchMsgs() {
		searchMsgs = null;
		if (pollMsgSearchTxt != null && !pollMsgSearchTxt.getText().isEmpty())
			searchMsgs = new ArrayList<String>(Arrays.asList(pollMsgSearchTxt.getText().split("\\|")));
		logger.info("Search Messages:" + searchMsgs);
		return searchMsgs;
	}

	private Composite createSectionComposite(Composite composite, String val, int cols, boolean title) {
		Section section;
		if (title) {
			section = WidgetFactory.createTitleOnlySection(toolkit, composite);
			section.setText(val);
		} else {
			section = WidgetFactory.createNoTitleOnlySection(toolkit, composite);
		}
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Composite secComposite = WidgetFactory.createComposite(toolkit, section);
		secComposite.setLayout(new GridLayout(cols, false));
		section.setClient(secComposite);
		return secComposite;
	}

	private void setParentLayout(Composite composite) {
		composite.layout();
		composite.redraw();

	}

	public String getTopic() {
		return topic;
	}

}
