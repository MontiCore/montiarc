/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import java.util.List;

component EmitTimed<T>(List<List<T>> output) {
  port out T out;

  int index = 0;

  automaton {
    initial state S;
    S -> S / {
      if (index >= output.size()) index = 0;
      if (index < output.size()) {
        for (int i = 0; i < output.get(index).size(); i++) {
          out = output.get(index).get(i);
        }
        index++;
      }
    }
  }
}
