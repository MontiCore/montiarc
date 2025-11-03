/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

component EmitSync<T>(SyncStream<T> output) {
  port sync out T out;

  SyncStream<T> remaining = output;

  automaton {
    initial state S;
    S -> S / {
      if (!remaining.isEmpty()) {
        out = remaining.first();
        remaining = remaining.dropFirst();
      }
    }
  }
}
