package {service.namespace}.jpa.helper;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EclipseLinkHelper {

    // Logger
    private static Logger LOG = LoggerFactory.getLogger(EclipseLinkHelper.class);

    /**
     * Get real SQL Query of JPA Query
     * 
     * @param em
     * @param query
     * @param params
     * @return
     */
    public static String getNativeSql(EntityManager em, Query query, Map<String, Object> params) {
        try {

            Session session = em.unwrap(JpaEntityManager.class).getActiveSession();
            DatabaseQuery databaseQuery = ((EJBQueryImpl) query).getDatabaseQuery();
            databaseQuery.prepareCall(session, new DatabaseRecord());
            DatabaseRecord recordWithValues = new DatabaseRecord();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                recordWithValues.add(new DatabaseField(param.getKey()), param.getValue());
            }
            String sqlStringWithArgs = databaseQuery.getTranslatedSQLString(session, recordWithValues);
            return sqlStringWithArgs;
        } catch (Throwable t) {
            String msg = "Could not getNativeSql due to " + t.getMessage() + " em=" + em + " query=" + query
                + " params=" + params;
            LOG.error(msg, t);
            return msg;
        }
    }
}
