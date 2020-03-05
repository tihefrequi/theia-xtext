package {service.namespace}.utils.sap.netweaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sap.security.api.IGroup;
import com.sap.security.api.IPrincipal;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sapportals.wcm.WcmException;
import com.sapportals.wcm.repository.IResource;
import com.sapportals.wcm.repository.manager.IAclSecurityManager;
import com.sapportals.wcm.repository.security.IResourceAcl;
import com.sapportals.wcm.repository.security.IResourceAclEntry;
import com.sapportals.wcm.repository.security.IResourceAclEntryListIterator;
import com.sapportals.wcm.repository.security.IResourceAclManager;
import com.sapportals.wcm.util.acl.IAclPermission;

public class AclWrapper {

    private IResourceAcl acl;
    private IResourceAcl inheritedAcl;
    private IResource resource;
    private IResourceAclManager manager;

    public AclWrapper(IResourceAcl acl) {
        this.acl = acl;

        try {
            resource = acl.getResource();
            manager = ((IAclSecurityManager) resource.getRepositoryManager().getSecurityManager(resource))
                .getAclManager();
            inheritedAcl = manager.getInheritedAcl(resource);
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }
    }

    public AclWrapper(IResource resource) {
        this.resource = resource;

        try {
            manager = ((IAclSecurityManager) resource.getRepositoryManager().getSecurityManager(resource))
                .getAclManager();
            acl = manager.getAcl(resource);
            inheritedAcl = manager.getInheritedAcl(resource);
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }
    }

    public void clear(Collection<String> userOwnersUniqueNames, Collection<String> groupOwnersUniqueNames)
        throws UMException, WcmException {
        List<?> owners = getAcl().getOwnersUME();
        List<IPrincipal> newOwners = new ArrayList<IPrincipal>();
        if (groupOwnersUniqueNames != null) {
            for (String uniqueName : groupOwnersUniqueNames) {
                newOwners.add(getGroup(uniqueName));
            }
        }
        if (userOwnersUniqueNames != null) {
            for (String uniqueName : userOwnersUniqueNames) {
                newOwners.add(getUser(uniqueName));
            }
        }
        for (IPrincipal newOwner : newOwners) {
            getAcl().addOwner(newOwner);
        }
        for (Object oldOwner : owners) {
            if (oldOwner instanceof IPrincipal && !newOwners.contains(oldOwner)) {
                getAcl().removeOwner((IPrincipal) oldOwner);
            }
        }

        // clear entries
        for (IResourceAclEntryListIterator i = getAcl().getEntries().iterator(); i.hasNext();) {
            getAcl().removeEntry(i.next());
        }
    }

    public void clearInherited() throws WcmException {
        if (inheritedAcl != null) {
            clearAclEntries(inheritedAcl);
        }
    }

    public void clearAclEntries(IResourceAcl resourceAcl) throws WcmException {
        IResourceAclEntryListIterator resourceAcls = resourceAcl.getEntries().iterator();
        while (resourceAcls.hasNext()) {
            IResourceAclEntry resourceAclEntry = resourceAcls.next();
            if (resourceAclEntry != null) {
                resourceAcl.removeEntry(resourceAclEntry);
            }
        }
    }

    private static IGroup getGroup(String uniqueName) throws UMException {
        return UMFactory.getGroupFactory().getGroupByUniqueName(uniqueName);
    }

    private static IUser getUser(String uniqueName) throws UMException {
        return UMFactory.getUserFactory().getUserByUniqueName(uniqueName);
    }

    private static IUser getUserByUniqueId(String uniqueId) throws UMException {
        return UMFactory.getUserFactory().getUser(uniqueId);
    }

    public void addOwner(IPrincipal principal) throws WcmException {
        getAcl().addOwner(principal);
    }

    public void addOwnersUME(List ownersUME) throws WcmException {
        if (ownersUME != null) {
            for (int i = 0; i < ownersUME.size(); i++) {
                getAcl().addOwner((IPrincipal) ownersUME.get(i));
            }
        }
    }

