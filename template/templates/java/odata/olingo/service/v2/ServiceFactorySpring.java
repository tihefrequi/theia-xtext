package {service.namespace}.odata;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.ODataDebugCallback;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;

import {service.namespace}.odata.edm.{Service.name}ServiceEdmProvider;
import {service.namespace}.odata.processor.{Service.name}ServiceDebugCallback;
import {service.namespace}.odata.processor.{Service.name}ServiceErrorCallback;
import {service.namespace}.odata.processor.{Service.name}ServiceSingleProcessor;

import {service.namespace}.utils.spring.SpringContextsUtil;

/**
 * Provides the OData v2 service for {service.name}
 *
 */
public class {Service.name}ServiceFactory extends ODataServiceFactory {

  @Override
  public ODataService createService(ODataContext ctx) throws ODataException {

		// ...init EDM provider
		EdmProvider edmProvider = new {Service.name}ServiceEdmProvider();
		
		// ...init single processor
		ODataSingleProcessor singleProcessor = (ODataSingleProcessor)SpringContextsUtil.getBean("odataServiceSingleProcessor");

		// ...create service
		return createODataSingleProcessorService(edmProvider, singleProcessor);
  }
  
	/**
	 * @see ODataServiceFactory#getCallback(Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ODataCallback> T getCallback(final Class<T> callbackInterface) {
		return (T) (callbackInterface.isAssignableFrom(ODataErrorCallback.class) ? new {Service.name}ServiceErrorCallback()
				: callbackInterface.isAssignableFrom(ODataDebugCallback.class) ? new {Service.name}ServiceDebugCallback()
						: super.getCallback(callbackInterface));
	}
}
