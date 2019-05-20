package minSerializers;

import java.nio.ByteBuffer;
import java.util.HashMap;

import minSerializerInterface.MinSerializer;

public class IntSerializer implements MinSerializer{
	
	private HashMap<String, Integer> tokens = new HashMap<String, Integer>(); 

	public byte[] serialize() {
		ByteBuffer buffer = ByteBuffer.allocate(256);
		if (tokens.size() > 0) {
			buffer.put("int#".getBytes());
			buffer.putInt(tokens.size());
			for (String key : tokens.keySet()) {
				byte[] keyBytes = key.getBytes();
//				System.out.print(keyBytes);
				buffer.putInt(keyBytes.length);
				buffer.put(keyBytes);
				buffer.putInt(tokens.get(key));
			}
//			buffer.put("$".getBytes());
		}
		byte[] result = new byte[buffer.position()];
		buffer.flip();
		for (int i = 0; i < result.length; i++)
			result[i] = buffer.get(i);
		return result;
	}
	
	public boolean recognize(Class<?> token) {
//		String g = token.getTypeName();
		return token.getTypeName().equals("java.lang.Integer") || token.getTypeName().equals("int");
	}

	public void addToken(String name, Object value) {
		tokens.put(name, (Integer)value);
	}

	public void clearData() {
		tokens.clear();		
	}

	
}
