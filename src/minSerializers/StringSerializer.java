package minSerializers;

import java.nio.ByteBuffer;
import java.util.HashMap;

import minSerializerInterface.MinSerializer;

public class StringSerializer implements MinSerializer {

	private HashMap<String, String> tokens = new HashMap<String, String>(); 
	
	public byte[] serialize() {
		ByteBuffer buffer = ByteBuffer.allocate(256);
		if (tokens.size() > 0) {
			buffer.put("String#".getBytes());
			buffer.putInt(tokens.size());
			for (String key : tokens.keySet()) {
				byte[] keyBytes = key.getBytes();
//				System.out.print(keyBytes);
				buffer.putInt(keyBytes.length);
				buffer.put(key.getBytes());
				buffer.putInt(tokens.get(key).getBytes().length);
				buffer.put(tokens.get(key).getBytes());
			}
		}
		byte[] result = new byte[buffer.position()];
		buffer.flip();
		for (int i = 0; i < result.length; i++)
			result[i] = buffer.get(i);
		return result;
	}

	public void addToken(String name, Object value) {
		tokens.put(name, (String)value);		
	}
	
	public boolean recognize(Class<?> token) {
		return token.getTypeName().equals("java.lang.String");
	}

	public void clearData() {
		tokens.clear();		
	}

	

}
