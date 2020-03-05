package org.slf4j.sap.netweaver;

import com.sap.tc.logging.Location;
import java.util.HashMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class LocationLoggerFactory implements ILoggerFactory {
	private HashMap<String, LocationLogger> loggers = new HashMap();

	public Logger getLogger(String name) {
		LocationLogger logger = null;
		synchronized (this) {
			logger = (LocationLogger) this.loggers.get(name);
			if (logger == null) {
				logger = createAndRegisterLogger(name);
			}
		}
		return logger;
	}

	private LocationLogger createAndRegisterLogger(String name) {
		Location location = Location.getLocation(name);
		LocationLogger logger = new LocationLogger(location);

		this.loggers.put(name, logger);
		return logger;
	}
}