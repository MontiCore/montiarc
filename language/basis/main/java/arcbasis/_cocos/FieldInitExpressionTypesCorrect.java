/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.IArcTypeCalculator;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * [RRW14a] T2: Initial values of variables must conform to their types.
 */
public class FieldInitExpressionTypesCorrect implements ArcBasisASTArcFieldCoCo {

  /**
   * Used to extract the type to which initialization expressions of fields evaluate.
   */
  protected final IArcTypeCalculator typeCalculator;

  /**
   * Initializes the used {@link IArcTypeCalculator} to be a {@link ArcBasisTypeCalculator}.
   *
   * @see #FieldInitExpressionTypesCorrect(IArcTypeCalculator)
   */
  public FieldInitExpressionTypesCorrect() {
    this(new ArcBasisTypeCalculator());
  }

  /**
   * Creates this coco with a custom {@link IArcTypeCalculator} used to extract the type to which initialization expressions of
   * fields evaluate.
   */
  public FieldInitExpressionTypesCorrect(@NotNull IArcTypeCalculator typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  protected IArcTypeCalculator getTypeCalculator() {
    return this.typeCalculator;
  }

  @Override
  public void check(@NotNull ASTArcField astField) {
    Preconditions.checkNotNull(astField);
    Preconditions.checkArgument(astField.isPresentSymbol(), "Could not perform coco check '%s'. Perhaps you missed the " +
      "symbol table creation.", this.getClass().getSimpleName());

    if (astField.getSymbol().getType() == null) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
          this.getClass().getSimpleName());
      return;
    }

    VariableSymbol fieldSym = astField.getSymbol();
    SymTypeExpression fieldType = fieldSym.getType();

    ASTExpression initExpr = astField.getInitial();
    TypeCheckResult expressionType = this.getTypeCalculator().deriveType(initExpr);

    if (!expressionType.isPresentCurrentResult()) {
      Log.debug(String.format("Checking coco '%s' is skipped for field '%s', as the type of the initialization " +
            "expression could not be calculated. Position: '%s'.",
          this.getClass().getSimpleName(), astField.getName(), astField.get_SourcePositionStart()),
        "CoCos");
    } else if (expressionType.isType()) {
      Log.error(ArcError.FIELD_INITIALIZATION_IS_TYPE_REF.format(
          expressionType.getCurrentResult().print(),
          fieldSym.getName()),
        astField.get_SourcePositionStart(), astField.get_SourcePositionEnd());
    } else if (!TypeCheck.compatible(fieldType, expressionType.getCurrentResult())) {
      Log.error(ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE.format(
          fieldSym.getName(),
          expressionType.getCurrentResult().print(),
          fieldType.print()),
        astField.get_SourcePositionStart(), astField.get_SourcePositionEnd());
    }
  }
}
