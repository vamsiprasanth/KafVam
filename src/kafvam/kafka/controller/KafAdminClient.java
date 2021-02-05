package kafvam.kafka.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import kafvam.kafka.KafkaInit;
import kafvam.kafka.entity.CGDetails;
import kafvam.kafka.entity.KafkaDetails;
import kafvam.kafka.entity.KafkaTopic;
import kafvam.rcp.dialog.CustomProgressMonitorDialog;

/**
 * @author Vamsi Prasanth
 *
 */
public class KafAdminClient {
	private Logger logger = LogManager.getLogger(getClass());
	private KafConsumer kafConsumer = new KafConsumer();
	private List<String> groupIds;
	private List<KafkaTopic> kafkaTopicDetails;

	public KafkaDetails getKafkaDetails() {
		logger.info("getKafkaDetails");

		long sTime = System.currentTimeMillis();
		long eTime;
		KafkaDetails details = null;
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaInit.getBrokerUrl());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "kafdrop-admin-details");
		if (KafkaInit.isSSL()) {
			props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
			props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KafkaInit.getTrustLocation());
			props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, KafkaInit.getTrustPasswd());
		}
		AdminClient adminClient = AdminClient.create(props);

		try {
			ListConsumerGroupsResult cgGroup = adminClient.listConsumerGroups();
			int numOfCgs = cgGroup.all().get().size();
			eTime = System.currentTimeMillis();
			logger.info("Time(ms) for getting CG Size:" + (eTime - sTime));
			ListTopicsResult rs = adminClient.listTopics();

			Map<String, TopicDescription> tpsMap = adminClient.describeTopics(rs.names().get()).all().get();
			int topicCnt = rs.names().get().size();
			int totPartitions = tpsMap.values().stream().map(x -> x.partitions().size()).reduce(0, Integer::sum);
			int underReplicatedCnt = 0;
			for (Entry<String, TopicDescription> dd : tpsMap.entrySet()) {
				List<TopicPartitionInfo> partitionInfoList = dd.getValue().partitions();
				for (TopicPartitionInfo info : partitionInfoList) {
					if (info.isr().size() < info.replicas().size()) {
						underReplicatedCnt++;
					}
				}
			}
			adminClient.close();
			eTime = System.currentTimeMillis();
			logger.info("Time(ms) for describing topic details:" + (eTime - sTime));

			logger.info("Topic Cnt:" + topicCnt + " TotalPartitions:" + totPartitions + "UnderReplicatedCnt:"
					+ underReplicatedCnt + "Consumer Group Count:" + numOfCgs);

			details = new KafkaDetails(KafkaInit.getBrokerUrl(), topicCnt, totPartitions, underReplicatedCnt, numOfCgs);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return details;
	}

	public List<KafkaTopic> getAllTopics(boolean refresh) {
		logger.info("getAllTopics");
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaInit.getBrokerUrl());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "kafdrop-admin-topics");
		if (KafkaInit.isSSL()) {
			props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
			props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KafkaInit.getTrustLocation());
			props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, KafkaInit.getTrustPasswd());
		}
		AdminClient adminClient = AdminClient.create(props);
		ListTopicsResult rs = adminClient.listTopics();
		adminClient.close();
		try {
			Set<String> topics = rs.names().get();
			Set<String> matchedTopics = new HashSet<>();
			String topicPattern = KafkaInit.getTopicPattern();
			if (!topicPattern.isEmpty()) {
				List<String> patterns = Arrays.asList(topicPattern.split("\\|"));
				for (String pattern : patterns) {
					for (String topic : topics) {
						if (topic.toLowerCase().matches(".*" + pattern.toLowerCase() + ".*"))
							matchedTopics.add(topic);

					}
				}
			} else
				matchedTopics = topics;
			kafkaTopicDetails = addProgressBar(matchedTopics, refresh);
			return kafkaTopicDetails;
		} catch (Exception e) {
			logger.error(e);
			System.exit(1);
			return null;
		}

	}

	public List<KafkaTopic> getTopicDetails() {
		return kafkaTopicDetails;
	}

	public KafkaTopic getTopicDetails(String topic) {
		Optional<KafkaTopic> kt = kafkaTopicDetails.stream().filter(x -> x.getName().equals(topic)).findFirst();
		if (kt.isPresent())
			return kt.get();
		return null;
	}

	public void populateGpIds() {
		if (groupIds == null) {
			logger.info("Populate Gpids");
			List<String> gpIds = new ArrayList<>();
			final Properties props = new Properties();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaInit.getBrokerUrl());
			props.put(ConsumerConfig.CLIENT_ID_CONFIG, "kafdrop-admin-gpinfo");
			if (KafkaInit.isSSL()) {
				props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
				props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KafkaInit.getTrustLocation());
				props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, KafkaInit.getTrustPasswd());
			}
			AdminClient adminClient = AdminClient.create(props);
			try {
				long sTime = System.currentTimeMillis();
				gpIds = adminClient.listConsumerGroups().all().get().stream().map(s -> s.groupId())
						.collect(Collectors.toList());
				logger.info(" Time Taken to populate gpids:" + (System.currentTimeMillis() - sTime) + " , size:"
						+ gpIds.size());
				groupIds = gpIds;
			} catch (InterruptedException | ExecutionException e) {
				logger.error(e);
				System.exit(1);
			}
		}
	}

	public boolean isGpIdsPopulated() {
		return groupIds != null;
	}

	public List<CGDetails> getGroupInformation(String topic) {
		logger.info("getGroupInformation");
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaInit.getBrokerUrl());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "kafdrop-admin-gpinfo");
		if (KafkaInit.isSSL()) {
			props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
			props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KafkaInit.getTrustLocation());
			props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, KafkaInit.getTrustPasswd());
		}
		long sTime = System.currentTimeMillis();
		long eTime;
		AdminClient adminClient = AdminClient.create(props);
		List<CGDetails> cgDetails = new ArrayList<>();
		if (isGpIdsPopulated()) {
			try {
				for (final String groupId : groupIds) {
					Map<TopicPartition, OffsetAndMetadata> consumerGroupOffsets = adminClient
							.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();
					Map<TopicPartition, Long> topicEndOffsets = getTopicEndOffsets(groupId,
							consumerGroupOffsets.keySet());
					for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : consumerGroupOffsets.entrySet()) {
						if (entry.getKey().topic().equals(topic)) {
							long curOffset = entry.getValue().offset();
							long endOffset = topicEndOffsets.get(entry.getKey());
							long lag = endOffset - curOffset;
							if (lag < 0) {
								lag = 0;
							}
							cgDetails
									.add(new CGDetails(entry.getKey().partition(), curOffset, endOffset, lag, groupId));
						}
					}
				}
				eTime = System.currentTimeMillis();
				logger.info("Time(ms) for getting getGroupInformation:" + (eTime - sTime));

				logger.info("Consumer Group Count for topic:" + topic + "is " + cgDetails.size());
				adminClient.close();
				return cgDetails;
			} catch (Exception e) {
				logger.error(e);
				System.exit(1);
			}
		}
		return null;
	}

	public List<CGDetails> getGroupInformation(String topic, String groupIds) {
		logger.info("Get GroupInformation");
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaInit.getBrokerUrl());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "kafdrop-admin-gpinfo");
		if (KafkaInit.isSSL()) {
			props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
			props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KafkaInit.getTrustLocation());
			props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, KafkaInit.getTrustPasswd());
		}
		long sTime = System.currentTimeMillis();
		long eTime;
		AdminClient adminClient = AdminClient.create(props);
		List<CGDetails> cgDetails = new ArrayList<>();
		try {
			for (final String groupId : groupIds.split("\\|")) {
				Map<TopicPartition, OffsetAndMetadata> consumerGroupOffsets = adminClient
						.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();
				Map<TopicPartition, Long> topicEndOffsets = getTopicEndOffsets(groupId, consumerGroupOffsets.keySet());
				for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : consumerGroupOffsets.entrySet()) {
					if (entry.getKey().topic().equals(topic)) {
						long curOffset = entry.getValue().offset();
						long endOffset = topicEndOffsets.get(entry.getKey());
						long lag = endOffset - curOffset;
						if (lag < 0) {
							lag = 0;
						}
						cgDetails.add(new CGDetails(entry.getKey().partition(), curOffset, endOffset, lag, groupId));
					}
				}
			}
			eTime = System.currentTimeMillis();
			logger.info("Time(ms) for getting getGroupInformation:" + (eTime - sTime));

			logger.info("Consumer Group Count for topic:" + topic + "is " + cgDetails.size());
			adminClient.close();
			if (cgDetails != null)
				cgDetails.sort(Comparator.comparing(CGDetails::getGpId).thenComparing(CGDetails::getPartitionId));

			return cgDetails;
		} catch (Exception e) {
			logger.error(e);
			System.exit(1);
			return null;
		}
	}

	private Map<TopicPartition, Long> getTopicEndOffsets(String groupId, Set<TopicPartition> keySet) {
		Consumer<String, String> kafConsumer = createConsumer(KafkaInit.getBrokerUrl(), groupId);
		return kafConsumer.endOffsets(keySet);
	}

	private Consumer<String, String> createConsumer(String bootstrapServer, String gpId) {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, gpId);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		if (KafkaInit.isSSL()) {
			props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
			props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KafkaInit.getTrustLocation());
			props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, KafkaInit.getTrustPasswd());
		}
		final Consumer<String, String> consumer = new KafkaConsumer<>(props);
		return consumer;
	}

	public List<KafkaTopic> addProgressBar(Set<String> topics, boolean refresh) {
		logger.info("add Progress Bar");

		List<KafkaTopic> kafTopicList = new ArrayList<>();

		Shell shell = getScreenCentredShell();
		CustomProgressMonitorDialog dialog = new CustomProgressMonitorDialog(shell, "Broker Info Loading Progress");
		dialog.setCancelable(true);
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InterruptedException {
					logger.info("Progress Monitor Entered...");
					monitor.beginTask("Processing Topics From Server, Topic Count:" + topics.size(), topics.size());
					long sTime = System.currentTimeMillis();
					long eTime;
					int cnt = 0;
					for (String tp : topics) {
						cnt++;
						monitor.subTask("Processing topic " + cnt + "- " + tp);
						monitor.worked(1);
						KafkaTopic kt = new KafkaTopic(tp);
						kt.setPartitions(kafConsumer.getPartitionSize(tp));
						kafTopicList.add(kt);
						if (monitor.isCanceled()) {
							logger.info("Cancelled triggered from Progress Dialog, Application exiting...");
							monitor.done();
							if (!refresh)
								System.exit(0);
							else
								break;
						}
					}
					monitor.done();
					logger.info("Progress done...");
					eTime = System.currentTimeMillis();
					logger.info("Topic Count:" + kafTopicList.size());
					logger.info("Time(ms) for getting all topics and partitions:" + (eTime - sTime));

				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			logger.error(e);
		}
		return kafTopicList;

	}

	private Shell getScreenCentredShell() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell centreShell = new Shell(display);
		Point size = centreShell.computeSize(-1, -1);
		Rectangle screen = display.getMonitors()[0].getBounds();
		centreShell.setBounds((screen.width - size.x) / 2, (screen.height - size.y) / 2, size.x, size.y);
		return centreShell;
	}

	public void setKafkaTopicDetails(List<KafkaTopic> kafkaTopicDetails) {
		this.kafkaTopicDetails = kafkaTopicDetails;
	}
}
