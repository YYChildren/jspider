package net.javacoding.jspider.core.util.hash;

import net.javacoding.jspider.Constants;

public class HashString extends AbstractHashFunction<String> {
	@Override
	public byte[] getBytes(String input) {
		return input.getBytes(Constants.CHARSET);
	}
}