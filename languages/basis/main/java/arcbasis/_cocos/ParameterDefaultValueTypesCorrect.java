/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
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
  protected final IArcTypeCalculator typeCalculator;

  protected IArcTypeCalculator getTypeCalculator() {
    return this.typeCalculator;
  }

  /**
   * Creates this coco with an {@link ArcBasisTypeCalculator}.
   * @see #ParameterDefaultValueTypesCorrect(IArcTypeCalculator)
   */

  public ParameterDefaultValueTypesCorrect() {
    this(new ArcBasisTypeCalculator());
  }

  /**
   * Creates this coco with a custom {@link IArcTypeCalculator} to extract the types to which parameter's default values evaluate
   * to.
   */

  public ParameterDefaultValueTypesCorrect(@NotNull IArcTypeCalculator typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  @Override
  public void check(@NotNull ASTArcParameter astParam) {
    Preconditions.checkNotNull(astParam);
    Preconditions.checkArgument(astParam.isPresentSymbol(), "Could not perform coco check '%s'. Perhaps you missed " +
      "the symbol table creation.", this.getClass().getSimpleName());

    if (astParam.getSymbol().getType() == null) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
          this.getClass().getSimpleName());
      return;
    }

    VariableSymbol paramSym = astParam.getSymbol();
    SymTypeExpression paramType = paramSym.getType();

    if (astParam.isPresentDefault()) {
      ASTExpression defaultExpr = astParam.getDefault();
      TypeCheckResult expressionType = this.getTypeCalculator().deriveType(defaultExpr);

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

      } else if (!TypeCheck.compatible(paramType, expressionType.getResult())) {
        Log.error(ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE.format(
          paramSym.getName(),
          expressionType.getResult().print(),
          paramType.print()),
          astParam.get_SourcePositionStart(), astParam.get_SourcePositionEnd());
      }
    }
  }
}
