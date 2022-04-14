/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

import java.util.ArrayList;
import java.util.List;

public class Transitionpath {

  private List<Configuration> path;

  public Transitionpath() {
    path = new ArrayList<>();
  }

  public List<Configuration> getPath() {
    return path;
  }

  public void add(Configuration conf) {
    path.add(conf);
  }

  public Configuration getLast() {return path.get(path.size() - 1);}

}
