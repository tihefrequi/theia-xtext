package {service.namespace}.utils.sap.netweaver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.sap.security.api.IUser;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sapportals.portal.security.usermanagement.UserManagementException;
import com.sapportals.wcm.WcmException;
import com.sapportals.wcm.repository.AccessDeniedException;
import com.sapportals.wcm.repository.CheckedOutException;
import com.sapportals.wcm.repository.Content;
import com.sapportals.wcm.repository.ICollection;
import com.sapportals.wcm.repository.IMutablePropertyMap;
import com.sapportals.wcm.repository.IProperty;
import com.sapportals.wcm.repository.IResource;
import com.sapportals.wcm.repository.IResourceContext;
import com.sapportals.wcm.repository.IResourceFactory;
import com.sapportals.wcm.repository.MutablePropertyMap;
import com.sapportals.wcm.repository.NotSupportedException;
import com.sapportals.wcm.repository.ResourceContext;
import com.sapportals.wcm.repository.ResourceException;
import com.sapportals.wcm.repository.ResourceFactory;
import com.sapportals.wcm.repository.manager.IAclSecurityManager;
import com.sapportals.wcm.repository.manager.IRepositoryManager;
import com.sapportals.wcm.repository.security.IResourceAcl;
import com.sapportals.wcm.repository.security.IResourceAclManager;
import com.sapportals.wcm.service.IServiceTypesConst;
import com.sapportals.wcm.service.indexmanagement.IIndex;
import com.sapportals.wcm.service.indexmanagement.IIndexService;
import com.sapportals.wcm.service.urimapper.IUriMapperService;
import com.sapportals.wcm.service.urlgenerator.IURLGeneratorService;
import com.sapportals.wcm.service.urlgenerator.PathKey;
import com.sapportals.wcm.util.acl.AclPersistenceException;
import com.sapportals.wcm.util.acl.IAclPermission;
import com.sapportals.wcm.util.content.IContent;
import com.sapportals.wcm.util.uri.IRidList;
import com.sapportals.wcm.util.uri.IUriReference;
import com.sapportals.wcm.util.uri.RID;
import com.sapportals.wcm.util.uri.RidList;
import com.sapportals.wcm.util.usermanagement.WPUMFactory;

/**
 * 
 * Helper to work with KM
 * 
 * @author storm
 */
public class KMHelper {
    private static Location LOC = Location.getLocation(KMHelper.class);

    /**
     * Content (d.h. Inhalt des Files) als String zurückgeben
     * Dieser Content ist gefiltered (d.h. bereits mit einem Content-Filter o.ä. bearbeitet)
     * 
     * @return Content of File as String
     */
    public static String getContent(IResource resource) {

        InputStream contentStream = null;
        try {
            IContent c = resource.getContent();
            contentStream = c.getInputStream();
            if (StringUtils.isNotBlank(c.getEncoding())) {
                return KMHelper.inputStream2StringEnc(contentStream, c.getEncoding());
            } else {
                return KMHelper.inputStream2String(contentStream);
            }
        } catch (Exception e) {
            LOC.catching("KMHelper.getContent() failed for resource=" + resource, e);
        } finally {
            closeInputStream(contentStream);
            contentStream = null;
        }
        return null;
    }

    /**
     * @return unfiltered content of this file as a string
     */
    public static String getUnfilteredContent(IResource resource) {

        InputStream contentStream = null;
        try {
            contentStream = resource.getUnfilteredContent().getInputStream();
            return inputStream2String(contentStream);
        } catch (Exception e) {
            LOC.catching("KMHelper.getUnfilteredContent() failed for resource=" + resource, e);
        } finally {
            closeInputStream(contentStream);
            contentStream = null;
        }
        return null;
    }

    /**
     * Converts the InputStream to the returned String.
     */
    public static String inputStream2String(InputStream is) {

        int MAX_READ_LEN = 4096;
        StringBuffer sbuf = new StringBuffer();
        int bytesRead = 0;
        int totalBytesRead = 0;
        String inputString;
        byte[] buf = new byte[MAX_READ_LEN];
        try {
            do {
                bytesRead = is.read(buf, 0, MAX_READ_LEN);
                if (bytesRead > 0) {
                    totalBytesRead += bytesRead;
                    inputString = new String(buf, 0, bytesRead);
                    sbuf.append(inputString);
                }
            } while (bytesRead > 0);
        } catch (IOException e) {
            LOC.catching("KMHelper.inputStream2String() failed with IOException. bytesRead=" + bytesRead
                + ", totalBytesRead=" + totalBytesRead, e);
        }
        return sbuf.toString();
    }

