/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a configured component type variant. Excludes all symbols not found in this specific variant.
 */
public class VariantPortSymbol extends PortSymbol {

  protected PortSymbol parent;

  protected ArcComponentTypeSymbol component;

  public VariantPortSymbol(PortSymbol parent, ArcComponentTypeSymbol component) {
    super(parent.getName());
    this.parent = parent;
    this.setIncoming(parent.isIncoming());
    this.setOutgoing(parent.isOutgoing());
    this.setType(parent.getType());
    this.setEnclosingScope(parent.getEnclosingScope());
    this.setAccessModifier(parent.getAccessModifier());
    this.setTiming(parent.getTiming());
    if (parent.isPresentAstNode()) {
      this.setAstNode(parent.getAstNode());
    }

    if (!parent.isPresentAstNode()) { // workaround for not being able to recalculate the timings without the AST
      this.setStronglyCausal(parent.getStronglyCausal());
    }

    this.component = component;
  }

  public PortSymbol getOriginal() {
    return parent;
  }

  @Override
  public Boolean getStronglyCausal() {
    // Recalculate on demand for variant ports
    if (this.stronglyCausal == null) {
      this.stronglyCausal = this.isHereditaryStronglyCausal();
    }

    return this.stronglyCausal;
  }

  protected boolean isHereditaryStronglyCausal() {
    if (this.isOutgoing() && this.component.isAtomic()) {
      // outgoing ports of atomic components are strongly causal if their behavior specification is strongly causal
      // or if the component has no incoming ports
      return (component.getBehavior().isPresent()
        && component.getBehavior().get().isDelayed()) || component.getAllIncomingPorts().isEmpty();
    } else if (this.isOutgoing() && this.component.isDecomposed()) {
      // outgoing ports of composed components are strongly causal if their composed behavior is strongly causal
      return this.isComposedStronglyCausal();
    }
    return false;
  }

  protected boolean isComposedStronglyCausal() {
    if (!this.component.isDecomposed() || !this.isOutgoing()) {
      return false;
    }

    List<List<ASTConnector>> paths = new ArrayList<>();
    paths.add(new ArrayList<>(this.component.getAstNode().getConnectorsMatchingTarget(this.getName())));

    while (!paths.isEmpty()) {
      List<List<ASTConnector>> newPaths = new ArrayList<>();
      for (List<ASTConnector> path : paths) {
        if (path.isEmpty()) continue;

        ASTPortAccess lastSource = path.get(path.size() - 1).getSource(); //the source of the connector that is the furthest away from the current port.

        if (lastSource.isPresentPortSymbol() && lastSource.getPortSymbol().getStronglyCausal()) {
          // If any part of the path is strongly causal, this path is strongly causal, not compromising the current port's strong causality.
          // not entering this if-block also implies the instance to which this port belongs has incoming ports: if there were none, the port would be strongly causal
          continue;
        }

        boolean sourceIsOnOwningComponent = lastSource.isPresentComponentSymbol()
          && lastSource.getComponentSymbol().isTypePresent()
          && lastSource.getComponentSymbol().getType().getTypeInfo().equals(component);

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
          .forEach(incomingPortOfSubcomponent -> component.getAstNode()
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
