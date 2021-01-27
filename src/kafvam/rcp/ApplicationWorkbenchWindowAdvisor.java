package kafvam.rcp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;

import kafvam.kafka.KafkaInit;
import kafvam.rcp.common.RCPConsoleLogger;
import kafvam.rcp.dialog.DialogInput;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private Logger logger = LogManager.getLogger(getClass());
	private RCPConsoleLogger rcpConsoleLogger;

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowPerspectiveBar(true);
		configurer.setShowProgressIndicator(true);
		configurer.setTitle("KafVam - Kafka Message Viewer");

		RCPManager.getInstance().init();

		BasicConfigurator.configure();
		String log4jFromBundlePath = Platform.getInstallLocation().getURL().getPath() + "log4j.properties";
		String log4jConfPath = "C:/tmp" + "/log4j.properties";
		if (new File(log4jFromBundlePath).isFile()) {
			PropertyConfigurator.configure(log4jFromBundlePath);
			logger.info("Log4j file loaded from path:" + log4jFromBundlePath);
		} else {
			PropertyConfigurator.configure(log4jConfPath);
			logger.info("Log4j file loaded from path:" + log4jConfPath);

		}
		String propFilePath = Platform.getInstallLocation().getURL().getPath() + "kafvam.properties";
		logger.info("Property File Path :" + propFilePath);
		String brokerURLFromProps = "";
		String topicPattern = "";
		if (new File(propFilePath).isFile()) {
			try (InputStream input = new FileInputStream(propFilePath)) {
				logger.info("Loaded properties");
				Properties props = new Properties();
				props.load(input);
				brokerURLFromProps = props.getProperty("brokerurl");
				topicPattern = props.getProperty("topicpattern");
			} catch (IOException e) {
				logger.error("Unable to load from Property file");
			}
		}
		logger.info("BrokerUrlfromProps:" + brokerURLFromProps + ", topicPattern:" + topicPattern);

		DialogInput dialog = new DialogInput(getScreenCentredShell(), brokerURLFromProps, topicPattern);
		int returnCode = dialog.open();
		logger.info("DialogInput returnVal:" + returnCode);
		if (returnCode == 1)
			System.exit(0);

		KafkaInit.init(dialog.getBrokerUrl(), dialog.getTopicPattern());
		checkBrokerUrlValid(KafkaInit.getBrokerUrl());
		logger.info(
				"Values from Dialog,BrokerUrl:" + dialog.getBrokerUrl() + ", topicPattern:" + dialog.getTopicPattern());

		rcpConsoleLogger = new RCPConsoleLogger();
		rcpConsoleLogger.init();
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(rcpConsoleLogger.getMyConsole());

	}

	private void checkBrokerUrlValid(String brokerUrl) {
		AdminClient adminClient = null;
		try {
			final Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
			props.put(ConsumerConfig.CLIENT_ID_CONFIG, "kafdrop-admin");
			props.put("retries", 3);
			props.put("request.timeout.ms", 5000);
			props.put("default.api.timeout.ms", 5000);
			adminClient = AdminClient.create(props);
			adminClient.listTopics(new ListTopicsOptions().timeoutMs(5000)).listings().get();
		} catch (Exception e) {
			MessageBox dialog = new MessageBox(getScreenCentredShell(), SWT.ICON_WARNING | SWT.OK);
			dialog.setText("Error in Kafka Broker Initialization");
			e.printStackTrace();
			dialog.setMessage("Please check the Broker URL: " + e.getMessage());
			dialog.open();
			System.exit(1);
		} finally {
			if (adminClient != null)
				adminClient.close();
		}

	}

	@Override
	public void postWindowCreate() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
				.setImage(Activator.getImageDescriptor("icons/types.gif").createImage());
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setBackgroundMode(SWT.INHERIT_FORCE);
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.getWindow().getShell().setMaximized(true);
	}

	public Shell getScreenCentredShell() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell centreShell = new Shell(display);
		Point size = centreShell.computeSize(-1, -1);
		Rectangle screen = display.getMonitors()[0].getBounds();
		centreShell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
		return centreShell;
	}

	@Override
	public boolean preWindowShellClose() {
		rcpConsoleLogger.dispose();
		return super.preWindowShellClose();
	}
}
