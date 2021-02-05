package kafvam.rcp.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Vamsi Prasanth
 *
 */
public class UiChangedEventCaster
{
	private List<UiChangedListener> listeners = new CopyOnWriteArrayList<UiChangedListener>();

	public synchronized void addListener(UiChangedListener listener)
	{
		listeners.add(listener);

	}

	public synchronized void removeListener(UiChangedListener listener)
	{
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	public synchronized void notifyUiChangedEvent(UiChangedEvent event)
	{
		for (UiChangedListener listener : listeners) {
			listener.handleChangeEvent(event);
		}
	}

	public void notifySimulatorUiLogEvent(UiChangedEvent event)
	{
		for (UiChangedListener listener : listeners) {
			listener.handleChangeEvent(event);
		}
	}

	public void notifyAppendLogEvent(UiChangedEvent event)
	{
		for (UiChangedListener listener : listeners) {
			listener.handleChangeEvent(event);
		}
	}

	public void notifyDialogQuitEvent(UiChangedEvent event)
	{
		for (UiChangedListener listener : listeners) {
			listener.handleChangeEvent(event);
		}
	}
}
