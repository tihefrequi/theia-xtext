package {service.namespace}.ui.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserProvider;
import {userprovider.fullpackageandclassname};

public class VariablesFactory {

	/**
	 * Determine current User Variables
	 * 
	 * @param servletRequest
	 * @param servletResponse
	 * @return
	 */
	public static Variables getVariables(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

		// User Provider
		IApplicationUserProvider userProvider = new {userprovider.classname}();

		// Variables Container
		Variables variables = new Variables();

		// Current User
		UserInfos userInfos = new UserInfos();

		// General Infos
		IApplicationUser user = userProvider.getLoggedInUser(servletRequest, servletResponse);
		userInfos.setUniqueName(user.getUniqueName());

		userInfos.setAttribute("uniqueName",user.getUniqueName());
		userInfos.setAttribute("firstName",user.getFirstName());
		userInfos.setAttribute("lastName",user.getLastName());
		userInfos.setAttribute("displayName",user.getDisplayName());

		userInfos.setAttribute("middleName", user.getMiddleName());
		userInfos.setAttribute("honoricPrefix", user.getHonorificPrefix());
		userInfos.setAttribute("honoricSuffix", user.getHonorificSuffix());
		userInfos.setAttribute("profileUrl", user.getProfileUrl());
		userInfos.setAttribute("photoUrl", user.getPhotoUrl());
		userInfos.setAttribute("userType", user.getUserType());
		userInfos.setAttribute("prefLang", user.getPreferredLanguage());
		userInfos.setAttribute("locale", user.getLocale());
		userInfos.setAttribute("timeZone", user.getTimeZone());
		userInfos.setAttribute("active", String.valueOf(user.getActive()));
		userInfos.setAttribute("company", user.getCompany());
		userInfos.setAttribute("companyRel", user.getCompanyRelationship());
		userInfos.setAttribute("department", user.getDepartment());
		userInfos.setAttribute("division", user.getDivision());
		userInfos.setAttribute("costCenter", user.getCostCenter());
		userInfos.setAttribute("manager", user.getManager());
		userInfos.setAttribute("email", user.getEmail());
		userInfos.setAttribute("phone", user.getPhone());
		userInfos.setAttribute("iamAddress", user.getInstantMessageAddress());
		userInfos.setAttribute("country", user.getCountry());
		userInfos.setAttribute("postalCode", user.getPostalCode());
		userInfos.setAttribute("region", user.getRegion());
		userInfos.setAttribute("locality", user.getLocality());
		userInfos.setAttribute("streetAddress", user.getStreetAddress());
		userInfos.setAttribute("customAttribute1", user.getCustomAttribute1());
		userInfos.setAttribute("customAttribute2", user.getCustomAttribute2());
		userInfos.setAttribute("customAttribute3", user.getCustomAttribute3());
		userInfos.setAttribute("customAttribute4", user.getCustomAttribute4());
		userInfos.setAttribute("customAttribute5", user.getCustomAttribute5());
		userInfos.setAttribute("customAttribute6", user.getCustomAttribute6());
		userInfos.setAttribute("customAttribute7", user.getCustomAttribute7());
		userInfos.setAttribute("customAttribute8", user.getCustomAttribute8());
		userInfos.setAttribute("customAttribute9", user.getCustomAttribute9());
		userInfos.setAttribute("customAttribute10", user.getCustomAttribute10());
		//userInfos.setAttribute("groups", StringUtils.join(user.getGroupUniqueNames(),","));
		//userInfos.setAttribute("roles", StringUtils.join(user.getRoleUniqueNames(),","));

		// Authorizations
		userInfos.setAuthorization(AuthorizationFactory.getAuthorization(user, servletRequest, servletResponse));

		variables.setCurrentUser(userInfos);

		return variables;
	}
}
