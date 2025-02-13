/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import java.util.List;

component EmitList<T>(List<T> output) {
  port out T out;

  automaton {
    initial state S;
    S -> S / {
      for (int i = 0; i < output.size(); i++) {
        out = output.get(i);
      }
    };
  }
}
