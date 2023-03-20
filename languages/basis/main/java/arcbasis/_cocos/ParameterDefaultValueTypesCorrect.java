/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.ITypeRelations;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * [Wor16] MT7: Default values of configuration parameters must conform to the parameters' types.
 */
public class ParameterDefaultValueTypesCorrect implements ArcBasisASTArcParameterCoCo {

  /**
   * Used to extract the type to which parameter's default values evaluate to.
   */
  protected final IArcTypeCalculator tc;

  protected final ITypeRelations tr;

  public ParameterDefaultValueTypesCorrect(@NotNull IArcTypeCalculator tc, @NotNull ITypeRelations tr) {
    this.tc = Preconditions.checkNotNull(tc);
    this.tr = Preconditions.checkNotNull(tr);
  }

  @Override
  public void check(@NotNull ASTArcParameter astParam) {
    Preconditions.checkNotNull(astParam);
    Preconditions.checkArgument(astParam.isPresentSymbol());

    if (astParam.getSymbol().getType() == null) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
          this.getClass().getSimpleName());
      return;
    }

    VariableSymbol paramSym = astParam.getSymbol();
    SymTypeExpression paramType = paramSym.getType();

    if (astParam.isPresentDefault()) {
      ASTExpression defaultExpr = astParam.getDefault();
      TypeCheckResult expressionType = this.tc.deriveType(defaultExpr);

      if(!expressionType.isPresentResult()) {
        Log.debug(String.format("Checking coco '%s' is skipped for parameter '%s', as the type of the its default " +
              "value expression could not be calculated. Position: '%s'.",
            this.getClass().getSimpleName(), astParam.getName(), astParam.get_SourcePositionStart()),
          "CoCos");

      } else if (expressionType.isType()) {
        Log.error(ArcError.PARAM_DEFAULT_VALUE_IS_TYPE_REF.format(
            expressionType.getResult().print(),
            paramSym.getName()),
            astParam.get_SourcePositionStart(), astParam.get_SourcePositionEnd());

      } else if (!tr.compatible(paramType, expressionType.getResult())) {
        Log.error(ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE.format(
          paramSym.getName(),
          expressionType.getResult().print(),
          paramType.print()),
          astParam.get_SourcePositionStart(), astParam.get_SourcePositionEnd());
      }
    }
  }
}
