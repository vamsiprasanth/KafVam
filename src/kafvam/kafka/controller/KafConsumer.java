package kafvam.kafka.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import kafvam.kafka.KafkaInit;
import kafvam.kafka.entity.KafkaTopic;
import kafvam.kafka.entity.KafkaTopicPartition;
import kafvam.kafka.exception.KafkaException;

public class KafConsumer {
	private Logger logger = LogManager.getLogger(getClass());

	private static final int MAX_POLL = 3;
	private Consumer<String, String> pollKafConsumer;
	private KafConsumerRunner kafkaConsumerRunner;
	private Consumer<String, String> partitionConsumer;

	public List<String> viewMsgs(String bootstrapServer, String topic, List<String> searchMsgs, boolean fromBeginning,
			int partition, long offset, long numOfMsgs, long latestOffset) {
		logger.info("view Messages");
		List<String> msgs = new ArrayList<>();
		int emptyPolls = 0;
		try {
			Consumer<String, String> kafConsumer = createConsumer(bootstrapServer, "kafvamviewmsg", fromBeginning);
			kafConsumer.subscribe(Collections.singletonList(topic), new ConsumerRebalanceListener() {

				@Override
				public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
				}

				@Override
				public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
					if (fromBeginning || partition == -1) {
						logger.info("Partition Seek to Beginning");
						kafConsumer.seekToBeginning(partitions);

					} else {
						logger.info("Partition " + partition + " Seek to offset:" + offset + "for topic " + topic);
						kafConsumer.seek(new TopicPartition(topic, partition), offset);

					}

				}
			});
			long currentOffset = 0;
			logger.info("SearchMsgs has value:" + (searchMsgs != null && !searchMsgs.isEmpty()));
			if (searchMsgs != null)
				searchMsgs.forEach(x -> logger.info("Search msg:" + x));
			logger.info(
					"Current Offset:" + currentOffset + ", Latest Offset:" + latestOffset + ", numOfMsgs:" + numOfMsgs);
			while (msgs.size() < numOfMsgs && currentOffset < latestOffset) {
				ConsumerRecords<String, String> records = kafConsumer.poll(Duration.ofMillis(500));
				logger.debug("Polled recs,size:" + records.count() + ", emptypoll count:" + emptyPolls);
				for (ConsumerRecord<String, String> rec : records) {
					currentOffset = rec.offset();
					logger.debug("Current Offset:" + currentOffset);
					if (msgs.size() >= numOfMsgs) {
						break;
					}
					if (searchMsgs == null || searchMsgs.isEmpty() || match(searchMsgs, rec.value())) {
						logger.debug("key:" + rec.key() + ", Val:" + rec.value());
						msgs.add("Offset:" + currentOffset + ", Key:" + rec.key() + ", Val:" + rec.value());
						rec.offset();
						emptyPolls = 0;
					}
				}
				if (emptyPolls > MAX_POLL) {
					logger.debug("Max Poll Reached");
					break;
				}
				emptyPolls++;
			}
			kafConsumer.close();
		} catch (Exception e) {
			logger.info("Kafka Consumer Creation Exception", e);
			logger.error(e);
		}
		logger.info("Total msgs: " + msgs.size());
		return msgs;
	}

	public void pollMsgs(String bootstrapServer, String topic, List<String> searchMsgs, boolean fromBeginning) {
		try {
			logger.info("poll Messages");
			pollKafConsumer = createConsumer(KafkaInit.getBrokerUrl(), "kafvampollmsg", fromBeginning);
			kafkaConsumerRunner = new KafConsumerRunner(pollKafConsumer, searchMsgs, topic, fromBeginning);
			Thread thread = new Thread(kafkaConsumerRunner);
			thread.start();
		} catch (KafkaException e) {
			logger.info("Kafka Consumer Creation Exception", e);
			logger.error(e);
		}

	}

	public void stopPoll() {
		if (kafkaConsumerRunner != null) {
			kafkaConsumerRunner.shutdown();
			kafkaConsumerRunner = null;
			logger.info("Stop Poll Completed");

		}
	}

	public Consumer<String, String> createConsumer(String bootstrapServer, String gpId, boolean fromBeginning)
			throws KafkaException {
		Consumer<String, String> consumer = null;
		logger.info("Server URL: " + bootstrapServer + ", gpid:" + gpId + ", fromBeginning:" + fromBeginning);
		try {
			final Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, gpId);
			if (fromBeginning)
				props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			else
				props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			if(KafkaInit.isSSL()) {
				props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
				props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KafkaInit.getTrustLocation());
				props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, KafkaInit.getTrustPasswd());
			}
			consumer = new KafkaConsumer<>(props);
		} catch (Exception e) {
			logger.error("Consumer creation failed!", e);
			throw new KafkaException(e);
		}
		return consumer;
	}

	public Consumer<String, String> createPartitionConsumer(String bootstrapServer, String gpId, boolean fromBeginning)
			throws KafkaException {
		if (partitionConsumer != null) {
			return partitionConsumer;
		}
		logger.info("Server URL: " + bootstrapServer + ", gpid:" + gpId + ", fromBeginning:" + fromBeginning);
		try {
			final Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, gpId);
			if (fromBeginning)
				props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			else
				props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			if(KafkaInit.isSSL()) {
				props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
				props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KafkaInit.getTrustLocation());
				props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, KafkaInit.getTrustPasswd());
			}
			partitionConsumer = new KafkaConsumer<>(props);
		} catch (Exception e) {
			logger.error("Consumer creation failed!", e);
			throw new KafkaException(e);
		}
		return partitionConsumer;
	}

	public ConcurrentHashMap<Integer, KafkaTopicPartition> getPartitionSize(String topic) {
		try {
			partitionConsumer = createPartitionConsumer(KafkaInit.getBrokerUrl(), "kafvampartition", true);

			final List<PartitionInfo> partitionInfoSet = partitionConsumer.partitionsFor(topic);
			partitionConsumer.assign(partitionInfoSet.stream()
					.map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition()))
					.collect(Collectors.toList()));

			partitionConsumer.poll(Duration.ofMillis(0));
			final Set<TopicPartition> assignedPartitionList = partitionConsumer.assignment();
			final KafkaTopic kafTopic = getTopicInfo(partitionConsumer, topic);
			final ConcurrentHashMap<Integer, KafkaTopicPartition> partitions = kafTopic.getPartitionMap();

			partitionConsumer.seekToBeginning(assignedPartitionList);
			assignedPartitionList.forEach(topicPartition -> {
				final KafkaTopicPartition tp = partitions.get(topicPartition.partition());
				final long startOffset = partitionConsumer.position(topicPartition);
				logger.debug("topic:" + topicPartition.topic() + " , partition: {}" + topicPartition.partition()
						+ " startOffset: {}" + startOffset);

				tp.setFirstOffset(startOffset);
			});
			logger.info("First Offset set");

			partitionConsumer.seekToEnd(assignedPartitionList);
			assignedPartitionList.forEach(topicPartition -> {
				final long latestOffset = partitionConsumer.position(topicPartition);
				final KafkaTopicPartition partition = partitions.get(topicPartition.partition());
				partition.setSize(latestOffset);
				logger.debug("topic:" + topicPartition.topic() + " , partition: {}" + topicPartition.partition()
						+ " latestOffset: {}" + latestOffset);
			});
			logger.info("Last Offset set");
			return partitions;
		} catch (KafkaException e) {
			logger.info("Kafka Consumer Creation Exception", e);
			logger.error(e);
			return new ConcurrentHashMap<Integer, KafkaTopicPartition>();
		}
	}

	private KafkaTopic getTopicInfo(Consumer<String, String> kafConsumer, String topic) {
		final List<PartitionInfo> partitionInfoList = kafConsumer.partitionsFor(topic);
		final KafkaTopic kafTopic = new KafkaTopic(topic);
		final ConcurrentHashMap<Integer, KafkaTopicPartition> partitions = new ConcurrentHashMap<Integer, KafkaTopicPartition>();
		logger.info("Topic:" + topic + "," + partitionInfoList.size());

		for (PartitionInfo partitionInfo : partitionInfoList) {
			final KafkaTopicPartition topicPartitionVo = new KafkaTopicPartition(partitionInfo.partition());
			final Set<Integer> inSyncReplicaIds = Arrays.stream(partitionInfo.inSyncReplicas()).map(Node::id)
					.collect(Collectors.toSet());
			final Set<Integer> offlineReplicaIds = Arrays.stream(partitionInfo.offlineReplicas()).map(Node::id)
					.collect(Collectors.toSet());

			for (Node node : partitionInfo.replicas()) {
				final boolean isInSync = inSyncReplicaIds.contains(node.id());
				final boolean isOffline = offlineReplicaIds.contains(node.id());
				topicPartitionVo
						.addReplica(new KafkaTopicPartition.PartitionReplica(node.id(), isInSync, false, isOffline));
			}

			final Node leader = partitionInfo.leader();
			if (leader != null) {
				topicPartitionVo.addReplica(new KafkaTopicPartition.PartitionReplica(leader.id(), true, true, false));
			}
			partitions.put(partitionInfo.partition(), topicPartitionVo);
		}

		kafTopic.setPartitions(partitions);
		return kafTopic;
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
		stopPoll();
		
	}
}
