package net.javacoding.jspider.core.storage.bloommem;

import net.javacoding.jspider.api.model.EMailAddress;
import net.javacoding.jspider.core.storage.spi.EMailAddressDAOSPI;
import net.javacoding.jspider.core.storage.spi.StorageSPI;
import net.javacoding.jspider.core.model.*;

import java.util.*;

/**
 * $Id: EMailAddressDAOImpl.java,v 1.2 2003/04/11 16:37:06 vanrogu Exp $
 */
class EMailAddressDAOImpl implements EMailAddressDAOSPI {

    protected StorageSPI storage;

    protected Map<Integer, Map<String, EMailAddressReferenceInternal>> byResource;
    protected Map<String, EMailAddressInternal> addresses;

    public EMailAddressDAOImpl ( StorageSPI storage ) {
        this.storage = storage;
        this.byResource= new HashMap<Integer, Map<String, EMailAddressReferenceInternal>>();
        this.addresses = new HashMap<String, EMailAddressInternal> ();
    }

    public void register(ResourceInternal resource, EMailAddressInternal address) {
        Map<String, EMailAddressReferenceInternal> map = byResource.get(new Integer(resource.getId()));
        if (map == null) {
            map = new HashMap<String, EMailAddressReferenceInternal>();
            byResource.put(new Integer(resource.getId()), map);
        }

        EMailAddressReferenceInternal reference =(EMailAddressReferenceInternal) map.get(address.getAddress());
        if ( reference == null ) {
            reference = new EMailAddressReferenceInternal(storage, resource.getId(), address.getAddress(), 0);
            map.put(address.getAddress(), reference);
        }
        reference.incrementCount();
        addresses.put(address.getAddress(), address);
    }

    public EMailAddressInternal[] findByResource(ResourceInternal resource) {
        EMailAddressReferenceInternal[] refs = findReferencesByResource(resource);
        ArrayList<EMailAddress> al = new ArrayList<EMailAddress>();
        for (int i = 0; i < refs.length; i++) {
            EMailAddressReferenceInternal ref = refs[i];
            al.add(ref.getEMailAddress());
        }
        return (EMailAddressInternal[])al.toArray(new EMailAddressInternal[al.size()]);
    }

    public EMailAddressReferenceInternal[] findReferencesByResource(ResourceInternal resource) {
    	Map<String, EMailAddressReferenceInternal> map = byResource.get(new Integer(resource.getId()));
        if (map == null) {
            return new EMailAddressReferenceInternal[0];
        } else {
            return map.values().toArray(new EMailAddressReferenceInternal[map.size()]);
        }
    }

    public EMailAddressInternal find(String address) {
        return (EMailAddressInternal) addresses.get(address);
    }
}
