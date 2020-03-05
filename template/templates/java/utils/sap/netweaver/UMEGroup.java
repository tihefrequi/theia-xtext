package {service.namespace}.utils.sap.netweaver;

import {service.namespace}.auth.IApplicationGroup;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.sap.security.api.IGroup;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class UMEGroup implements IApplicationGroup {
    private static Location LOC = Location.getLocation(UMEGroup.class);
    private IGroup group;

    public UMEGroup(IGroup group) {
        super();
        this.group = group;
    }

    @Override
    public String getUniqueName() {
        return group.getUniqueName();
    }

    @Override
    public String getDisplayName() {
        return group.getDisplayName() + " (" + getUniqueName() + ")";
    }

    @Override
    public Set<String> getDirectGroupUniqueNames() {
        return getRecursiveGroupUniqueNames(false);
    }

    @Override
    public Set<String> getRecursiveGroupUniqueNames() {
        return getRecursiveGroupUniqueNames(true);
    }

    @Override
    public Set<String> getDirectUserUniqueNames() {
        return getRecursiveUserUniqueNames(false);
    }

    @Override
    public Set<String> getRecursiveUserUniqueNames() {
        return getRecursiveUserUniqueNames(true);
    }

    /**
     * 
     * @param recursive
     * @return
     */
    private Set<String> getRecursiveGroupUniqueNames(boolean recursive) {
        Set<String> uniqueNames = new HashSet<String>();
        IGroupFactory groupFactory = UMFactory.getGroupFactory();
        Iterator members = group.getGroupMembers(recursive);
        while (members != null && members.hasNext()) {
            String uniqueId = (String) members.next();
            try {
                IGroup member = groupFactory.getGroup(uniqueId);
                uniqueNames.add(member.getUniqueName());
            } catch (Exception e) {
                LOC.traceThrowableT(Severity.WARNING,
                    "Could not access group with unique id [" + uniqueId
                        + "] while we should retrieve the getRecursiveGroupUniqueNames[recursive=" + recursive
                        + "] from group [" + getUniqueName()
                        + "] with Error [" + e.getMessage() + "]",
                    e);
            }
        }
        return uniqueNames;
    }

    /**
     * 
     * @param recursive
     * @return
     */
    private Set<String> getRecursiveUserUniqueNames(boolean recursive) {
        Set<String> uniqueNames = new HashSet<String>();
        IUserFactory userFactory = UMFactory.getUserFactory();
        Iterator members = group.getUserMembers(recursive);
        while (members != null && members.hasNext()) {
            String uniqueId = (String) members.next();
            try {
                IUser member = userFactory.getUser(uniqueId);
                uniqueNames.add(member.getUniqueName());
            } catch (Exception e) {
                LOC.traceThrowableT(Severity.WARNING,
                    "Could not access user with unique id [" + uniqueId
                        + "] while we should retrieve the getRecursiveUserUniqueNames[recursive=" + recursive
                        + "] from group [" + getUniqueName() + "] with Error [" + e.getMessage() + "]",
                    e);
            }
        }
        return uniqueNames;
    }

}
