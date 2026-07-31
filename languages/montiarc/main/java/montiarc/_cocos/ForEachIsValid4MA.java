/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.SymTypeRelationsOfIterables;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.util.MCError;

import java.util.Optional;

import static de.monticore.types3.SymTypeRelations.isCompatible;
import static java.lang.String.format;

public class ForEachIsValid4MA extends ForEachIsValid {

  public ForEachIsValid4MA() { }

  @Override
  public void check(ASTEnhancedForControl node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression typeOfVariable = TypeCheck3.symTypeFromAST(node.getFormalParameter().getMCType());
    SymTypeExpression typeOfExpression = TypeCheck3.typeOf(node.getExpression());

    if (typeOfVariable.isObscureType() || typeOfExpression.isObscureType()) {
      return;
    }

    Optional<SymTypeExpression> symTypeOfIteration;
    if (SymTypeRelations.isStringOrSubType(typeOfExpression)) {
      symTypeOfIteration = Optional.of(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.CHAR));
    } else {
      symTypeOfIteration = SymTypeRelationsOfIterables.getIterationType(typeOfExpression);
    }

    if (symTypeOfIteration.isEmpty()) {
      Log.error(FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE + " "
          + format(FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG, typeOfExpression.printFullName()),
        node.getExpression().get_SourcePositionStart(),
        node.getExpression().get_SourcePositionEnd()
      );
      return;
    }

    if (symTypeOfIteration.get().isObscureType()) {
      return;
    }

    if (!isCompatible(typeOfVariable, symTypeOfIteration.get())) {
      Log.error(MCError.FOR_EACH_TYPE_MISMATCH.format(
          symTypeOfIteration.get().printFullName(),
          typeOfVariable.printFullName()
        ),
        node.getFormalParameter().get_SourcePositionStart(),
        node.getFormalParameter().get_SourcePositionEnd()
      );
    }
  }
}
