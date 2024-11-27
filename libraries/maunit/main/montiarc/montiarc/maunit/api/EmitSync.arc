/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import java.util.List;

component EmitSync<T>(List<T> output) {
  port out T out;

  int index = 0;

  <<sync>> automaton {
    initial state S;
    S -> S / {
      if (!output.isEmpty()) {
        if (index >= output.size()) index = 0;
        out = output.get(index);
        index++;
      }
    };
  }
}
