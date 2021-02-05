package kafvam.kafka.entity;

/**
 * @author Vamsi Prasanth
 *
 */
public class CGDetails {
	private long partitionId;
	private long curOffset;
	private long endOffset;
	private long lag;

	private String gpId;

	public CGDetails(long partitionId, long curOffset, long endOffset, long lag, String gpId) {
		super();
		this.partitionId = partitionId;
		this.curOffset = curOffset;
		this.endOffset = endOffset;
		this.lag = lag;
		this.gpId = gpId;
	}

	public long getLag() {
		return lag;
	}

	public long getPartitionId() {
		return partitionId;
	}

	public long getCurOffset() {
		return curOffset;
	}

	public long getEndOffset() {
		return endOffset;
	}

	public String getGpId() {
		return gpId;
	}
}
