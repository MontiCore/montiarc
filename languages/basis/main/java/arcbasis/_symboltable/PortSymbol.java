/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTConnectorTOP;
import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.check.SymTypeExpression;
import montiarc.Timing;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PortSymbol extends PortSymbolTOP {

  protected Boolean stronglyCausal = null;

  /**
   * @param name the name of this port.
   */
  protected PortSymbol(String name) {
    super(name);
  }

  /**
   * @param name      the name of this port.
   * @param incoming  whether the port is incoming.
   * @param outgoing  whether the port is outgoing.
   * @param type      the type of this port.
   * @param timing    the timing of this port.
   */
  protected PortSymbol(String name, boolean incoming, boolean outgoing, SymTypeExpression type, Timing timing) {
    super(name);
    this.type = type;
    this.timing = timing;
    this.incoming = incoming;
    this.outgoing = outgoing;
  }

  public boolean isTypePresent() {
    return this.type != null;
  }

  /**
   * @return the type for the type of this port.
   */
  public SymTypeExpression getType() {
    Preconditions.checkState(this.type != null, "Type of Port '%s' has not been set. Did you " +
      "forget to run the symbol table completer?", this.getName());
    return this.type;
  }

  /**
   * @param type the type of this port.
   */
  public void setType(@NotNull SymTypeExpression type) {
    Preconditions.checkNotNull(type);
    this.type = type;
  }

  /**
   * @return the type of this port.
   * @throws java.util.NoSuchElementException if the port type is not found.
   */
  public TypeSymbol getTypeInfo() {
    return this.getType().getTypeInfo() instanceof TypeSymbolSurrogate ?
      ((TypeSymbolSurrogate) this.getType().getTypeInfo()).lazyLoadDelegate() : this.getType().getTypeInfo();
  }

  /**
   * @return the timing of this port.
   */
  @Override
  public @NotNull Timing getTiming() {
    if (this.timing == null) {
      this.timing = this.getHereditaryTiming().orElse(Timing.DEFAULT);
    }
    return this.timing;
  }

  /**
   * @param timing the timing of this port.
   */
  @Override  // We override only add the @Nullable annotation
  public void setTiming(@Nullable Timing timing) {
    this.timing = timing;
  }

  @Override
  public Boolean getDelayed() {
    return this.isDelayed();
  }

  public boolean isDelayed() {
    if (this.delayed == null) {
     this.delayed = this.isHereditaryDelayed();
    }
    return this.delayed;
  }

  @Override  // We override only add the @Nullable annotation
  public void setDelayed(@Nullable Boolean delayed) {
    this.delayed = delayed;
  }

  protected Optional<Timing> getHereditaryTiming() {
    Optional<ComponentTypeSymbol> component = this.getComponent();
    if (component.isEmpty()) {
      return Optional.empty();
    } else if (component.get().isAtomic()) {
      return component.get().getTiming();
    } else if (this.isOutgoing()) {
      Optional<ASTPortAccess> source = component.get().getAstNode()
        .getConnectorsMatchingTarget(this.getName())
        .stream().findFirst().map(ASTConnector::getSource);
      return source.filter(ASTPortAccess::isPresentPortSymbol).map(p -> p.getPortSymbol().getTiming());
    } else if (this.isIncoming()) {
      Optional<ASTPortAccess> source = component.get().getAstNode()
        .getConnectorsMatchingSource(this.getName())
        .stream().findFirst().map(c -> c.getTarget(0));
      return source.filter(ASTPortAccess::isPresentPortSymbol).map(p -> p.getPortSymbol().getTiming());
    } else {
      return Optional.empty();
    }
  }

  protected boolean isHereditaryDelayed() {
    if (this.getComponent().isEmpty()) {
      // ill-structured symbol table
      return false;
    } else if (this.getComponent().get().isAtomic()) {
      // ports in atomic components are delayed explicitly
      return false;
    } else if (this.isOutgoing() && this.getComponent().get().isDecomposed()) {
      // outgoing ports in composed components are delayed if their source is delayed
      Optional<ASTPortAccess> source = this.getComponent().get().getAstNode()
        .getConnectorsMatchingTarget(this.getName())
        .stream().findFirst().map(ASTConnectorTOP::getSource);
      return source.filter(ASTPortAccess::isPresentPortSymbol)
        .map(p -> p.getPortSymbol().isDelayed()).orElse(false);
    }
    return false;
  }

  public boolean isStronglyCausal() {
    if(this.stronglyCausal == null) {
      if (this.isOutgoing() && this.isDelayed()) {
        this.stronglyCausal = true;
      } else {
        this.stronglyCausal = this.isHereditaryStronglyCausal();
      }
    }

    return this.stronglyCausal;
  }

  protected boolean isHereditaryStronglyCausal() {
    if (this.getComponent().isEmpty()) {
      // ill-structured symbol table
      return false;
    } else if (this.isOutgoing() && this.getComponent().get().isAtomic()) {
      // outgoing ports of atomic components are strongly causal if their behavior specification is strongly causal
      return this.getComponent().get().isStronglyCausal();
    } else if (this.isOutgoing() && this.getComponent().get().isDecomposed()) {
      // outgoing ports of composed components are strongly causal if their composed behavior is strongly causal
      return this.isComposedStronglyCausal();
    }
    return false;
  }

  protected boolean isComposedStronglyCausal() {
    if (this.getComponent().isEmpty() || !this.getComponent().get().isDecomposed() || !this.isOutgoing()) {
      return false;
    }

    ComponentTypeSymbol owner = this.getComponent().get();

    List<List<ASTConnector>> paths = new ArrayList<>();
    paths.add(new ArrayList<>(this.getComponent().get().getAstNode().getConnectorsMatchingTarget(this.getName())));

    while(!paths.isEmpty()) {
      List<List<ASTConnector>> newPaths = new ArrayList<>();
      for (List<ASTConnector> path: paths) {
        if(path.isEmpty()) continue;

        ASTPortAccess lastSource = path.get(path.size() - 1).getSource(); //the source of the connector that is the furthest away from the current port.

        if(lastSource.isPresentPortSymbol() && lastSource.getPortSymbol().isStronglyCausal()) {
          // If any part of the path is strongly causal, this path is strongly causal, not compromising the current port's strong causality.
          // not entering this if-block also implies the instance to which this port belongs has incoming ports: if there were none, the port would be strongly causal
          continue;
        }

        boolean sourceIsOnOwningComponent = lastSource.isPresentComponentSymbol()
          && lastSource.getComponentSymbol().isPresentType()
          && lastSource.getComponentSymbol().getType().getTypeInfo().equals(owner);

        if(!lastSource.isPresentComponentSymbol() || sourceIsOnOwningComponent) {
          //  if no instance is set, this port likely belongs to `component` (the function parameter).
          //  This would mean that we have a path which is not strongly causal
          //  (whenever we encountered a strongly causal port/component, we did not follow the path further. Thus, the current path cannot contain strongly causal ports/components).
          // Therefore, we can assume we can set stronglyCausal = false here
          return false;
        }
        ComponentInstanceSymbol instance = lastSource.getComponentSymbol();
        if(!instance.isPresentType()) continue; //Incomplete symboltable. See above.
        instance.getType().getTypeInfo().getAllIncomingPorts()
          .forEach(incomingPortOfSubcomponent -> owner.getAstNode()
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


  /**
   * @return an {@code Optional} of the component type this port belongs to. The {@code Optional} is empty if the port
   * does not belong to a component type.
   */
  public Optional<ComponentTypeSymbol> getComponent() {
    if (this.getEnclosingScope() == null) {
      return Optional.empty();
    }
    if (!getEnclosingScope().isPresentSpanningSymbol()) {
      return Optional.empty();
    }
    IScopeSpanningSymbol symbol = getEnclosingScope().getSpanningSymbol();
    if (symbol instanceof ComponentTypeSymbol) {
      return Optional.of((ComponentTypeSymbol) symbol);
    } else {
      return Optional.empty();
    }
  }
}
