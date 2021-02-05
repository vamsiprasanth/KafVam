package kafvam.rcp.view;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import kafvam.rcp.Activator;
import kafvam.rcp.handler.HandlerTopicMetrics;
/**
 * @author Vamsi Prasanth
 *
 */
public class TopicMetrics extends ViewPart {
	private Logger logger = LogManager.getLogger(getClass());
	public static final String ID = "KafVam.topicmetrics";
	private HandlerTopicMetrics tpMetricsHndlr;
	private FormToolkit toolkit;
	private Composite topicMetricsComposite;
	private Composite cgMetricsComposite;
	private CTabFolder tabFolder;
	private CTabItem topicMetricsTabItem;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());

		tabFolder = new CTabFolder(parent, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tabFolder.setSimple(false);
		tabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		topicMetricsTabItem = new CTabItem(tabFolder, SWT.NONE);
		topicMetricsTabItem.setText("Topic Metrics");
		CTabItem cgMetricsTabItem = new CTabItem(tabFolder, SWT.NONE);
		cgMetricsTabItem.setText("Consumer Group Metrics");

		topicMetricsComposite = new Composite(tabFolder, SWT.NONE);
		topicMetricsTabItem.setControl(topicMetricsComposite);
		this.getClass().getResourceAsStream("icons/kafka.png");
		topicMetricsTabItem.setImage(Activator.getImageDescriptor("icons/kafka.png").createImage());
		topicMetricsComposite.setLayout(new GridLayout(1, false));

		cgMetricsComposite = new Composite(tabFolder, SWT.NONE);
		cgMetricsTabItem.setControl(cgMetricsComposite);
		cgMetricsTabItem.setImage(Activator.getImageDescriptor("icons/kafka.png").createImage());
		GridLayout gridLayout = new GridLayout(1, true);
		cgMetricsComposite.setLayout(gridLayout);
		cgMetricsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		logger.info("Topic Metrics View created!");
		tpMetricsHndlr = new HandlerTopicMetrics(this, toolkit);
		tpMetricsHndlr.initCG(cgMetricsComposite);
		tabFolder.setSelection(0);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public Composite getTopicMetricsComposite() {
		return topicMetricsComposite;
	}

	public Composite getCgMetricsComposite() {
		return cgMetricsComposite;
	}

}
