package net.javacoding.jspider.core.util.hash;

public interface HashFunction<T> {
	public long hash(T input);
	public byte[] getBytes(T input);
}