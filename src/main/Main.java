package main;

import java.lang.reflect.Field;

import mainDeserializer.Deserializer;
import packet.Packet;
import mainSerializer.Serializer;

public class Main {

	public static void main(String[] args) {
		Packet p1 = new Packet();
		p1.i1 = -1;
		p1.d1 = 3.0;
		p1.s1 = "b";
		p1.p1 = null;
		
		Packet p2 = new Packet();
		p2.i1 = 23;
		p2.d1 = 4.0;
		p2.s1 = "aab";
		p2.p1 = p1;
		
		Packet p3 = new Packet();
		p3.i1 = 11;
		p3.d1 = 1.0;
		p3.s1 = "baa";
		p3.p1 = null;
		
		Packet[] a = new Packet[] {p1, p2, p3};
//		Packet p = new Packet();
//		p.i1 = 1;
//		p.d1 = 2.0;
//		p.s1 = "ab";
//		p.p1 = p1;
		
		Serializer serializer = new Serializer();
		Deserializer deserializer = new Deserializer();
		byte[] resultSerializing = serializer.serialize(p2);
		Object res = deserializer.deserialize(resultSerializing);
		System.out.print(res);
	}
}















//Integer g = 123;
//System.out.print(g);
//Packet p = new Packet(2);
//String a = "1";
//Serializer ser = new Serializer();
//byte[] f = ser.serialize(p);
//System.out.print(f);