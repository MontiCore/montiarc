/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static arcbasis._symboltable.util.PortTraversalUtil.connectionsToMultimap;
import static arcbasis._symboltable.util.PortTraversalUtil.subcomponentEffectChainsToMultimap;

/**
 * Checks that for every cycle there is at least one component that is strongly causal modulo a port on this cycle.
 */
public class FeedbackStrongCausality implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    Multimap<String, String> connectors = connectionsToMultimap(node);
    Multimap<String, String> subConnectors = subcomponentEffectChainsToMultimap(node);
    connectors.putAll(subConnectors);
    List<List<String>> cycles = findAllCycles(connectors);
    for (List<String> cycle : cycles) {
      // We want to show the loop. So we add the initial port again
      //  we can only do that here as not to impede the normalization
      cycle.add(cycle.getFirst());
      String descriptor = String.join(" -> ", cycle);
      Log.error(ArcError.FEEDBACK_CAUSALITY + ": " + descriptor);
    }
  }

  /**
   * Finds all unique cycles in the connection graph.
   * This implementation is very simple and not optimized.
   * Because most components should only consist of a few subcomponents
   * and effect chains of subcomponents are computed separatly, it should suffice
   *
   * @param connections the connections of the Ports
   */
  public List<List<String>> findAllCycles(Multimap<String, String> connections) {
    Preconditions.checkNotNull(connections);
    Set<List<String>> uniqueCycles = new HashSet<>();

    // Try starting a search from every node.
    for (String start : connections.keySet()) {
      List<String> path = new ArrayList<>();
      findCyclesFrom(start, start, connections, path, uniqueCycles);
    }

    return new ArrayList<>(uniqueCycles);
  }

  /**
   * finds all cycles passing through start
   * @param start initial start position. also where the dfs stops
   * @param current current position in dfs
   * @param connections port connections
   * @param path path taken to this position
   * @param uniqueCycles return value of cycles.
   */
  protected void findCyclesFrom(String start,
                              String current,
                              Multimap<String, String> connections,
                              List<String> path,
                              Set<List<String>> uniqueCycles) {
    path.add(current);

    for (String neighbor : connections.get(current)) {
      if (neighbor.equals(start)) {
        uniqueCycles.add(normalize(path));
      } else if (!path.contains(neighbor)) {
        findCyclesFrom(start, neighbor, connections, path, uniqueCycles);
      }
    }

    path.removeLast();
  }

  /**
   * Rotate the cycle so it starts at its smallest node, allowing to compare them.
   */
  protected List<String> normalize(List<String> cycle) {
    int minIndex = 0;
    for (int i = 1; i < cycle.size(); i++) {
      if (cycle.get(i).compareTo(cycle.get(minIndex)) < 0) {
        minIndex = i;
      }
    }

    List<String> rotated = new ArrayList<>(cycle.size());
    for (int i = 0; i < cycle.size(); i++) {
      rotated.add(cycle.get((minIndex + i) % cycle.size()));
    }
    return rotated;
  }

}
