package baseMinDeserializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import tuple.Tuple;

public abstract class BaseMinDeserializer<T>  {
	
	private int getIntFrom(byte[] input, int index) {
		return ByteBuffer.wrap(Arrays.copyOfRange(input, index, index + 4)).getInt();
	}
	
	protected abstract T getTypeFrom(byte[] input, int index, int border);
	
	protected abstract int getBytesSize();
	
	public abstract String getKeyWord();
	
	public abstract Class getTypeClass();
	
	public Tuple<HashMap<String, T>, Integer> deserialize(byte[] input){
		int index = 0;
		int CountOfFields = getIntFrom(input, index);
		index += 4;
		HashMap<String, T> nameValue = new HashMap<String, T>();
		for (int i = 0; i < CountOfFields; i++) {
			int valueLength = 0;
			int nameLength = getIntFrom(input, index);
			index += 4; 
			byte[] nameArray = Arrays.copyOfRange(input, index, index + nameLength);
			String name = new String(nameArray, StandardCharsets.UTF_8);
			index += nameLength;
			if (getKeyWord().equals("String#")) {
				valueLength = getIntFrom(input, index);
				index += 4;
			}			
			T t = getTypeFrom(input, index, valueLength);
			index += getBytesSize() + valueLength;
			nameValue.put(name, t);
		}
		return new Tuple<HashMap<String, T>, Integer>(nameValue, index);
	}
	
	public boolean recognize(byte[] input, int index) {
		String keyWord = getKeyWord();
		byte[] helpArray = Arrays.copyOfRange(input, index, index + keyWord.length());
		String typeCheck = new String(helpArray, StandardCharsets.UTF_8);
		return typeCheck.equals(keyWord);
	}
	
	public int getRecognizedDataSize(byte[] input, int startIndex) {
		int index = startIndex;
		index += getKeyWord().length();
		int fieldsCount = getIntFrom(input, index);
		index += 4;
		for (int i = 0; i < fieldsCount; i ++) {
			int nameSize = getIntFrom(input, index);
			index += 4 + nameSize + getBytesSize();
			if (getKeyWord().equals("String#")) {
				int valueSize = getIntFrom(input, index);
				index += 4 + valueSize;
			}
		}
		return index - startIndex;
		
	}
}