    /**
     * Converts the InputStream to the returned String with given encoding.
     * @throws IOException 
     */
    public static String inputStream2StringEnc(InputStream in, String enc) {

        if (in == null)
            return null;
        ByteArrayOutputStream out = null;
        int bytesRead = 0;
        int totalBytesRead = 0;
        try {
            out = new ByteArrayOutputStream();
            byte buffer[] = new byte[0x19000];
            for (bytesRead = 0; (bytesRead = in.read(buffer)) != -1;) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
        } catch (IOException e) {
            LOC.catching("KMHelper.inputStream2StringEnc() failed with IOException. bytesRead=" + bytesRead
                + ", totalBytesRead=" + totalBytesRead, e);
        }
        if (out != null) {
            try {
                return out.toString(enc);
            } catch (UnsupportedEncodingException e) {
                LOC.catching("KMHelper.inputStream2StringEnc() failed with UnsupportedEncodingException. enc=" + enc
                    + ", totalBytesRead=" + totalBytesRead, e);
            }
        }
        return null;
    }

    /**
     * Just retrieve KM Resource given via RID and resourceContext
     * This is a Convenience Function for avoiding Exceptions
     * 
     * @param rid
     *        Resource to retrieve
     * @return IResource or null in case of Errors
     */
    public static IResource getResource(RID rid, IResourceContext resourceContext) {

        IResource resource = null;
        String logger_infos = "getResource(IResource,Context) sourceDocumentRID=["
            + (rid != null ? rid.toString() : "null") + "] context=["
            + (resourceContext != null ? resourceContext.toString() : "null") + "]";
        if (rid == null || StringUtils.isEmpty(rid.toString())) {
            LOC.warningT(logger_infos + "Parameter Problem with rid!");
            return resource;
        }
        try {
            resource = ResourceFactory.getInstance().getResource(rid, resourceContext);
        } catch (ResourceException e) {
            LOC.traceThrowableT(Severity.ERROR, logger_infos + "Resource exception caught: " + e.getMessage(), e);
        }
        if (resource == null) {
            LOC.warningT(logger_infos + "getResource resource=[null]");
        } else {
            LOC.infoT("getResource resource=[" + resource.getDisplayName() + "]");
        }
        return resource;
    }

    /**
     * Tries to get the resource if it exists and if not throws ResourceException.
     * 
     * @param name The path to the resource. May contain internal links. Example: /documents/test/mytest.txt
     */
    public static IResource getResource(String name, IUser user) throws ResourceException {

        IResourceContext ctx = getResourceContext(user);
        return getResource(name, ctx);
    }

    /**
     * Tries to get the resource if it exists and if not throws ResourceException.
     * 
     * @param name The path to the resource. May contain internal links. Example: /documents/test/mytest.txt
     */
    public static IResource getResource(RID name, IUser user) throws ResourceException {

        IResourceContext ctx = getResourceContext(user);
        IResource result = getResource(name, ctx);
        if (null == result) {
            throw new ResourceException("Resource: " + name.toString() + " does not exist");
        }
        return result;
    }

    /**
     * Tries to get the resource if it exists and if not throws ResourceException.
     * 
     * @param name The path to the resource. May contain internal links. Example: /documents/test/mytest.txt
     * @return <code>null</code> if the resource does not exist
     * @since 5.0
     */
    public static IResource getResourceIfExists(String name, IUser user) throws ResourceException {

        IResourceContext ctx = getResourceContext(user);
        return getResourceIfExists(name, ctx);
    }

    /**
     * Tries to get the resource if it exists and if not throws ResourceException.
     * 
     * @param name The path to the resource. May contain internal links. Example: /documents/test/mytest.txt
     * @param resourceContext The context in which to access the resource
     * @throws ResourceException If the name does not specify a resource which can be accessed with the given
     *                           context.
     */
    public static IResource getResource(String name, IResourceContext resourceContext) throws ResourceException {

        IResource result = getResourceIfExists(name, resourceContext);
        if (null == result) {
            throw new ResourceException("Resource: " + name + " does not exist");
        }
        return result;
    }

