/* (c) https://github.com/MontiCore/monticore */
package mceffect.effect;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import java.awt.*;
import javax.swing.*;
import mceffect.graph.EffectEdge;
import mceffect.graph.EffectNode;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;

public class Utils {
  public static void visualize(Graph<EffectNode, EffectEdge> graph) {
    System.out.println("Start Visualizing");
    int i = 0;
    // Create JGraphXAdapter for visualization
    JGraphXAdapter<EffectNode, EffectEdge> jgxAdapter = new JGraphXAdapter<>(graph);

    // Create JGraphXAdapter for visualization

    // Apply a layout for better visualization
    mxGraphLayout layout = new mxHierarchicalLayout(jgxAdapter);
    layout.execute(jgxAdapter.getDefaultParent());

    // Display in a JFrame
    JFrame frame = new JFrame("JGraphT Visualization with JGraphX");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Disable adding new edges
    jgxAdapter.setAllowDanglingEdges(false);

    mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
    frame.getContentPane().add(graphComponent);
    graphComponent.setConnectable(false); // Disable edge creation by dragging from vertex
    graphComponent.setDragEnabled(false); // Disable dragging vertices
    frame.setSize(900, 900);
    frame.setVisible(true);

    try {
      Thread.sleep(1000000000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
