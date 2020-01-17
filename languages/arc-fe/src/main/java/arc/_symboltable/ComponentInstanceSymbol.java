/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ComponentInstanceSymbol extends ComponentInstanceSymbolTOP {

  protected ComponentSymbolLoader type;
  protected List<ASTExpression> arguments;

  /**
   * @param name the name of this component.
   */
  protected ComponentInstanceSymbol(String name) {
    super(name);
    this.arguments = new ArrayList<>();
  }

  /**
   * @return the loader of the type of this component.
   */
  public ComponentSymbolLoader getType() {
    return this.type;
  }

  /**
   * @param type the loader of the type of this component.
   */
  public void setType(@NotNull ComponentSymbolLoader type) {
    Preconditions.checkArgument(type != null);
    this.type = type;
  }

  /**
   * @return the type of this component.
   * @throws java.util.NoSuchElementException if the type of this component is not found.
   */
  public ComponentSymbol getTypeInfo() {
    return this.getType().getLoadedSymbol();
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
    Preconditions.checkArgument(argument != null);
    this.arguments.add(argument);
  }

  /**
   * @param arguments the configuration arguments to add to this component.
   * @see this#addArgument(ASTExpression)
   */
  public void addArguments(@NotNull List<ASTExpression> arguments) {
    Preconditions.checkArgument(arguments != null);
    Preconditions.checkArgument(!arguments.contains(null));
    for (ASTExpression argument : arguments) {
      this.addArgument(argument);
    }
  }
}