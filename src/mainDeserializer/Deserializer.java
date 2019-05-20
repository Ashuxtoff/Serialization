package mainDeserializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import baseMinDeserializer.BaseMinDeserializer;
import packet.Packet;
import minDeserializerInterface.MinDeserializer;
import minDeserializers.DoubleDeserializer;
import minDeserializers.IntDeserializer;
import minDeserializers.StringDeserializer;
import minSerializerInterface.MinSerializer;
import tuple.Tuple;

public class Deserializer {
	
	private ArrayList<BaseMinDeserializer> minDeserializers = new ArrayList<BaseMinDeserializer>();
	private int nestingDeserializingLevel = 0;
	private boolean isArrayDeserializing = false;
	private MinDeserializer currentMinDeserializer = null;
	
	public Deserializer() {
		registerMinDeserializer(new IntDeserializer());
		registerMinDeserializer(new DoubleDeserializer());
		registerMinDeserializer(new StringDeserializer());
	}
	
	private int getIntFrom(byte[] input, int index) {
		return ByteBuffer.wrap(Arrays.copyOfRange(input, index, index + 4)).getInt();
	}
	
	private byte[] getBytesCopy(byte[] input, int begin, int end) {
		return Arrays.copyOfRange(input, begin, end);
	}
	
	private String getBytesSliceString(byte[] input, int begin, int end) {
		return new String(getBytesCopy(input, begin, end), StandardCharsets.UTF_8);
	}
	
	private String getBytesString(byte[] input) {
		return new String(input, StandardCharsets.UTF_8);
	}
	
	private void registerMinDeserializer(BaseMinDeserializer minDeserializer) {
		minDeserializers.add(minDeserializer);
	}
	
	public Object deserialize(byte[] input) {
		Object resultObject = null;
		HashMap<String, Object> nestingPacketsInfo = new HashMap<String, Object>();
		ArrayList<HashMap<String, ?>> fieldsInfo = new ArrayList<HashMap<String, ?>>();
		try {
			int index = 0;
			while (input[index] != '#')
				index ++;
			byte[] classNameBytes = getBytesCopy(input, 0, index);
			String className = getBytesString(classNameBytes);
			index ++;
			if (className.equals("Packet")){
				className = "packet.Packet";
				if (nestingDeserializingLevel > 0) {
					int nestingPacketLengthName = getIntFrom(input, index);
					index += 4 + nestingPacketLengthName;
				}
				int countOfFields = getIntFrom(input, index);
				index += 4;
				Class<Packet> clazz = (Class<Packet>)Class.forName(className);
				for (int i = 0; i < countOfFields; i ++) {
					if (getBytesSliceString(input, index, index + 7).equals("Packet#")) {
						int startSlice = index;
						index += 7;
						int nestingPacketLengthName = getIntFrom(input, index);
						index += 4;
						String nestingPacketName = getBytesSliceString(input, index, index + nestingPacketLengthName);
						index += nestingPacketLengthName;
						byte[] nestingPacketSlice = getBytesCopy(input, startSlice, input.length);
						nestingDeserializingLevel += 1;
						nestingPacketsInfo.put(nestingPacketName, deserialize(nestingPacketSlice));
						int fieldsCount = getIntFrom(input, index);
						index += 4;
						for (int j = 0; j < fieldsCount; j ++) {
							for (BaseMinDeserializer minDeserializer : minDeserializers) {
								if (minDeserializer.recognize(input, index)) {
									index += minDeserializer.getRecognizedDataSize(input, index);
									break;
								}
							}
						}
					}
					else {
						for (BaseMinDeserializer minDeserializer : minDeserializers) {
							if (minDeserializer.recognize(input, index)) {
								index += minDeserializer.getKeyWord().length();
								Tuple<HashMap, Integer> result = minDeserializer.deserialize(Arrays.copyOfRange(input, index, input.length));
								fieldsInfo.add(result.value1);
								index += result.value2;
								break;
							}
						}
					}
				}
				Packet packet = (Packet)clazz.newInstance();
				for (HashMap<String, ?> infoItem : fieldsInfo) {
					for (String fieldName : infoItem.keySet()) {
						Field field = clazz.getField(fieldName);
						Object value = infoItem.get(fieldName);
						field.set(packet, value);
					}
				}
				if (nestingPacketsInfo.size() > 0) {
					for (String fieldName : nestingPacketsInfo.keySet()) {
						Field field = clazz.getField(fieldName);
						Object value = nestingPacketsInfo.get(fieldName);
						field.set(packet, value);
					}
					nestingDeserializingLevel -= 1;
				}
				resultObject = packet;
			}
			else if (className.equals("arrayPacket")) {
				int length = getIntFrom(input, index);
				index += 4;
				ArrayList result = new ArrayList();
				for (int i = 0; i < length; i++) {
					result.add(deserialize(getBytesCopy(input, index, input.length)));
					index += 7;
					int countOfFields = getIntFrom(input, index);
					index += 4;
					boolean wasRecognized = false;
					for (int j = 0; j < countOfFields; j ++) {	
						wasRecognized = false;
						for (BaseMinDeserializer minDeserializer : minDeserializers) {
							if (minDeserializer.recognize(input, index)) {
								index += minDeserializer.getRecognizedDataSize(input, index);
								wasRecognized = true;
								break;
							}
						}
						if (!(wasRecognized)) {
							index += 7;
							int nameLength = getIntFrom(input, index);
							index += 4 + nameLength;
							int nestingFieldsCount = getIntFrom(input, index);
							index += 4;
							for (int k = 0; k < nestingFieldsCount; k++) {
								for (BaseMinDeserializer minDeserializer : minDeserializers) 
									if (minDeserializer.recognize(input, index)) {
										index += minDeserializer.getRecognizedDataSize(input, index);
										break;
									}
							}
							
						}
					}
				}
				resultObject = result.toArray();
			}
			else {
				Object value = null;
				for (BaseMinDeserializer minDeserializer : minDeserializers) {
					if (minDeserializer.recognize(input, 0)) {
						Class objClass = minDeserializer.getTypeClass();
						Tuple<HashMap, Integer> result = minDeserializer.deserialize(Arrays.copyOfRange(input, index, input.length));
						for (Object key : result.value1.keySet()) {
							value = result.value1.get(key);
						}
					}						
				}
				resultObject = value;
			}
			
		}		
		catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | InstantiationException ex) { 
			ex.printStackTrace();
		}
		return resultObject;
	}
}
