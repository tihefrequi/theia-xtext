/**
 * 
 */
package {service.namespace}.jpa.entitymanager;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author storm
 *
 */
public class AbstractEntityManager {

    private static final String PERSISTENCE_UNIT_NAME = "{service.namespace}.persistenceUnit";

    // Logger
    protected static Logger LOG = LoggerFactory
        .getLogger("{service.namespace}.jpa.EntityManager");

	/**
	 * Get current UTC DateTime
	 *
	 * @return
	 */
	protected static Calendar currentUTCDateTime() {
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}

    /**
     * Wrap and Throw Exception
     * 
     * @param action
     * @param context
     * @param data
     * @param t
     */
    protected static void fail(String action, String context, Object data, Throwable t) {
        fail("Failed " + action + " during " + context + " with Exception " + t.getMessage() + ". data=" + data, t);
    }
    
	/**
	 * Wrap and Throw Exception
	 * 
	 * @param action
	 * @param context
	 * @param t
	 */
	protected static void fail(String action, String context, Throwable t) {
		fail("Failed " + action + " during " + context + " with Exception " + t.getMessage(), t);
	}

    /**
     * Throw Exception
     * 
     * @param action
     * @param context
     * @param data
     */
    protected static void fail(String action, String context, Object data) {
        fail("Failed " + action + " during " + context + ". data=" + data);
    }

    /**
     * Wrap and Throw Exception
     * 
     * @param message 
     * @param t
     */
    protected static void fail(String message, Throwable t) {
        LOG.error(message, t);
        throw new RuntimeException(message, t);
    }

    /**
     * Throw Exception
     * 
     * @param message 
     */
    protected static void fail(String message) {
        LOG.error(message);
        throw new RuntimeException(message);
    }

    /**
     * 
     * @param context
     * @return
     */
    protected static Long traceStart(String context) {
        if (LOG.isTraceEnabled()) {
            return System.currentTimeMillis();
        } else {
            return null;
        }
    }

    /**
     * 
     * @param context
     * @param executionStart
     */
    protected static void traceEnd(String context, Long executionStart) {
        if (LOG.isTraceEnabled()) {
            if (executionStart == null) {
                LOG.trace("Wrong tracestart/end combination. Got null executionstart.");
            } else {
                LOG.trace("Execution Runtime of " + context + " took "
                    + (Math.max(0, System.currentTimeMillis() - executionStart)+"ms"));
            }
        }
    }

    /**
     * Get Entity Manager from Persistence Unit
     * 
     * @return entity Manager
     */
    public static EntityManager getEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = emf.createEntityManager();
        return em;
    }
	
	public static class QueryBuilder {
			
		Query query = null;

		public QueryBuilder(String query) {
			EntityManager em = getEntityManager();
			this.query = em.createQuery(query);
		}

		public QueryBuilder firstResult(int startPosition) {
			this.query.setFirstResult(startPosition);
			return this;
		}

		public QueryBuilder flushMode(FlushModeType flushMode) {
			this.query.setFlushMode(flushMode);
			return this;
		}

		public QueryBuilder hint(String hintName, Object value) {
			this.query.setHint(hintName, value);
			return this;
		}

		public QueryBuilder lockMode(LockModeType lockMode) {
			this.query.setLockMode(lockMode);
			return this;
		}

		public QueryBuilder maxResults(int maxResult) {
			this.query.setMaxResults(maxResult);
			return this;
		}

		public QueryBuilder param(int position, Object value) {
			this.query.setParameter(position, value);
			return this;
		}

		public <T> QueryBuilder param(Parameter<T> param, T value) {
			this.query.setParameter(param, value);
			return this;
		}

		public QueryBuilder param(String name, Object value) {
			this.query.setParameter(name, value);
			return this;
		}

		public QueryBuilder param(int position, Calendar value, TemporalType temporalType) {
			this.query.setParameter(position, value, temporalType);
			return this;
		}

		public QueryBuilder param(int position, Date value, TemporalType temporalType) {
			this.query.setParameter(position, value, temporalType);
			return this;
		}

		public QueryBuilder param(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
			this.query.setParameter(param, value, temporalType);
			return this;
		}

		public QueryBuilder param(Parameter<Date> param, Date value, TemporalType temporalType) {
			this.query.setParameter(param, value, temporalType);
			return this;
		}

		public QueryBuilder param(String name, Calendar value, TemporalType temporalType) {
			this.query.setParameter(name, value, temporalType);
			return this;
		}

		public QueryBuilder param(String name, Date value, TemporalType temporalType) {
			this.query.setParameter(name, value, temporalType);
			return this;
		}

		public Query build() {
			return query;
		}
	}

}
