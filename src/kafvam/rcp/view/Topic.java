package kafvam.rcp.view;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import kafvam.rcp.Activator;
import kafvam.rcp.action.Refresh;
import kafvam.rcp.common.WidgetFactory;
import kafvam.rcp.handler.HanderTopicViewer;
import kafvam.rcp.handler.HandlerTopic;

public class Topic extends ViewPart {
	private Logger logger = LogManager.getLogger(getClass());
	public static final String ID = "KafVam.topic";
	private TableViewer viewer;
	private HandlerTopic tpHandler;
	private Text filterTxt;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		filterTxt = WidgetFactory.createText(parent);
		filterTxt.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Table table = viewer.getTable();
		final Color backGround = new Color(Display.getDefault(), 244, 244, 244);
		table.setBackground(backGround);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		tpHandler = new HandlerTopic(this);
		tpHandler.init();
		tpHandler.execute();

		Refresh lCustomAction = new Refresh(viewer);
		lCustomAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh.gif"));
		lCustomAction.setToolTipText("Refresh Topic List");
		getViewSite().getActionBars().getToolBarManager().add(lCustomAction);

		addListener(viewer);
	}

	private void addListener(TableViewer viewer) {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IHandlerService hs = (IHandlerService) getSite().getService(IHandlerService.class);

				try {
					hs.executeCommand(HanderTopicViewer.ID, null);
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}

			}
		});

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void setFocus() {

	}

	public Text getFilterTxt() {
		return filterTxt;
	}

	public TableViewer getViewer() {
		return viewer;
	}
}
