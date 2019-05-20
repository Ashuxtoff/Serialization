package minDeserializerInterface;

import java.util.HashMap;
import tuple.Tuple;

public interface MinDeserializer <T>{
	public Tuple<HashMap<String, T>, Integer> deserialize(byte[] input);
	public boolean recognize(byte[] token, int index);
	public int getKeyWordLength();
	public int getRecognizedDataSize(byte[] input, int index);
	public Class getTypeClass();
}
