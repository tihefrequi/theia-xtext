package {service.namespace}.utils.sap.netweaver;

import com.sap.tc.logging.Location;
import {service.namespace}.utils.base.AbstractTimedSection;

public class TimedSection extends AbstractTimedSection {
    private Location logger;
    
	/**
	* Start Log of Timing Infos, if debug is on or times are greater some ms
	*/
	private TimedSection(Location logger, String sectionName, String sectionInput) {
		super(sectionName, sectionInput);
		this.logger = logger;
	}

	/**
	* Start Log of Timing Infos, if debug is on or times are greater some ms
	*/
	public static TimedSection start(Location logger, String sectionName, String sectionInput) {
    	TimedSection timedSection = new TimedSection(logger, sectionName, sectionInput);
    	timedSection.start();
    	return timedSection;
	}

	@Override
	protected boolean isDebug() {
		return logger.beDebug();
	}

	@Override
	protected void warningT(String message) {
		logger.warningT(message);
	}

	@Override
	protected void debugT(String message) {
		logger.debugT(message);
	}

}
