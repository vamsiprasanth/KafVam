package kafvam.rcp;

import kafvam.rcp.events.UiChangedEventCaster;
import kafvam.rcp.events.UiChangedListener;

/**
 * @author Vamsi Prasanth
 *
 */
public class RCPManager {
	private static RCPManager rcpManager = new RCPManager();
	private UiChangedEventCaster uiChangedEventCaster;

	public static RCPManager getInstance() {
		return rcpManager;
	}

	public void init() {
		uiChangedEventCaster = new UiChangedEventCaster();
	}

	public UiChangedEventCaster getUiChangedEventCaster() {
		return uiChangedEventCaster;
	}

	public void addUiChangedListener(UiChangedListener listener) {
		uiChangedEventCaster.addListener(listener);
	}

	public void removeUiChangedListener(UiChangedListener listener) {
		uiChangedEventCaster.removeListener(listener);
	}

}
