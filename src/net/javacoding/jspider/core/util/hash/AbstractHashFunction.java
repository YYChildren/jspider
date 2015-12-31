package net.javacoding.jspider.core.util.hash;


public abstract class AbstractHashFunction<T> implements HashFunction<T> {
		@Override
		public long hash(T input) {
		    return (int) MPQ.getInstance().hash(getBytes(input));
		}
}
