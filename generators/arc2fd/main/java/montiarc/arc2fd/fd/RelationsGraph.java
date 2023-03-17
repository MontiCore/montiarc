/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import montiarc.arc2fd.smt.FDRelation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class which can construct Graphs on generic types and apply Breadth First
 * Search (BFS).
 * <p>
 * This comes in handy, since we can use it to determine which parts of a
 * graph aren't reachable.
 * And this gives us the relations which aren't reachable / unnecessary and
 * should be removed.
 *
 * @param <G> Generic Type of the Elements we want to add in the Graph
 */
public class RelationsGraph<G> {

  /**
   * Adjacency Map (where the keys correspond to the nodes and the LinkedList
   * corresponds to the nodes
   * reachable from the current one)
   */
  private final Map<G, LinkedList<G>> adj;

  private final Set<G> allNodes = new HashSet<>();

  /**
   * Construct an empty graph
   */
  RelationsGraph() {
    adj = new HashMap<>();
  }

  /**
   * Construct an RelationsGraph and populate it with a List of Relations
   *
   * @param edges List of Relations whose nodes (= keys) and edges (= values)
   *              we want to add to the graph.
   */
  RelationsGraph(List<FDRelation<G>> edges) {
    adj = new HashMap<>();
    edges.stream().filter(Objects::nonNull).forEach(edge -> edge.getRelationsHashMap().forEach(this::addEdges));
  }

  /**
   * Performs a BFS on the current Graph Instance from the given startNode
   * and returns a list of all
   * visited nodes during this search.
   *
   * @param startNode Node from which we want to start our BFS.
   * @return Set of all visited nodes during our BFS.
   */
  public Set<G> getUnusedNodes(G startNode) {
    Set<G> unusedNodes = new HashSet<>(allNodes);
    unusedNodes.removeAll(BFS(startNode));
    return unusedNodes;
  }

  /**
   * Add an Edge (from v to w) to the Graph
   *
   * @param v From-Node
   * @param w To-Node
   */
  void addEdge(G v, G w) {
    addEdges(v, Set.of(w));
  }

  /**
   * Adds a List of Edges (from v to all elements of set w)
   *
   * @param v From-Node
   * @param w Set of "To-Nodes" (add a connection from v to all elements in w)
   */
  void addEdges(G v, Set<G> w) {
    if (!adj.containsKey(v))
      adj.put(v, new LinkedList<>());

    // Add new LinkedLists to all nodes which aren't already part of the
    // adjacency map
    w.stream().filter(wTmp -> !adj.containsKey(wTmp)).forEach(wTmp -> adj.put(wTmp, new LinkedList<>()));

    // Add all connections
    adj.get(v).addAll(w);

    // Add the nodes to "allNodes" to keep track of all nodes we currently have
    allNodes.add(v);
    allNodes.addAll(w);
  }

  /**
   * Performs a Breadth-First-Search on the current Graph and returns a Set
   * of Nodes which where visited
   * during the BFS traversal.
   *
   * @param startingNode Starting Point of our BFS
   * @return Set of all Nodes we've visited during our BFS.
   */
  public Set<G> BFS(G startingNode) {
    if (Objects.isNull(startingNode) || !adj.containsKey(startingNode))
      return new HashSet<>();

    // Mark all the vertices as not visited (= false)
    Map<G, Boolean> visited = new HashMap<>();

    // Mark all nodes as un-visited
    adj.forEach((key, values) -> {
      visited.put(key, false);
      values.forEach(v -> visited.put(v, false));
    });

    // Create a queue for BFS
    LinkedList<G> queue = new LinkedList<>();

    // Mark the current node as visited and enqueue it
    visited.put(startingNode, true);
    queue.add(startingNode);

    // Perform the BFS (as long as the queue is empty)
    while (queue.size() != 0) {
      // Dequeue a vertex from queue and print it
      startingNode = queue.poll();

      // Get all adjacent vertices we haven't visited yet and add each one to
      // the queue
      // Additionally, mark each one as visited.
      adj.get(startingNode).stream().filter(n -> !visited.get(n)).forEach(node -> {
        visited.put(node, true);
        queue.add(node);
      });
    }

    // Determine which keys are left, where left means that they aren't part
    // of the BFS and thus
    // aren't connected to our root node. Thus, we can remove them at a later
    // point in time...
    Set<G> visitedKeys = new HashSet<>();
    adj.forEach((key, values) -> {
      if (visited.get(key))
        visitedKeys.add(key);
      visitedKeys.addAll(values.stream().filter(visited::get).collect(Collectors.toSet()));
    });
    return visitedKeys;
  }

  /**
   * Get the complete Graph as an adjacency matrix
   *
   * @return Map (= adjacency matrix)
   */
  public Map<G, LinkedList<G>> getAdj() {
    return adj;
  }
}
