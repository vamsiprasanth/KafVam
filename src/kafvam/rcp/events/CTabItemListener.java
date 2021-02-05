package kafvam.rcp.events;

/**
 * @author Vamsi Prasanth
 *
 */
public class CTabItemListener implements UiChangedEvent {
	public EVENT_TYPE eventType;

	public CTabItemListener(EVENT_TYPE eventType) {
		this.eventType = eventType;
	}

	@Override
	public EVENT_TYPE getEventType() {
		return eventType;
	}

}
