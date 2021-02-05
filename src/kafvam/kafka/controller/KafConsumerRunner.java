package kafvam.kafka.controller;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import kafvam.rcp.RCPManager;
import kafvam.rcp.events.AddLogEventImpl;
import kafvam.rcp.events.UiChangedEvent;

/**
 * @author Vamsi Prasanth
 *
 */
public class KafConsumerRunner implements Runnable {
	private Logger logger = LogManager.getLogger(getClass());

	private final AtomicBoolean closed = new AtomicBoolean(false);
	private Consumer<String, String> consumer;
	private boolean fromBeginning;
	private String topic;
	private List<String> searchMsgs;

	public KafConsumerRunner(Consumer<String, String> pollKafConsumer, List<String> searchMsgs, String topic,
			boolean fromBeginning) {
		this.topic = topic;
		this.fromBeginning = fromBeginning;
		this.consumer = pollKafConsumer;
		this.searchMsgs = searchMsgs;
	}

	public void run() {
		try {
			logger.info("FromBeginning flag:" + fromBeginning);
			logger.info("isSearchMsgs empty:"+(searchMsgs == null || searchMsgs.isEmpty()));
			consumer.subscribe(Collections.singletonList(topic), new ConsumerRebalanceListener() {

				@Override
				public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				}

				@Override
				public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
					if (fromBeginning) {
						consumer.seekToBeginning(partitions);
						logger.info("Partition Seek to Beginning");

					}

				}
			});
			consumer.subscribe(Arrays.asList(topic));
			while (!closed.get()) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
				for (ConsumerRecord<String, String> rec : records) {
					long currentOffset = rec.offset();
					String msg = "Offset:" + currentOffset + ", Key:" + rec.key() + ", Val:" + rec.value();
					if (searchMsgs == null || searchMsgs.isEmpty()) {
						RCPManager.getInstance().getUiChangedEventCaster().notifyAppendLogEvent(new AddLogEventImpl(
								UiChangedEvent.EVENT_TYPE.NOTIFY_ADD_LOG_APPEND, "Kafka Message", msg));
					} else {
						searchMsgs.forEach(x -> logger.info("Search msg:" + x));
						if (match(searchMsgs, rec.value())) {
							RCPManager.getInstance().getUiChangedEventCaster().notifyAppendLogEvent(new AddLogEventImpl(
									UiChangedEvent.EVENT_TYPE.NOTIFY_ADD_LOG_APPEND, "Kafka Message", msg));

						}
					}
				}

			}
		} catch (WakeupException e) {
			logger.info("Kafka Wake up Exception:" + e.getMessage());
			if (!closed.get())
				throw e;
		} finally {
			consumer.close();
			logger.info("Kafka Consumer closed");
		}
	}

	private boolean match(List<String> searchMsgs, String match) {
		logger.debug("match:" + match);
		for (String msg : searchMsgs) {
			boolean flag = true;
			for (String innerMsg : msg.split("&")) {
				if (!match.toUpperCase().matches(".*" + innerMsg.toUpperCase().trim() + ".*")) {
					flag = false;
				}

			}
			if (flag)
				return true;
		}
		return false;
	}

	public void shutdown() {
		logger.info("Shutdown called");
		closed.set(true);
		consumer.wakeup();
	}
}
