package {service.namespace}.utils.sap.netweaver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;

import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserChangeBuilder;
import {service.namespace}.auth.IApplicationUserProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;
import com.sap.security.api.IGroup;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.IUserMaint;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class UMEApplicationUserProvider extends AbstractUMEApplicationPrincipalProvider implements IApplicationUserProvider {
	private static Location LOC = Location.getLocation(UMEApplicationUserProvider.class);
	private static final int MAX_SEARCH_RESULT_SIZE_USERS = 20;
	
	@Override
	public int countUsers(IPrincipalSearchRequest searchRequest) {
	    boolean isJokerSearch = UMEHelper.hasNoSearchTermOrOnlyJoker(searchRequest.getSearchTerm());
	    TimedSection timedSection = null;
	    int count = 0;
	    try {
	        if (searchRequest.getHide().isEmpty() && searchRequest.getContainedInGroups().isEmpty() && isJokerSearch) {
	            // IF We have no group and hide restrictions AND a joker search, we have to return the count for all
	            // users
	            timedSection = TimedSection.start(LOC, "countUsers(all with joker)",
	                "isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest);
	            IUserFactory userFactory = UMFactory.getUserFactory();
	            ISearchResult uniqueIDsSearchResult = userFactory.getUniqueIDs();
	            count = uniqueIDsSearchResult.size();
	        } else if (searchRequest.getHide().isEmpty() && searchRequest.getContainedInGroups().isEmpty()
	            && !isJokerSearch) {
	            // IF We have no group and hide restrictions BUT NOT a joker search, we have to check ALL users based on
	            // names
	            timedSection = TimedSection.start(LOC, "countUsers(noHides,noGroups)",
	                "isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest);
	            count = UMEHelper.countUsersWithName(searchRequest.getSearchTerm(), MAX_SEARCH_RESULT_SIZE_USERS);
	        } else if (!searchRequest.getContainedInGroups().isEmpty()) {
	            // IF We have Group Search Request, we collect all members by groups
	            timedSection = TimedSection.start(LOC,
	                "countUsers(withGroups,hide=" + !searchRequest.getHide().isEmpty() + ")",
	                "isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest);
	            List<IApplicationUser> foundUsers = new ArrayList<>();
	            addUserMembersFromGivenGroups(searchRequest, foundUsers);
	            count = foundUsers.size();
	        } else {
	            timedSection = TimedSection.start(LOC, "countUsers(noOptimization)",
	                "isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest);
	            count = getUsers(searchRequest).length;
	        }
	    } catch (UMException e) {
	        LOC.traceThrowableT(Severity.ERROR,
	            "Error in countUsers() isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest, e);
	    } finally {
	        if (timedSection != null) {
	            timedSection.finish("count=" + count);
	        }
	    }
	    return count;
	}
	
	@Override
	public IApplicationUser[] getUsers(IPrincipalSearchRequest searchRequest) {
	    boolean isJokerSearch = UMEHelper.hasNoSearchTermOrOnlyJoker(searchRequest.getSearchTerm());
	    TimedSection timedSection = null;
	    List<IApplicationUser> foundUsers = new ArrayList<>();
	    try {
	        IUserFactory userFactory = UMFactory.getUserFactory();
	        if (searchRequest.getHide().isEmpty() && searchRequest.getContainedInGroups().isEmpty() && isJokerSearch) {
	            // IF We have no group and hide restrictions AND a joker search, we have to return the all
	            // users (restricted to maximum requested)
	            timedSection = TimedSection.start(LOC, "getUsers(all with joker)",
	                "isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest);
	            addUsersIfMatchesSearch(userFactory.getUniqueIDs(), searchRequest, foundUsers);
	        } else if (searchRequest.getHide().isEmpty() && searchRequest.getContainedInGroups().isEmpty()
	            && !isJokerSearch) {
	            // IF We have no group and hide restrictions BUT NOT a joker search, we have to check ALL users based on
	            // names
	            timedSection = TimedSection.start(LOC, "getUsers(noHides,noGroups)",
	                "isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest);
	            addUsersIfMatchesSearch(
	                UMEHelper.searchUsersWithName(searchRequest.getSearchTerm(), MAX_SEARCH_RESULT_SIZE_USERS),
	                searchRequest, foundUsers);
	        } else if (!searchRequest.getContainedInGroups().isEmpty()) {
	            // IF We have Group Search Request, we collect all members by groups
	            timedSection = TimedSection.start(LOC,
	                "getUsers(withGroups,hide=" + !searchRequest.getHide().isEmpty() + ")",
	                "isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest);
	            addUserMembersFromGivenGroups(searchRequest, foundUsers);
	        } else {
	            timedSection = TimedSection.start(LOC, "getUsers(noOptimization)",
	                "isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest);
	            addUsersIfMatchesSearch(
	                UMEHelper.searchUsersWithName(searchRequest.getSearchTerm(), MAX_SEARCH_RESULT_SIZE_USERS),
	                searchRequest, foundUsers);
	        }
	    } catch (Exception e) {
	        LOC.traceThrowableT(Severity.ERROR,
	            "Error in getUsers() isJokerSearch=" + isJokerSearch + " searchRequest=" + searchRequest, e);
	    } finally {
	        if (timedSection != null) {
	            timedSection.finish("foundUsers.size=" + foundUsers.size());
	        }
	    }
	    return foundUsers.toArray(new IApplicationUser[] {});
	}
	
	@Override
	public IApplicationUser getLoggedInUser(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
	    IUser user = UMFactory.getAuthenticator().getLoggedInUser(servletRequest, servletResponse);
	    IApplicationUser applicationUser = null;
	    if (user != null) {
	        applicationUser = new UMEUser(user);
	    }
	    return applicationUser;
	}
	
	@Override
	public IApplicationUser getLoggedInUser() {
	    IUser user = UMFactory.getAuthenticator().getLoggedInUser();
	    IApplicationUser applicationUser = null;
	    if (user != null) {
	        applicationUser = new UMEUser(user);
	    }
	    return applicationUser;
	}
	
	@Override
	public IApplicationUser getUserByUniqueName(String userUniqueName) {
	    try {
	        // @TODO make error tolerant, think about NotFoundException
	        IUserFactory userFactory = UMFactory.getUserFactory();
	        return new UMEUser(userFactory.getUserByUniqueName(userUniqueName));
	    } catch (Exception e) {
	        LOC.traceThrowableT(Severity.ERROR, "Error in search of user [" + userUniqueName + "]", e);
	    }
	    return null;
	}
	
  @Override
  public void updateUser(IApplicationUserChangeBuilder changeBuilder) throws Exception {
    // Retrieve the modifiable user object.
    IUserMaint userMaint = (IUserMaint) changeBuilder.getChangeData();
    // Write the changes to the user store.
    userMaint.save();
    userMaint.commit();
  }
  
	private void addUserMembersFromGivenGroups(IPrincipalSearchRequest searchRequest, List<IApplicationUser> foundUsers)
	    throws UMException {
	    Set<String> containedInGroups = searchRequest.getContainedInGroups();
	    IGroupFactory groupFactory = UMFactory.getGroupFactory();
	    for (String groupUniqueName : containedInGroups) {
	        try {
	            IGroup group = groupFactory.getGroupByUniqueName(groupUniqueName);
	            if (group != null) {
	                addUserMembersFromIGroup(group, searchRequest, foundUsers);
	            }
	        } catch (Exception e) {
	            LOC.traceThrowableT(Severity.ERROR,
	                "Error in addUserMembersFromGivenGroups() [groupUniqueName=" + groupUniqueName + "]", e);
	        }
	    }
	}
	
	private void addUserMembersFromIGroup(IGroup group, IPrincipalSearchRequest searchRequest,
	    List<IApplicationUser> foundUsers) throws UMException {
	    IGroupFactory groupFactory = UMFactory.getGroupFactory();
	    Iterator<String> userSearchResult = group.getUserMembers(false);
	    addUsersIfMatches(userSearchResult, searchRequest, foundUsers);
	    Iterator<String> subGroupSearchResult = group.getGroupMembers(false);
	    while (subGroupSearchResult != null && subGroupSearchResult.hasNext()
	        && foundUsers.size() < MAX_SEARCH_RESULT_SIZE_USERS) {
	        String subGroupId = subGroupSearchResult.next();
	        try {
	            IGroup subGroup = groupFactory.getGroup(subGroupId);
	            addUserMembersFromIGroup(subGroup, searchRequest, foundUsers);
	        } catch (Exception e) {
	            LOC.traceThrowableT(Severity.ERROR,
	                "Error in addUserMembersFromIGroup() [subGroupId=" + subGroupId + "]", e);
	        }
	    }
	}
	
	private void addUsersIfMatchesSearch(ISearchResult userSearchResult, IPrincipalSearchRequest searchRequest,
	    List<IApplicationUser> foundUsers) throws UMException {
	    IUserFactory userFactory = UMFactory.getUserFactory();
	
	    if (userSearchResult != null) {
	        int state = userSearchResult.getState();
	        if (state == ISearchResult.SEARCH_RESULT_OK || state == ISearchResult.SIZE_LIMIT_EXCEEDED) {
	            while (userSearchResult != null && userSearchResult.hasNext()
	                && foundUsers.size() < MAX_SEARCH_RESULT_SIZE_USERS) {
	                UMEUser appUser = new UMEUser(userFactory.getUser((String) userSearchResult.next()));
	                addUserIfMatches(appUser, searchRequest, foundUsers);
	            }
	        }
	        if (state != ISearchResult.SEARCH_RESULT_OK) {
	            String msg = "Error State " + searchResultStateAsTest(state) + " in search of user with searchRequest='"
	                + searchRequest + "'";
	            if (ISearchResult.SIZE_LIMIT_EXCEEDED == state) {
	                LOC.warningT(msg);
	            } else {
	                LOC.errorT(msg);
	            }
	        }
	    }
	}
	
	private void addUsersIfMatches(Iterator<String> userSearchResult, IPrincipalSearchRequest searchRequest,
	    List<IApplicationUser> foundUsers) throws UMException {
	    IUserFactory userFactory = UMFactory.getUserFactory();
	    while (userSearchResult != null && userSearchResult.hasNext()
	        && foundUsers.size() < MAX_SEARCH_RESULT_SIZE_USERS) {
	        UMEUser appUser = new UMEUser(userFactory.getUser(userSearchResult.next()));
	        addUserIfMatches(appUser, searchRequest, foundUsers);
	    }
	}
	
	/**
	 * Add User to foundUsers group, if he matched search Request
	 * 
	 * @param appUser
	 * @param searchRequest
	 * @param foundUsers
	 */
	private void addUserIfMatches(IApplicationUser appUser, IPrincipalSearchRequest searchRequest,
	    List<IApplicationUser> foundUsers) {
	    boolean addUser = false;
	    if (!searchRequest.getHide().contains(appUser.getUniqueName())
	        && (searchRequest.getContainedInGroups().isEmpty()
	            || CollectionUtils.containsAny(appUser.getGroupUniqueNames(), searchRequest.getContainedInGroups()))) {
	        addUser = UMEHelper.matchesUserSearchTerm(appUser, searchRequest.getSearchTerm());
	    }
	    //LOC.debugT((addUser ? "Adding" : "Skipping") + " appUser " + appUser.getUniqueName() + " for searchRequest=" + searchRequest);
	    if (addUser) {
	        foundUsers.add(appUser);
	    }
	}

}
