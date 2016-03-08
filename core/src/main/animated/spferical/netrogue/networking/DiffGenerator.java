package animated.spferical.netrogue.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DiffGenerator {

	public static List<Diff> generateDiffs(NetworkObject oldObject, NetworkObject newObject) {
		
		if (oldObject == newObject)
			return new ArrayList<>();
		
		List<Diff> diffs = new ArrayList<Diff>();
		for(Map.Entry<String, Object> attributeEntry : oldObject.getAllAttributes().entrySet()) {
			String key = attributeEntry.getKey();
			Object value = attributeEntry.getValue();
			
			if (newObject.has(key))
			{
				if (newObject.get(key).equals(value))
				{
					// This attribute is the same on both objects
					continue;
				}
				else
				{
					// Make a ModfiyAttributeDiff
					// OldObject[key] != NewObject[key]
					diffs.add(new ModifyAttributeDiff(oldObject.ID, ++newObject.lastUpdate, key, newObject.get(key)));
				}
			}
			else
			{
				// Generate a DeleteAttributeDiff
				diffs.add(new DeleteAttributeDiff(oldObject.ID, ++newObject.lastUpdate, key));
			}
		}
		
		for(Map.Entry<String, Object> attributeEntry : newObject.getAllAttributes().entrySet()) {
			String key = attributeEntry.getKey();
			Object value = attributeEntry.getValue();
			
			if (oldObject.has(key))
			{
				continue;
			}
			else
			{
				// Make a AddAttributeDiff
				// OldObject[key] == null
				diffs.add(new AddAttributeDiff(oldObject.ID, ++newObject.lastUpdate, key, value));
			}
		}
		
		for(Map.Entry<Long, NetworkObject> childEntry : oldObject.getAllChildren().entrySet()) {
			Long key = childEntry.getKey();
			NetworkObject value = childEntry.getValue();
			
			if (newObject.hasChild(key))
			{
				// Both the new object and the old object
				// have this same child. That's good.
				continue;
			}
			else
			{
				// Generate a DeleteChildDiff
				diffs.add(new DeleteChildDiff(oldObject.ID, ++newObject.lastUpdate, key));
			}
		}
		
		for(Map.Entry<Long, NetworkObject> childEntry : newObject.getAllChildren().entrySet()) {
			Long key = childEntry.getKey();
			NetworkObject value = childEntry.getValue();
			
			if (oldObject.hasChild(key))
			{
				continue;
			}
			else
			{
				// Generate an AddChildDiff
				diffs.add(new AddChildDiff(oldObject.ID, ++newObject.lastUpdate, key, value));
			}
		}
		
		// Recursively gets diffs of all children
		for (Map.Entry<Long, NetworkObject> childEntry : newObject.getAllChildren().entrySet())
		{
			Long key = childEntry.getKey();
			NetworkObject value = childEntry.getValue();
			
			if (!oldObject.hasChild(key))
				continue;
			
			List<Diff> childDiffs = DiffGenerator.generateDiffs(oldObject.getChild(key), value);
			for (Diff diff : childDiffs)
				diffs.add(diff);
		}
		
		return diffs;
	}
}