    /**
     * Retrieves the resource if it exists.
     * 
     * @param name The path to the resource. May contain internal links. Example: /documents/test/mytest.txt
     * @param resourceContext The context in which to access the resource
     * @return <code>null</code> if the resource does not exist
     * @throws ResourceException
     * @since 5.0
     */
    public static IResource getResourceIfExists(String name, IResourceContext resourceContext)
        throws ResourceException {

        if (name != null) {
            name = name.trim(); // Check that no part of the name has leading or trailing spaces / underscores
        }
        IResource result = ResourceFactory.getInstance().getResource(RID.getRID(name), resourceContext);
        return result;
    }

    /**
     * Returns the corresponding KM resource if it exists, otherwise creates a new one
     */
    public static IResource getOrCreateResource(String name, IResourceContext resourceContext) {
        return getOrCreateResource(name, resourceContext, false);
    }

    /**
     * Returns the corresponding KM resource if it exists, otherwise creates a new one
     */
    public static IResource getOrCreateResource(String name, IResourceContext resourceContext, boolean isCollection) {
        try {
            IResource result = getResourceIfExists(name, resourceContext);
            if (null != result)
                return result;

            LOC.infoT("The resource [" + name + "] does not exist. Trying to create a new one.");
            return createResource(name, resourceContext, true, isCollection);
        } catch (ResourceException e) {
            LOC.traceThrowableT(Severity.ERROR,
                "Cannot get/create the resource [" + name + "]. Check whether the resource exists or can be created.",
                e);
        }
        return null;

    }

    /**
     * Returns the corresponding KM collection if it exists, otherwise creates a new one
     * @throws ResourceException if it was not possible to create an IResourceContext for the user
     */
    public static ICollection getOrCreateCollection(String name, IUser user) throws ResourceException {
        IResourceContext resourceContext = getResourceContext(user);
        return getOrCreateCollection(name, resourceContext);
    }

    /**
     * Returns the corresponding KM collection if it exists, otherwise creates a new one
     */
    public static ICollection getOrCreateCollection(String name, IResourceContext resourceContext) {
        return (ICollection) getOrCreateResource(name, resourceContext, true);
    }

    /**
     * Uses the KM Admin resource Context to check if the resource exists; never throws a Exception. Note that this 
     * method internally uses {@link IResourceFactory#checkExistence(RID, IResourceContext)}, which does not resolve 
     * internal links. 
     * 
     * @param name
     *        of the resource
     * @return if we have found an resource, if not maybe because of an exception we return safely false 
     * @throws ResourceException 
     */
    public static boolean existsResource(String name) throws ResourceException {

        return existsResource(name, KMAccessHelper.getResourceContext());
    }

    /**
     * Check if the resource exists, never throws a Exception. Note that this method internally uses 
     * {@link IResourceFactory#checkExistence(RID, IResourceContext)}, which does not resolve internal links.
     * 
     * @param name
     *        of the resource
     * @param resourceContext
     *        is the context in which we search the resource
     * @return if we have found an resource, if not maybe because of an exception we return safely false 
     */
    public static boolean existsResource(String name, IResourceContext resourceContext) {

        boolean exists = false;
        try {
            // Check that no part of the name has leading or trailing spaces / underscores
            if (name != null) {
                name = name.trim();
            }
            RID resourceRID = RID.getRID(name);
            exists = ResourceFactory.getInstance().checkExistence(resourceRID, resourceContext);
        } catch (Throwable ignore) {
            // We ignore this error as we want only a fast check function
        }
        return exists;
    }

    public static void closeQuietly(IContent contentToClose) {

        try {
            if (contentToClose != null) {
                contentToClose.close();
            }
        } catch (Throwable e) {
            // do nothing
        }

    }

