package net.javacoding.jspider.core.storage.bloommem;
import net.javacoding.jspider.core.util.hash.HashFunction;
import net.javacoding.jspider.core.util.hash.HashString;
import junit.framework.TestCase;


public class BloomFilterTest extends TestCase{
	public BloomFilterTest ( ) {
        super ( "BloomFilterTest" );
    }

    public void testNew(){
    	HashFunction<String> hf = new HashString();
    	BloomFilter<String> blf = new BloomFilter<String>(1000000, hf);  
    	blf.add("asasdf");
    }
}
