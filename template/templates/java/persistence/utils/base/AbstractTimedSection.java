package {service.namespace}.utils.base;

public abstract class AbstractTimedSection {
    private long timerExecutionStart;
    private String sectionName;
    private String sectionInput;

    /**
    * Start Log of Timing Infos, if debug is on or times are greater some ms
    */
    protected AbstractTimedSection(String sectionName, String sectionInput) {
        this.sectionName = sectionName;
        this.sectionInput = sectionInput;
        this.timerExecutionStart = System.currentTimeMillis();
    }

    /**
    * start Log Timing Infos
    */
    public void start() {
    	if (isDebug()) {
        	debugT("Started " + sectionName + " based on " + sectionInput);
    	}
    }

    /**
    * Finish Log Timing Infos, if debug is on or times are greater some ms, show warning
    * 
    * @param timedSection
    */
    public void finish() {
        finish(null);
    }

    /**
    * Finish Log Timing Infos, if debug is on or times are greater some ms, show warning
    * 
    * @param timedSection
    * @param sectionResult
    */
    public void finish(String sectionResult) {
        long timerDurationMS = System.currentTimeMillis() - timerExecutionStart;
        final String finishedInfo = "Finished " + sectionName + " took " + timerDurationMS + "ms ";
        final String searchInputInfo = " based on " + sectionInput;
        if (timerDurationMS >= 1000) {
            if (sectionResult != null) {
                warningT(finishedInfo + " (slow) returns => " + sectionResult + searchInputInfo);
            } else {
                warningT(finishedInfo + " (slow) " + searchInputInfo);
            }
        } else if (isDebug()) {
            if (sectionResult != null) {
                debugT(finishedInfo + " returns => " + sectionResult + searchInputInfo);
            } else {
                debugT(finishedInfo + searchInputInfo);
            }
        }
    }

    protected abstract boolean isDebug();

    protected abstract void warningT(String message);

    protected abstract void debugT(String message);


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TimedSection [timerExecutionStart=" + timerExecutionStart + ", sectionName=" + sectionName
            + ", sectionInput=" + sectionInput + "]";
    }

}
