/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._ast.ASTPort;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArcBasisScopesGenitorP3 implements ArcBasisVisitor2 {

  @Override
  public void visit(ASTArcPort node) {
    if (!node.getEnclosingScope().isPresentSpanningSymbol()) {
      return;
    }
    updateStronglyCausal(node.getSymbol());
  }

  protected boolean updateStronglyCausal(PortSymbol port) {
    // Calculate if needed
    if (port.getStronglyCausal() == null && port.isPresentAstNode()) {
      ASTPort node = port.getAstNode();
      if (!node.getEnclosingScope().isPresentSpanningSymbol()) {
        node.getSymbol().setStronglyCausal(false);
      }
      IScopeSpanningSymbol symbol = node.getEnclosingScope().getSpanningSymbol();
      if (symbol instanceof ArcComponentTypeSymbol) {
        node.getSymbol().setStronglyCausal(isHereditaryStronglyCausal(node.getSymbol(), (ArcComponentTypeSymbol) symbol));
      } else {
        node.getSymbol().setStronglyCausal(false);
      }
    }

    return port.getStronglyCausal();
  }

  protected boolean isHereditaryStronglyCausal(PortSymbol portSymbol, ArcComponentTypeSymbol componentTypeSymbol) {
    if (portSymbol.isOutgoing() && componentTypeSymbol.isAtomic()) {
      // outgoing ports of atomic components are strongly causal if their behavior specification is strongly causal
      return componentTypeSymbol.getBehavior().isPresent()
        && componentTypeSymbol.getBehavior().get().isDelayed();
    } else if (portSymbol.isOutgoing() && componentTypeSymbol.isDecomposed()) {
      // outgoing ports of composed components are strongly causal if their composed behavior is strongly causal
      return isComposedStronglyCausal(portSymbol, componentTypeSymbol);
    }
    return false;
  }

  protected boolean isComposedStronglyCausal(PortSymbol portSymbol, ArcComponentTypeSymbol componentTypeSymbol) {
    if (!componentTypeSymbol.isDecomposed() || !portSymbol.isOutgoing()) {
      return false;
    }

    List<List<ASTConnector>> paths = new ArrayList<>();
    paths.add(new ArrayList<>(componentTypeSymbol.getAstNode().getConnectorsMatchingTarget(portSymbol.getName())));

    while (!paths.isEmpty()) {
      List<List<ASTConnector>> newPaths = new ArrayList<>();
      for (List<ASTConnector> path : paths) {
        if (path.isEmpty()) continue;

        ASTPortAccess lastSource = path.get(path.size() - 1).getSource(); //the source of the connector that is the furthest away from the current port.

        if (lastSource.isPresentPortSymbol() && updateStronglyCausal(lastSource.getPortSymbol())) {
          // If any part of the path is strongly causal, this path is strongly causal, not compromising the current port's strong causality.
          // not entering this if-block also implies the instance to which this port belongs has incoming ports: if there were none, the port would be strongly causal
          continue;
        }

        boolean sourceIsOnOwningComponent = lastSource.isPresentComponentSymbol()
          && lastSource.getComponentSymbol().isTypePresent()
          && lastSource.getComponentSymbol().getType().getTypeInfo().equals(componentTypeSymbol);

        if (!lastSource.isPresentComponentSymbol() || sourceIsOnOwningComponent) {
          //  if no instance is set, this port likely belongs to `component` (the function parameter).
          //  This would mean that we have a path which is not strongly causal
          //  (whenever we encountered a strongly causal port/component, we did not follow the path further. Thus, the current path cannot contain strongly causal ports/components).
          // Therefore, we can assume we can set stronglyCausal = false here
          return false;
        }
        SubcomponentSymbol instance = lastSource.getComponentSymbol();
        if (!instance.isTypePresent()) continue; //Incomplete symboltable. See above.
        instance.getType().getTypeInfo().getAllIncomingPorts()
          .forEach(incomingPortOfSubcomponent -> componentTypeSymbol.getAstNode()
            .getConnectorsMatchingTarget(instance.getName() + "." + incomingPortOfSubcomponent.getName())
            .forEach(connector -> {
              List<ASTConnector> newPath = new ArrayList<>(path);
              newPath.add(connector);
              newPaths.add(newPath);
            }));
      }
      paths = newPaths;
    }
    // if we reach then we did not find any path contradicting strong causality
    return true;
  }
}
