/* (c) https://github.com/MontiCore/monticore */

package arcbasis._symboltable;

import arcbasis._ast.ASTPortDirection;
import arcbasis._ast.ASTPortDirectionIn;
import arcbasis._ast.ASTPortDirectionOut;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.symbols.basicsymbols._symboltable.*;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class PortSymbol extends PortSymbolTOP {

  protected ASTPortDirection direction;
  protected SymTypeExpression type;

  /**
   * @param name the name of this port.
   */
  protected PortSymbol(String name) {
    super(name);
  }

  /**
   * @param name the name of this port.
   * @param direction the direction of this port.
   * @param type the type of this port.
   */
  protected PortSymbol(String name, ASTPortDirection direction, SymTypeExpression type) {
    super(name);
    this.direction = direction;
    this.type = type;
  }

  public ASTPortDirection getDirection() {
    return this.direction;
  }

  /**
   * @param direction the direction of this port.
   */
  public void setDirection(@NotNull ASTPortDirection direction) {
    Preconditions.checkArgument(direction != null);
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

  /**
   * @return the type for the type of this port.
   */
  public SymTypeExpression getType() {
    return this.type;
  }

  /**
   * @param type the loader for the type of this port.
   */
  public void setType(@NotNull SymTypeExpression type) {
    Preconditions.checkArgument(type != null);
    this.type = type;
  }

  /**
   * @return the type of this port.
   * @throws java.util.NoSuchElementException if the port type is not found.
   */
  public TypeSymbol getTypeInfo() {
    return this.getType().getTypeInfo();
  }

  /**
   * @return an {@code Optional} of the component type this port belongs to. The {@code Optional}
   * is empty if the port does not belong to a component type.
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
