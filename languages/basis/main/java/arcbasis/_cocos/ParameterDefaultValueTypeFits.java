/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * [Wor16] MT7: Default values of configuration parameters must conform to the parameters' types.
 */
public class ParameterDefaultValueTypeFits implements ArcBasisASTArcParameterCoCo {

  /**
   * Used to extract the type to which parameter's default values evaluate to.
   */
  protected final IArcTypeCalculator tc;


  public ParameterDefaultValueTypeFits(@NotNull IArcTypeCalculator tc) {
    this.tc = Preconditions.checkNotNull(tc);
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
      SymTypeExpression expressionType = this.tc.typeOf(defaultExpr);

      if (expressionType.isObscureType()) {
        Log.debug(astParam.get_SourcePositionStart()
            + ": Skip execution of CoCo, could not calculate the parameter's type.",
          this.getClass().getCanonicalName()
        );
      } else if (!SymTypeRelations.isCompatible(paramType, expressionType)) {
        Log.error(ArcError.PARAM_DEFAULT_TYPE_MISMATCH.format(paramType.printFullName(), expressionType.printFullName()),
          astParam.get_SourcePositionStart(), astParam.get_SourcePositionEnd()
        );
      }
    }
  }
}
