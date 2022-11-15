/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.SourcePosition;
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

public class ConfigurationParameterParentAssignment implements ArcBasisASTComponentTypeCoCo {

  /**
   * Used to extract the type to which instantiation arguments evaluate to.
   */
  protected final IArcTypeCalculator typeCalculator;

  /**
   * Creates this coco with an {@link ArcBasisTypeCalculator}.
   *
   * @see #ConfigurationParameterParentAssignment(IArcTypeCalculator)
   */
  public ConfigurationParameterParentAssignment() {
    this(new ArcBasisTypeCalculator());
  }

  /**
   * Creates this coco with a custom {@link IArcTypeCalculator} to extract the types to which instantiation arguments evaluate to.
   */
  public ConfigurationParameterParentAssignment(@NotNull IArcTypeCalculator typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  protected IArcTypeCalculator getTypeCalculator() {
    return this.typeCalculator;
  }

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());

    ComponentTypeSymbol component = node.getSymbol();

    if (component.isPresentParentComponent()) {
      CompTypeExpression parent = component.getParent();

      // Check CoCos
      checkParentInstantiationArgsAreNotTooMany(component, parent);
      checkParentInstantiationArgsBindAllMandatoryParams(component, parent);
      checkParentInstantiationArgsHaveCorrectTypes(component, parent);
    }
  }

  /**
   * Checks that there are not more parent configuration arguments provided than there are parent configuration parameters in the
   * component type that should be instantiated.
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected void checkParentInstantiationArgsAreNotTooMany(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    List<ASTExpression> parentArgs = comp.getParentConfiguration();
    List<VariableSymbol> paramsOfParentCompType = parent.getTypeInfo().getParameters();

    if (parentArgs.size() > paramsOfParentCompType.size()) {
      ASTExpression firstIllegalArg = parentArgs.get(paramsOfParentCompType.size());
      ASTExpression lastIllegalArg = parentArgs.get(parentArgs.size() - 1);

      Log.error(ArcError.TOO_MANY_PARENT_INSTANTIATION_ARGUMENTS.format(
          parentArgs.size(), comp.getName(), parent.printName(), paramsOfParentCompType.size()
      ), firstIllegalArg.get_SourcePositionStart(), lastIllegalArg.get_SourcePositionEnd());
    }
  }

  /**
   * Checks that there are enough parent configuration arguments provided to bind all mandatory parent configuration parameters of the
   * component type that should be instantiated.
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected void checkParentInstantiationArgsBindAllMandatoryParams(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());


    List<ASTExpression> parentArgs = comp.getParentConfiguration();
    List<VariableSymbol> paramsOfParentCompType = parent.getTypeInfo().getParameters();

    long mandatoryParamsAmount = paramsOfParentCompType.stream()
        .map(VariableSymbol::getAstNode)
        .map(ASTArcParameter.class::cast)
        .filter(param -> !param.isPresentDefault())
        .count();

    if (mandatoryParamsAmount > parentArgs.size()) {
      SourcePosition lastArgumentPos = !parentArgs.isEmpty() ?
          parentArgs.get(parentArgs.size() - 1).get_SourcePositionEnd() :
          comp.getAstNode().get_SourcePositionEnd();

      Log.error(ArcError.TOO_FEW_PARENT_INSTANTIATION_ARGUMENTS.format(
          parentArgs.size(), comp.getName(), parent.printName(), mandatoryParamsAmount
      ), lastArgumentPos);
    }
  }

  /**
   * Checks that all arguments have compatible types with the parent's configurations arguments.
   *
   * @param comp   The AST node of the component whose parent arguments should be checked.
   * @param parent The parent to be checked against
   */
  protected void checkParentInstantiationArgsHaveCorrectTypes(@NotNull ComponentTypeSymbol comp, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    List<TypeCheckResult> parentArgs = comp.getParentConfiguration().stream()
        .map(expr -> this.getTypeCalculator().deriveType(expr))
        .collect(Collectors.toList());

    List<Optional<SymTypeExpression>> parentSignature = parent.getTypeInfo()
        .getParameters().stream()
        .map(ISymbol::getName)
        .map(parent::getTypeExprOfParameter)
        .collect(Collectors.toList());

    for (int i = 0; i < Math.min(parentArgs.size(), parentSignature.size()); i++) {
      if (!parentArgs.get(i).isPresentResult()) {
        Log.debug(String.format("Checking coco '%s' is skipped for parent configuration argument No. '%d', as the type of " +
                    "the argument expression could not be calculated. Position: '%s'.",
                this.getClass().getSimpleName(), i + 1,
                comp.getParentConfiguration().get(i).get_SourcePositionStart()),
            "CoCos");

      } else if (parentArgs.get(i).isType()) {
        ASTExpression typeRefExpr = comp.getParentConfiguration().get(i);
        String correspondingParamName = parent.getTypeInfo().getParameters().get(i).getName();

        Log.error(ArcError.PARENT_CONFIG_PARAM_BINDING_IS_TYPE_REF.format(
            parentArgs.get(i).getResult().print(), correspondingParamName, i + 1,
            parent.getTypeInfo().getName(), comp.getName()
        ), typeRefExpr.get_SourcePositionStart(), typeRefExpr.get_SourcePositionEnd());

      } else if (parentSignature.get(i).isEmpty() ||
          !TypeCheck.compatible(parentSignature.get(i).get(), parentArgs.get(i).getResult())) {
        ASTExpression incompatibleArgument = comp.getParentConfiguration().get(i);
        String correspondingParamName = parent.getTypeInfo().getParameters().get(i).getName();

        Log.error(ArcError.PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH.format(
            i + 1, comp.getName(), parentArgs.get(i).getResult().print(),
            parentSignature.get(i).map(SymTypeExpression::print).orElse("UNKNOWN"), correspondingParamName,
            parent.printName()
        ), incompatibleArgument.get_SourcePositionStart(), incompatibleArgument.get_SourcePositionEnd());
      }
    }
  }
}
