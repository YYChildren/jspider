package net.javacoding.jspider.core.util.hash;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import net.javacoding.jspider.Constants;
import net.javacoding.jspider.core.logging.LogFactory;

public class MPQ {
	private final int CRYPTO_LEN = 0x500;
	private final int TYPE = 0;
	private long[] cryptTable; 
	private static class MPQIn{
		private static MPQ mpq = new MPQ();
	}
	private MPQ(){
		cryptTable = new long[CRYPTO_LEN];
		prepare_crypt_table();
	}
	public static MPQ getInstance(){
		return MPQIn.mpq;
	}
	
	private void prepare_crypt_table(){
	    long seed = 0x00100001;
	    int index1 = 0, index2 = 0, i;
	    for( index1 = 0; index1 < 0x100; index1++ ){
	        for( index2 = index1, i = 0; i < 5; i++, index2 += 0x100){
	            long temp1, temp2;
	 
	            seed = (seed * 125 + 3) % 0x2AAAAB;
	            temp1 = (seed & 0xFFFF) << 0x10;
	 
	            seed = (seed * 125 + 3) % 0x2AAAAB;
	            temp2 = (seed & 0xFFFF);
	 
	            cryptTable[index2] = ( temp1 | temp2 );
	        }
	    }
	}
	public String dhash(String key){
		return Long.toHexString(hash(key,TYPE)) + Long.toHexString(hash(key,TYPE+1));
	}
	public long hash(String key){
		return hash(key,TYPE);
	}
	public long hash(String key,int type){
		try {
			return hash(key,Constants.CHARSET,type);
		} catch (UnsupportedEncodingException e) {
			LogFactory.getLog(MPQ.class).error(e.getStackTrace());
			return 0;
		}
	}
	public long hash(String key,String charset,int type) throws UnsupportedEncodingException{
		return hash(key.getBytes(charset),type);
	}
	
	public long hash(String key,Charset charset,int type) throws UnsupportedEncodingException{
		return hash(key.getBytes(charset),type);
	}
	
	public long hash(byte[] key){
		return hash(key,TYPE);
	}
	
	public long hash(byte[] key, int type){
	    long seed1 = 0x7FED7FED;
	    long seed2 = 0xEEEEEEEE;
	    int typeL = type << 8;
	    int ch;
	    for(byte c : key){
	    	ch = c & 0xff;
	        seed1 = cryptTable[typeL + ch] ^ (seed1 + seed2);
	        seed2 = ch + seed1 + seed2 + (seed2 << 5) + 3;
	    }
	    return seed1;
	}
}