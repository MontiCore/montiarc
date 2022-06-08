/* (c) https://github.com/MontiCore/monticore */
package sim.serialiser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph<T> implements Serializable {

  private Map<T, List<T>> edgemap;

  public Graph() {
    edgemap = new HashMap<>();
  }

  public void addVertex(T s) {
    edgemap.put(s, new LinkedList<>());
  }

  public void addEdge(T source, T destination) {
    if (!edgemap.containsKey(source)) {
      addVertex(source);
    }

    if (!edgemap.containsKey(destination)) {
      addVertex(destination);
    }

    edgemap.get(source).add(destination);
  }

  public int getVertexCount() {
    return edgemap.keySet().size();
  }

}
