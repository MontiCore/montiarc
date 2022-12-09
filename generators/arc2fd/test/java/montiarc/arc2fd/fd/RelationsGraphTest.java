/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

class RelationsGraphTest {
  RelationsGraph<String> rg = new RelationsGraph<>();

  @BeforeEach
  public void setUp() { }

  /**
   * Method under test {@link RelationsGraph#getUnusedNodes(Object)}
   */
  @Test
  void getUnusedNodes() {
    // Given
    String startingNode = "a";
    Set<String> trueUnusedNodes = Set.of("f", "g");

    // When
    createGraph(startingNode);
    Set<String> unusedNodes = rg.getUnusedNodes(startingNode);

    // Then
    Assertions.assertEquals(trueUnusedNodes, unusedNodes);
  }

  /**
   * Method under test {@link RelationsGraph#addEdge(Object, Object)}}
   */
  @Test
  void addEdge() {
    // Given
    Map<String, LinkedList<String>> trueAdj = new HashMap<>();
    trueAdj.put("a", new LinkedList<>(Set.of("b")));
    trueAdj.put("b", new LinkedList<>());

    // When
    rg.addEdge("a", "b");

    // Then
    Assertions.assertEquals(trueAdj, rg.getAdj());
  }

  /**
   * Method under test {@link RelationsGraph#addEdges(Object, Set)}}
   */
  @Test
  void addEdges() {
    // Given
    Map<String, LinkedList<String>> trueAdj = new HashMap<>();
    trueAdj.put("a", new LinkedList<>(Set.of("b", "c", "d")));
    trueAdj.put("b", new LinkedList<>());
    trueAdj.put("c", new LinkedList<>());
    trueAdj.put("d", new LinkedList<>());

    // When
    rg.addEdges("a", Set.of("b", "c", "d"));

    // Then
    Assertions.assertEquals(trueAdj, rg.getAdj());
  }

  /**
   * Method under test {@link RelationsGraph#BFS(Object)}}
   */
  @Test
  void BFS() {
    // Given
    String startingNodeA = "a";
    createGraph(startingNodeA);
    Set<String> trueTraversedNodesFromA = Set.of("a", "b", "c", "d");

    // When

    Set<String> traversedNodesA = rg.BFS(startingNodeA);

    // Then
    Assertions.assertEquals(trueTraversedNodesFromA, traversedNodesA);

    // Given
    String startingNodeF = "f";
    Set<String> trueTraversedNodesFromF = Set.of("a", "b", "c", "d", "f", "g");

    // When
    Set<String> traversedNodesF = rg.BFS(startingNodeF);

    // Then
    Assertions.assertEquals(trueTraversedNodesFromF, traversedNodesF);
  }

  /**
   * Creates a small Graph to analyze
   *
   * @param startingNode First node of the Graph
   */
  private void createGraph(String startingNode) {
    rg.addEdge(startingNode, "b");
    rg.addEdge("b", "c");
    rg.addEdge("c", "d");
    rg.addEdge("d", "b");
    rg.addEdge("f", "g");
    rg.addEdge("g", "a");
  }
}
