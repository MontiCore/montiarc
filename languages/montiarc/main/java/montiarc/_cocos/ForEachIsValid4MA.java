/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types3.SymTypeRelations.getNominalSuperTypes;
import static de.monticore.types3.SymTypeRelations.isCompatible;
import static java.lang.String.format;

public class ForEachIsValid4MA extends ForEachIsValid {

  public ForEachIsValid4MA() { }

  public static final String FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE = ERROR_CODE;

  public static final String FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG =
    "For-each loop expression must be iterable (e.g., an array or a list). Instead, the type is '%s'";

  public static final String FOR_EACH_TYPE_MISMATCH_ERROR_CODE = "0xA0908";

  public static final String FOR_EACH_TYPE_MISMATCH_ERROR_MSG = "Type mismatch, expected '%s' but provided '%s'";

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

    if (!typeOfExpression.isArrayType() && symTypeOfIterable.isEmpty()) {
      Log.error(FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE + " "
          + format(FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG, typeOfExpression.printFullName()),
        node.getExpression().get_SourcePositionStart(),
        node.getExpression().get_SourcePositionEnd()
      );
    } else {
      SymTypeExpression typeArg = typeOfExpression.isArrayType() ?
        typeOfExpression.asArrayType().cloneWithLessDim(1) :
        symTypeOfIterable.orElseThrow().asGenericType().getArgument(0);

      if (typeArg.isObscureType()) {
        return;
      }

      if (!isCompatible(typeOfVariable, typeArg)) {
        Log.error(FOR_EACH_TYPE_MISMATCH_ERROR_CODE + " " +
            format(FOR_EACH_TYPE_MISMATCH_ERROR_MSG,
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
