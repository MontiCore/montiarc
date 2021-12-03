/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
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
    if(node.getSymbol().isPresentParentComponent()) {
      Preconditions.checkArgument(node.getSymbol().getParent().getTypeInfo().getParameters().stream().allMatch(
        param -> node.getSymbol().getParent().getTypeExprOfParameter(param.getName()).isPresent()
      ));
    }

    ComponentTypeSymbol component = node.getSymbol();
    List<VariableSymbol> parameters = component.getParameters();

    if (component.isPresentParentComponent()) {
      CompTypeExpression parent = component.getParent();
      List<SymTypeExpression> parentParameters = parent.getTypeInfo().getParameters().stream()
        .map(ISymbol::getName)
        .map(parent::getTypeExprOfParameter)
        .map(paramType -> paramType.orElseThrow(IllegalStateException::new))
        .collect(Collectors.toList());

      if (parameters.size() < parentParameters.size()) {
        Log.error(
          ArcError.TOO_FEW_CONFIGURATION_PARAMETER.format(component.getFullName(),
            parentParameters.size()), node.getHead().get_SourcePositionStart());
      }

      // TypeCheck compatibility between own and parent's parameters
      for (int i = 0; i < Math.min(parentParameters.size(), parameters.size()); i++) {
        SymTypeExpression superParameterType = parentParameters.get(i);
        SymTypeExpression paramType = parameters.get(i).getType();
        if (!TypeCheck.compatible(paramType, superParameterType)) {
          Log.error(
            ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH.format(
              parameters.get(i).getName(), i + 1, component.getFullName(),
              paramType.printFullName(),
              superParameterType.printFullName()),
            node.getHead().getArcParameterList().get(i).get_SourcePositionStart());
        }

        // Check that all parameters of the parent that have default values also have default values in the child
        if (this.hasParameterDefaultValue(parent, i)) {
          if (!this.hasParameterDefaultValue(component, i)) {
            Log.error(
              ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH.format(
                parameters.get(i).getName(), i + 1, component.getFullName()),
              node.getHead().getArcParameterList().get(i).get_SourcePositionStart());
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
