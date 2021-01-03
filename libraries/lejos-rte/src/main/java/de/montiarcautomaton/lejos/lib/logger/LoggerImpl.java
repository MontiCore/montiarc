/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.logger;

import montiarc.runtimes.timesync.implementation.IComputable;

/**
 * Component behavior implementation for simple logging.
 * 
 */
public class LoggerImpl implements IComputable<LoggerInput, LoggerResult> {
  
  @Override
  public LoggerResult getInitialValues() {
    return new LoggerResult();
  }
  
  @Override
  public LoggerResult compute(LoggerInput input) {
    if (input.getMessage() != null) {
      System.out.println(input.getMessage());
    }
    return new LoggerResult();
  }
  
}
