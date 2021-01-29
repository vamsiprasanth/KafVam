package kafvam.rcp.view;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import kafvam.rcp.common.WidgetFactory;
import kafvam.rcp.handler.HandlerBrokerDetails;

public class BrokerDetails extends ViewPart {
	public static final String ID = "KafVam.brokerdetails";
	private Logger logger = LogManager.getLogger(getClass());

	private FormToolkit toolkit;
	private ScrolledForm form;
	private Section section;
	private Label bootstrapServer;
	private Label totPartition;
	private Label totTopics;
	private Label totCgs;

	@Override
	public void createPartControl(Composite _parent) {

		toolkit = new FormToolkit(_parent.getDisplay());
		form = toolkit.createScrolledForm(_parent);
		_parent.setBackground(WidgetFactory.WHITE_COLOR);
		form.getBody().setLayout(new ColumnLayout());
		Font boldFont = WidgetFactory.TAHOMA_FONT;

		section = WidgetFactory.createSection(toolkit, form.getBody());
		section.setText("Topic Description");
		section.clientVerticalSpacing = 0;
		section.marginWidth = 0;
		section.marginHeight = 0;

		Composite composite = WidgetFactory.createComposite(toolkit, section);

		section.setClient(composite);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 3;
		composite.setLayout(gridLayout);
		IHandlerService hs = (IHandlerService) getSite().getService(IHandlerService.class);
		try {
			WidgetFactory.createLabel(toolkit, composite, "BootstrapServer:").setFont(boldFont);
			bootstrapServer = WidgetFactory.createWrapLabel(toolkit, composite, "");
			bootstrapServer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			WidgetFactory.createLabel(toolkit, composite, "Partitions:").setFont(boldFont);
			totPartition = WidgetFactory.createLabel(toolkit, composite, "");
			WidgetFactory.createLabel(toolkit, composite, "Topics:").setFont(boldFont);
			totTopics = WidgetFactory.createLabel(toolkit, composite, "");
			WidgetFactory.createLabel(toolkit, composite, "Consumer Groups:").setFont(boldFont);
			totCgs = WidgetFactory.createLabel(toolkit, composite, "");
			WidgetFactory.createLabel(toolkit, composite, "").setVisible(false);
			hs.executeCommand(HandlerBrokerDetails.ID, null);
		} catch (Exception e) {
			MessageDialog.openError(getSite().getShell(), "Error", e.getMessage());
			logger.error(e);
			System.exit(1);
		}
		composite.layout();
	}

	@Override
	public void setFocus() {
		section.setFocus();
	}

	public Label getBootstrapServer() {
		return bootstrapServer;
	}

	public Label getTotPartition() {
		return totPartition;
	}

	public Label getTotTopics() {
		return totTopics;
	}

	public Label getTotCgs() {
		return totCgs;
	}

}
