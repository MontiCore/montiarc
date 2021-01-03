/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcTypeCheck;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
<<<<<<< HEAD
import de.monticore.types.check.TypeCheck;
=======
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

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
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    ComponentTypeSymbol component = node.getSymbol();
    List<VariableSymbol> parameters = component.getParameters();
    if (component.isPresentParentComponent()) {
      ComponentTypeSymbol parent = component.getParent();
      List<VariableSymbol> parentParameters = parent.getParameters();
      if (parameters.size() < parentParameters.size()) {
        Log.error(
          String.format(ArcError.TO_FEW_CONFIGURATION_PARAMETER.toString(), component.getFullName(),
            parentParameters.size()), node.getHead().get_SourcePositionStart());
      }
      for (int i = 0; i < Math.min(parentParameters.size(), parameters.size()); i++) {
        SymTypeExpression superParameterType = parentParameters.get(i).getType();
        SymTypeExpression paramType = parameters.get(i).getType();
        if (!ArcTypeCheck.compatible(paramType, superParameterType)) {
          Log.error(
            String.format(ArcError.CONFIGURATION_PARAMETER_TYPE_MISMATCH.toString(),
              parameters.get(i).getName(), i + 1, component.getFullName(),
              paramType.getTypeInfo().getFullName(),
              superParameterType.getTypeInfo().getFullName()),
            node.getHead().getArcParametersList().get(i).get_SourcePositionStart());
        }
        if (this.hasParameterDefaultValue(parent, i)) {
          if (!this.hasParameterDefaultValue(component, i)) {
            Log.error(
              String.format(ArcError.CONFIGURATION_PARAMETER_VALUE_MISMATCH.toString(),
                parameters.get(i).getName(), i + 1, component.getFullName()),
              node.getHead().getArcParametersList().get(i).get_SourcePositionStart());
          }
        }
      }
    }
  }

  private boolean hasParameterDefaultValue(ComponentTypeSymbol symbol, int position) {
    if (symbol.isPresentAstNode()) {
      final ASTComponentType astNode = symbol.getAstNode();
      return astNode.getHead().getArcParametersList().get(position).isPresentDefault();
    }
    return false;
  }
}