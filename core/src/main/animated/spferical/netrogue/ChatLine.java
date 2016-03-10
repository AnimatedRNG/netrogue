package animated.spferical.netrogue;

import animated.spferical.netrogue.networking.NetworkObject;

public class ChatLine extends NetworkObject {

	private static final long serialVersionUID = -8140108225409546840L;

	public ChatLine() {
	}

	public ChatLine(String line, long timestamp) {
		put("line", line);
		put("timestamp", timestamp);
	}

	public String getLine() {
		return (String) get("line");
	}

	public long getTimestamp() {
		return (long) get("timestamp");
	}

}
