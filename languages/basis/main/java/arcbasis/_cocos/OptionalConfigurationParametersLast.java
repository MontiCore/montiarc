/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Asserts that the optional configuration parameters of a component type always succeed the mandatory ones. This is
 * necessary to ensure a consistent signature for the instantiation of the component type.
 *
 * Implements [Wor16] MR4. Citation:
 * "Component types may define default values for each configuration parameter [...]. However, if any configuration
 * parameter defines a default value, all following parameters must define a default value as well. Otherwise, assigning
 * arguments at component instantiation requires more complex argument mapping semantics that might become confusing or
 * even non-deterministic."
 */
public class OptionalConfigurationParametersLast implements ArcBasisASTComponentTypeCoCo {
  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());

    ComponentTypeSymbol comp = node.getSymbol();
    List<ASTArcParameter> parameters = parameterASTsOf(comp);

    boolean alreadySawOptionalParameter = false;
    Optional<Integer> firstOptionalParamPosition = Optional.empty();  // measured in [0; length-1]

    for(int i = 0; i < parameters.size(); i++) {
      ASTArcParameter param = parameters.get(i);
      if(isMandatory(param) && alreadySawOptionalParameter) {
        Log.error(ArcError.OPTIONAL_CONFIG_PARAMS_LAST.format(param.getName(), i + 1, comp.getFullName(),
          parameters.get(firstOptionalParamPosition.get()).getName(), 1 + firstOptionalParamPosition.get()),
          param.get_SourcePositionStart(), param.get_SourcePositionEnd());
      } else if(isOptional(param)) {
        alreadySawOptionalParameter = true;
        firstOptionalParamPosition = Optional.of(i);
      }
    }
  }

  /**
   * @return Whether the parameter is optional, i.e. it has a default value.
   */
  protected static boolean isOptional(@NotNull ASTArcParameter param) {
    Preconditions.checkNotNull(param);
    return param.isPresentDefault();
  }

  /**
   * @return Whether the parameter is mandatory, i.e. it has no default value.
   */
  protected static boolean isMandatory(@NotNull ASTArcParameter param) {
    Preconditions.checkNotNull(param);
    return !param.isPresentDefault();
  }

  /**
   * After asserting that the symbols of the component's parameters all have {@link ASTArcParameter} nodes attached,
   * this method returns this list of ASTArcParameters. When the assertions fail, an error is logged.
   */
  protected static List<ASTArcParameter> parameterASTsOf(@NotNull ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);

    List<VariableSymbol> paramSyms = comp.getParameters();

    paramSyms.forEach(p -> Preconditions.checkArgument(p.isPresentAstNode(),
      "Parameter '%s' of component type '%s' has no AST node attached. Thus can not check CoCo '%s'.",
      p.getName(), comp.getFullName(), OptionalConfigurationParametersLast.class.getSimpleName())
    );

    paramSyms.forEach( p -> Preconditions.checkArgument(p.getAstNode() instanceof ASTArcParameter,
      "The AST node attached to parameter '%s' of component type '%s' is not an '%s'. Thus can not check " +
        "coco '%s'.", p.getName(), comp.getFullName(),OptionalConfigurationParametersLast.class.getSimpleName())
    );

    return paramSyms.stream()
      .map(VariableSymbol::getAstNode)
      .map(ast -> (ASTArcParameter) ast)
      .collect(Collectors.toList());
  }
}
