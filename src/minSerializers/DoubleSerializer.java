package minSerializers;

import java.nio.ByteBuffer;
import java.util.HashMap;

import minSerializerInterface.MinSerializer;

public class DoubleSerializer implements MinSerializer {

	private HashMap<String, Double> tokens = new HashMap<String, Double>(); 
	
	public byte[] serialize() {
		ByteBuffer buffer = ByteBuffer.allocate(256);
		if (tokens.size() > 0) {
			buffer.put("double#".getBytes());
			buffer.putInt(tokens.size());
			for (String key : tokens.keySet()) {
				byte[] keyBytes = key.getBytes();
//				System.out.print(keyBytes);
				//buffer.put((byte)keyBytes.length); - укоротить?
				buffer.putInt(keyBytes.length);
				buffer.put(keyBytes);
//				buffer.put((byte)(8));
				buffer.putDouble(tokens.get(key));
			}
		}
		byte[] result = new byte[buffer.position()];
		buffer.flip();
		for (int i = 0; i < result.length; i++)
			result[i] = buffer.get(i);
		return result;
	}

	public boolean recognize(Class<?> token) {
		return token.getTypeName().equals("java.lang.Double") || token.getTypeName().equals("double");
	}

	public void addToken(String name, Object value) {
		tokens.put(name, (Double)value);		
	}

	public void clearData() {
		tokens.clear();
	}

}
