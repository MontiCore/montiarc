/* (c) https://github.com/MontiCore/monticore */
package montiarc.lejos.lib.timer;

/**
 * Component behavior implementation of the bumperbot timer.
 * 
 */
public class Timer extends TimerTOP {
  protected boolean isRunning;
  protected long deadline;
  
  public Timer(Integer delay) {
    super(delay);
  }

  @Override
  public void compute() {
    if (this.getCmd().getValue() == TimerCmd.SINGLE) {
      deadline = System.currentTimeMillis() + delay;
      isRunning = true;
    }
    else if (this.getCmd().getValue() == TimerCmd.DOUBLE) {
      deadline = System.currentTimeMillis() + ((long) delay << 1);
      isRunning = true;
    }
    
    if (isRunning && System.currentTimeMillis() >= deadline) {
      isRunning = false;
    }
  }
  
}
