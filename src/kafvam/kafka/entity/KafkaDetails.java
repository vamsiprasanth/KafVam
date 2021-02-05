package kafvam.kafka.entity;

/**
 * @author Vamsi Prasanth
 *
 */
public class KafkaDetails {
	private String bootstrapServer;
	private int totalTopics;
	private int totalPartitions;
	private int underReplicatedCnt;
	private int numOfCgs;

	public KafkaDetails(String bootstrapServer, int topicCnt, int totPartitions, int underReplicatedCnt, int numOfCgs) {
		super();
		this.bootstrapServer = bootstrapServer;
		this.totalTopics = topicCnt;
		this.totalPartitions = totPartitions;
		this.underReplicatedCnt = underReplicatedCnt;
		this.numOfCgs = numOfCgs;
	}

	public int getNumOfCgs() {
		return numOfCgs;
	}

	public int getUnderReplicatedCnt() {
		return underReplicatedCnt;
	}

	public String getBootstrapServer() {
		return bootstrapServer;
	}

	public int getTotalTopics() {
		return totalTopics;
	}

	public int getTotalPartitions() {
		return totalPartitions;
	}

	@Override
	public String toString() {
		return "KafkaDetails [bootstrapServer=" + bootstrapServer + ", totalTopics=" + totalTopics
				+ ", totalPartitions=" + totalPartitions + ", underReplicatedCnt=" + underReplicatedCnt + ", numOfCgs="
				+ numOfCgs + "]";
	}
}
