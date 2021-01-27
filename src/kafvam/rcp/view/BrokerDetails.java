package kafvam.rcp.view;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import kafvam.rcp.common.WidgetFactory;
import kafvam.rcp.handler.HandlerBrokerDetails;

public class BrokerDetails extends ViewPart {
	public static final String ID = "KafVam.brokerdetails";
	private FormToolkit toolkit;
	private Section section;
	private Label bootstrapServer;
	private Label totPartition;
	private Label totTopics;
	private Label totCgs;

	@Override
	public void createPartControl(Composite _parent) {

		toolkit = new FormToolkit(_parent.getDisplay());
		_parent.setBackground(WidgetFactory.WHITE_COLOR);
		Font boldFont = WidgetFactory.TAHOMA_FONT;

		section = WidgetFactory.createSection(toolkit, _parent);
		section.setText("Topic Description");
		GridData layout1 = new GridData();
		section.setLayoutData(layout1);
		section.clientVerticalSpacing = 0;
		section.marginWidth = 0;
		section.marginHeight = 0;

		Composite composite = WidgetFactory.createComposite(toolkit, section);
		{
			TableWrapLayout twl_composite = new TableWrapLayout();
			twl_composite.numColumns = 2;
			twl_composite.horizontalSpacing = 25;
			twl_composite.verticalSpacing = 3;
			composite.setLayout(twl_composite);
		}
		composite.layout();

		section.setClient(composite);
		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.verticalSpacing = 3;
		composite.setLayout(gridLayout);
		IHandlerService hs = (IHandlerService) getSite().getService(IHandlerService.class);
		try {
			WidgetFactory.createLabel(toolkit, composite, "BootstrapServer:").setFont(boldFont);
			bootstrapServer = WidgetFactory.createLabel(toolkit, composite, "");
			WidgetFactory.createLabel(toolkit, composite, "Partitions:").setFont(boldFont);
			totPartition = WidgetFactory.createLabel(toolkit, composite, "");
			WidgetFactory.createLabel(toolkit, composite, "Topics:").setFont(boldFont);
			totTopics = WidgetFactory.createLabel(toolkit, composite, "");
			WidgetFactory.createLabel(toolkit, composite, "Consumer Groups:").setFont(boldFont);
			totCgs = WidgetFactory.createLabel(toolkit, composite, "");
			WidgetFactory.createLabel(toolkit, composite, "").setVisible(false);
			hs.executeCommand(HandlerBrokerDetails.ID, null);
		} catch (Exception e) {
			MessageDialog.openError(getSite().getShell(), "Error", "Error opening job.");
			e.printStackTrace();
		}

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
