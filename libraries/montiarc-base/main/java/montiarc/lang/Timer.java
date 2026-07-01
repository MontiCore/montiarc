/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.rte.Simulation;

public class Timer extends TimerTOP {

  protected long startTime;

  protected Timer(Duration duration) {
    this.duration = duration;
    if (montiarc.rte.Simulation.nanosecondsPerTick > 0) {
      this.startTime = montiarc.rte.Simulation.ticks;
    } else {
      this.startTime = System.nanoTime();
    }
  }

  public static Timer start(Duration duration) {
    return new Timer(duration);
  }

  @Override
  public boolean completed() {
    return startedAgo().milliseconds >= duration.milliseconds;
  }

  @Override
  public Duration startedAgo() {
    if (montiarc.rte.Simulation.nanosecondsPerTick > 0) {
      return Duration.ofMilliseconds((Simulation.ticks - startTime) * montiarc.rte.Simulation.nanosecondsPerTick / 1000000);
    } else {
      return Duration.ofMilliseconds((System.nanoTime() - startTime) / 1000000);
    }
  }

  @Override
  public Duration remaining() {
    return duration.subtract(startedAgo());
  }
}
