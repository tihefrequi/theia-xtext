package {service.namespace}.odata.processor;

import org.apache.olingo.odata2.api.ODataDebugCallback;

/**
 * Implements the debugging callback for the OData v2 service for {service.name}.
 * Defines whether the debugging via ?odata-debug=json should be
 * enabled.
 * 
 * @author storm
 *
 */
public class {Service.name}ServiceDebugCallback implements ODataDebugCallback {

	/**
	 * @see ODataDebugCallback#isDebugEnabled()
	 */
	@Override
	public boolean isDebugEnabled() {
		return true;
	}

}