    public void addReader(IPrincipal principal) throws WcmException {
        getAcl().addEntry(createEntry(principal, IAclPermission.ACL_PERMISSION_READ));
    }

    public void addWriter(IPrincipal principal) throws WcmException {
        getAcl().addEntry(createEntry(principal, IAclPermission.ACL_PERMISSION_READWRITE));
    }

    public void addDeleter(IPrincipal principal) throws WcmException {
        getAcl().addEntry(createEntry(principal, IAclPermission.ACL_PERMISSION_DELETE));
    }

    public void addFullController(IPrincipal principal) throws WcmException {
        getAcl().addEntry(createEntry(principal, IAclPermission.ACL_PERMISSION_FULL_CONTROL));
    }

    public void addReaderGroup(String uniqueName) throws UMException, WcmException {
        addReader(getGroup(uniqueName));
    }

    public void addWriterGroup(String uniqueName) throws UMException, WcmException {
        addWriter(getGroup(uniqueName));
    }

    public void addDeleterGroup(String uniqueName) throws UMException, WcmException {
        addDeleter(getGroup(uniqueName));
    }

    public void addFullControllerGroup(String uniqueName) throws UMException, WcmException {
        addFullController(getGroup(uniqueName));
    }

    public void addReaderUser(String uniqueName) throws UMException, WcmException {
        addReader(getUser(uniqueName));
    }

    public void addWriterUser(String uniqueName) throws UMException, WcmException {
        addWriter(getUser(uniqueName));
    }

    public void addDeleterUser(String uniqueName) throws UMException, WcmException {
        addDeleter(getUser(uniqueName));
    }

    public void addFullControllerUser(String uniqueName) throws UMException, WcmException {
        addFullController(getUser(uniqueName));
    }

    public void addFullControllerUserByUniqueId(String uniqueName) throws UMException, WcmException {
        addFullController(getUserByUniqueId(uniqueName));
    }

    public void removeUser(String uniqueName) throws UMException, WcmException {
        remove(getUser(uniqueName));
    }

    public void removeGroup(String uniqueName) throws UMException, WcmException {
        remove(getGroup(uniqueName));
    }

    public void remove(IPrincipal principal) throws UMException, WcmException {
        if (hasAcl()) {
            for (IResourceAclEntryListIterator i = getAcl().getEntries().iterator(); i.hasNext();) {
                IResourceAclEntry entry = i.next();
                if (principal.equals(entry.getPrincipalUME())) {
                    getAcl().removeEntry(entry);
                }
            }
        }
    }

    private IResourceAclEntry createEntry(IPrincipal principal, String permission) throws WcmException {
        return manager.createAclEntry(principal, false, manager.getPermission(permission), 0);
    }

    public void remove() throws WcmException {
        acl = null;
        manager.removeAcl(resource);
    }

    public boolean hasAcl() {
        return acl != null;
    }

    public IResourceAcl getAcl() throws WcmException {
        if (acl == null) {
            acl = manager.getAcl(resource);
            if (acl == null) {
                acl = manager.createAcl(resource);
            }
        }
        return acl;
    }

    public String logAcls() {
        return logAclsFor(this.resource);
    }

    public static synchronized String logAclsFor(IResource resource) {
        AclWrapper aclWrapperSource = new AclWrapper(resource);
        try {
            IResourceAclEntryListIterator aclEntryList = aclWrapperSource.getAcl().getEntries().iterator();
            String debug = "";
            int entriesCount = 0;
            while (aclEntryList.hasNext()) {
                IResourceAclEntry aclEntrySource = aclEntryList.next();
                entriesCount++;
                debug += "\n[permissions="
                    + (aclEntrySource.getPermission() != null ? aclEntrySource.getPermission().getName()
                        : aclEntrySource.getPermission())
                    + "] principal=" + aclEntrySource.getPrincipalUME().getUniqueID();
            }
            debug = "ACL for(" + resource.getRID() + ") = #Entries=" + entriesCount + "  " + debug;
            return debug;
        } catch (Throwable t) {
            return "error(" + t.getMessage() + ")";
        }
    }
}
