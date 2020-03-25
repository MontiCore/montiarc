/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import java.util.ArrayList;
import java.util.List;

import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.CommonSymbol;
import montiarc.helper.SymbolPrinter;

/**
 * Represents an instance of a component.
 *
 */
public class ComponentInstanceSymbol extends CommonSymbol {

  public static final ComponentInstanceKind KIND = new ComponentInstanceKind();

  private final ComponentSymbolReference componentType;

  /**
   * List of configuration arguments.
   */
  private List<ASTExpression> configArgs = new ArrayList<>();

  /**
   * Constructor for de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol
   *
   * @param name
   * @param componentType the referenced component definition
   */
  public ComponentInstanceSymbol(String name, ComponentSymbolReference componentType) {
    super(name, KIND);
    this.componentType = componentType;
  }

  /**
   * @return componentType
   */
  public ComponentSymbolReference getComponentType() {
    return this.componentType;
  }

  /**
   * @return List of configuration arguments
   */
  public List<ASTExpression> getConfigArguments() {
    return this.configArgs;
  }

  /**
   * @param cfg configuration argument to add
   */
  public void addConfigArgument(ASTExpression cfg) {
    this.configArgs.add(cfg);
  }

  /**
   * @param cfgList configuration arguments to set
   */
  public void setConfigArgs(List<ASTExpression> configArgs) {
    this.configArgs = configArgs;
  }

  @Override
  public String toString() {
    return SymbolPrinter.printComponentInstance(this);
  }
}
