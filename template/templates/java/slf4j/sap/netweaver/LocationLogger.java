package org.slf4j.sap.netweaver;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class LocationLogger extends MarkerIgnoringBase {
	private Location location;

	public LocationLogger(Location location) {
		this.location = location;
	}

	public void debug(String msg) {
		this.location.debugT(msg);
	}

	public void debug(String format, Object arg) {
		FormattingTuple ft = MessageFormatter.format(format, arg);
		this.location.debugT(ft.getMessage());
	}

	public void debug(String format, Object[] argArray) {
		FormattingTuple ft = MessageFormatter.format(format, argArray);
		this.location.debugT(ft.getMessage());
	}

	public void debug(String msg, Throwable t) {
		FormattingTuple ft = MessageFormatter.format(msg, t);
		this.location.traceThrowableT(Severity.DEBUG, ft.getMessage(), t);
	}

	public void debug(String format, Object arg1, Object arg2) {
		FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
		this.location.debugT(ft.getMessage());
	}

	public void error(String msg) {
		this.location.errorT(msg);
	}

	public void error(String format, Object arg) {
		FormattingTuple ft = MessageFormatter.format(format, arg);
		this.location.errorT(ft.getMessage());
	}

	public void error(String format, Object[] argArray) {
		FormattingTuple ft = MessageFormatter.format(format, argArray);
		this.location.errorT(ft.getMessage());
	}

	public void error(String msg, Throwable t) {
		FormattingTuple ft = MessageFormatter.format(msg, t);
		this.location.traceThrowableT(Severity.ERROR, ft.getMessage(), t);
	}

	public void error(String format, Object arg1, Object arg2) {
		FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
		this.location.errorT(ft.getMessage());
	}

	public void info(String msg) {
		this.location.infoT(msg);
	}

	public void info(String format, Object arg) {
		FormattingTuple ft = MessageFormatter.format(format, arg);
		this.location.infoT(ft.getMessage());
	}

	public void info(String format, Object[] argArray) {
		FormattingTuple ft = MessageFormatter.format(format, argArray);
		this.location.infoT(ft.getMessage());
	}

	public void info(String msg, Throwable t) {
		FormattingTuple ft = MessageFormatter.format(msg, t);
		this.location.traceThrowableT(Severity.INFO, ft.getMessage(), t);
	}

	public void info(String format, Object arg1, Object arg2) {
		FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
		this.location.infoT(ft.getMessage());
	}

	public boolean isDebugEnabled() {
		return this.location.beDebug();
	}

	public boolean isErrorEnabled() {
		return this.location.beError();
	}

	public boolean isInfoEnabled() {
		return this.location.beInfo();
	}

	public boolean isTraceEnabled() {
		return this.location.beDebug();
	}

	public boolean isWarnEnabled() {
		return this.location.beWarning();
	}

	public void trace(String msg) {
		debug(msg);
	}

	public void trace(String format, Object arg) {
		debug(format, arg);
	}

	public void trace(String format, Object[] argArray) {
		debug(format, argArray);
	}

	public void trace(String msg, Throwable t) {
		debug(msg, t);
	}

	public void trace(String format, Object arg1, Object arg2) {
		debug(format, arg1, arg2);
	}

	public void warn(String msg) {
		this.location.warningT(msg);
	}

	public void warn(String format, Object arg) {
		FormattingTuple ft = MessageFormatter.format(format, arg);
		this.location.warningT(ft.getMessage());
	}

	public void warn(String format, Object[] argArray) {
		FormattingTuple ft = MessageFormatter.format(format, argArray);
		this.location.warningT(ft.getMessage());
	}

	public void warn(String msg, Throwable t) {
		FormattingTuple ft = MessageFormatter.format(msg, t);
		this.location.traceThrowableT(Severity.WARNING, ft.getMessage(), t);
		this.location.warningT(ft.getMessage());
	}

	public void warn(String format, Object arg1, Object arg2) {
		FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
		this.location.warningT(ft.getMessage());
	}
}
