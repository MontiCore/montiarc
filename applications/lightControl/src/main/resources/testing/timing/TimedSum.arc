/* (c) https://github.com/MontiCore/monticore */
package testing.timing;

//import java.lang.String;

/**
 * This component only differs from UntimedSum in its timing.
 * This one has instant timing, which means that it can react instantly to messages,
 * but only if they have not sent a tick yet.
 * That means that the other ports have to send a message before this message can be read again.
 */
component TimedSum {

  timing instant;

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