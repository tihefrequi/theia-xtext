package {batch.namespace}.utils;

import java.util.logging.Logger;

import {batch.namespace}.utils.interfaces.IJobContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class SapNetweaverJobContext implements IJobContext {
    private final Logger logger;
    private final Location LOC;
    private final com.sap.scheduler.runtime.JobContext jobContext;
    private ReturnCode returnCode = ReturnCode.INITIAL;

    public SapNetweaverJobContext(com.sap.scheduler.runtime.JobContext jobContext, Class locationClass) {
        this.logger = jobContext.getLogger();
        this.LOC = Location.getLocation(locationClass);
        this.jobContext = jobContext;
    }

    /* (non-Javadoc)
     * @see {batch.namespace}.utils.IJobLogger#entering(java.lang.String, java.lang.Object)
     */
    @Override
    public void logStart(String msg, Object... objects) {
        logger.info("Entering " + msg);
        LOC.entering(msg, objects);
    }

    /* (non-Javadoc)
     * @see {batch.namespace}.utils.IJobLogger#exiting(java.lang.String, java.lang.Object)
     */
    @Override
    public void logEnd(String msg, Object... objects) {
        logger.info("Exiting " + msg);
        LOC.exiting(msg, objects);
    }

    /* (non-Javadoc)
     * @see {batch.namespace}.utils.IJobLogger#info(java.lang.String)
     */
    @Override
    public void info(String msg) {
        logger.info(msg);
        LOC.infoT(msg);
    }

    /* (non-Javadoc)
     * @see {batch.namespace}.utils.IJobLogger#info(java.lang.String)
     */
    @Override
    public void debug(String msg) {
        // logger.finest(msg);
        LOC.debugT(msg);
    }

    /* (non-Javadoc)
     * @see {batch.namespace}.utils.IJobLogger#warning(java.lang.String)
     */
    @Override
    public void warning(String msg) {
        logger.warning(msg);
        LOC.warningT(msg);
    }

    /* (non-Javadoc)
     * @see {batch.namespace}.utils.IJobLogger#error(java.lang.String)
     */
    @Override
    public void error(String msg) {
        logger.severe(msg);
        LOC.errorT(msg);
    }

    /* (non-Javadoc)
     * @see {batch.namespace}.utils.IJobLogger#error(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error(String msg, Throwable t) {
        logger.severe(msg + " Exception: " + t.getMessage());
        LOC.traceThrowableT(Severity.ERROR, msg + " Exception: " + t.getMessage(), t);
    }

    @Override
    public void setReturnCodeIfHighest(ReturnCode returnCode) {
        if (this.returnCode.isHigherLevel(returnCode)) {
            this.returnCode = returnCode;
            short platformSpecificCode = (short) -1;
            switch (returnCode) {
            case OK:
                platformSpecificCode = (short) 0;
                break;
            case WARNING:
                platformSpecificCode = (short) 1;
                break;
            case INITIAL:
                platformSpecificCode = (short) -1;
                break;
            case ERROR:
                platformSpecificCode = (short) 99;
                break;
            default:
                platformSpecificCode = (short) 2;
                break;
            }
            jobContext.setReturnCode(platformSpecificCode);

        }

    }

    @Override
    public ReturnCode returnCode() {
        return returnCode;
    }

    @Override
    public boolean isLevelDebug() {
        return LOC.beDebug();
    }
}