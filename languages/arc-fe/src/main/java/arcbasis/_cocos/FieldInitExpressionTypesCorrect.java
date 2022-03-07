/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcField;
import arcbasis.check.ArcBasisDeriveType;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * [RRW14a] T2: Initial values of variables must conform to their types.
 */
public class FieldInitExpressionTypesCorrect implements ArcBasisASTArcFieldCoCo {

  /** Used to extract the type to which initialization expressions of fields evaluate. */
  protected final IDerive typeDeriver;

  /**
   * Initializes the used {@link IDerive} to be a {@link ArcBasisDeriveType}.
   * @see #FieldInitExpressionTypesCorrect(IDerive)
   */
  public FieldInitExpressionTypesCorrect() {
    this(new ArcBasisDeriveType());
  }

  /**
   * Creates this coco with a custom {@link IDerive} used to extract the type to which initialization expressions of
   * fields evaluate.
   */
  public FieldInitExpressionTypesCorrect(@NotNull IDerive typeDeriver) {
    this.typeDeriver = Preconditions.checkNotNull(typeDeriver);
  }

  /**
   * Checks to which type the {@code expression} evaluates to and returns it, wrapped in an optional. If the expression
   * does not evaluate to a type, e.g., because it is malformed, the returned optional is empty.
   */
  protected Optional<SymTypeExpression> extractTypeOf(@NotNull ASTExpression expression) {
    Preconditions.checkNotNull(expression);

    this.typeDeriver.init();
    expression.accept(this.typeDeriver.getTraverser());
    return this.typeDeriver.getResult();
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
    Optional<SymTypeExpression> expressionType = this.extractTypeOf(initExpr);

    if(expressionType.isPresent() && !TypeCheck.compatible(fieldType, expressionType.get())) {
      Log.error(ArcError.FIELD_INIT_EXPRESSION_WRONG_TYPE.format(
        fieldSym.getFullName(),
        expressionType.get().print(),
        fieldType.print()),
        astField.get_SourcePositionStart(), astField.get_SourcePositionEnd());
    }
    if(!expressionType.isPresent()) {
      Log.debug(String.format("Checking coco '%s' is skipped for field '%s', as the type of the initialization " +
            "expression could not be calculated. Position: '%s'.",
            this.getClass().getSimpleName(), astField.getName(), astField.get_SourcePositionStart()),
        "CoCos");
    }
  }
}
