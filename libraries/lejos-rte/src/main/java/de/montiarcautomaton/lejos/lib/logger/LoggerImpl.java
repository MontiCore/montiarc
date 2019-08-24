/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.lejos.lib.logger;

import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;

/**
 * Component behavior implementation for simple logging.
 * 
 * @author Gerrit Leonhardt
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
