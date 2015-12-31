package net.javacoding.jspider.core.util.hash;

public class HashInteger extends AbstractHashFunction<Integer> {
	@Override
	public byte[] getBytes(Integer input) {
		byte[] b = new byte[4];
		int intValue = (int)input;
		b[3] = (byte)(intValue & 0xff);
		b[2] = (byte)(intValue >> 8 & 0xff);
		b[1] = (byte)(intValue >> 16 & 0xff);
		b[0] = (byte)(intValue >> 24 & 0xff);
		return b;
	}
}