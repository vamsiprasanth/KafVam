package kafvam.rcp.events;

/**
 * @author Vamsi Prasanth
 *
 */
public interface UiChangedEvent
{

	enum EVENT_TYPE {
		NOTIFY_ADD_LOG_APPEND,
		NOTIFY_ADD_LOG,
		NOTIFY_CLEAR_LOG,
		NOTIFY_VIEWMSG,
		NOTIFY_POLLMSG

	};

	public abstract EVENT_TYPE getEventType();

}
