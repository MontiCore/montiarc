/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * [RRW14a] T2: Initial values of variables must conform to their types.
 */
public class FieldInitTypeFits implements ArcBasisASTArcFieldCoCo {

  private static final String LOG_NAME = "FieldInitTypeFits";

  @Override
  public void check(@NotNull ASTArcField astField) {
    Preconditions.checkNotNull(astField);
    Preconditions.checkArgument(astField.isPresentSymbol());
    Preconditions.checkNotNull(astField.getSymbol());
    Preconditions.checkNotNull(astField.getSymbol().getType());
    Log.trace(() -> "Start the context-condition check. ", LOG_NAME);

    if (astField.getSymbol().getType().isObscureType()) {
      Log.debug(() -> "Skip the context-condition check. " +
          "The symtype of the field's type is obscure. " +
          "An error should have already been logged.",
        LOG_NAME
      );
      return;
    }

    VariableSymbol field = astField.getSymbol();
    SymTypeExpression fieldSymType = field.getType();

    ASTExpression expr = astField.getInitial();
    SymTypeExpression exprSymType = TypeCheck3.typeOf(expr, fieldSymType);

    if (exprSymType.isObscureType()) {
      Log.debug(() -> "Skip the context-condition check. " +
          "The symtype of the initializer expression is obscure. " +
          "An error should have already been logged.",
        LOG_NAME
      );
      return;
    }
    if (!SymTypeRelations.isCompatible(fieldSymType, exprSymType)) {
      Log.error(ArcError.FIELD_INIT_TYPE_MISMATCH.format(
          fieldSymType.printFullName(), exprSymType.printFullName()
        ),
        astField.get_SourcePositionStart(),
        astField.get_SourcePositionEnd());
    }
    Log.trace(() -> "Finished the context-condition check.", LOG_NAME);
  }
}
