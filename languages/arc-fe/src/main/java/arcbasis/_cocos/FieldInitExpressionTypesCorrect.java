/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis.check.ArcBasisDeriveType;
import arcbasis.check.ArcBasisSynthesizeType;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * [RRW14a] T2: Initial values of variables must conform to their types.
 */
public class FieldInitExpressionTypesCorrect implements ArcBasisASTArcFieldCoCo {

  /** Used to check whether initialization expressions match a field's type. */
  protected final TypeCheck typeChecker;

  /**
   * Creates this coco with an TypeCheck, combined with {@link ArcBasisDeriveType} to check whether initialization
   * expressions for fields match the fields' types.
   */
  public FieldInitExpressionTypesCorrect() {
    this(new TypeCheck(new ArcBasisSynthesizeType(), new ArcBasisDeriveType(new TypeCheckResult())));
  }

  /**
   * Creates this coco with a custom {@link TypeCheck} to use to check whether initialization expressions for
   * fields match the fields' types.
   */
  public FieldInitExpressionTypesCorrect(@NotNull TypeCheck typeChecker) {
    this.typeChecker = Preconditions.checkNotNull(typeChecker);
  }

  /**
   * Creates this coco with a custom {@link IDerive} to use to check whether initialization expressions for fields
   * match the fields' types.
   */
  public FieldInitExpressionTypesCorrect(@NotNull IDerive deriverFromExpr) {
    this(new TypeCheck(new ArcBasisSynthesizeType(), Preconditions.checkNotNull(deriverFromExpr)));
  }

  @Override
  public void check(@NotNull ASTArcField astField) {
    Preconditions.checkNotNull(astField);
    Preconditions.checkArgument(astField.isPresentSymbol(), "Could not perform coco check '%s'. Perhaps you missed the " +
      "symbol table creation.", this.getClass().getSimpleName());
    Preconditions.checkNotNull(astField.getSymbol().getType(), "Could not perform coco check '%s'. Perhaps you " +
      "missed the symbol table completion.", this.getClass().getSimpleName());

    VariableSymbol fieldSym = astField.getSymbol();
    SymTypeExpression fieldType = fieldSym.getType();

    ASTExpression initExpr = astField.getInitial();
    SymTypeExpression expressionType = this.typeChecker.typeOf(initExpr);

    if(!TypeCheck.compatible(fieldType, expressionType)) {
      Log.error(ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE.format(
        fieldSym.getFullName(),
        expressionType.print(),
        fieldType.print()),
        astField.get_SourcePositionStart());
    }
  }
}
