/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.*;

public class ComponentInstanceSymbol extends ComponentInstanceSymbolTOP {

  protected CompTypeExpression type;
  protected List<ASTExpression> arguments;

  /**
   * @param name the name of this component.
   */
  public ComponentInstanceSymbol(String name) {
    super(name);
    this.arguments = new ArrayList<>();
  }

  /**
   * @return the type of this component
   */
  public CompTypeExpression getType() {
    Preconditions.checkState(this.type != null,
      "Type of component instance '%s' has not been set yet.", this.getFullName());
    return this.type;
  }

  /**
   * @return whether the type for this component instance has already been set.
   */
  public boolean isPresentType() {
    return this.type != null;
  }

  /**
   * @param type the type of this component
   */
  public void setType(CompTypeExpression type) {
    this.type = type;
  }

  /**
   * @return a {@code List} of the configuration arguments of this component.
   */
  public List<ASTExpression> getArguments() {
    return this.arguments;
  }

  /**
   * @param argument the configuration argument to add to this component.
   */
  public void addArgument(@NotNull ASTExpression argument) {
    Preconditions.checkNotNull(argument);
    this.arguments.add(argument);
  }

  /**
   * @param arguments the configuration arguments to add to this component.
   * @see this#addArgument(ASTExpression)
   */
  public void addArguments(@NotNull List<ASTExpression> arguments) {
    Preconditions.checkNotNull(arguments);
    Preconditions.checkArgument(!arguments.contains(null));
    for (ASTExpression argument : arguments) {
      this.addArgument(argument);
    }
  }
}