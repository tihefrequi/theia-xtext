package {service.namespace}.utils.logger;
{customcode.import}
public interface ILogger {

	{customcode.start}

	void logStart(String msg, Object... objects);

	void logEnd(String msg, Object... objects);

	void info(String msg);

	void warning(String msg);

	void error(String msg);

	void error(String msg, Throwable t);

	void debug(String msg);

	boolean isLevelDebug();

}