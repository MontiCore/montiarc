/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
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
 * [Wor16] MT7: Default values of configuration parameters must conform to the parameters' types.
 */
public class ParameterDefaultValueTypesCorrect implements ArcBasisASTArcParameterCoCo {

  /**
   * Used to extract the type to which parameter's default values evaluate to.
   */
  protected final IDerive typeDeriver;


  /**
   * Creates this coco with an {@link ArcBasisDeriveType}.
   * @see #ParameterDefaultValueTypesCorrect(IDerive)
   */

  public ParameterDefaultValueTypesCorrect() {
    this(new ArcBasisDeriveType());
  }

  /**
   * Creates this coco with a custom {@link IDerive} to extract the types to which parameter's default values evaluate
   * to.
   */

  public ParameterDefaultValueTypesCorrect(@NotNull IDerive typeDeriver) {
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
  public void check(@NotNull ASTArcParameter astParam) {
    Preconditions.checkNotNull(astParam);
    Preconditions.checkArgument(astParam.isPresentSymbol(), "Could not perform coco check '%s'. Perhaps you missed " +
      "the symbol table creation.", this.getClass().getSimpleName());
    Preconditions.checkNotNull(astParam.getSymbol().getType(), "Could not perform coco check '%s'. Perhaps you " +
      "missed the symbol table creation.", this.getClass().getSimpleName());

    VariableSymbol paramSym = astParam.getSymbol();
    SymTypeExpression paramType = paramSym.getType();

    if (astParam.isPresentDefault()) {
      ASTExpression defaultExpr = astParam.getDefault();
      Optional<SymTypeExpression> expressionType = this.extractTypeOf(defaultExpr);

      if (expressionType.isPresent() && !TypeCheck.compatible(paramType, expressionType.get())) {
        Log.error(ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE.format(
          paramSym.getFullName(),
          expressionType.get().print(),
          paramType.print()),
          astParam.get_SourcePositionStart());
      }
      if(!expressionType.isPresent()) {
        Log.debug(String.format("Checking coco '%s' is skipped for parameter '%s', as the type of the its default " +
              "value expression could not be calculated. Position: '%s'.",
            this.getClass().getSimpleName(), astParam.getName(), astParam.get_SourcePositionStart()),
          "CoCos");
      }
    }
  }
}