    public static void updateContent(IResource resource, Content newContent) {

        try {
            if (resource.isVersioned()) {
                resource.checkOut();
                resource.checkIn(newContent, null);
            } else {
                resource.updateContent(newContent);
            }
        } catch (NotSupportedException e) {
            throw new RuntimeException(e);
        } catch (AccessDeniedException e) {
            throw new RuntimeException(e);
        } catch (ResourceException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Replace Content of IResource with given String
     * 
     * @param resource
     * @param contentType for example text/xml. Note that this is not equal to {@link IResource#getResourceType()}
     * @param content
     *        String in utf-8 encoding
     * 
     * @return given Resource
     */
    public static IResource writeContentUTF8(IResource resource, String contentType, String content)
        throws NotSupportedException, AccessDeniedException, ResourceException {

        ByteArrayInputStream dataStream = null;
        try {
            dataStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            // utf-8 is always supported
        }
        IContent newContent = new Content(dataStream, contentType, dataStream.available());
        if (resource.isVersioned()) {
            resource.checkOut();
            resource.checkIn(newContent, null);
        } else {
            resource.updateContent(newContent);
        }
        return resource;
    }

    /**
     * Change property values on given resource
     * 
     * @param resource
     * @param newProperty
     * @throws Exception 
     */
    public static void changeResourceProperty(IResource resource, IProperty newProperty) throws ResourceException {

        IMutablePropertyMap mutableMap = null;
        try {
            if (resource.isVersioned()) {
                try {
                    resource.checkOut();
                } catch (CheckedOutException e1) {
                    LOC.traceThrowableT(Severity.WARNING,
                        "Resource already checked out [" + resourceInfoLong(resource)
                            + "] We still try to set the Property, if the former checkout was done by ourselves in another run of this program",
                        e1);
                }
                mutableMap = new MutablePropertyMap();
                mutableMap.put(newProperty);
            } else {
                resource.setProperty(newProperty);
            }
        } catch (ResourceException e) {
            final String errorMessage = "Error during change of resource [" + resourceInfoLong(resource)
                + "] propertyValue [" + newProperty + "] propertyName ["
                + (newProperty == null ? null : newProperty.getPropertyName()) + "]";
            LOC.traceThrowableT(Severity.ERROR, errorMessage, e);
            throw new ResourceException(errorMessage, e);
        } finally {
            if (resource.isVersioned()) {
                try {
                    resource.checkIn(resource.getContent(), mutableMap);
                } catch (ResourceException e2) {
                    final String errorMessage = "Error during checkin of resource [" + resourceInfoLong(resource)
                        + "] propertyValue [" + newProperty + "] propertyName ["
                        + (newProperty == null ? null : newProperty.getPropertyName()) + "]";
                    LOC.traceThrowableT(Severity.ERROR, errorMessage, e2);
                    throw new ResourceException(errorMessage, e2);
                }
            }
        }
    }

    /** 
     * BTEAWC-180 Close the input stream in any case! Otherwise the system will open more and more input streams
     * until all system resources are depleted
     */
    public static void closeInputStream(InputStream inputStream) {

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOC.traceThrowableT(Severity.ERROR, "getContent/finally", "Failed to close input stream", e);
            }
        }
    }

    /**
     * 
     * Umwandeln einer IResource in eine langlebige GUID (String)
     * 
     * @param resource
     *        Resource, für die wir die GUID bestimmen wollen
     * @return GUID or null
     */
    public static String getGUIDFromResource(IResource resource) {
        // Parameter Checking
        if (resource == null) {
            LOC.warningT("getGUIDFromResource(resource=[" + resource + "])");
            return null;
        }

        try {
            return getGUIDFromRID(resource.getRID());
        } catch (WcmException e) {
            LOC.traceThrowableT(Severity.ERROR, "Failed to retrieve GUID of " + resourceInfoLong(resource), e);
            return null;
        }
    }

    /**
     * Retrieve a GUID for the given string via the {#link {@link IUriMapperService}
     * @return A valid GUID
     * @throws WcmException If the GUID could not be constructed for some reason
     * @since 2012-02-28
     */
    public static String getGUIDFromRID(String rid) throws WcmException {
        return getGUIDsFromRIDs(new RidList(rid.trim()))[0];
    }

    /**
     * Retrieve a GUID for the given RID via the {#link {@link IUriMapperService}
     * @return A valid GUID
     * @throws WcmException If the GUID could not be constructed for some reason
     * @since 2012-02-28
     */
    public static String getGUIDFromRID(RID rid) throws WcmException {
        return getGUIDsFromRIDs(new RidList(new RID[] { rid }))[0];
    }

    /**
     * Retrieve GUIDs for the given RIDs via the {@link IUriMapperService}
     * @return A non-empty array of GUIDs
     * @throws WcmException If the GUIDs could not be constructed for some reason
     * @since 2012-02-28
     */
    public static String[] getGUIDsFromRIDs(IRidList rids) throws WcmException {
        String[] resourceGuids = null;

        IUriMapperService uriMapper = (IUriMapperService) ResourceFactory
            .getInstance().getServiceFactory().getService(IServiceTypesConst.URIMAPPER_SERVICE);
        if (uriMapper == null) {
            throw new WcmException("Failed to retrieve IUriMapperService");
        }

        // GUID vom URI-Mapper besorgen
        LOC.infoT("getGUIDFromRID", "rids=[" + rids + "]");

        // i.e. not available for NW 7.0 SPS14 : resourceGuids = uriMapper.getCreateConstantIDs(rids);
        if (rids != null && rids.size() > 0) {
            resourceGuids = new String[rids.size()];
            for (int i = 0; i < rids.size(); i++) {
                String resourceGuid = uriMapper.getCreateConstantID(rids.get(i));
                resourceGuids[i] = resourceGuid;
            }
        }

        LOC.infoT("getGUIDFromRID", "resourceGuids =[" + StringUtils.join(resourceGuids, ';') + "]");

        // Final Logging
        if (resourceGuids == null || resourceGuids.length == 0) {
            throw new WcmException("Failed to get or create GUIDs for RID list=[" + rids + "]");
        }

        return resourceGuids;
    }

    /**
     * 
     * Umwandeln einer String-GUID in eine RID
     * 
     * @param resourceGuid
     *        GUID einer Resource
     * @param resourceContext
     *        Resource-Context, in dem die IResource zurückgegeben werden soll
     * @return RID or null
     * @deprecated
     */
    @Deprecated
    public static RID getRIDFromGUID(String resourceGuid, IResourceContext resourceContext) {

        return getRIDFromGUID(resourceGuid);
    }

    /**
     * 
     * Umwandeln einer String-GUID in eine RID
     * 
     * @param resourceGuid
     *        GUID einer Resource
     * @return RID or null
     */
    public static RID getRIDFromGUID(String resourceGuid) {

        String logger_infos = "getRIDFromGUID(guid=[" + resourceGuid + "]";
        // Parameter Checking
        if (StringUtils.isEmpty(resourceGuid)) {
            LOC.warningT(logger_infos + "PARAMETERPROBLEM!");
            return null;
        }

        // Get URI-MapperService and try to retrieve RID
        RID rid = null;
        IUriMapperService uriMapper = null;
        try {
            uriMapper = (IUriMapperService) ResourceFactory
                .getInstance().getServiceFactory().getService(IServiceTypesConst.URIMAPPER_SERVICE);
            if (uriMapper != null) {
                try {
                    rid = uriMapper.getRIDFromConstantID(resourceGuid);
                } catch (WcmException e) {
                    LOC.traceThrowableT(Severity.ERROR,
                        logger_infos + "getRIDFromConstantID() Exception WcmException caught" + e.getMessage(), e);
                    return null;
                }
            }
        } catch (ResourceException e2) {
            LOC.traceThrowableT(Severity.ERROR, logger_infos + "Exception ResourceException caught" + e2.getMessage()
                + "for Service " + IServiceTypesConst.URIMAPPER_SERVICE, e2);
            return null;
        }
        if (rid == null || StringUtils.isEmpty(rid.toString())) {
            LOC.warningT(logger_infos + " rid is empty");
        } else {
            LOC.infoT(logger_infos + " returning rid=" + rid);
        }
        return rid;
    }

    /**
     * 
     * Umwandeln einer String-KM-URL ("/irj/go/km/docs/<pfad>" in eine RID nach "/<pfad>"
     * 
     * @param resourceUrl
     *        URL einer Resource
     * @return RID or null
     */
    public static RID getRIDFromAccessURL(String resourceUrl) {

        String URL_PREFIX = "/irj/go/km/docs";
        String logger_infos = "getRIDFromAccessURL(url=[" + resourceUrl + "]";
        // Parameter Checking
        if (StringUtils.isEmpty(resourceUrl) || StringUtils.indexOf(resourceUrl, URL_PREFIX) == -1) {
            LOC.warningT(logger_infos + "PARAMETERPROBLEM!");
            return null;
        }

        String path = StringUtils.substringAfter(resourceUrl, URL_PREFIX);

        RID rid = RID.getRID(path);
        return rid;
    }

    /**
     * 
     * Umwandeln einer String-GUID in eine IResource
     * 
     * @param resourceGuid
     *        GUID einer Resource
     * @param user
     *        User für den Resource-Context, in dem die IResource zurückgegeben werden soll
     * @return IResource or null
     */
    public static IResource getResourceFromGUID(String resourceGuid, IUser user) {

        String logger_infos = "getResourceFromGUID(guid=[" + resourceGuid + "],user=[" + user + "] ";
        // Parameter Checking
        if (resourceGuid == null || user == null) {
            LOC.warningT(logger_infos + "Parameter Problems");
            return null;
        }

        // get RID from GUID
        RID rid = getRIDFromGUID(resourceGuid);
        if (rid == null) {

            LOC.errorT(logger_infos + "Could not get RID for GUID-Resource.");
            return null;
        }

        // get Resource from RID
        try {
            IResourceContext ctx = getResourceContext(user);
            IResource resource = getResource(rid, ctx);
            return resource;
        } catch (ResourceException e) {
            LOC.traceThrowableT(Severity.WARNING, logger_infos + " rid=[" + rid.toExternalForm() + "] resource=null!",
                e);
            return null;
        }

    }

    /**
     * Creates the collection in KM with subfolders if they are not existst. Checks first whether the parent folder exists. 
     * If <code>createSubFolders</code> is true the subfolders will be created as well. 
     * Otherwise an exception will be thrown, if the parent folder don't exists.
     * @param pathInKM Path in KM to create
     * @param resourceContext Resource context
     * @param createSubFolders True, to create subfolders
     * @return IResource instance if the resource could be created
     * @throws ResourceException 
     */
    public static ICollection createCollection(String pathInKM, IResourceContext resourceContext,
        boolean createSubFolders) throws ResourceException {
        return (ICollection) createResource(pathInKM, resourceContext, createSubFolders, true);
    }

    /**
     * Creates the resource in KM with subfolders if they are not existst. Checks first whether the parent folder exists. 
     * If <code>createSubFolders</code> is true the subfolders will be created as well. 
     * Otherwise an exception will be thrown, if the parent folder don't exists.
     * @param pathInKM Path in KM to create
     * @param resourceContext Resource context
     * @param createSubFolders True, to create subfolders
     * @return IResource instance if the resource could be created
     * @throws ResourceException 
     */
    public static IResource createResource(String pathInKM, IResourceContext resourceContext, boolean createSubFolders)
        throws ResourceException {
        return createResource(pathInKM, resourceContext, createSubFolders, false);
    }

    /*
     * Creates the resource/collection in KM with subfolders if they are not existst. Checks first whether the parent folder exists. 
     * If <code>createSubFolders</code> is true the subfolders will be created as well. 
     * Otherwise an exception will be thrown, if the parent folder don't exists.
     * If <code>isCollection</code> is true the collecition will be created instead of resource. 
     * @param pathInKM Path in KM to create
     * @param resourceContext Resource context
     * @param createSubFolders True, to create subfolders
     * @param isCollection True, to create collection instead of a resource
     * @return IResource instance if the resource could be created
     * @throws ResourceException 
     */
    private static IResource createResource(String pathInKM, IResourceContext resourceContext, boolean createSubFolders,
        boolean isCollection) throws ResourceException {

        try {

            // Get RID of the path
            if (pathInKM != null) {
                pathInKM = pathInKM.trim();
            }
            RID rid = RID.getRID(pathInKM);

            // Check whether the resource already exists
            IResourceFactory resourceFactory = ResourceFactory.getInstance();
            LOC.infoT("Checking the existence of the resource [" + rid.getPath() + "]");
            if (resourceFactory.checkExistence(rid, resourceContext)) {
                LOC.infoT("The resource already exists");
                return resourceFactory.getResource(rid, resourceContext);
            }

            // Get parent collection
            ICollection parentCollection;
            RID parentRid = RID.getRID(StringUtils.substringBeforeLast(pathInKM, "/"));

            // Check whether the parent collection already exists
            LOC.infoT("Checking the existence of the parent collection resource [" + parentRid.getPath() + "]");
            if (resourceFactory.checkExistence(rid, resourceContext)) {
                parentCollection = (ICollection) resourceFactory.getResource(parentRid, resourceContext);
                LOC.infoT("The parent collection is already exists, so the resource can be created");
            } else {
                if (!createSubFolders) {
                    throw new ResourceException(
                        "The parent collection for the resource [" + pathInKM + "] does not exists");
                } else {
                    LOC.infoT("Trying to create the parent collection (incl. subfolders)");
                    parentCollection = (ICollection) resourceFactory.getResource(parentRid, resourceContext, true);
                }
            }

            // Create resource/collection
            if (isCollection) {
                return parentCollection.createCollection(StringUtils.substringAfterLast(pathInKM, "/"), null);
            } else {
                return parentCollection.createResource(StringUtils.substringAfterLast(pathInKM, "/"), null, null);
            }

        } catch (Exception e) {
            throw new ResourceException("Failed to create resource [" + pathInKM + "]", e);
        }
    }

    /**
     * @deprecated
     * @see #hasPermission
    * Checks whether a certain user has got write permission on a certain resource.
    * 
    * @param user
    *        the user for which the check shall be done
    * @param res
    *        the resource on which the check shall be done
    * 
    * @return yes if the user has got write permission on the resource, no otherwise
    */
    @Deprecated
    public static boolean hasWriteAccess(com.sapportals.portal.security.usermanagement.IUser user, IResource res) {
        return hasPermission(user, res, IAclPermission.ACL_PERMISSION_WRITE);
    }

    /**
     * Checks whether a certain user has got read permission on a certain resource.
     * 
     * @param user
     *        the user for which the check shall be done
     * @param res
     *        the resource on which the check shall be done
     * @param perm
     *        the requested permission i.e. IAclPermission.ACL_PERMISSION_WRITE, IAclPermission.ACL_PERMISSION_READ, ...         
     * 
     * @return yes if the user has got read permission on the resource, no otherwise
     */
    public static boolean hasPermission(com.sapportals.portal.security.usermanagement.IUser user, IResource res,
        String perm) {
        try {
            return checkResourcePermission(user, res, perm);
        } catch (Exception e) {
            LOC.traceThrowableT(Severity.ERROR,
                "Failed when trying to determine km permission [" + perm + "] on " + resourceInfoLong(res), e);
        }
        return false;
    }

    /**
     * Checks whether a certain user has got write permission on a certain resource.
     * 
     * @param user
     *        the user for which the check shall be done
     * @param res
     *        the resource on which the check shall be done
     * 
     * @return yes if the user has got write permission on the resource, no otherwise
     */
    public static boolean hasWriteAccess(IUser user, IResource res) {

        try {
            return checkResourcePermission(user, res, IAclPermission.ACL_PERMISSION_WRITE);
        } catch (Exception e) {
            LOC.traceThrowableT(Severity.ERROR,
                "Failed when trying to determine write rights on " + resourceInfoLong(res), e);
        }
        return false;
    }

    /**
     * Checks whether a certain user has got write permission on a certain resource.
     * 
     * @param user
     *        the user for which the check shall be done
     * @param resourcename
     *        the name of the resource on which the check shall be done
     * 
     * @return yes if the user has got write permission on the resource, no otherwise
     */
    public static boolean hasWriteAccess(IUser user, String resourcename) {

        try {
            IResource resource = KMHelper.getResource(resourcename, user);
            return checkResourcePermission(user, resource, IAclPermission.ACL_PERMISSION_WRITE);
        } catch (Exception e) {
            LOC.traceThrowableT(Severity.ERROR, "Failed when trying to determine write rights on " + resourcename, e);
        }
        return false;
    }

    /**
     * Check Permissions for KM-Resource
     * 
     * @param user
     * @param resource
     * @param permission please use only values like IAclPermission.ACL_PERMISSION_WRITE or others
     * @return
     * @throws AclPersistenceException
     * @throws ResourceException
     */
    public static boolean checkResourcePermission(com.sapportals.portal.security.usermanagement.IUser user,
        IResource res, String permission) throws AclPersistenceException, ResourceException {

        IResourceAclManager resAclMan = getResourceAclManager(res);
        IAclPermission aclPermission = resAclMan.getPermission(permission);
        IResourceAcl acl = resAclMan.getAcl(res);
        if (acl == null) {
            acl = resAclMan.getInheritedAcl(res);
        }
        // Workaround for KM delivery state - Everyone -> Full Control,
        // but no explicit ACLs are set - in this case, even getInheritedAcl returns null
        if (acl == null || acl.checkPermission(user, aclPermission)) {
            return true;
        }
        return false;
    }

    /**
     * check if the user has the given permission in a given resource acl
     * @param acl
     * @param user
     * @param aclPermission
     * @return
     * @throws AclPersistenceException
     */
    public static boolean checkPermission(IResourceAcl acl, IUser user, IAclPermission aclPermission)
        throws AclPersistenceException {
        return acl.checkPermission(user, aclPermission);
    }

    /**
     * Check Permissions for KM-Resource
     * 
     * @param user
     * @param resource
     * @param permission please use only values like IAclPermission.ACL_PERMISSION_WRITE or others
     * @return
     * @throws AclPersistenceException
     * @throws ResourceException
     */
    public static boolean checkResourcePermission(IUser user, IResource resource, String permission)
        throws AclPersistenceException, ResourceException {

        IResourceAclManager resAclMan = getResourceAclManager(resource);
        IAclPermission aclPermission = resAclMan.getPermission(permission);
        IResourceAcl acl = resAclMan.getAcl(resource);
        if (acl == null) {
            acl = resAclMan.getInheritedAcl(resource);
        }
        // Workaround for KM delivery state - Everyone -> Full Control,
        // but no explicit ACLs are set - in this case, even getInheritedAcl returns null
        if (acl == null || checkPermission(acl, user, aclPermission)) {
            return true;
        }
        return false;
    }

    /**
     * @param resource
     * @return
     * @throws ResourceException
     */
    public static IResourceAclManager getResourceAclManager(IResource resource) throws ResourceException {

        IRepositoryManager repMan = resource.getRepositoryManager();
        IAclSecurityManager aclSecMan = (IAclSecurityManager) repMan.getSecurityManager(resource);
        IResourceAclManager resAclMan = aclSecMan.getAclManager();
        return resAclMan;
    }

    /**
     * this method deindexes the document from all indexes where it is indexed and index it again.<br>
     * <strong>NOTE:</strong> the index queue settings should be set appropriately. especially, if high traffic is
     * expected
     */
    public static void reindex(IResource resource) throws WcmException {

        try {
            IIndexService indexService = (IIndexService) ResourceFactory
                .getInstance().getServiceFactory().getService(IServiceTypesConst.INDEX_SERVICE);
            List indexes = indexService.getIndexesForResource(resource);
            for (Iterator indexIter = indexes.iterator(); indexIter.hasNext();) {
                IIndex index = (IIndex) indexIter.next();
                index.deindexDocument(resource);
                index.indexDocument(resource);
            }
        } catch (IOException e) {
            throw new WcmException(e);
        }
    }

    /**
     * @return The KM content access path prefix specified in the URL generator service, typically
     * <code>/irj/go/km/docs</code>.
     */
    public static String getIrjGoKmDocs() throws WcmException {
        IURLGeneratorService urlGen = (IURLGeneratorService) ResourceFactory
            .getInstance().getServiceFactory().getService(IURLGeneratorService.URLGENERATOR_SERVICE);
        IUriReference uriRef = urlGen.getRelativeUri(PathKey.CONTENT_ACCESS_PATH);
        return uriRef.toString();
    }

    /**
     * Resource info log (Displayname + Path)
     * 
     * @param r
     *        Resource
     * @return Resource info long
     */
    public static String resourceInfoLong(IResource r) {

        try {
            return "Resource [" + (r != null ? r.getDisplayName(true) : "null") + "] path=["
                + (r != null ? (r.getRID() != null ? r.getRID().getPath().toString() : "null") : "null") + "] ";
        } catch (Exception e) {
            return resourceInfo(r) + "Exception caught during DebugUtils " + e.getMessage();
        }
    }

    /**
     * Some Debugging Info (only Displayname)
     * 
     * @param r
     *        Resource
     * @return Resouce info
     */
    public static String resourceInfo(IResource r) {
        return "Resource [" + (r != null ? r.getDisplayName(true) : "null") + "] ";
    }

    /**
     * Creates a KM context for given UME user
     * @since 1.0
     * @deprecated Use {@link #getResourceContext(IUser, Locale)} to provide a fallback locale in case the user
     *             does not have one configured
     */
    @Deprecated
    public static IResourceContext getResourceContext(IUser user) throws ResourceException {

        return getResourceContext(user, null);
    }

    /**
     * Creates a KM context for given UME user with a fallback locale
     * @since 1.0 SP5
     */
    public static IResourceContext getResourceContext(IUser user, Locale fallbackLocale) throws ResourceException {

        try {
            return new ResourceContext(getEP5User(user), fallbackLocale);
        } catch (UserManagementException e) {
            throw new ResourceException("Failed to create resource context for user=[" + user + "]", e);
        }
    }

    /**
     * Returns UME User of the given EP5 User
     * 
     * @param UME
     *        User com.sap.security.api.IUser
     * @return EP5 User com.sapportals.portal.security.usermanagement.IUser
     * @throws UserManagementException
     */
    public static com.sapportals.portal.security.usermanagement.IUser getEP5User(IUser user)
        throws UserManagementException {

        com.sapportals.portal.security.usermanagement.IUserFactory userFactory = WPUMFactory.getUserFactory();
        com.sapportals.portal.security.usermanagement.IUser eP5User = userFactory.getEP5User(user);
        return eP5User;
    }

}
