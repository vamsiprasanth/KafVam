package kafvam.rcp.handler;

import static kafvam.rcp.common.RCPConstants.CGMETRICS;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import kafvam.kafka.KafkaInit;
import kafvam.kafka.controller.KafAdminClient;
import kafvam.kafka.entity.CGDetails;
import kafvam.kafka.entity.KafkaTopic;
import kafvam.kafka.entity.KafkaTopicPartition;
import kafvam.rcp.common.WidgetFactory;
import kafvam.rcp.view.TopicMetrics;

/**
 * @author Vamsi Prasanth
 *
 */
public class HandlerTopicMetrics {
	private Logger logger = LogManager.getLogger(getClass());

	private KafAdminClient kac = KafkaInit.getKafAdminClient();
	private Map<Integer, KafkaTopicPartition> tpMap;
	private List<CGDetails> cgDetails;
	private TableViewer topicMetricsViewer;
	private TableViewer cgMetricsViewer;
	private TopicMetrics tpMetricsView;
	private String topicSelected;
	private FormToolkit toolkit;
	private Composite gpViewer;
	private Text tCGrp;
	private Label lCGInfo;

	public HandlerTopicMetrics(TopicMetrics tpMetrics, FormToolkit toolKit) {
		this.tpMetricsView = tpMetrics;
		this.toolkit = toolKit;
		tpMetricsView.getSite().getWorkbenchWindow().getSelectionService()
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
							resetCGComposite();
							topicSelected = data.getName();
							tpMap = data.getPartitionMap();
							String[] titles = { "Partition", "First Offset", "Last Offset", "Size", "Leader" };
							int[] bounds = { 100, 100, 100, 100, 100 };
							populateMetrics("TPMETRICS", tpMetricsView.getTopicMetricsComposite(), data.getName(),
									tpMap, titles, bounds);

						}
					}
				}
			}
		}

	};

	public void initCG(Composite composite) {
		gpViewer = createSectionComposite(composite, "Consumer Group Viewer", 1);
		Label lCGrp = WidgetFactory.createLabel(gpViewer);
		lCGrp.setText("Enter Consumer Group:");
		tCGrp = WidgetFactory.createMultiText(gpViewer);
		GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gridData1.heightHint = 3 * tCGrp.getLineHeight();
		tCGrp.setLayoutData(gridData1);
		Button bCGrp = WidgetFactory.createButton(gpViewer);
		bCGrp.setText("Populate");
		bCGrp.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (WidgetFactory.isValidInput(tCGrp, "Consumer Group")) {
					String[] titles = new String[] { "Partition", "Current Offset", "End Offset", "Lag", "GroupId" };
					int[] bounds = new int[] { 100, 100, 100, 100, 100 };
					populateCGMetrics(gpViewer, tCGrp.getText(), titles, bounds);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		Composite appendix = createSectionComposite(composite, "Appendix", 1);
		Label header1 = toolkit.createLabel(appendix, "");
		header1.setText("1. Multiple consumer groups can be input with separator as '|'");
		header1.setFont(WidgetFactory.BOLD_FONT_8);
		header1.setForeground(new Color(appendix.getDisplay(), new RGB(0, 128, 255)));

	}

	private void populateMetrics(String type, Composite composite, String topic,
			Map<Integer, KafkaTopicPartition> tpMap, String[] titles, int[] bounds) {
		TableViewer viewer;
		if (!type.equals(CGMETRICS)) {
			Control[] controls = composite.getChildren();
			for (Control control : controls) {
				control.dispose();
			}
		}
		if (!type.equals(CGMETRICS)) {
			topicMetricsViewer = new TableViewer(composite, SWT.RESIZE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewer = topicMetricsViewer;
		} else {
			cgMetricsViewer = new TableViewer(composite, SWT.RESIZE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			viewer = cgMetricsViewer;
		}
		viewer.setContentProvider(new ArrayContentProvider());
		if (type.equals(CGMETRICS)) {
			viewer.setInput(cgDetails);
		} else {
			viewer.setInput(tpMap.values().stream().collect(Collectors.toList()));
			viewer.setComparator(new ViewerComparator());
		}
		Table table = viewer.getTable();
		table.setBackground(WidgetFactory.LIGHT_VIOLET_COLOR);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// if (type.equals(CGMETRICS))
		// table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2,
		// 1));

		for (int i = 0; i < titles.length; i++) {
			final String title = titles[i];
			createTableViewerColumn(viewer, title, bounds[i], 0).setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof CGDetails) {
						CGDetails p = (CGDetails) element;
						return getCGMetrics(p, title);
					} else if (element instanceof KafkaTopicPartition) {
						KafkaTopicPartition p = (KafkaTopicPartition) element;
						return getTPMetrics(p, title);
					}
					return "";
				}
			});
			;

		}
		viewer.refresh();

		if (type.equals(CGMETRICS))
			setParentLayout(tpMetricsView.getCgMetricsComposite());
		setParentLayout(composite);
		logger.info("Topic Metrics Populated -" + type);

	}

	protected void resetCGComposite() {
		tCGrp.setText("");
		cgDetails = null;
		if (gpViewer != null) {
			Control[] controls = gpViewer.getChildren();
			for (Control control : controls) {
				if (control instanceof Table || (control == lCGInfo))
					control.dispose();
			}
		}
		setParentLayout(tpMetricsView.getCgMetricsComposite());
	}

	private Composite createSectionComposite(Composite composite, String val, int cols) {
		Section section = WidgetFactory.createTitleOnlySection(toolkit, composite);
		section.setText(val);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite secComposite = WidgetFactory.createComposite(toolkit, section);
		secComposite.setLayout(new GridLayout(cols, false));
		secComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		section.setClient(secComposite);
		return secComposite;
	}

	private void populateCGMetrics(Composite composite, String gpIds, String[] titles, int[] bounds) {
		if (gpIds != null && !gpIds.isEmpty()) {
			cgDetails = kac.getGroupInformation(topicSelected, gpIds);
		}
		Control[] controls = composite.getChildren();
		for (Control control : controls) {
			if (control instanceof Table || (control == lCGInfo))
				control.dispose();
		}
		if (cgDetails == null || cgDetails.isEmpty()) {
			lCGInfo = WidgetFactory.createLabel(composite);
			lCGInfo.setText("Given Consumer Group not Found for Topic-" + topicSelected + "!!");
			lCGInfo.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_RED));
			setParentLayout(tpMetricsView.getCgMetricsComposite());
			composite.layout();
			return;

		}
		populateMetrics(CGMETRICS, composite, topicSelected, tpMap, titles, bounds);
	}

	private String getCGMetrics(CGDetails gcDetails, String title) {
		String retVal = "";
		switch (title) {
		case "Partition":
			retVal = String.valueOf(gcDetails.getPartitionId());
			break;
		case "Current Offset":
			retVal = String.valueOf(gcDetails.getCurOffset());
			break;
		case "End Offset":
			retVal = String.valueOf(gcDetails.getEndOffset());
			break;
		case "Lag":
			retVal = String.valueOf(gcDetails.getLag());
			break;
		case "GroupId":
			retVal = String.valueOf(gcDetails.getGpId());
			break;
		}
		return retVal;
	}

	private String getTPMetrics(KafkaTopicPartition ktp, String title) {
		String retVal = "";
		switch (title) {
		case "Partition":
			retVal = String.valueOf(ktp.getId());
			break;
		case "First Offset":
			retVal = String.valueOf(ktp.getFirstOffset());
			break;
		case "Last Offset":
			retVal = String.valueOf(ktp.getSize());
			break;
		case "Size":
			retVal = String.valueOf(ktp.getSize() - ktp.getFirstOffset());
			break;
		case "Leader":
			retVal = String.valueOf(ktp.getLeader().getId());
			break;
		}
		return retVal;
	}

	private void setParentLayout(Composite composite) {

		composite.layout();
		composite.redraw();

	}

	private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		column.setWidth(bound);
		return viewerColumn;
	}

}
