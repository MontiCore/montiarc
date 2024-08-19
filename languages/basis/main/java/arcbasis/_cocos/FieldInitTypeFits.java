/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis.check.IArcTypeCalculator;
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

  @Override
  public void check(@NotNull ASTArcField astField) {
    Preconditions.checkNotNull(astField);
    Preconditions.checkArgument(astField.isPresentSymbol());

    if (astField.getSymbol().getType() == null) {
      Log.debug("Could not perform coco check '" + this.getClass().getSimpleName() + "', due to missing type.",
          this.getClass().getSimpleName());
      return;
    }

    VariableSymbol fieldSym = astField.getSymbol();
    SymTypeExpression fieldType = fieldSym.getType();

    ASTExpression initExpr = astField.getInitial();
    SymTypeExpression expressionType = TypeCheck3.typeOf(initExpr, fieldType);

    if (expressionType.isObscureType()) {
      Log.debug(astField.get_SourcePositionStart()
          + ": Skip execution of CoCo, could not calculate the field's type.",
        this.getClass().getCanonicalName()
      );
    } else if (!SymTypeRelations.isCompatible(fieldType, expressionType)) {
      Log.error(ArcError.FIELD_INIT_TYPE_MISMATCH.format(fieldType.printFullName(), expressionType.printFullName()),
        astField.get_SourcePositionStart(), astField.get_SourcePositionEnd()
      );
    }
  }
}
