package minDeserializers;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import baseMinDeserializer.BaseMinDeserializer;
import minDeserializerInterface.MinDeserializer;
import tuple.Tuple;

public class DoubleDeserializer extends BaseMinDeserializer<Double> {

	public Tuple<HashMap<String, Double>, Integer> deserialize(byte[] input) {
		return super.deserialize(input);
	}

	public boolean recognize(byte[] input, int index) {
		return super.recognize(input, index);
	}
	
	public int getRecognizedDataSize(byte[] input, int startIndex) {
		return super.getRecognizedDataSize(input, startIndex);
	}

	public Class getTypeClass() {		
		return Double.TYPE;
	}

	protected Double getTypeFrom(byte[] input, int index, int border) {
		return ByteBuffer.wrap(Arrays.copyOfRange(input, index, index + 8)).getDouble();
	}

	protected int getBytesSize() {
		return 8;
	}

	public String getKeyWord() {
		return "double#";
	}
}
