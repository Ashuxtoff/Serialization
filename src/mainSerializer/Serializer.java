package mainSerializer;

import packet.Packet;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import minSerializerInterface.MinSerializer;
import minSerializers.*;

public class Serializer {
	
	private ArrayList<MinSerializer> minSerializers = new ArrayList<MinSerializer>();
	private boolean isArraySerializing = false;
	private HashMap defaults = new HashMap();
	private boolean isNestingSerialize = false;
	
	public Serializer() {
		registerMinSerializer(new IntSerializer());
		registerMinSerializer(new DoubleSerializer());
		registerMinSerializer(new StringSerializer());
		defaults.put(Integer.TYPE, Integer.valueOf(0));  
		defaults.put(Double.TYPE, Double.valueOf(0));
	}
	
	private void registerMinSerializer(MinSerializer minSerializer) {
		minSerializers.add(minSerializer);
	}
	
	private boolean isArray(Object obj) {
		return obj.getClass().getTypeName().endsWith("[]");
	}
	
	private boolean isNesting(Object obj) {
		return !(isPrimitiveOrString(obj) || isArray(obj));
	}
	
	private boolean isPrimitiveOrString(Object obj) {
		return obj instanceof Integer || obj instanceof Double || obj instanceof Boolean 
				|| obj instanceof Short	|| obj instanceof Float || obj instanceof String 
				|| obj instanceof Byte || obj instanceof Long;
	}
	
	public byte[] serialize(Object object) {
		return serialize(object, null);
	}
	
	private byte[] serialize(Object object, String name) {		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		if (isArray(object)) {
			isArraySerializing = true;
			int length = 0;
			String arrayTypeString = object.getClass().getTypeName();
			buffer.put("arrayPacket#".getBytes());
			//buffer.put(arrayTypeString.substring(0, arrayTypeString.length() - 2).getBytes());
			//buffer.put("#".getBytes());
			for (Object subObj : (Packet[])object) {
				length++;
			}
			buffer.putInt(length);
//			buffer.put("#".getBytes());
			for (Object subObj : (Packet[])object) {
				buffer.put(serialize(subObj));
			}
		}
		else if (isPrimitiveOrString(object)) {
			Class objectClass = object.getClass();
			for (MinSerializer minSerializer : minSerializers) {
				if (minSerializer.recognize(objectClass)) {
					minSerializer.addToken("", object);
					break;
				}
			}
		}
		else {
			Class objectClass = object.getClass();
//			if (!isArraySerializing) {
				String className = objectClass.getName();
				String[] splittedClassName = className.split("\\.");
				buffer.put(splittedClassName[splittedClassName.length-1].getBytes()); // все начинаем с класса
				buffer.put("#".getBytes());
				if (name != null) {  // name != null <=> вызываем объект как поле другого объекта
					buffer.putInt(name.length());
					buffer.put(name.getBytes());
				}
//			}
			Field[] objectFields = objectClass.getFields(); 
			int countOfFields = 0;
			for (Field field : objectFields) { // подсчет количества активных полей
				try {
					Object value = field.get(object);
					if (defaults.containsKey(field.getType())) { // а именно, если лежит в словаре дефолтов примитивов - сравниваем значени€ с дефолтными
						if (value != defaults.get(field.getType()))
							countOfFields ++;
					}					
					else // если нет - провер€ем на Null
						if (value != null)
							countOfFields ++;
				} 
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			buffer.putInt(countOfFields);	// после названи€ класса - число активных полей, то есть сериализованных	
			for (Field field : objectFields) {
				try {
					Object fieldObject = field.get(object);
					if (fieldObject != null) // если пол€ null - не сериализуютс€
						if (isNesting(fieldObject)) {
							for (MinSerializer minSerializer : minSerializers) {
								buffer.put(minSerializer.serialize());
								minSerializer.clearData();
							}
							buffer.put(serialize(fieldObject, field.getName())); // если вложенный сложный тип - кидаем им€, чтобы 
							isNestingSerialize = true;
						}
						else                                                     // восстановить его на стороне десериализатора.
							for (MinSerializer minSerializer : minSerializers) {
								if (minSerializer.recognize(field.getType())) {
									Object value = field.get(object);
									if (defaults.containsKey(field.getType())) {
										if (value != defaults.get(field.getType())) { // если пол€ примитивные
											minSerializer.addToken(field.getName(), value);
											break;
										}
									}
									else { // если нет
										minSerializer.addToken(field.getName(), value);
										break;
									}
										
								}
							}
				} 
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		if (!isNestingSerialize) {
			for (MinSerializer minSerializer : minSerializers) 
				buffer.put(minSerializer.serialize());
		}
		isNestingSerialize = false;
//		buffer.put("@".getBytes());
		byte[] result = new byte[buffer.position()];
		buffer.flip();
		for (int i = 0; i < result.length; i++)
			result[i] = buffer.get(i);
		return result;	
	}
}
