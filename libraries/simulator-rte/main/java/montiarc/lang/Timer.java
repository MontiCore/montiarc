/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

public class Timer extends TimerTOP {

  protected long startTime;

  protected Timer(Duration duration) {
    this.duration = duration;
    if (Simulation.nanosecondsPerTick > 0) {
      this.startTime = Simulation.ticks;
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
    if (Simulation.nanosecondsPerTick > 0) {
      return Duration.ofMilliseconds((Simulation.ticks - startTime) * Simulation.nanosecondsPerTick / 1000000);
    } else {
      return Duration.ofMilliseconds((System.nanoTime() - startTime) / 1000000);
    }
  }
}
