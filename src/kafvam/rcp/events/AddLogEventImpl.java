package kafvam.rcp.events;

import java.util.List;

/**
 * @author Vamsi Prasanth
 *
 */
public class AddLogEventImpl implements UiChangedEvent {

	private List<String> msgs;
	private String msg;
	private String type;
	private EVENT_TYPE eventType;

	public AddLogEventImpl(EVENT_TYPE _eventType, String _type, List<String> _msgs) {
		eventType = _eventType;
		msgs = _msgs;
		type = _type;

	}

	public AddLogEventImpl(EVENT_TYPE _eventType, String _type, String _msg) {
		eventType = _eventType;
		msg = _msg;
		type = _type;

	}

	@Override
	public EVENT_TYPE getEventType() {
		return eventType;
	}

	public String getType() {
		return type;
	}

	public List<String> getMessages() {
		return msgs;
	}

	public String getMessage() {
		return msg;
	}

}
