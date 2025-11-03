/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

component EmitList<T>(UntimedStream<T> output) {
  port out T out;

  automaton {
    initial state S;
    S -> S / {
      UntimedStream<T> remaining = output;
      while (!remaining.isEmpty()) {
        out = remaining.first();
        remaining = remaining.dropFirst();
      }
    }
  }
}
