package minDeserializers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import baseMinDeserializer.BaseMinDeserializer;
import tuple.Tuple;
import minDeserializerInterface.MinDeserializer;

public class IntDeserializer extends BaseMinDeserializer<Integer>{

	public Tuple<HashMap<String, Integer>, Integer> deserialize(byte[] input) {
		return super.deserialize(input);
	}

	public boolean recognize(byte[] input, int index) {
		return super.recognize(input, index);
	}

	public int getRecognizedDataSize(byte[] input, int startIndex) {
		return super.getRecognizedDataSize(input, startIndex);
	}

	public Class getTypeClass() {
		return Integer.TYPE;
	}

	protected Integer getTypeFrom(byte[] input, int index, int border) {
		return ByteBuffer.wrap(Arrays.copyOfRange(input, index, index + 4)).getInt();
	}

	protected int getBytesSize() {
		return 4;
	}

	public String getKeyWord() {
		return "int#";
	}
	
	

}
