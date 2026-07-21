/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static arcbasis._symboltable.util.PortTraversalUtil.connectionsToMultimap;
import static arcbasis._symboltable.util.PortTraversalUtil.subcomponentEffectChainsToMultimap;

public class ArcBasisScopesGenitorP3 implements ArcBasisVisitor2 {

  @Override
  public void visit(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    fillEffects(node, new HashSet<>());
    updateStronglyCausal(node);
  }

  /**
   * Fills the effect chains
   *
   * @param node  of the component to be filled
   * @param known what subcomponents have already been analyzed. Init as empty
   */
  protected void fillEffects(@NotNull ASTArcComponentType node, @NotNull Set<ComponentTypeSymbol> known) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(known);

    ComponentTypeSymbol symbol = node.getSymbol();
    if (!symbol.getEffectChains().isEmpty()) {
      // if the chain is already filled, we must have done that previously
      return;
    }
    if (node.getBehavior().isPresent() && node.getBehavior().get().isDelayed()) {
      // if the component is delayed, it does not affect any ports
      //  (see discussion in core#1057)
      return;
    }

    Multimap<PortSymbol, PortSymbol> chain = symbol.getEffectChains();
    if (symbol.isAtomic()) {

      // atomic not delayed components are considered to effect all out ports
      List<PortSymbol> inPorts = symbol.getIncomingPorts();
      List<PortSymbol> outPorts = symbol.getOutgoingPorts();
      for (PortSymbol inport : inPorts) {
        chain.putAll(inport, outPorts);
      }
    } else {
      // starting at every port, perform dfs to find all affected ports
      List<PortSymbol> inPorts = symbol.getIncomingPorts()
        .stream().toList();
      Set<String> finalPorts = symbol.getOutgoingPorts()
        .stream().map(PortSymbol::getName).collect(Collectors.toSet());

      if (known.isEmpty()) {
        known.add(node.getSymbol());
      }
      for (ASTComponentInstance subComponent : node.getSubComponents()) {
        if (!subComponent.getSymbol().isTypePresent()) {
          continue;
        }
        ComponentTypeSymbol subSymbol = subComponent.getSymbol().getType().getTypeInfo();
        // in case the effectChain of the subcomponent is not yet initialized,
        if (!known.contains(subSymbol)) {
          // the subcomponent might be node itself or there is some kind of
          //  dependency cycle that could result in an infinite loop
          known.add(subSymbol);
          if (subSymbol.isPresentAstNode()) {
            fillEffects((ASTArcComponentType) subSymbol.getAstNode(), known);
          }
        }
      }

      Multimap<String, String> connectors = connectionsToMultimap(node);
      Multimap<String, String> subConnectors = subcomponentEffectChainsToMultimap(node);
      connectors.putAll(subConnectors);

      for (PortSymbol inPort : inPorts) {
        Set<String> connected = dfsAffectedPorts(inPort.getName(), connectors, finalPorts, new HashSet<>());
        List<PortSymbol> ports = connected.stream().map(s -> node.getSpannedScope()
          .resolvePortMany(s)).flatMap(Collection::stream).toList();
        chain.putAll(inPort, ports);
      }
    }
  }

  /**
   * Find all terminating ports connected to the starting port.
   *
   * @param startPoint       the port where to start the depth first search
   * @param connectors       the connectors to follow
   * @param terminatingPorts the ports where to stop the search
   * @param visited          to avoid infinite loops this saves all ports already visited
   */
  protected Set<String> dfsAffectedPorts(String startPoint,
                                         Multimap<String, String> connectors,
                                         Set<String> terminatingPorts,
                                         Set<String> visited) {
    Set<String> effectedPorts = new HashSet<>();
    if (terminatingPorts.contains(startPoint)) {
      effectedPorts.add(startPoint);
      return effectedPorts;
    }

    for (String next : connectors.get(startPoint)) {
      if (!visited.contains(next)) {
        visited.add(next);
        effectedPorts.addAll(dfsAffectedPorts(next, connectors, terminatingPorts, visited));
      }
    }
    return effectedPorts;
  }

  protected void updateStronglyCausal(ASTArcComponentType node) {
    ComponentTypeSymbol nodeSymbol = node.getSymbol();
    for (PortSymbol port : nodeSymbol.getPorts()) {
      // In ports are never strongly causal
      // We preset out ports as strongly cause to overwrite later
      port.setStronglyCausal(port.isOutgoing());
    }
    Multimap<PortSymbol, PortSymbol> chain = nodeSymbol.getEffectChains();
    Set<PortSymbol> affectedPorts = new HashSet<>(chain.values());
    for (PortSymbol port : affectedPorts) {
      // if the port is affected by any in port in the same tick, it is not strongly causual
      port.setStronglyCausal(Boolean.FALSE);
    }
  }
}
