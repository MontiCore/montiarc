/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
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
 * [RRW14a] T2: Initial values of variables must conform to their types.
 */
public class FieldInitExpressionTypesCorrect implements ArcBasisASTArcFieldCoCo {

  /**
   * Used to extract the type to which initialization expressions of fields evaluate.
   */
  protected final IArcTypeCalculator tc;

  protected final ITypeRelations tr;

  public FieldInitExpressionTypesCorrect(@NotNull IArcTypeCalculator tc, @NotNull ITypeRelations tr) {
    this.tc = Preconditions.checkNotNull(tc);
    this.tr = Preconditions.checkNotNull(tr);
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
    TypeCheckResult expressionType = this.tc.deriveType(initExpr);

    if (!expressionType.isPresentResult()) {
      Log.debug(String.format("Checking coco '%s' is skipped for field '%s', as the type of the initialization " +
            "expression could not be calculated. Position: '%s'.",
          this.getClass().getSimpleName(), astField.getName(), astField.get_SourcePositionStart()),
        "CoCos");
    } else if (expressionType.isType()) {
      Log.error(ArcError.FIELD_INITIALIZATION_IS_TYPE_REF.format(
          expressionType.getResult().print(),
          fieldSym.getName()),
        astField.get_SourcePositionStart(), astField.get_SourcePositionEnd());
    } else if (!tr.compatible(fieldType, expressionType.getResult())) {
      Log.error(ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE.format(
          fieldSym.getName(),
          expressionType.getResult().print(),
          fieldType.print()),
        astField.get_SourcePositionStart(), astField.get_SourcePositionEnd());
    }
  }
}
