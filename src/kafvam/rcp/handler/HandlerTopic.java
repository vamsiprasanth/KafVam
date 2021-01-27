package kafvam.rcp.handler;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import kafvam.kafka.KafkaInit;
import kafvam.kafka.controller.KafAdminClient;
import kafvam.kafka.entity.KafkaTopic;
import kafvam.rcp.view.Topic;

public class HandlerTopic {
	private Logger logger = LogManager.getLogger(getClass());
	private KafAdminClient kac = KafkaInit.getKafAdminClient();
	private Topic tpView;

	public HandlerTopic(Topic topic) {
		tpView = topic;
	}

	public void init() {
		TableViewer viewer = tpView.getViewer();
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(kac.getAllTopics());
		viewer.setComparator(new ViewerComparator());

	}

	public void execute() {
		TableViewer viewer = tpView.getViewer();
		Table table = viewer.getTable();
		MyFilter filter = new MyFilter();
		String[] titles = { "Topic", "Partitions", "Total Msgs Published", "Total Msgs Available" };
		int[] bounds = { 200, 100, 150, 100 };
		TableViewerColumn tpNameCol = createTableViewerColumn(viewer, titles[0], bounds[0], 0);
		tpNameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				KafkaTopic p = (KafkaTopic) element;
				return p.getName();
			}
		});
		TableViewerColumn partitionCol = createTableViewerColumn(viewer, titles[1], bounds[1], 0);
		partitionCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				KafkaTopic p = (KafkaTopic) element;
				return String.valueOf(p.getTotalPartitions());
			}
		});
		TableViewerColumn pubMsgsCol = createTableViewerColumn(viewer, titles[2], bounds[2], 0);
		pubMsgsCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				KafkaTopic p = (KafkaTopic) element;
				return String.valueOf(p.getTotalSize());
			}
		});
		TableViewerColumn availableMsgsCol = createTableViewerColumn(viewer, titles[3], bounds[3], 0);
		availableMsgsCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				KafkaTopic p = (KafkaTopic) element;
				return String.valueOf(p.getAvailableSize());
			}
		});
		tpView.getFilterTxt().addListener(SWT.Verify, new Listener() {
			@Override
			public void handleEvent(Event e) {
				final String oldS = ((Text) e.widget).getText();
				final String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
				filter.setSearchText(newS);
				viewer.refresh();
			}
		});
		viewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				KafkaTopic t1 = (KafkaTopic) e1;
				KafkaTopic t2 = (KafkaTopic) e2;
				return t1.getName().compareTo(t2.getName());
			};
		});
		viewer.addFilter(filter);
		for (int i = 0, n = table.getColumnCount(); i < n; i++)
			table.getColumn(i).pack();
		tpView.getSite().setSelectionProvider(viewer);
		logger.info("Topic view initialized!");
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

class MyFilter extends ViewerFilter {
	private Logger logger = LogManager.getLogger(getClass());

	private String searchString;

	public void setSearchText(String s) {
		this.searchString = ".*" + s + ".*";
		logger.info(searchString);
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		KafkaTopic p = (KafkaTopic) element;

		if (p.getName().toLowerCase().matches(searchString.toLowerCase())) {
			return true;
		}

		return false;
	}
}