package {service.namespace}.utils;

import {service.namespace}.utils.base.AbstractTimedSection;

public class TimedSection extends AbstractTimedSection {
    private org.slf4j.Logger logger;

    /**
    * Start Log of Timing Infos, if debug is on or times are greater some ms
    */
    private TimedSection(org.slf4j.Logger logger, String sectionName, String sectionInput) {
        super(sectionName, sectionInput);
        this.logger = logger;
    }

    /**
    * Start Log of Timing Infos, if debug is on or times are greater some ms
    */
    public static TimedSection start(org.slf4j.Logger logger, String sectionName, String sectionInput) {
    	TimedSection timedSection = new TimedSection(logger, sectionName, sectionInput);
    	timedSection.start();
    	return timedSection;
    }

    @Override
    protected boolean isDebug() {
        return logger.isDebugEnabled();
    }

    @Override
    protected void warningT(String message) {
        logger.warn(message);
    }

    @Override
    protected void debugT(String message) {
        logger.debug(message);
    }

}
