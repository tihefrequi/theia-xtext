package {service.namespace}.utils.sap.cloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class AclWrapper {

	private CmisObject cmisObject;
	private Session cmisSession;
		
	public AclWrapper(Session cmisSession, CmisObject cmisObject) {
		this.cmisObject = cmisObject;
		this.cmisSession = cmisSession;
	}

	public void clear(Collection<String> userOwnersUniqueNames, Collection<String> groupOwnersUniqueNames, Collection<String> roleOwnersUniqueNames) {
		
		//...get resource acls
		Acl acl = getAcl();
			
		//...get the list of original acl's to remove
		List<Ace> acesToRemove = acl.getAces();
		
		//...create the list with new owners
		List<Ace> acesToAdd = new ArrayList<Ace>();		
		if(CollectionUtils.isNotEmpty(userOwnersUniqueNames)) {
			for(String userOwnersUniqueName : userOwnersUniqueNames) {
				acesToAdd.add(
					createOwnerEntry(new EcmServicePrincipal(userOwnersUniqueName, EcmServiceConstants.PrincipalType.USER)));					
			}				
		}
		if(CollectionUtils.isNotEmpty(groupOwnersUniqueNames)) {
			for(String groupOwnersUniqueName : groupOwnersUniqueNames) {
				acesToAdd.add(
						createOwnerEntry(new EcmServicePrincipal(groupOwnersUniqueName, EcmServiceConstants.PrincipalType.GROUP)));					
			}				
		}
		if(CollectionUtils.isNotEmpty(roleOwnersUniqueNames)) {
			for(String roleOwnersUniqueName : roleOwnersUniqueNames) {
				acesToAdd.add(
						createOwnerEntry(new EcmServicePrincipal(roleOwnersUniqueName, EcmServiceConstants.PrincipalType.ROLE)));					
			}				
		}
		
		//...reset owner acls
		this.cmisObject.applyAcl(acesToAdd, acesToRemove, AclPropagation.OBJECTONLY);
	}

	public void addOwner(EcmServicePrincipal principal){		
		List<Ace> acesToAdd = new ArrayList<Ace>();		
		acesToAdd.add(createEntry(principal, new String[] {EcmServiceConstants.PERMISSION_ALL}));		
		this.cmisObject.addAcl(acesToAdd, AclPropagation.OBJECTONLY);
	}

	public void addReader(EcmServicePrincipal principal) {
		List<Ace> acesToAdd = new ArrayList<Ace>();		
		acesToAdd.add(createEntry(principal, new String[] {EcmServiceConstants.PERMISSION_READ}));		
		this.cmisObject.addAcl(acesToAdd, AclPropagation.OBJECTONLY);
	}

	public void addWriter(EcmServicePrincipal principal) {
		List<Ace> acesToAdd = new ArrayList<Ace>();		
		acesToAdd.add(createEntry(principal, new String[] {EcmServiceConstants.PERMISSION_WRITE}));		
		this.cmisObject.addAcl(acesToAdd, AclPropagation.OBJECTONLY);
	}

	public void addDeleter(EcmServicePrincipal principal) {
		List<Ace> acesToAdd = new ArrayList<Ace>();		
		acesToAdd.add(createEntry(principal, new String[] {EcmServiceConstants.PERMISSION_DELETE}));		
		this.cmisObject.addAcl(acesToAdd, AclPropagation.OBJECTONLY);
	}

	public void addReaderGroup(String uniqueName) {
		addReader(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.GROUP));
	}

	public void addWriterGroup(String uniqueName) {
		addWriter(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.GROUP));
	}

	public void addDeleterGroup(String uniqueName) {
		addDeleter(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.GROUP));
	}

	public void addReaderUser(String uniqueName) {
		addReader(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.USER));
	}

	public void addWriterUser(String uniqueName) {
		addWriter(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.USER));
	}

	public void addDeleterUser(String uniqueName) {
		addDeleter(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.USER));
	}

	public void addReaderRole(String uniqueName) {
		addReader(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.ROLE));
	}

	public void addWriterRole(String uniqueName) {
		addWriter(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.ROLE));
	}

	public void addDeleterRole(String uniqueName) {
		addDeleter(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.ROLE));
	}

	public void removeUser(String uniqueName) {
		remove(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.USER));
	}

	public void removeGroup(String uniqueName) {
		remove(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.GROUP));
	}

	public void removeRole(String uniqueName) {
		remove(new EcmServicePrincipal(uniqueName, EcmServiceConstants.PrincipalType.ROLE));
	}

	
	public void remove(EcmServicePrincipal principal) {
		Acl acl = getAcl();
		
		//...search for ace to remove
		List<Ace> aceToRemove = new ArrayList<Ace>();
		for (Iterator<Ace> i = acl.getAces().iterator(); i.hasNext();) {
			Ace entry = i.next();
			if (StringUtils.equals(principal.getEcmServiceId(),entry.getPrincipalId())) {
				aceToRemove.add(entry);
			}
		}
		
		//...remove ace
		if(CollectionUtils.isNotEmpty(aceToRemove)) {
			this.cmisObject.removeAcl(aceToRemove, AclPropagation.OBJECTONLY);
		}
		
	}

	public Ace createOwnerEntry(EcmServicePrincipal principal) {
		return this.createEntry(principal, new String[] {EcmServiceConstants.PERMISSION_ALL});
	}
	
	public Ace createEntry(EcmServicePrincipal principal, String... permissions) {
		return this.cmisSession.getObjectFactory().createAce(principal.getEcmServiceId(), Arrays.asList(permissions));
	}

	public void remove() {		
		Acl acl = getAcl();		
		if(acl != null) {
			cmisObject.removeAcl(acl.getAces(), AclPropagation.OBJECTONLY);			
		}
	}

	public Acl getAcl() {
		Acl	acl = cmisObject.getAcl();
		if (acl == null) {
			//...creating the empty list we reset the inherited permissions
			acl = cmisObject.setAcl(new ArrayList<Ace>());
		}
		return acl;
	}

	public String logAcls() {
		return logAclsFor(this.cmisSession, this.cmisObject);
	}

	public static synchronized String logAclsFor(Session cmisSession, CmisObject cmisObject) {
		AclWrapper aclWrapperSource = new AclWrapper(cmisSession, cmisObject);
		try {
			
			Iterator<Ace> aclEntryList = aclWrapperSource.getAcl().getAces().iterator();
			String debug = "";
			int entriesCount = 0;
			while (aclEntryList.hasNext()) {
				Ace aclEntrySource = aclEntryList.next();
				entriesCount++;
				debug += "\n[permissions="
						+ (StringUtils.join(aclEntrySource.getPermissions(), ",")
						+ "] principal=" + aclEntrySource.getPrincipalId());
			}
			debug = "ACL for(" + cmisObject.getId() + ") = #Entries=" + entriesCount + "  " + debug;
			return debug;
		} catch (Throwable t) {
			return "error(" + t.getMessage() + ")";
		}
	}
}
