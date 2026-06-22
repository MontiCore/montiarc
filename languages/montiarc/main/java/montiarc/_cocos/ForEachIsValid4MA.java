/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import montiarc.util.MCError;

import static de.monticore.types3.SymTypeRelations.getNominalSuperTypes;
import static de.monticore.types3.SymTypeRelations.isCompatible;

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

    Optional<SymTypeExpression> symTypeOfIterable = getIterable(typeOfExpression);

    if (symTypeOfIterable.isPresent() && symTypeOfIterable.get().isObscureType()) {
      return;
    }

    boolean isStringIteration = SymTypeRelations.isString(typeOfExpression);

    if (!typeOfExpression.isArrayType() && !isStringIteration && symTypeOfIterable.isEmpty()) {
      Log.error(MCError.FOR_EACH_EXPR_NOT_ITERABLE.format(typeOfExpression.printFullName()),
        node.getExpression().get_SourcePositionStart(),
        node.getExpression().get_SourcePositionEnd()
      );
    } else {
      SymTypeExpression typeArg;
      if (typeOfExpression.isArrayType()) {
        typeArg = typeOfExpression.asArrayType().cloneWithLessDim(1);
      } else if (isStringIteration) {
        typeArg = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.CHAR);
      } else {
        typeArg = symTypeOfIterable.orElseThrow().asGenericType().getArgument(0);
      }

      if (typeArg.isObscureType()) {
        return;
      }

      if (!isCompatible(typeOfVariable, typeArg)) {
        Log.error(MCError.FOR_EACH_TYPE_MISMATCH.format(
              typeArg.printFullName(),
              typeOfVariable.printFullName()
            ),
          node.getFormalParameter().get_SourcePositionStart(),
          node.getFormalParameter().get_SourcePositionEnd()
        );
      }
    }
  }

  protected static Optional<SymTypeExpression> getIterable(SymTypeExpression type) {
    Preconditions.checkNotNull(type);

    if (MCCollectionSymTypeRelations.isList(type)
      || MCCollectionSymTypeRelations.isSet(type)) {
      return Optional.of(type);
    }

    if (type.isGenericType()) {
      String name = type.asGenericType().getTypeConstructorFullName();
      if ((name.equals("java.lang.Iterable") || name.equals("Iterable"))
        && type.asGenericType().sizeArguments() == 1) {
        return Optional.of(type);
      }
    }

    for (SymTypeExpression superType : getNominalSuperTypes(type)) {
      Optional<SymTypeExpression> typeOfIterable = getIterable(superType);
      if (typeOfIterable.isPresent()) {
        return typeOfIterable;
      }
    }

    return Optional.empty();
  }
}
