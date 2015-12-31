package net.javacoding.jspider.core.storage.bloommem;

import net.javacoding.jspider.core.storage.spi.SiteDAOSPI;
import net.javacoding.jspider.core.storage.spi.StorageSPI;
import net.javacoding.jspider.core.model.SiteInternal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * $Id: SiteDAOImpl.java,v 1.6 2003/04/11 16:37:07 vanrogu Exp $
 */
class SiteDAOImpl implements SiteDAOSPI {

    protected StorageSPI storage;
    protected Map<URL, SiteInternal> byURL;
    protected Map<Integer, SiteInternal> byId;

    public SiteDAOImpl ( StorageSPI storage ) {
        this.storage = storage;
        this.byURL = new HashMap<URL, SiteInternal> ( );
        this.byId = new HashMap<Integer, SiteInternal> ( );
    }

    public SiteInternal find(int id) {
        return (SiteInternal)byId.get(new Integer(id));
    }

    public SiteInternal find(URL siteURL) {
        return (SiteInternal)byURL.get(siteURL);
    }

    public void create(int id, SiteInternal site) {
        byURL.put(site.getURL(), site);
        byId.put(new Integer(id), site);
    }

    public void save(int id, SiteInternal site) {
        URL siteURL = site.getURL();
        byURL.put(siteURL, site);
        byId.put(new Integer(id), site);
    }

    public SiteInternal[] findAll() {
        return (SiteInternal[]) byURL.values().toArray(new SiteInternal[byURL.size()]);
    }

}
