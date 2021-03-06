/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.timer;

import de.montiarc.runtimes.timesync.implementation.IComputable;

/**
 * Component behavior implementation of the bumperbot timer.
 * 
 */
public class TimerImpl implements IComputable<TimerInput, TimerResult> {
  private final int delay;
  private boolean isRunning;
  private long deadline;
  
  public TimerImpl(Integer delay) {
    this.delay = delay;
  }
  
  @Override
  public TimerResult getInitialValues() {
    return new TimerResult();
  }
  
  @Override
  public TimerResult compute(TimerInput input) {
    if (input.getCmd() == TimerCmd.SINGLE) {
      deadline = System.currentTimeMillis() + delay;
      isRunning = true;
    }
    else if (input.getCmd() == TimerCmd.DOUBLE) {
      deadline = System.currentTimeMillis() + ((long) delay << 1);
      isRunning = true;
    }
    
    if (isRunning && System.currentTimeMillis() >= deadline) {
      isRunning = false;
      return new TimerResult(TimerSignal.ALERT);
    }
    return new TimerResult(TimerSignal.SLEEP);
  }
  
}
