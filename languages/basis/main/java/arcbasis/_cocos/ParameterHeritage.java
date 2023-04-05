/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.ITypeRelations;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParameterHeritage implements ArcBasisASTComponentTypeCoCo {

  protected final IArcTypeCalculator tc;

  protected final ITypeRelations tr;

  public ParameterHeritage(@NotNull IArcTypeCalculator tc, @NotNull ITypeRelations tr) {
    this.tc = Preconditions.checkNotNull(tc);
    this.tr = Preconditions.checkNotNull(tr);
  }

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();

    if (component.isPresentParent()) {
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

      Log.error(ArcError.TOO_MANY_ARGUMENTS_HERITAGE.format(
          paramsOfParentCompType.size(), parentArgs.size()
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

      Log.error(ArcError.TOO_FEW_ARGUMENTS_HERITAGE.format(
        mandatoryParamsAmount, parentArgs.size()
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
        .map(this.tc::deriveType)
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

        Log.error(ArcError.TYPE_REF_NO_EXPRESSION2.toString(),
          typeRefExpr.get_SourcePositionStart(), typeRefExpr.get_SourcePositionEnd()
        );

      } else if (parentSignature.get(i).isEmpty() ||
          !tr.compatible(parentSignature.get(i).get(), parentArgs.get(i).getResult())) {
        ASTExpression incompatibleArgument = comp.getParentConfiguration().get(i);

        Log.error(ArcError.COMP_ARG_HERITAGE_TYPE_MISMATCH.format(
          parentSignature.get(i).map(SymTypeExpression::print).orElse("UNKNOWN"),
            parentArgs.get(i).getResult().print()),
          incompatibleArgument.get_SourcePositionStart(), incompatibleArgument.get_SourcePositionEnd()
        );
      }
    }
  }
}
