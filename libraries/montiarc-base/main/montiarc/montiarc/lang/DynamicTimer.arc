/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import montiarc.lang.Signal.SIGNAL;

/**
 * This component acts as a timer that can be started and sends a signal,
 * once the specified duration has been exceeded.
 * Starting the timer again when it's already running does nothing.
 */
component DynamicTimer {

  port in  Duration start;
  port out Signal completed;

  Timer timer = Timer.start(Duration.ofMilliseconds(0));

  automaton {
    initial state idle;
    state running;

    idle -> idle;

    idle -> running start / {
      timer = Timer.start(start);
    }
    running -> idle [timer.completed()] / {
      completed = SIGNAL;
    }
  }
}
