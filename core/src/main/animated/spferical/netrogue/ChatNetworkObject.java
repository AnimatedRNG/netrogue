package animated.spferical.netrogue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import animated.spferical.netrogue.networking.NetworkObject;

public class ChatNetworkObject extends NetworkObject {

	private static final long serialVersionUID = -1973370471877629392L;

	public String getChatLines() {
		StringBuilder chatBuilder = new StringBuilder();
		Collection<NetworkObject> children = getAllChildren().values();

		// get all chatlines
		ArrayList<ChatLine> lines = new ArrayList<ChatLine>();
		for (NetworkObject obj : children) {
			if (obj instanceof ChatLine) {
				lines.add((ChatLine) obj);
			}
		}

		// sort them in chronological order
		Collections.sort(lines,
				(c1, c2) -> (int) (c1.getTimestamp() - c2.getTimestamp()));

		for (ChatLine chatLine : lines) {
			chatBuilder.append(chatLine.getLine() + "\n");
		}
		return chatBuilder.toString();
	}
}
