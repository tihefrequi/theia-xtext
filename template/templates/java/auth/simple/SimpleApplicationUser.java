package {service.namespace}.auth.simple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import {service.namespace}.auth.IApplicationUser;

public class SimpleApplicationUser implements IApplicationUser {

	private String uniqueName;
	private String displayName;
	private String firstName;
	private String lastName;
	private String middleName;

	private String honorificPrefix;
	private String honorificSuffix;
	private String profileUrl;
	private String photoUrl;
	private String userType;
	private String preferredLanguage;
	private String locale;
	private String timeZone;
	private boolean active;
	private String company;
	private String companyRelationship;
	private String department;
	private String division;
	private String costCenter;
	private String manager;
	private String email;
	private String phone;
	private String instantMessageAddress;
	private String country;
	private String postalCode;
	private String region;
	private String locality;
	private String streetAddress;
	private String customAttribute1;
	private String customAttribute2;
	private String customAttribute3;
	private String customAttribute4;
	private String customAttribute5;
	private String customAttribute6;
	private String customAttribute7;
	private String customAttribute8;
	private String customAttribute9;
	private String customAttribute10;

	private Set<String> groupUniqueNames = new HashSet<>();
	private Set<String> roleUniqueNames = new HashSet<>();

	public SimpleApplicationUser() {
	}

	@Override
	public String getUniqueName() {
		return uniqueName;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public String getMiddleName() {
		return middleName;
	}

	@Override
	public String getHonorificPrefix() {
		return honorificPrefix;
	}

	@Override
	public String getHonorificSuffix() {
		return honorificSuffix;
	}

	@Override
	public String getProfileUrl() {
		return profileUrl;
	}

	@Override
	public String getPhotoUrl() {
		return photoUrl;
	}

	@Override
	public String getUserType() {
		return userType;
	}

	@Override
	public String getPreferredLanguage() {
		return preferredLanguage;
	}

	@Override
	public String getLocale() {
		return locale;
	}

	@Override
	public String getTimeZone() {
		return timeZone;
	}

	@Override
	public boolean getActive() {
		return active;
	}

	@Override
	public String getCompany() {
		return company;
	}

	@Override
	public String getCompanyRelationship() {
		return companyRelationship;
	}

	@Override
	public String getDepartment() {
		return department;
	}

	@Override
	public String getDivision() {
		return division;
	}

	@Override
	public String getCostCenter() {
		return costCenter;
	}

	@Override
	public String getManager() {
		return manager;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	@Override
	public String getInstantMessageAddress() {
		return instantMessageAddress;
	}

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public String getPostalCode() {
		return postalCode;
	}

	@Override
	public String getRegion() {
		return region;
	}

	@Override
	public String getLocality() {
		return locality;
	}

	@Override
	public String getStreetAddress() {
		return streetAddress;
	}

	@Override
	public String getCustomAttribute1() {
		return customAttribute1;
	}

	@Override
	public String getCustomAttribute2() {
		return customAttribute2;
	}

	@Override
	public String getCustomAttribute3() {
		return customAttribute3;
	}

	@Override
	public String getCustomAttribute4() {
		return customAttribute4;
	}

	@Override
	public String getCustomAttribute5() {
		return customAttribute5;
	}

	@Override
	public String getCustomAttribute6() {
		return customAttribute6;
	}

	@Override
	public String getCustomAttribute7() {
		return customAttribute7;
	}

	@Override
	public String getCustomAttribute8() {
		return customAttribute8;
	}

	@Override
	public String getCustomAttribute9() {
		return customAttribute9;
	}

	@Override
	public String getCustomAttribute10() {
		return customAttribute10;
	}

	@Override
	public Set<String> getGroupUniqueNames() {
		return groupUniqueNames;
	}

	@Override
	public Set<String> getRoleUniqueNames() {
		return roleUniqueNames;
	}

	//SETTERS

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;
	}
	public void setHonorificSuffix(String honorificSuffix) {
		this.honorificSuffix = honorificSuffix;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public void setPreferredLanguage(String preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public void setCompanyRelationship(String companyRelationship) {
		this.companyRelationship = companyRelationship;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setInstantMessageAddress(String instantMessageAddress) {
		this.instantMessageAddress = instantMessageAddress;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public void setCustomAttribute1(String customAttribute1) {
		this.customAttribute1 = customAttribute1;
	}
	public void setCustomAttribute2(String customAttribute2) {
		this.customAttribute2 = customAttribute2;
	}
	public void setCustomAttribute3(String customAttribute3) {
		this.customAttribute3 = customAttribute3;
	}
	public void setCustomAttribute4(String customAttribute4) {
		this.customAttribute4 = customAttribute4;
	}
	public void setCustomAttribute5(String customAttribute5) {
		this.customAttribute5 = customAttribute5;
	}
	public void setCustomAttribute6(String customAttribute6) {
		this.customAttribute6 = customAttribute6;
	}
	public void setCustomAttribute7(String customAttribute7) {
		this.customAttribute7 = customAttribute7;
	}
	public void setCustomAttribute8(String customAttribute8) {
		this.customAttribute8 = customAttribute8;
	}
	public void setCustomAttribute9(String customAttribute9) {
		this.customAttribute9 = customAttribute9;
	}
	public void setCustomAttribute10(String customAttribute10) {
		this.customAttribute10 = customAttribute10;
	}
	public void setGroupUniqueNames(Set<String> groupUniqueNames) {
		this.groupUniqueNames = groupUniqueNames;
	}
	public void setRoleUniqueNames(Set<String> roleUniqueNames) {
		this.roleUniqueNames = roleUniqueNames;
	}
}
