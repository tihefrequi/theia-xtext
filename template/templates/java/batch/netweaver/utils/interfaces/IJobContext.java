package {batch.namespace}.utils.interfaces;

import {service.namespace}.utils.logger.ILogger;
import {batch.namespace}.utils.ReturnCode;

public interface IJobContext extends ILogger {

    void setReturnCodeIfHighest(ReturnCode returnCode);

    public ReturnCode returnCode();
}