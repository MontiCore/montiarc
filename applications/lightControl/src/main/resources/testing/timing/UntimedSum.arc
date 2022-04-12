/* (c) https://github.com/MontiCore/monticore */
package testing.timing;

//import java.lang.String;

/**
 * This component only differs from TimedSum in its timing.
 * This one is untimed, which means it may react to messages in the order they are received.
 */
component UntimedSum {

  timing untimed;

  port in int a;
  port in int b;
  port out int c;
  int d = 0;

  automaton {
    initial state Solo;

    Solo -> Solo [true] <a> / {
      d += a;
      c = d;
    };

    Solo -> Solo [true] <b> / {
      d += b;
      c = d;
    };
  }
}