package animated.spferical.netrogue.networking;

import java.io.Serializable;
import java.util.Set;

public class StringArray implements Serializable, Cloneable {

	private static final long serialVersionUID = 2052448547441252082L;
	public String[] data;
	
	public StringArray() {
		this.data = new String[1];
	}
	
	public StringArray(Set<String> data) {
		this.data = data.toArray(new String[data.size()]);
	}
	
	public StringArray(String[] data) {
		this.data = data;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StringArray))
			return false;
		StringArray otherArray = (StringArray) other;
		if (otherArray.data.length != data.length)
			return false;
		for (int i = 0; i < otherArray.data.length; i++)
			if (!data[i].equals(otherArray.data[i]))
				return false;
		return true;
	}
	
	@Override
	public String toString() {
		String finalString = "[";
		for (String string : this.data)
		{
			finalString += string + "], [";
		}
		return finalString.substring(0, finalString.length() - 3);
	}
}
