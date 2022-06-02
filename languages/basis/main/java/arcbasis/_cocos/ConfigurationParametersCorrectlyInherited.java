/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This CoCo checks that a component which extends another component correctly
 * specifies the configuration parameters. Considered valid are components which
 * specify at least as many configuration parameters as the extended component.
 * Furthermore, if `n` specifies the number of parameters of the extended
 * component, the types of the first `n` parameters of the extending component
 * have to match those of it's super component. This means that the names of the
 * parameters do not have to match those of the super component. If the extended
 * component has optional parameters with default values, then all new
 * parameters of the inheriting component have to be optional as well.
 *
 * @implements [Hab16] R14: Components that inherit from a parametrized
 * component provide configuration parameters with the same types, but are
 * allowed to provide more parameters. (p.69 Lst. 3.49)
 */
public class ConfigurationParametersCorrectlyInherited implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());

    ComponentTypeSymbol component = node.getSymbol();
    List<VariableSymbol> parameters = component.getParameters();

    if (component.isPresentParentComponent()) {
      CompTypeExpression parent = component.getParent();
      List<Optional<SymTypeExpression>> parentParameters = parent.getTypeInfo().getParameters().stream()
        .map(ISymbol::getName)
        .map(parent::getTypeExprOfParameter)
        .collect(Collectors.toList());

      if (parameters.size() < parentParameters.size()) {
        Log.error(
          ArcError.TOO_FEW_INHERITED_CONFIG_PARAMS.format(component.getFullName(), parameters.size(),
            parentParameters.size(), parent.printName()), node.getHead().get_SourcePositionStart());
      }

      // TypeCheck compatibility between own and parent's parameters
      for (int i = 0; i < Math.min(parentParameters.size(), parameters.size()); i++) {
        Optional<SymTypeExpression> superParameterType = parentParameters.get(i);
        Optional<SymTypeExpression> paramType = Optional.ofNullable(parameters.get(i).getType());
        if (paramType.isEmpty() || superParameterType.isEmpty() || !TypeCheck.compatible(paramType.get(), superParameterType.get())) {
          Log.error(
            ArcError.INHERITED_CONFIG_PARAM_TYPE_MISMATCH.format(
              parameters.get(i).getName(), i + 1, component.getFullName(), paramType.map(SymTypeExpression::printFullName).orElse("UNKNOWN"),
              superParameterType.map(SymTypeExpression::printFullName).orElse("UNKNOWN"),
              superParameterType.map(t -> t.getTypeInfo().getName()).orElse("UNKNOWN"), i + 1),
            node.getHead().getArcParameterList().get(i).get_SourcePositionStart(),
            node.getHead().getArcParameterList().get(i).get_SourcePositionEnd());
        }

        // Check that all parameters of the parent that have default values also have default values in the child
        if (this.hasParameterDefaultValue(parent, i)) {
          if (!this.hasParameterDefaultValue(component, i)) {
            Log.error(
              ArcError.INHERITED_CONFIG_PARAM_MISSES_DEFAULT_VALUE.format(
                parameters.get(i).getName(), i + 1, component.getFullName(),
                  superParameterType.map(t -> t.getTypeInfo().getName()).orElse("UNKNOWN"), parent.printName(), i + 1),
              node.getHead().getArcParameterList().get(i).get_SourcePositionStart(),
              node.getHead().getArcParameterList().get(i).get_SourcePositionEnd());
          }
        }
      }
    }
  }

  private boolean hasParameterDefaultValue(@NotNull CompTypeExpression compTypeExpr, int position) {
    Preconditions.checkNotNull(compTypeExpr);
    return hasParameterDefaultValue(compTypeExpr.getTypeInfo(), position);
  }

  private boolean hasParameterDefaultValue(@NotNull ComponentTypeSymbol symbol, int position) {
    Preconditions.checkNotNull(symbol);
    if (symbol.isPresentAstNode()) {
      final ASTComponentType astNode = symbol.getAstNode();
      return astNode.getHead().getArcParameterList().get(position).isPresentDefault();
    }
    return false;
  }
}
