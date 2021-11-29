/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcParameter;
import arcbasis.check.ArcBasisDeriveType;
import arcbasis.check.ArcTypeCheck;
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
 * [Wor16] MT7: Default values of configuration parameters must conform to the parameters' types.
 */
public class ParameterDefaultValueTypesCorrect implements ArcBasisASTArcParameterCoCo {

  /**
   * Used to check whether default values of parameters match the parameters' types.
   */
  protected final TypeCheck typeChecker;

  /**
   * Creates this coco with an ArcTypeCheck, combined with {@link ArcBasisDeriveType} to check whether default values of
   * parameters match the parameters' types.
   */
  public ParameterDefaultValueTypesCorrect() {
    this(new ArcTypeCheck(new ArcBasisSynthesizeType(), new ArcBasisDeriveType(new TypeCheckResult())));
  }

  /**
   * Creates this coco with a custom {@link TypeCheck} to use to check whether default values of parameters match the
   * parameters' types.
   */
  public ParameterDefaultValueTypesCorrect(@NotNull TypeCheck typeChecker) {
    Preconditions.checkNotNull(typeChecker);
    this.typeChecker = typeChecker;
  }

  /**
   * Creates this coco with a custom {@link IDerive} to use to check whether default values of parameters match the
   * parameters' types.
   */
  public ParameterDefaultValueTypesCorrect(@NotNull IDerive deriveFromExpr) {
    this(new ArcTypeCheck(new ArcBasisSynthesizeType(), Preconditions.checkNotNull(deriveFromExpr)));
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
      SymTypeExpression expressionType = this.typeChecker.typeOf(defaultExpr);

      if (!ArcTypeCheck.compatible(paramType, expressionType)) {
        Log.error(ArcError.DEFAULT_PARAM_EXPRESSION_WRONG_TYPE.format(
          paramSym.getFullName(),
          expressionType.print(),
          paramType.print()),
          astParam.get_SourcePositionStart());
      }
    }
  }
}
