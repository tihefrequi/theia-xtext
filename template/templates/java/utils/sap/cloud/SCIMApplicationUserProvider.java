package {service.namespace}.utils.sap.cloud;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.json.JsonObject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserAttribute;
import com.sap.security.um.user.UserProvider;
import {service.namespace}.auth.IApplicationUser;
import {service.namespace}.auth.IApplicationUserChangeBuilder;
import {service.namespace}.auth.IApplicationUserProvider;
import {service.namespace}.auth.IPrincipalSearchRequest;
import {service.namespace}.utils.sap.cloud.IASApplicationUserProvider;
import {service.namespace}.utils.JsonUtils;

public class SCIMApplicationUserProvider extends IASApplicationUserProvider implements IApplicationUserProvider {

	private DestinationConfiguration destConfiguration;
	private Client scimRestClient;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SCIMApplicationUserProvider.class);
	
	private static final String SCIM_DEST = "SCIM_REST_API";	
	private static final String SCIM_PATH_USERS = "Users";
	
	
	public SCIMApplicationUserProvider(){
		try {
			Context ctx = new InitialContext();
			ConnectivityConfiguration configuration = (ConnectivityConfiguration) ctx
					.lookup("java:comp/env/connectivityConfiguration");

			destConfiguration = configuration.getConfiguration(SCIM_DEST);

			if (destConfiguration == null) {
				LOGGER.error("Couldn't find the SCIM destination ["+SCIM_DEST+"]");
			} else {
				//create rest client for the SCIM REST API		
				scimRestClient = getSCIMClient(destConfiguration);
			}

			userProvider = (UserProvider) ctx.lookup("java:comp/env/user/Provider");
			
		} catch(NamingException e) {
			LOGGER.error("Couldn't find the user provider", e);
		}
		
	}

  @Override
  public void updateUser(IApplicationUserChangeBuilder changeBuilder) throws Exception {

    String destUrl = null;
    String userUrl = null;

    try {
      if (destConfiguration != null) {
        IApplicationUser user = changeBuilder.getUser();
        // ...construct the url
        destUrl = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_URL);
        userUrl = destUrl + "/" + SCIM_PATH_USERS + "/" + user.getUniqueName();

        WebTarget scimUserTarget = scimRestClient.target(userUrl);

        // ...post user data
        JsonObject changes =
            JsonUtils.putKeyValueInObject((JsonObject)changeBuilder.getChangeData(), SCIMConstants.ID, user.getUniqueName());
        Entity<JsonObject> entity = javax.ws.rs.client.Entity.json(changes);
        scimUserTarget.request().put(entity);

      } else {
        String text = "Can't update user data. destConfiguration = null";
        LOGGER.trace(text);
        throw new RuntimeException(text);
      }

    } catch (Exception e) {
      String text = "Can't update user data for [" + userUrl + "]";
      LOGGER.trace(text, e);
      throw new RuntimeException(text, e);
    }

  }
	
	private Client getSCIMClient(DestinationConfiguration destConfiguration) {
		
		Client client = ClientBuilder.newClient();
		
		String destAuthType = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_AUTHENTICATION_TYPE);
		
		//we support only basic authentication
		if (StringUtils.equals(destAuthType, "BasicAuthentication")) {
			final String user = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_USER);
			final String password = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_PASSWORD);

			client = client.register(new ClientRequestFilter() {

				@Override
				public void filter(ClientRequestContext clientRequestContext) throws IOException {
					MultivaluedMap<String, Object> headers = clientRequestContext.getHeaders();
			        final String basicAuthentication = getBasicAuthentication();
			        headers.add("Authorization", basicAuthentication);
				}
				
				private String getBasicAuthentication() {
			        String token = user + ":" + password;
			        try {
			            return "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
			        } catch (UnsupportedEncodingException ex) {
			            throw new IllegalStateException("Cannot encode with UTF-8", ex);
			        }
			    }
				
			});
		}
		return client;

		
	}

	@Override
	  public IApplicationUser[] getUsers(IPrincipalSearchRequest searchRequest) {
	 
		String destUrl = null;
		String searchUrl = null;
	 
		List<IApplicationUser> applicationUsers = new ArrayList<IApplicationUser>();
		String searchTerm = searchRequest.getSearchTerm();
		if (destConfiguration != null) {
		  if (searchTerm != null && !"".equals(searchTerm)) {
	 
			// ...construct the url
			destUrl = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_URL);
			// SAP horribly only allows to eq/and on the attributes id,emails,userName,name.familyName,addresses.country,groups
			// see Home/SAP Cloud Platform Identity Authentication Service/Developer Guide/API Documentation/SCIM REST API/Manage Users SCIM REST API/
			// https://help.sap.com/viewer/6d6d63354d1242d185ab4830fc04feb1/Cloud/en-US/3af7dfae0aad4cf79ab8d822f8322e76.html
			searchUrl = destUrl + "/" + SCIM_PATH_USERS + "?filter=name.familyName%20eq%20'"
				+ StringUtils.replace(searchTerm, "'", "\'") + "'";
	 
			try {
			  LOGGER.trace("Starting usersearch for ["+searchTerm+"] with url ["+searchUrl+"]");
			  WebTarget scimUserTarget = scimRestClient.target(searchUrl);
	 
			  JsonObject scimUserObjects =
				  scimUserTarget.request("application/scim+json").get(JsonObject.class);
	 
			  for (JsonObject user : scimUserObjects.getJsonArray("Resources")
				  .getValuesAs(JsonObject.class)) {
				applicationUsers.add(new SCIMUser(user));
			  }
	 
			} catch (Exception e) {
			  LOGGER.error("Can't find a user with name [" + searchTerm + "] searchUrl=["+searchUrl+"] due to "+e.getMessage() + "/"+e.getClass().getName(), e);
			}
	 
		  } else {
			LOGGER.error("Retrieving of users without search term is not supported! searchTerm="
				+ searchTerm);
		  }
		} else {
		  String text = "Can't search for user data. destConfiguration = null";
		  LOGGER.trace(text);
		  throw new RuntimeException(text);
		}
	 
		return applicationUsers.toArray(new IApplicationUser[] {});
	  }

	protected IApplicationUser toApplicationUser(User user) throws UnsupportedUserAttributeException, IOException {
	
		String destUrl = null;
		String userUrl = null;
		
		try {
			if (destConfiguration != null) {
				//...construct the url
				destUrl = destConfiguration.getProperty(DestinationConfiguration.DESTINATION_URL);
				userUrl = destUrl + "/" + SCIM_PATH_USERS + "/" + user.getName();
		
				WebTarget scimUserTarget = scimRestClient.target(userUrl);
		
				//...get user
				JsonObject scimUserObject = scimUserTarget.request().get(JsonObject.class);
				SCIMUser applicationUser = new SCIMUser(user, scimUserObject);
		
				return toApplicationUserCustom(scimUserObject, applicationUser);
			} else {
				LOGGER.warn("destConfiguration is null");
				return super.toApplicationUser(user);
			}

		} catch (Exception e) {
			// If we can't retrieve the user data from SCIM (see log for more details) 
			// we work with IASUser instead
			LOGGER.trace("Can't retrieve user data from [" + userUrl + "]", e);
			return super.toApplicationUser(user);
		}
		
	}
	

	private IApplicationUser toApplicationUserCustom(JsonObject user, IApplicationUser applicationUser) {
		//extension Point to override the application user
		return applicationUser;
	}
	
}