package net.javacoding.jspider.core.storage.bloommem;

import java.util.BitSet;

import net.javacoding.jspider.core.util.hash.HashFunction;



public class BloomFilter<T> {
	private final HashFunction<T> hashFunction;
	private final BitSet hashBits;
	private final int hashFunctionCount;

	public BloomFilter(int capacity, HashFunction<T> hashFunction) {
		this(capacity, bestErrorRate(capacity), hashFunction);
	}

	public BloomFilter(int capacity, double errorRate,HashFunction<T> hashFunction) {
		this(capacity, errorRate, hashFunction, bestM(capacity, errorRate),bestK(capacity, errorRate));
	}
	
	// 哈希函数个数k、位数组大小m、加入的字符串数量n的关系可以参考
	// 参考文献1 http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html。
	// 该文献证明了对于给定的m、n，当 k = ln(2)* m/n 时出错的概率是最小的。
	public BloomFilter(int capacity, double errorRate, HashFunction<T> hashFunction, int m , int k){
		this.hashFunction = hashFunction;
		this.hashFunctionCount = k;
		this.hashBits = new BitSet(m);
	}

	/**
	 * @return 
	 */
	public double getTruthiness() {
		return hashBits.cardinality()/hashBits.size();
	}

	/**
	 * @param item
	 */
	public void add(T item) {
        long longHash = hashFunction.hash(item);
		for (int i = 0; i < this.hashFunctionCount; i++) {
			int hash = this.computeHash(longHash, i);
			hashBits.set(hash, true);
		}
	}

	public boolean contains(T item) {
		long longHash = hashFunction.hash(item);
		int primaryHash = (int)(longHash & 0xffffffff);
		int secondaryHash = (int)(longHash >> 32 & 0xffffffff);
		for (int i = 0; i < this.hashFunctionCount; i++) {
			int hash = computeHash(primaryHash, secondaryHash, i);
			if (!hashBits.get(hash)) {
				return false;
			}
		}
		return true;
	}

	private static double bestErrorRate(int capacity) {
		double c = 1.0/capacity;
		if (Math.abs(c) > 0) {
			return c;
		}
		double y = Integer.MAX_VALUE / (double) capacity;
		return (double) Math.pow(0.6185, y);
	}

	private static int bestK(int capacity, double errorRate) {
		return (int) Math.round(Math.log(2.0) * bestM(capacity, errorRate) / capacity);
	}

	private static int bestM(int capacity, double errorRate) {
		return (int) Math.ceil(capacity * ( Math.log(1.0 / Math.pow(2, Math.log(2.0))/ Math.log(errorRate) )  ));
	}

	private int computeHash(long hash, int i) {
		int primaryHash = (int)(hash & 0xffffffff);
		int secondaryHash = (int)(hash >> 32 & 0xffffffff);
		return computeHash(primaryHash, secondaryHash, i);
	}
	
	private int computeHash(int primaryHash, int secondaryHash, int i) {
		int resultingHash = (primaryHash + (i * secondaryHash))% hashBits.size();
		if(resultingHash < 0 ){
			resultingHash += hashBits.size();
		}
		return resultingHash;
	}
}