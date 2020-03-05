package {service.namespace}.utils.sap.netweaver;

import java.util.ArrayList;
import java.util.List;

import {service.namespace}.auth.IApplicationGroup;
import {service.namespace}.auth.IApplicationGroupProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class UMEApplicationGroupProvider extends AbstractUMEApplicationPrincipalProvider implements IApplicationGroupProvider {
	private static Location LOC = Location.getLocation(UMEApplicationGroupProvider.class);
	private static final int MAX_SEARCH_RESULT_SIZE_GROUPS = 20;
	
	@Override
	public int countGroups(IPrincipalSearchRequest searchRequest) {
	    TimedSection timedSection = TimedSection.start(LOC, "countGroups()", "searchRequest=" + searchRequest);
	    int count = 0;
	    try {
	        count = UMEHelper.countGroupsWithName(searchRequest.getSearchTerm(), MAX_SEARCH_RESULT_SIZE_GROUPS);
	    } catch (UMException e) {
	        LOC.traceThrowableT(Severity.ERROR, "Error in countGroups() with searchRequest=" + searchRequest, e);
	    } finally {
	        if (timedSection != null) {
	            timedSection.finish("count=" + count);
	        }
	    }
	    return count;
	}
	
	@Override
	public IApplicationGroup[] getGroups(IPrincipalSearchRequest searchRequest) {
	    TimedSection timedSection = TimedSection.start(LOC, "countGroups()", "searchRequest=" + searchRequest);
	    List<IApplicationGroup> foundGroups = new ArrayList<>();
	    ISearchResult groupSearchResult;
	    try {
	        IGroupFactory groupFactory = UMFactory.getGroupFactory();
	        groupSearchResult = UMEHelper.getGroupsWithName(searchRequest.getSearchTerm(),
	            MAX_SEARCH_RESULT_SIZE_GROUPS);
	        addGroupsIfMatches(groupSearchResult, searchRequest, foundGroups);
	    } catch (Exception e) {
	        LOC.traceThrowableT(Severity.ERROR, "Error in search of groups with searchRequest=" + searchRequest, e);
	    } finally {
	        if (timedSection != null) {
	            timedSection.finish("foundGroups.size=" + foundGroups.size());
	        }
	    }
	    return foundGroups.toArray(new IApplicationGroup[] {});
	}
	
	@Override
	public IApplicationGroup getGroupByUniqueName(String groupUniqueName) {
	    try {
	        // @TODO make error tolerant, think about NotFoundException
	        IGroupFactory groupFactory = UMFactory.getGroupFactory();
	        return new UMEGroup(groupFactory.getGroupByUniqueName(groupUniqueName));
	    } catch (Exception e) {
	        LOC.traceThrowableT(Severity.ERROR, "Error in search of groups [" + groupUniqueName + "]", e);
	    }
	    return null;
	}
	
	private void addGroupsIfMatches(ISearchResult searchResult, IPrincipalSearchRequest searchRequest,
	    List<IApplicationGroup> foundGroups) throws UMException {
	    IGroupFactory groupFactory = UMFactory.getGroupFactory();
	
	    if (searchResult != null) {
	        int state = searchResult.getState();
	        if (state == ISearchResult.SEARCH_RESULT_OK || state == ISearchResult.SIZE_LIMIT_EXCEEDED) {
	            while (searchResult != null && searchResult.hasNext()
	                && foundGroups.size() < MAX_SEARCH_RESULT_SIZE_GROUPS) {
	                UMEGroup appGroup = new UMEGroup(groupFactory.getGroup((String) searchResult.next()));
	                addGroupIfMatches(appGroup, searchRequest, foundGroups);
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
	
	/**
	 * Add User to foundUsers group, if he matched search Request
	 * 
	 * @param appUser
	 * @param searchRequest
	 * @param foundUsers
	 */
	private void addGroupIfMatches(IApplicationGroup appGroup, IPrincipalSearchRequest searchRequest,
	    List<IApplicationGroup> foundGroups) {
	    if (!searchRequest.getHide().contains(appGroup.getUniqueName())) {
	        if (UMEHelper.matchesGroupSearchTerm(appGroup, searchRequest.getSearchTerm())) {
	            foundGroups.add(appGroup);
	        }
	    }
	}

}