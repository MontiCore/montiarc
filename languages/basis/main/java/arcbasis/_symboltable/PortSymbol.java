/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.*;
import arcbasis.timing.Timing;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.symbols.basicsymbols._symboltable.*;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;

public class PortSymbol extends PortSymbolTOP {

  protected SymTypeExpression type;
  protected Timing timing;
  protected Boolean delayed = null;
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
  public Timing getTiming() {
    Preconditions.checkState(this.timing != null, "Type of Port '%s' has not been set. Did you " +
        "forget to run the symbol table completer?", this.getName());
    return this.timing;
  }

  /**
   * @param timing the timing of this port.
   */
  public void setTiming(@NotNull Timing timing) {
    Preconditions.checkNotNull(timing);
    this.timing = timing;
  }

  /**
   * If this port is delayed. This property is loaded lazily on demand.
   * <br><br>
   * An outgoing port is delayed iff:
   * <ol>
   *   <li>its timing is either {@link Timing#DELAYED} or {@link Timing#CAUSALSYNC}, or</li>
   *   <li>if it is the target of a port forward, where the source is delayed</li>
   * </ol>
   * Incoming ports are never delayed.
   *
   * @return if this port is delayed
   *
   * @see #computeDelay()
   */
  public boolean isDelayed() {
    if (this.delayed == null) {
      computeDelay();
    }
    return this.delayed;
  }

  /**
   * Used in lazy-loading the delay.
   * <br>
   * If the owning component is atomic, checks the ports explicitly specified timing.
   * Else, checks whether the port is the target of a port forward from a delayed port.
   */
  protected void computeDelay() {
    if (this.getComponent().isEmpty()) { // ill-structured symbol table
      this.delayed = false;
    } else if (this.getComponent().get().isAtomic()) { // atomic component
      this.delayed = this.getTiming().equals(Timing.DELAYED) || this.getTiming().equals(Timing.CAUSALSYNC);
    } else { // decomposed component
      if (this.isIncoming()) {
        this.delayed = false;
        return;
      }
      Optional<ASTPortAccess> portForwardSource = this.getComponent().get().getAstNode()
        .getConnectorsMatchingTarget(this.getName())
        .stream().findFirst().map(ASTConnectorTOP::getSource);
      this.delayed = portForwardSource.map(source -> {
        if (!source.isPresentPortSymbol()) {
          Log.trace("Tried to compute delay for port " + this.getFullName() +
            ", but source port " + source.getQName() + " has no port symbol.", "MontiArc");
          return false;
        } else {
          return source.getPortSymbol().isDelayed();
        }
      }).orElse(false);
    }
  }

  /**
   * If this port is strongly causal. This property is loaded lazily on demand.
   * <br><br>
   * An outgoing port of a decomposed component is strongly causal iff:
   * <ol>
   *   <li>it is delayed (see {@link #isDelayed()}, or</li>
   *   <li>all paths from an incoming port of its owning component to it contain at least one strongly causal port</li>
   * </ol>
   * Whether an outgoing port on an atomic component is strongly causal depends purely on it being delayed.
   * <br>
   * Incoming ports are never strongly causal.
   *
   * @return if this port is strongly causal with regard to any input port on its owning component
   *
   * @see #computeStronglyCausal()
   */
  public boolean isStronglyCausal() {
    if(this.stronglyCausal == null) {
      if(this.getComponent().isPresent()) {
        computeStronglyCausal();
      } else {
        this.simpleDetermineStronglyCausal();
        if(this.stronglyCausal == null) {
          this.stronglyCausal = false;
        }
      }
    }

    return this.stronglyCausal;
  }

  /**
   * Used in lazy-loading the strongly causal property.
   * <br>
   * First performs simple checks (see {@link #simpleDetermineStronglyCausal()}).
   * <br>
   * If this does not yield a result, tries to find a path from this port to an incoming port
   * of its owning component that has no delayed (and thus no other strongly causal) ports on it.
   */
  protected void computeStronglyCausal() {
    this.simpleDetermineStronglyCausal();
    if(this.getComponent().isEmpty() || this.stronglyCausal != null) {
      return;
    }

    ComponentTypeSymbol owner = this.getComponent().get();

    List<List<ASTConnector>> paths = new ArrayList<>();
    paths.add(new ArrayList<>(owner.getAstNode().getConnectorsMatchingTarget(this.getName())));

    buildPaths:
    while(!paths.isEmpty()) {
      List<List<ASTConnector>> newPaths = new ArrayList<>();
      for (List<ASTConnector> path: paths) {
        if(path.isEmpty()) continue;

        ASTPortAccess lastSource = path.get(path.size() - 1).getSource(); //the source of the connector that is the furthest away from the current port.

        if(lastSource.isPresentPortSymbol() && lastSource.getPortSymbol().isStronglyCausal()) {
          // If any part of the path is strongly causal, this path is strongly causal, not compromising the current port's strong causality.
          // not entering this if-block also implies  the instance to which this port belongs has incoming ports: if there were none, the port would be strongly causal
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
          this.stronglyCausal = false;
          break buildPaths;
        }
        ComponentInstanceSymbol instance = lastSource.getComponentSymbol();
        if(!instance.isPresentType()) continue; //Incomplete symboltable. See above.
        instance.getType().getTypeInfo().getAllIncomingPorts().forEach(incomingPortOfSubcomponent -> {
          owner.getAstNode().getConnectorsMatchingTarget(instance.getName() + "." + incomingPortOfSubcomponent.getName()).forEach(connector -> {
            List<ASTConnector> newPath = new ArrayList<>(path);
            newPath.add(connector);
            newPaths.add(newPath);
          });
        });
      }
      paths = newPaths;
    }

    if(this.stronglyCausal == null) {
      // if we reach this point and have not set a value,
      // we could not find any path contradicting strong causality
      this.stronglyCausal = true;
    }
  }

  /**
   * Performs simple checks to determine whether this port is strongly causal.
   * <ol>
   *   <li>If this port is delayed, it's strongly causal</li>
   *   <li>If this port is incoming, it's not strongly causal</li>
   *   <li>If this port has no owning component, it's not strongly causal (malformed symboltable)</li>
   *   <li>If this ports owning component has no incoming ports, it's strongly causal</li>
   *   <li>If this ports owning component is atomic, this port is strongly causal iff it is delayed</li>
   * </ol>
   */
  protected void simpleDetermineStronglyCausal() {
    if (this.isDelayed()) { // Delayed implies strongly causal
      this.stronglyCausal = true;
    } else if (this.isIncoming()) { // incoming ports are never strongly causal
      this.stronglyCausal = false;
    } else if (this.getComponent().isEmpty()) { // ill-structured symbol table
      this.stronglyCausal = false;
    } else {
      ComponentTypeSymbol owner = this.getComponent().get();
      if(owner.getAllIncomingPorts().isEmpty()) {
        this.stronglyCausal = true;
      } else if(owner.isAtomic()) {
        this.stronglyCausal = this.isDelayed();
      }
    }
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
