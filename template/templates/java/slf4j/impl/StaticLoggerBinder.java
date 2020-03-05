package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.sap.netweaver.LocationLoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {
	private static final String LOGGER_FACTORY_NAME = LocationLoggerFactory.class.getName();
	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

	private final LocationLoggerFactory loggerFactory = new LocationLoggerFactory();

	public static StaticLoggerBinder getSingleton() {
		return SINGLETON;
	}

	private StaticLoggerBinder() {
		System.out.println("Installing slf4j -> com.sap.tc.logging.Location redirection.");
	}

	@Override
    public final ILoggerFactory getLoggerFactory() {
		return this.loggerFactory;
	}

	@Override
    public final String getLoggerFactoryClassStr() {
		return LOGGER_FACTORY_NAME;
	}
}
