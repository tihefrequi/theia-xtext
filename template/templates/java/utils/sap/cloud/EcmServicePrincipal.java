package {service.namespace}.utils.sap.cloud;

import {service.namespace}.utils.sap.cloud.EcmServiceConstants.PrincipalType;

/**
 * Represents the principal on the ECM service side
 * 
 */
public class EcmServicePrincipal {
	
	private String principalId;
	private PrincipalType principalType;
	
	public EcmServicePrincipal(String principalId, PrincipalType principalType) {
		this.principalId = principalId;
		this.principalType = principalType;
	}

	/**
	 * @return the principalId
	 */
	public String getPrincipalId() {
		return principalId;
	}

	/**
	 * @return the principalType
	 */
	public PrincipalType getPrincipalType() {
		return principalType;
	}
	
	public String getEcmServiceId() {
		return principalType.getEcmServicePrefix()+principalId;
	}
		
}
