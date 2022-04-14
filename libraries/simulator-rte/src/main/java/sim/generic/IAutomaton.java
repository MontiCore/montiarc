/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

import java.util.List;
import java.util.Map;

public interface IAutomaton {

  void setupAutomaton();

  Map<Integer, List<Configuration>> getHistory();
}
