package kafvam.rcp.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import kafvam.rcp.Activator;
import kafvam.rcp.common.WidgetFactory;

public class DialogInput extends Dialog {

	private static final String DEFAULT_BROKER_URL = "localhost:9092";
	private String kafkaBrokerUrl;
	private String brokerURLFromProps;
	private Text kafkaBrokerUrlText;
	private Text topicPatternText;
	private Text trustLocationText;
	private Text trustPasswdText;
	private Button bIsSSL;
	private String topicPattern;
	private String topicPatternFromProps;
	private boolean isSSL;
	private String trustLocation;
	private String trustPasswd;

	public DialogInput(Shell parentShell, String brokerURLFromProps, String topicPatternFromProps,
			boolean isSSLfromProps, String trustLocationfromProps, String trustPasswdfromProps) {
		super(parentShell);
		if (brokerURLFromProps.isEmpty())
			this.brokerURLFromProps = DEFAULT_BROKER_URL;
		else
			this.brokerURLFromProps = brokerURLFromProps;
		this.topicPatternFromProps = topicPatternFromProps;
		this.isSSL = isSSLfromProps;
		this.trustLocation = trustLocationfromProps;
		this.trustPasswd = trustPasswdfromProps;
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
		Label brokerURL = WidgetFactory.createLabel(toolkit, comp, "Broker URL: ");
		brokerURL.setBackground(parent.getBackground());
		kafkaBrokerUrlText = WidgetFactory.createToolKitText(toolkit, comp, brokerURLFromProps);
		kafkaBrokerUrlText.setLayoutData(data);
		Label lTopicPattern = WidgetFactory.createLabel(toolkit, comp, "Topic Pattern:(separate by '|')");
		lTopicPattern.setBackground(parent.getBackground());
		topicPatternText = WidgetFactory.createToolKitText(toolkit, comp, topicPatternFromProps);
		topicPatternText.setLayoutData(data);
		WidgetFactory.createLabel(toolkit, comp, "").setVisible(false);
		bIsSSL = WidgetFactory.createCheck(toolkit, comp, "isSSLEnabled");
		bIsSSL.setBackground(parent.getBackground());
		bIsSSL.setSelection(isSSL);
		WidgetFactory.createLabel(toolkit, comp, "SSL Trust Location").setBackground(parent.getBackground());
		trustLocationText = WidgetFactory.createToolKitText(toolkit, comp, trustLocation);
		trustLocationText.setLayoutData(data);
		WidgetFactory.createLabel(toolkit, comp, "SSL Trust Passwd").setBackground(parent.getBackground());
		trustPasswdText = WidgetFactory.createToolKitPasswd(toolkit, comp, trustPasswd);
		trustPasswdText.setLayoutData(data);

		return comp;
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
			isSSL = bIsSSL.getSelection();
			trustLocation = trustLocationText.getText();
			trustPasswd = trustPasswdText.getText();
		}
		super.buttonPressed(buttonId);
	}

	public String getBrokerUrl() {
		return kafkaBrokerUrl;
	}

	public String getTopicPattern() {
		return topicPattern;
	}

	public boolean isSSL() {
		return isSSL;
	}

	public String getTrustLocation() {
		return trustLocation;
	}

	public String getTrustPasswd() {
		return trustPasswd;
	}

}
