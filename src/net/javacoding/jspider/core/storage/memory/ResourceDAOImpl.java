package net.javacoding.jspider.core.storage.memory;

import net.javacoding.jspider.api.model.FetchedResource;
import net.javacoding.jspider.api.model.Folder;
import net.javacoding.jspider.api.model.Resource;
import net.javacoding.jspider.core.event.impl.*;
import net.javacoding.jspider.core.model.*;
import net.javacoding.jspider.core.storage.spi.ResourceDAOSPI;
import net.javacoding.jspider.core.storage.spi.StorageSPI;
import net.javacoding.jspider.core.storage.exception.InvalidStateTransitionException;
import net.javacoding.jspider.core.util.URLUtil;

import java.net.URL;
import java.util.*;

/**
 * $Id: ResourceDAOImpl.java,v 1.12 2003/04/11 16:37:07 vanrogu Exp $
 */
class ResourceDAOImpl implements ResourceDAOSPI {

    protected StorageSPI storage;

    protected Map<URL, ResourceInternal> knownURLs;
    protected Map<Integer, ResourceInternal> byId;

    //protected Set<?> spideredResources; /* urls visited by a spider, but not yet parsed */

    protected Set<URL> ignoredForFetchingResources; /* urls ignored because of rule decisions */
    protected Set<URL> ignoredForParsingResources; /* urls ignored because non-HTML */
    protected Set<URL> forbiddenResources; /* forbidden urls */
    protected Set<URL> fetchErrorResources; /* urls that could not be visited by the spider */
    protected Set<URL> parseErrorResources; /* resources that could not be parsed correctly */
    protected Set<ResourceInternal> parsedResources; /* urls that were spidered AND interpreted */

    protected Map<URL, Map<URL, ResourceReferenceInternal>> referers;
    protected Map<URL, Map<URL, ResourceReferenceInternal>> referees;

    protected Map<Folder, Set<ResourceInternal>> byFolder;
    protected Map<URL, Set<ResourceInternal>> rootResources;

    public ResourceDAOImpl(StorageSPI storage) {
        this.storage = storage;
        //spideredResources = new HashSet<Object>();
        ignoredForFetchingResources = new HashSet<URL>();
        ignoredForParsingResources = new HashSet<URL>();
        forbiddenResources = new HashSet<URL>();
        fetchErrorResources = new HashSet<URL>();
        parseErrorResources = new HashSet<URL>();
        parsedResources = new HashSet<ResourceInternal>();
        knownURLs = new HashMap<URL, ResourceInternal>();
        this.byId = new HashMap<Integer, ResourceInternal>();
        this.referees = new HashMap<URL, Map<URL, ResourceReferenceInternal>>();
        this.referers = new HashMap<URL, Map<URL, ResourceReferenceInternal>>();
        this.byFolder = new HashMap<Folder, Set<ResourceInternal>>();
        this.rootResources = new HashMap<URL, Set<ResourceInternal>>();
    }

    public void create(int id, ResourceInternal resource) {
        URL url = resource.getURL();
            knownURLs.put(url, resource);
            byId.put(new Integer(id), resource);

            if (resource.getFolder() == null) {
                Set<ResourceInternal> set = rootResources.get(URLUtil.getSiteURL(url));
                if (set == null) {
                    set = new HashSet<ResourceInternal>();
                    rootResources.put(URLUtil.getSiteURL(url), set);
                }
                set.add(resource);
            } else {
                Set<ResourceInternal> set = byFolder.get(resource.getFolder());
                if (set == null) {
                    set = new HashSet<ResourceInternal>();
                    byFolder.put(resource.getFolder(), set);
                }
                set.add(resource);
            }
    }

    public void registerURLReference(URL url, URL refererURL) {
        ResourceInternal resource = (ResourceInternal) knownURLs.get(url);
        if (refererURL != null) {
            ResourceInternal referer = (ResourceInternal) knownURLs.get(refererURL);
            storeRef(referers, resource, referer, refererURL, url);
            storeRef(referees, referer, resource, refererURL, url);
        }
    }

    public ResourceInternal[] findByFolder(FolderInternal folder) {
    	Set<ResourceInternal> set = byFolder.get(folder);
        if (set == null) {
            return new ResourceInternal[0];
        }
        return (ResourceInternal[]) set.toArray(new ResourceInternal[set.size()]);
    }

    protected void storeRef(Map<URL, Map<URL, ResourceReferenceInternal>> map, ResourceInternal key, ResourceInternal data, URL referer, URL referee) {
        Map<URL, ResourceReferenceInternal> refmap = map.get(key.getURL());
        if (refmap == null) {
            refmap = new HashMap<URL, ResourceReferenceInternal>();
            map.put(key.getURL(), refmap);
        }
        ResourceReferenceInternal rri = (ResourceReferenceInternal) refmap.get(data.getURL());
        if (rri == null) {
            rri = new ResourceReferenceInternal(storage, referer, referee, 0);
            refmap.put(data.getURL(), rri);
        }
        rri.incrementCount();
    }

