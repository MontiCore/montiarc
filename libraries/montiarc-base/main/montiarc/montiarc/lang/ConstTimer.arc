/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

/**
 * This component acts as a timer that can be started and sends a signal,
 * once the specified duration has been exceeded.
 * Starting the timer again when it's already running does nothing.
 * The duration of the Timer is fixed and cannot be changed during runtime.
 */
component ConstTimer(Duration duration) {

  port in  Signal start;
  port out Signal completed;

  Timer timer = Timer.start(Duration.ofMilliseconds(0));

  automaton {
    initial state idle;
    state running;

    idle -> idle;

    idle -> running start / {
      timer = Timer.start(duration);
    };
    running -> idle [timer.completed()] / {
      completed = Signal.get();
    };
  }
}
