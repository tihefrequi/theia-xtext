package {service.namespace}.utils.sap.netweaver;

import org.apache.commons.lang3.StringUtils;

import {service.namespace}.auth.IApplicationGroup;
import {service.namespace}.auth.IApplicationUser;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.IGroupSearchFilter;
import com.sap.security.api.IPrincipalSearchFilter;
import com.sap.security.api.ISearchAttribute;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.IUserSearchFilter;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;


public class UMEHelper {
    private static Location LOC = Location.getLocation(UMEHelper.class);
    public static final String JOKER = "*";

    /**
     * Search Users with name in any name like field
     * (uniqueName,LastName,Firstname,DisplayName)
     * 
     * @param searchTerm
     * @param maxSearchResultSize
     * 
     * @return
     * @throws UMException
     */
    public static ISearchResult searchUsersWithName(String searchTerm, int maxSearchResultSize) throws UMException {
        String searchQuery = getEndJokeredSearchTerm(searchTerm);
        IUserFactory userFactory = UMFactory.getUserFactory();
        IUserSearchFilter searchFilter = userFactory.getUserSearchFilter();
        TimedSection timedSection = TimedSection.start(LOC, "getUsersWithName()",
            "searchTerm=" + searchTerm + " searchQuery=" + searchQuery + " maxSearchResultSize=" + maxSearchResultSize);
        // userSearchFilter.setDisplayName(searchQuery, ISearchAttribute.LIKE_OPERATOR, false);
        // If search attribute displayname is used, no other attribute can be used!
        searchFilter.setUniqueName(searchQuery, ISearchAttribute.LIKE_OPERATOR, false);
        searchFilter.setLastName(searchQuery, ISearchAttribute.LIKE_OPERATOR, false);
        searchFilter.setFirstName(searchQuery, ISearchAttribute.LIKE_OPERATOR, false);
        searchFilter.setSearchMethod(IPrincipalSearchFilter.SEARCHMETHOD_OR);
        searchFilter.setMaxSearchResultSize(maxSearchResultSize);
        ISearchResult searchResult = userFactory.searchUsers(searchFilter);
        int count = searchResult.size();
        timedSection.finish("count=" + count + " state=" + searchResult.getState());
        return searchResult;
    }

    /**
     * Count Users with name in any name like field
     * (uniqueName,LastName,Firstname,DisplayName)
     * 
     * @param searchTerm
     * @param maxSearchResultSize
     * @return
     * @throws UMException
     */
    public static int countUsersWithName(String searchTerm, int maxSearchResultSize) throws UMException {
        TimedSection timedSection = TimedSection.start(LOC, "countUsersWithName()",
            "searchTerm=" + searchTerm + " maxSearchResultSize=" + maxSearchResultSize);
        ISearchResult searchResult = searchUsersWithName(searchTerm, maxSearchResultSize);
        int count = searchResult.size();
        timedSection.finish("count=" + count + " state=" + searchResult.getState());
        return count;
    }

    /**
     * Search Groups with name in any name like field (uniqueName,DisplayName)
     * 
     * @param searchTerm
     * @param maxSearchResultSize
     * @return
     * @throws UMException
     */
    public static ISearchResult getGroupsWithName(String searchTerm, int maxSearchResultSize) throws UMException {
        String searchQuery = getEndJokeredSearchTerm(searchTerm);
        TimedSection timedSection = TimedSection.start(LOC, "getGroupsWithName()",
            "searchTerm=" + searchTerm + " searchQuery=" + searchQuery + " maxSearchResultSize=" + maxSearchResultSize);
        IGroupFactory userFactory = UMFactory.getGroupFactory();
        IGroupSearchFilter searchFilter = userFactory.getGroupSearchFilter();
        searchFilter.setSearchMethod(IPrincipalSearchFilter.SEARCHMETHOD_OR);
        // searchFilter.setDisplayName(searchQuery,
        // ISearchAttribute.LIKE_OPERATOR, false);
        // If search attribute displayname is used, no other attribute can be
        // used!
        searchFilter.setUniqueName(searchQuery, ISearchAttribute.LIKE_OPERATOR, false);
        searchFilter.setDescription(searchQuery, ISearchAttribute.LIKE_OPERATOR, false);
        searchFilter.setMaxSearchResultSize(maxSearchResultSize);
        ISearchResult searchResult = userFactory.searchGroups(searchFilter);
        int count = searchResult.size();
        timedSection.finish("count=" + count + " state=" + searchResult.getState());
        return searchResult;
    }

    /**
     * Count Groups with name in any name like field (uniqueName,displayName)
     * 
     * @param searchTerm
     * @param maxSearchResultSize
     * @return
     * @throws UMException
     */
    public static int countGroupsWithName(String searchTerm, int maxSearchResultSize) throws UMException {
        TimedSection timedSection = TimedSection.start(LOC, "countGroupsWithName()",
            "searchTerm=" + searchTerm + " maxSearchResultSize=" + maxSearchResultSize);
        ISearchResult searchResult = getGroupsWithName(searchTerm, maxSearchResultSize);
        int count = searchResult.size();
        timedSection.finish("count=" + count + " state=" + searchResult.getState());
        return count;
    }

    /**
     * 
     * @param searchTerm
     * @return true if searchTerm null or "" or JOKER '*'
     */
    public static boolean hasNoSearchTermOrOnlyJoker(String searchTerm) {
        return searchTerm == null || searchTerm.length() == 0 || searchTerm.equals(JOKER);
    }

    /**
     * 
     * @param searchTerm
     * @return searchTerm if already contains joker or is
     */
    public static String getEndJokeredSearchTerm(String searchTerm) {
        if (hasNoSearchTermOrOnlyJoker(searchTerm)) {
            return JOKER;
        } else {
            return searchTerm + JOKER;
        }
    }

    /**
     * 
     * @param user
     * @param searchTerm
     * @return
     */
    public static boolean matchesUserSearchTerm(IApplicationUser user, String searchTerm) {
        boolean isJokerSearch = UMEHelper.hasNoSearchTermOrOnlyJoker(searchTerm);
        if (isJokerSearch) {
            return true;
        } else {
            String searchTermWithoutJokers = searchTerm.replace(UMEHelper.JOKER, "");
            return (StringUtils.containsIgnoreCase(user.getFirstName(), searchTermWithoutJokers)
                || StringUtils.containsIgnoreCase(user.getLastName(), searchTermWithoutJokers)
                || StringUtils.containsIgnoreCase(user.getDisplayName(), searchTermWithoutJokers)
                || StringUtils.containsIgnoreCase(user.getUniqueName(), searchTermWithoutJokers));
        }
    }

    /**
     * 
     * @param user
     * @param searchTerm
     * @return
     */
    public static boolean matchesGroupSearchTerm(IApplicationGroup group, String searchTerm) {
        boolean isJokerSearch = UMEHelper.hasNoSearchTermOrOnlyJoker(searchTerm);
        if (isJokerSearch) {
            return true;
        } else {
            String searchTermWithoutJokers = searchTerm.replace(UMEHelper.JOKER, "");
            return (StringUtils.containsIgnoreCase(group.getUniqueName(), searchTermWithoutJokers)
                || StringUtils.containsIgnoreCase(group.getDisplayName(), searchTermWithoutJokers));
        }
    }
}

