package minSerializerInterface;

public interface MinSerializer {
	public byte[] serialize();
	public boolean recognize(Class<?> token);
	public void addToken(String name, Object value);
	public void clearData();
}
