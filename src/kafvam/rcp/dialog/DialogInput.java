package kafvam.rcp.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import kafvam.rcp.Activator;

public class DialogInput extends Dialog {

	private static final String DEFAULT_BROKER_URL = "localhost:9092";
	private String kafkaBrokerUrl;
	private String brokerURLFromProps;
	private Text kafkaBrokerUrlText;
	private Text topicPatternText;
	private String topicPattern;
	private String topicPatternFromProps;

	public DialogInput(Shell parentShell, String brokerURLFromProps, String topicPatternFromProps) {
		super(parentShell);
		if (brokerURLFromProps.isEmpty())
			this.brokerURLFromProps = DEFAULT_BROKER_URL;
		else
			this.brokerURLFromProps = brokerURLFromProps;
		this.topicPatternFromProps = topicPatternFromProps;
		topicPattern = "";
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Kafka Broker Details");
		newShell.setImage(Activator.getImageDescriptor("icons/types.gif").createImage());

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Composite comp = (Composite) super.createDialogArea(parent);
		GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 25;
		data.widthHint = 505;
		Label brokerURL = new Label(comp, SWT.RIGHT);
		brokerURL.setText("Broker URL: ");
		kafkaBrokerUrlText = toolkit.createText(comp, brokerURLFromProps, SWT.SINGLE);
		kafkaBrokerUrlText.setLayoutData(data);
		Label lTopicPattern = new Label(comp, SWT.RIGHT);
		lTopicPattern.setText("Topic Pattern:(separate by '|')");
		topicPatternText = toolkit.createText(comp, topicPatternFromProps, SWT.SINGLE);
		topicPatternText.setLayoutData(data);

		return comp;
	}

	public String getBrokerUrl() {
		return kafkaBrokerUrl;
	}

	public String getTopicPattern() {
		return topicPattern;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId != CANCEL) {
			kafkaBrokerUrl = kafkaBrokerUrlText.getText();
			topicPattern = topicPatternText.getText();
		}
		super.buttonPressed(buttonId);
	}
}
