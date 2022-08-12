/* (c) https://github.com/MontiCore/monticore */

package arcbasis._symboltable;

import arcbasis._ast.ASTPortDirection;
import arcbasis._ast.ASTPortDirectionIn;
import arcbasis._ast.ASTPortDirectionOut;
import arcbasis.timing.Timing;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.symbols.basicsymbols._symboltable.*;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;

public class PortSymbol extends PortSymbolTOP {

  protected ASTPortDirection direction;
  protected SymTypeExpression type;
  protected Timing timing;
  protected boolean delayed;

  /**
   * @param name the name of this port.
   */
  protected PortSymbol(String name) {
    super(name);
  }

  /**
   * @param name      the name of this port.
   * @param direction the direction of this port.
   * @param type      the type of this port.
   * @param timing    the timing of this port.
   */
  protected PortSymbol(String name, ASTPortDirection direction, SymTypeExpression type, Timing timing) {
    super(name);
    this.direction = direction;
    this.type = type;
    this.timing = timing;
    this.delayed = timing.equals(Timing.delayed()) || timing.equals(Timing.causalsync());
  }

  public ASTPortDirection getDirection() {
    return this.direction;
  }

  /**
   * @param direction the direction of this port.
   */
  public void setDirection(@NotNull ASTPortDirection direction) {
    Preconditions.checkNotNull(direction);
    this.direction = direction;
  }

  /**
   * @return {@code true}, if this is an incoming port, else {@code false}.
   */
  public boolean isIncoming() {
    return this.direction instanceof ASTPortDirectionIn;
  }

  /**
   * @return {@code true}, if this is an outgoing port, else {@code false}.
   */
  public boolean isOutgoing() {
    return this.direction instanceof ASTPortDirectionOut;
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
    this.delayed = timing.equals(Timing.delayed()) || timing.equals(Timing.causalsync());
  }

  /**
   * @return if this port is delayed (either by its timing or transitively through connections)
   */
  public boolean isDelayed() {
    return this.delayed;
  }

  /**
   * Sets this port to being delayed
   */
  public void setDelayed() {
    this.delayed = true;
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
