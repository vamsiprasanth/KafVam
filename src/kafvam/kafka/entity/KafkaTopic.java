package kafvam.kafka.entity;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaTopic {
	private final String name;
	private ConcurrentHashMap<Integer, KafkaTopicPartition> partitions = new ConcurrentHashMap<>();

	public KafkaTopic(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getTotalPartitions() {
		return partitions.size();
	}

	/**
	 * Returns the total number of messages published to the topic, ever
	 * 
	 * @return
	 */
	public long getTotalSize() {
		return partitions.values().stream().map(KafkaTopicPartition::getSize).reduce(0L, Long::sum);
	}

	/**
	 * Returns the total number of messages available to consume from the topic.
	 * 
	 * @return
	 */
	public long getAvailableSize() {
		return partitions.values().stream().map(p -> p.getSize() - p.getFirstOffset()).reduce(0L, Long::sum);
	}

	public ConcurrentHashMap<Integer, KafkaTopicPartition> getPartitionMap() {
		return partitions;
	}

	public void setPartitions(ConcurrentHashMap<Integer, KafkaTopicPartition> partitions) {
		this.partitions = partitions;
	}

	public Optional<KafkaTopicPartition> getPartition(int partitionId) {
		return Optional.ofNullable(partitions.get(partitionId));
	}
}
