/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import static de.monticore.types3.SymTypeRelations.isSubTypeOf;

public class ForEachIsValid4MA extends ForEachIsValid {

  public ForEachIsValid4MA() { }

  public static final String FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE = ERROR_CODE;

  public static final String FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG = "For-each loop expression must be an array of subtype of list.";

  public static final String FOR_EACH_TYPE_MISMATCH_ERROR_CODE = "0xA0908";

  public static final String FOR_EACH_TYPE_MISMATCH_ERROR_MSG = "Type mismatch, expected '%s' but provided '%s'";

  @Override
  public void check(@NotNull ASTEnhancedForControl node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression typeOfVariable = TypeCheck3.symTypeFromAST(node.getFormalParameter().getMCType());
    SymTypeExpression typeOfExpression = TypeCheck3.typeOf(node.getExpression());

    if (typeOfExpression.isObscureType()) {
      return;
    }

    if (!typeOfExpression.isArrayType() && !isIterable(typeOfExpression)) {
      Log.error(FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE + " " + FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG,
        node.getExpression().get_SourcePositionStart(),
        node.getExpression().get_SourcePositionEnd()
      );
    } else {
      SymTypeExpression arg = typeOfExpression.isArrayType() ?
        typeOfExpression.asArrayType().getArgument() :
        typeOfExpression.asGenericType().getArgument(0);
      if (!isSubTypeOf(arg, typeOfVariable)) {
        Log.error(FOR_EACH_TYPE_MISMATCH_ERROR_CODE + " "
            + String.format(FOR_EACH_TYPE_MISMATCH_ERROR_MSG, arg.printFullName(), typeOfVariable.printFullName()),
          node.getFormalParameter().get_SourcePositionStart(),
          node.getFormalParameter().get_SourcePositionEnd()
        );
      }
    }
  }

  protected static boolean isIterable(@NotNull SymTypeExpression type) {
    Preconditions.checkNotNull(type);

    if (MCCollectionSymTypeRelations.isList(type)
      || MCCollectionSymTypeRelations.isSet(type)) {
      return true;
    }

    if (type.isGenericType()) {
      String name = type.asGenericType().getTypeConstructorFullName();
      if ((name.equals("java.lang.Iterable") || name.equals("Iterable"))
        && type.asGenericType().sizeArguments() == 1) {
        return true;
      }
    }

    for (SymTypeExpression superType : SymTypeRelations.getNominalSuperTypes(type)) {
      if (isIterable(superType)) return true;
    }

    return false;
  }
}
