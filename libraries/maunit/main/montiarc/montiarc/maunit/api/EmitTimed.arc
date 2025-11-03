/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

component EmitTimed<T>(EventStream<T> output) {
  port out T out;

  EventStream<T> remaining = output;

  automaton {
    initial state S;
    S -> S / {
      if (!remaining.isEmpty()) {
        UntimedStream<T> untimedStream = remaining.first();
        while (!untimedStream.isEmpty()) {
          out = untimedStream.first();
          untimedStream = untimedStream.dropFirst();
        }
        remaining = remaining.dropFirst();
      }
    }
  }
}
