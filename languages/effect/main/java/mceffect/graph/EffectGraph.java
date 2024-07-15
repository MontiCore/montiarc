/* (c) https://github.com/MontiCore/monticore */
package mceffect.graph;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.ISymbol;
import java.util.List;
import mceffect.effect.Effect;
import mceffect.effect.EffectStorage;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.SimpleDirectedGraph;

public class EffectGraph {

  protected final SimpleDirectedGraph<EffectNode, EffectEdge> graph;

  public EffectGraph(ComponentTypeSymbol component, EffectStorage effectStorage) {

    // create effect graph
    graph = new SimpleDirectedGraph<>(EffectEdge.class);

    // add component interfaces as Node
    component.getAllPorts().forEach(p -> addNode(p, component));

    // add subcomponent interfaces as Node
    for (SubcomponentSymbol comp : component.getSubcomponents()) {
      ComponentSymbol subComponent = comp.getType().getTypeInfo();
      subComponent.getAllPorts().forEach(p -> addNode(p, comp));
    }

    // add effect between subcomponents as edges
    for (SubcomponentSymbol subComp : component.getSubcomponents()) {
      List<Effect> effect = effectStorage.getEffectsOfComponent((ComponentTypeSymbol) subComp.getType().getTypeInfo());
      effect.forEach(eff -> addEffectEdge(eff, subComp));
    }

    // add connectors as edges
    for (ASTConnector connector : component.getAstNode().getConnectors()) {
      PortSymbol src = connector.getSource().getPortSymbol();
      ISymbol srcComp =
          connector.getSource().isPresentComponentSymbol()
              ? connector.getSource().getComponentSymbol()
              : component;

      for (ASTPortAccess target : connector.getTargetList()) {
        PortSymbol tgt = target.getPortSymbol();
        ISymbol tgtComp =
            target.isPresentComponentSymbol() ? target.getComponentSymbol() : component;

        addEdge(src, srcComp, tgt, tgtComp);
      }
    }
  }

  private void addEdge(PortSymbol src, ISymbol srcComp, PortSymbol tgt, ISymbol tgtComp) {
    EffectNode srcNode = EffectNode.getInstance(src, srcComp);
    EffectNode tgtNode = EffectNode.getInstance(tgt, tgtComp);
    graph.addEdge(srcNode, tgtNode, new EffectEdge());
  }

  private void addEffectEdge(Effect effect, SubcomponentSymbol subComp) {
    EffectNode srcNode = EffectNode.getInstance(effect.getFrom(), subComp);
    EffectNode tgtNode = EffectNode.getInstance(effect.getTo(), subComp);
    graph.addEdge(srcNode, tgtNode, new EffectEdge(effect));
  }

  private void addNode(PortSymbol node, ISymbol component) {
    graph.addVertex(EffectNode.getInstance(node, component));
  }

  public List<GraphPath<EffectNode, EffectEdge>> getAllPath(
      ComponentTypeSymbol component, PortSymbol src, PortSymbol tgt) {

    AllDirectedPaths<EffectNode, EffectEdge> allPaths = new AllDirectedPaths<>(graph);
    EffectNode srcNode = EffectNode.getInstance(src, component);
    EffectNode tgtNode = EffectNode.getInstance(tgt, component);
    return allPaths.getAllPaths(srcNode, tgtNode, false, graph.edgeSet().size());
  }

  public SimpleDirectedGraph<EffectNode, EffectEdge> getGraph() {
    return graph;
  }
}
