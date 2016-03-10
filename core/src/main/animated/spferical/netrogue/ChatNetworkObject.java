package animated.spferical.netrogue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import animated.spferical.netrogue.networking.NetworkObject;

public class ChatNetworkObject extends NetworkObject {

	private static final long serialVersionUID = -1973370471877629392L;

	public String[] getChatLines() {
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


		List<String> strLines = new ArrayList<>();
		for (ChatLine chatLine : lines) {
			strLines.add(chatLine.getLine());
		}

		return strLines.toArray(new String[0]);
	}
}
