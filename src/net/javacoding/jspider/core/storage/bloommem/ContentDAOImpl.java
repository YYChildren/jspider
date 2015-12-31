package net.javacoding.jspider.core.storage.bloommem;

import net.javacoding.jspider.core.storage.spi.ContentDAOSPI;
import net.javacoding.jspider.core.storage.spi.StorageSPI;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;

/**
 * $Id: ContentDAOImpl.java,v 1.2 2003/04/11 16:37:06 vanrogu Exp $
 */
class ContentDAOImpl implements ContentDAOSPI {

    protected Map<Integer, byte[]> contents;
    protected StorageSPI storage;

    public ContentDAOImpl ( StorageSPI storage ) {
        this.storage = storage;
        this.contents = new HashMap<Integer, byte[]> ( );
    }

    public InputStream getInputStream(int id) {
        byte[] bytes = contents.get(new Integer(id));
        return new ByteArrayInputStream ( bytes );
    }

    public void setBytes(int id, byte[] bytes) {
        contents.put(new Integer(id), bytes);
    }

}
