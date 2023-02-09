/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.ArrayList;
import java.util.List;

public class TransitionPath {

  private List<Configuration> path;

  public TransitionPath() {
    path = new ArrayList<>();
  }

  public List<Configuration> getPath() {
    return path;
  }

  public void add(Configuration conf) {
    path.add(conf);
  }

  public Configuration getLast() {
    return path.get(path.size() - 1);
  }

}
