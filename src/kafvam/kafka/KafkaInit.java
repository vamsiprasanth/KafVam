package kafvam.kafka;

import kafvam.kafka.controller.KafAdminClient;

public class KafkaInit {
	private static String brokerUrl;
	private static String topicPattern;
	private static boolean isSSL;
	private static String trustLocation;
	private static String trustPasswd;

	private static KafAdminClient kafAdminClient = new KafAdminClient();

	public static void init(String _brokerUrl, String _topicPattern, boolean _isSSL, String _trustLocation,
			String _trustPasswd) {
		brokerUrl = _brokerUrl;
		topicPattern = _topicPattern;
		isSSL = _isSSL;
		trustLocation = _trustLocation;
		trustPasswd = _trustPasswd;
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

	public static boolean isSSL() {
		return isSSL;
	}

	public static String getTrustLocation() {
		return trustLocation;
	}

	public static String getTrustPasswd() {
		return trustPasswd;
	}

}
