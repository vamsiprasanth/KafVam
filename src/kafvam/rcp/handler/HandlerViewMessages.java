package kafvam.rcp.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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

public class HandlerViewMessages {
	private Logger logger = LogManager.getLogger(getClass());

	private TopicMessages tpMsgsView;
	private List<String> searchMsgs;
	private String sMsgTxt;
	private String topic;
	private int partition;

	private FormToolkit toolkit;
	private Text fOffset;
	private Text lOffset;
	private Text lSize;
	private Combo combo;
	private Text viewMsgSearchTxt;
	private Text offset;
	private Text numOfMsgs;

	private Map<Integer, Long> partitionFirstOffsetMap = new HashMap<>();
	private Map<Integer, Long> partitionLastOffsetMap = new HashMap<>();
	private long availableSize;

	public HandlerViewMessages(TopicMessages tpMsgsView, FormToolkit toolkit) {
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
							availableSize = data.getAvailableSize();
							populateViewMsgs(data.getName(), tpMap);
						}
					}
				}
			}
		}
	};

	private void populateViewMsgs(String topic, Map<Integer, KafkaTopicPartition> tpMap) {
		Composite topicMsgsViewComposite = tpMsgsView.getTopicMsgsViewComposite();
		this.topic = topic;

		Control[] controls = topicMsgsViewComposite.getChildren();
		for (Control control : controls) {
			control.dispose();
		}
		partitionFirstOffsetMap.clear();
		partitionLastOffsetMap.clear();
		tpMap.entrySet().forEach(x -> {
			partitionFirstOffsetMap.put(x.getKey(), x.getValue().getFirstOffset());
			partitionLastOffsetMap.put(x.getKey(), x.getValue().getSize());

		});
		Composite messageViewer = createSectionComposite(topicMsgsViewComposite, "Topic: " + topic, 6, true);
		Color foreColor = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
		fOffset = WidgetFactory.createNoBorderText(messageViewer);
		fOffset.setFont(WidgetFactory.TAHOMA_BOLD_FONT_8);
		fOffset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		fOffset.setEditable(false);
		fOffset.setBackground(messageViewer.getBackground());
		fOffset.setForeground(foreColor);
		lOffset = WidgetFactory.createNoBorderText(messageViewer);
		lOffset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		lOffset.setFont(WidgetFactory.TAHOMA_BOLD_FONT_8);
		lOffset.setEditable(false);
		lOffset.setBackground(messageViewer.getBackground());
		lOffset.setForeground(foreColor);

		lSize = WidgetFactory.createNoBorderText(messageViewer);
		lSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		lSize.setFont(WidgetFactory.TAHOMA_BOLD_FONT_8);
		lSize.setEditable(false);
		lSize.setBackground(messageViewer.getBackground());
		lSize.setForeground(foreColor);

		Composite selectComposite = createSectionComposite(topicMsgsViewComposite, "", 5, false);

		Label lpartition = WidgetFactory.createLabel(selectComposite);
		lpartition.setText("Partition");
		lpartition.setFont(WidgetFactory.BOLD_FONT_8);

		combo = WidgetFactory.buildCombo(selectComposite);
		combo.add("-1");
		tpMap.entrySet().forEach(x -> combo.add(String.valueOf(x.getKey())));
		combo.select(1);

		Label loffset = WidgetFactory.createLabel(selectComposite);
		loffset.setFont(WidgetFactory.BOLD_FONT_8);
		loffset.setText("Offset");
		offset = WidgetFactory.createText(selectComposite);
		offset.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		// WidgetFactory.createLabel(selectComposite).setLayoutData(new
		// GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));

		Label lnumOfMsgs = WidgetFactory.createLabel(selectComposite);
		lnumOfMsgs.setText("Num of Messages");
		lnumOfMsgs.setFont(WidgetFactory.BOLD_FONT_8);
		numOfMsgs = WidgetFactory.createText(selectComposite);
		numOfMsgs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));

		// WidgetFactory.createLabel(selectComposite).setLayoutData(new
		// GridData(SWT.FILL, SWT.FILL, true, false, 5, 1));

		Label sStr = WidgetFactory.createLabel(selectComposite);
		sStr.setText("Search String");
		sStr.setFont(WidgetFactory.BOLD_FONT_8);
		viewMsgSearchTxt = WidgetFactory.createMultiText(selectComposite);
		GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, false, 5, 1);
		gridData1.heightHint = 3 * viewMsgSearchTxt.getLineHeight();
		viewMsgSearchTxt.setLayoutData(gridData1);
		// WidgetFactory.createLabel(selectComposite).setLayoutData(gridData1);

		Composite appendix = createSectionComposite(topicMsgsViewComposite, "Appendix", 1, true);
		createHeader(appendix, "1. Select Partition as -1 to view messages from beginning offset from all partitions");
		createHeader(appendix, "2." + sMsgTxt);
		createHeader(appendix, "3. Max messages that can be viewed is 1000");

		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLabels();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		combo.notifyListeners(SWT.Selection, null);
		setParentLayout(topicMsgsViewComposite);
		logger.info("View Messages Tab Populated!");

	}

	public List<String> getSearchMsgs() {
		searchMsgs = null;
		if (viewMsgSearchTxt != null && !viewMsgSearchTxt.getText().isEmpty()
				&& !viewMsgSearchTxt.getText().contains("as separator for multiple search string"))
			searchMsgs = new ArrayList<String>(Arrays.asList(viewMsgSearchTxt.getText().split("\\|")));

		logger.info("Search Messages:" + searchMsgs);
		return searchMsgs;
	}

	private void updateLabels() {
		partition = Integer.parseInt(combo.getText());
		long fOffset_l = -1;
		long lOffset_l = -1;
		if (partition == -1) {
			fOffset_l = partitionFirstOffsetMap.values().stream().min(Long::compare).get();
			lOffset_l = partitionLastOffsetMap.values().stream().max(Long::compare).get();
			lSize.setText("Available Messages: " + availableSize);
			offset.setEnabled(false);
		} else {
			fOffset_l = partitionFirstOffsetMap.get(partition);
			lOffset_l = partitionLastOffsetMap.get(partition);
			lSize.setText("Available Messages: " + (lOffset_l - fOffset_l));
			offset.setEnabled(true);
		}
		fOffset.setText("First Offset: " + fOffset_l);
		lOffset.setText("Last Offset: " + lOffset_l);
		fOffset.requestLayout();
		lOffset.requestLayout();
		lSize.requestLayout();

	}

	private void createHeader(Composite composite, String appendix) {
		Label header1 = toolkit.createLabel(composite, "");
		header1.setText(appendix);
		header1.setFont(WidgetFactory.BOLD_FONT_8);
		header1.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));
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

	public long getLastOffset() {
		String val = lOffset.getText().replace("Last Offset: ", "");
		if (!val.isEmpty()) {
			return Long.parseLong(val);
		}
		return 0;
	}

	public String getOffset() {
		return offset.getText();
	}

	public String getNumOfMsgs() {
		return numOfMsgs.getText();
	}

	public String getTopic() {
		return topic;
	}

	public int getPartition() {
		return partition;
	}

}
