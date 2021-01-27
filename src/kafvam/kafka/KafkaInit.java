package kafvam.kafka;

import kafvam.kafka.controller.KafAdminClient;

public class KafkaInit {
	private static String brokerUrl;
	private static String topicPattern;
	private static KafAdminClient kafAdminClient = new KafAdminClient();

	public static void init(String _brokerUrl, String _topicPattern) {
		brokerUrl = _brokerUrl;
		topicPattern = _topicPattern;
	}

	public static String getTopicPattern() {
		return topicPattern;
	}

	public static String getBrokerUrl() {
		return brokerUrl;
	}

	public static KafAdminClient getKafAdminClient() {
		return kafAdminClient;
	}
}