    public ResourceInternal[] findAllResources() {
        return (ResourceInternal[]) knownURLs.values().toArray(new ResourceInternal[knownURLs.size()]);
    }

    public ResourceInternal[] getRefereringResources(ResourceInternal resource) {
        ResourceReferenceInternal[] refs = getIncomingReferences(resource);
        ArrayList<FetchedResource> al = new ArrayList<FetchedResource>();
        for (int i = 0; i < refs.length; i++) {
            ResourceReferenceInternal ref = refs[i];
            al.add(ref.getReferer());
        }
        return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
    }

    public ResourceReferenceInternal[] getOutgoingReferences(ResourceInternal resource) {
    	Map<URL, ResourceReferenceInternal> map = referees.get(resource.getURL());
        if (map == null) {
            return new ResourceReferenceInternal[0];
        } else {
            return (ResourceReferenceInternal[]) map.values().toArray(new ResourceReferenceInternal[map.size()]);
        }
    }

    public ResourceReferenceInternal[] getIncomingReferences(ResourceInternal resource) {
    	Map<URL, ResourceReferenceInternal> map = referers.get(resource.getURL());
        if (map == null) {
            return new ResourceReferenceInternal[0];
        } else {
            return (ResourceReferenceInternal[]) map.values().toArray(new ResourceReferenceInternal[map.size()]);
        }
    }

    public ResourceInternal[] getReferencedResources(ResourceInternal resource) {
        ResourceReferenceInternal[] refs = getOutgoingReferences(resource);
        ArrayList<Resource> al = new ArrayList<Resource>();
        for (int i = 0; i < refs.length; i++) {
            ResourceReferenceInternal ref = refs[i];
            al.add(ref.getReferee());
        }
        return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
    }

    public ResourceInternal[] getBySite(SiteInternal site) {
        ArrayList<ResourceInternal> al = new ArrayList<ResourceInternal>();
        Iterator<URL> it = knownURLs.keySet().iterator();
        while (it.hasNext()) {
            URL url = (URL) it.next();
            URL siteURL = URLUtil.getSiteURL(url);
            if (site.getURL().equals(siteURL)) {
                al.add(getResource(url));
            }
        }
        return (ResourceInternal[]) al.toArray(new ResourceInternal[al.size()]);
    }

    public ResourceInternal[] getRootResources(SiteInternal site) {
    	Set<ResourceInternal> set = rootResources.get(site.getURL());
        if ( set == null ) {
            return new ResourceInternal[0];
        } else {
            return (ResourceInternal[]) set.toArray(new ResourceInternal[set.size()]);
        }
    }

    public ResourceInternal getResource(int id) {
        return (ResourceInternal)byId.get(new Integer(id));
    }

    public ResourceInternal getResource(URL url) {
        return (ResourceInternal) knownURLs.get(url);
    }

    public synchronized void setSpidered(URL url, URLSpideredOkEvent event) {
        ResourceInternal resource = getResource(url);
        resource.setFetched(event.getHttpStatus(), event.getSize(), event.getTimeMs(), event.getMimeType(), null, event.getHeaders());
        resource.setBytes(event.getBytes());
    }

    public synchronized void setIgnoredForParsing(URL url) throws InvalidStateTransitionException {
        ResourceInternal resource = getResource(url);
        resource.setParseIgnored();
        ignoredForParsingResources.add(url);
    }

    public synchronized void setIgnoredForFetching(URL url, URLFoundEvent event) throws InvalidStateTransitionException {
        ResourceInternal resource = getResource(url);
        resource.setFetchIgnored();
        ignoredForFetchingResources.add(event.getFoundURL());
    }

    public synchronized void setForbidden(URL url, URLFoundEvent event) throws InvalidStateTransitionException {
        ResourceInternal resource = getResource(url);
        resource.setForbidden();
        forbiddenResources.add(event.getFoundURL());
    }

    public synchronized void setError(URL url, ResourceParsedErrorEvent event) throws InvalidStateTransitionException {
        ResourceInternal resource = getResource(url);
        resource.setParseError();
        parseErrorResources.add(url);
    }

    public synchronized void setParsed(URL url, ResourceParsedOkEvent event) throws InvalidStateTransitionException {
        ResourceInternal resource = getResource(url);
        resource.setParsed();
        parsedResources.add(resource);
    }

    public synchronized void setError(URL url, URLSpideredErrorEvent event) throws InvalidStateTransitionException {
        ResourceInternal resource = getResource(url);
        resource.setFetchError(event.getHttpStatus(), event.getHeaders());
        fetchErrorResources.add(url);
    }


}
