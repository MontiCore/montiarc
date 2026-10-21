/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;


/**
 * A variable version of the {@link ComponentTypeSymbol}
 */
public interface IVariableArcComponentTypeSymbol {

  ComponentTypeSymbol getTypeInfo();

  /**
   * @return the default value of the parameter of the given name, if it has
   * one
   */
  Optional<ASTExpression> getParameterDefault(@NotNull String parameter);

  void setParameterDefault(@NotNull String parameter, @NotNull ASTExpression value);
}
