package net.javacoding.jspider.core.storage.bloommem;

import net.javacoding.jspider.core.storage.spi.CookieDAOSPI;
import net.javacoding.jspider.core.storage.spi.StorageSPI;
import net.javacoding.jspider.api.model.Cookie;

import java.util.*;

/**
 * $Id: CookieDAOImpl.java,v 1.2 2003/04/11 16:37:06 vanrogu Exp $
 */
class CookieDAOImpl implements CookieDAOSPI {

    protected StorageSPI storage;
    protected Map<Integer, Cookie[]> cookies;

    public CookieDAOImpl ( StorageSPI storage ) {
        this.storage = storage;
        this.cookies = new HashMap<Integer, Cookie[]> ( );
    }

    public Cookie[] find(int id) {
        Cookie[] cookies = (Cookie[]) this.cookies.get(new Integer(id));
        if ( cookies == null ) {
            cookies = new Cookie[0];
        }
        return cookies;
    }

    public void save(int id, Cookie[] cookies) {
        this.cookies.put(new Integer(id), cookies);
    }

}
