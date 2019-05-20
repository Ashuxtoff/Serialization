package minDeserializers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import baseMinDeserializer.BaseMinDeserializer;
import minDeserializerInterface.MinDeserializer;
import tuple.Tuple;

public class StringDeserializer extends BaseMinDeserializer<String>{

	public Tuple<HashMap<String, String>, Integer> deserialize(byte[] input) {
		return super.deserialize(input);
	}

	public boolean recognize(byte[] input, int index) {
		return super.recognize(input, index);
	}
	
	public int getRecognizedDataSize(byte[] input, int startIndex) {
		return super.getRecognizedDataSize(input, startIndex);
	}

	public Class getTypeClass() {
		return String.class;
	}

	protected String getTypeFrom(byte[] input, int index, int border) {
		byte[] valueArray = Arrays.copyOfRange(input, index, index + border);
		String value = new String(valueArray, StandardCharsets.UTF_8);
		return value;
	}

	protected int getBytesSize() {
		return 0;
	}

	public String getKeyWord() {
		return "String#";
	}

}
