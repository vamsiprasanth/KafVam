package kafvam.rcp.common;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import kafvam.rcp.RCPManager;
import kafvam.rcp.events.AddLogEventImpl;
import kafvam.rcp.events.UiChangedEvent;
import kafvam.rcp.events.UiChangedListener;

public class RCPConsoleLogger implements UiChangedListener {
	private Logger logger = LogManager.getLogger(getClass());
	private static final int CLEAR_CONSOLE_LIMIT = 30000;
	private MessageConsole myConsole;
	private MessageConsoleStream stream;
	private int logLineCount;

	public MessageConsole getMyConsole() {
		return myConsole;
	}

	public void init() {
		myConsole = new MessageConsole("Message Log", null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { myConsole });
		stream = myConsole.newMessageStream();
		RCPManager.getInstance().addUiChangedListener(this);
		logLineCount = 0;
		logger.info("Initialized Console Logger");

	}

	@Override
	public void handleChangeEvent(UiChangedEvent e) {

		if (e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_CLEAR_LOG) {
			clearConsole();
		} else if (e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_ADD_LOG
				|| e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_ADD_LOG_APPEND) {
			final AddLogEventImpl event = (AddLogEventImpl) e;
			try {

				if (stream != null) {
					if (e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_ADD_LOG) {
						for (String msg : event.getMessages()) {
							stream.write(msg);
							stream.write("\n");
							if (logLineCount % 1000 == 0)
								stream.flush();
							logLineCount++;

						}
					} else if (e.getEventType() == UiChangedEvent.EVENT_TYPE.NOTIFY_ADD_LOG_APPEND) {
						stream.write(event.getMessage());
						stream.write("\n");
						if (logLineCount % 1000 == 0)
							stream.flush();

						logLineCount++;
					}
					if (logLineCount > CLEAR_CONSOLE_LIMIT) {
						logger.info("Console cleared as max limit reached");
						clearConsole();
					}
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void clearConsole() {
		if (myConsole != null) {
			logLineCount = 0;
			myConsole.clearConsole();
			logger.info("Console Cleared");
		}

	}

	public void dispose() {
		RCPManager.getInstance().removeUiChangedListener(this);

	}
}
