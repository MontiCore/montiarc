/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis.check.TypeExprOfGenericComponent;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Refinement declarations must respect the type bounds of the referenced component type.
 * This coco is checked for refinement declarations that are not raw, i.e., that do not omit type arguments.
 */
public class RefinementTypeBound implements ArcBasisASTComponentTypeCoCo {
  @Override
  public void check(@NotNull ASTComponentType astCompType) {
    Preconditions.checkNotNull(astCompType);
    Preconditions.checkArgument(astCompType.isPresentSymbol());

    for (CompKindExpression compExpr : astCompType.getSymbol().getRefinementsList()) {
      if (compExpr instanceof TypeExprOfGenericComponent) {

        Optional<SourcePosition> srcStart = astStartOf(compExpr);
        Optional<SourcePosition> srcEnd = astEndOf(compExpr);

        checkTypeArgCount(
          (TypeExprOfGenericComponent) compExpr,
          srcStart.isPresent() ? srcStart : astStartOf(astCompType.getHead().getSpecList()),
          srcEnd.isPresent() ? srcEnd :  astEndOf(astCompType.getHead().getSpecList())
        );
        checkTypeBounds(
          (TypeExprOfGenericComponent) compExpr,
          srcStart.isPresent() ? srcStart : astStartOf(astCompType.getHead().getSpecList()),
          srcEnd.isPresent() ? srcEnd :  astEndOf(astCompType.getHead().getSpecList())
        );
      }
    }
  }

  /**
   * @param astStart For error logging
   * @param astEnd For error logging
   */
  protected void checkTypeArgCount(@NotNull TypeExprOfGenericComponent bindingTypeExpr,
                                   @NotNull Optional<SourcePosition> astStart,
                                   @NotNull Optional<SourcePosition> astEnd) {
    List<SymTypeExpression> typeArgs = bindingTypeExpr.getTypeBindingsAsList();
    List<TypeVarSymbol> typeParams = bindingTypeExpr.getTypeInfo().getTypeParameters();

    // 1. Catching too few type arguments
    if (typeArgs.size() < typeParams.size()) {
      String errorMsg = ArcError.TOO_FEW_TYPE_ARGUMENTS.format(typeParams.size(), typeArgs.size());
      if (astStart.isPresent() && astEnd.isPresent()) {
        Log.error(errorMsg, astStart.get(), astEnd.get());
      } else {
        Log.error(errorMsg);
      }
    }

    // 2. Catching too many type arguments
    if (typeArgs.size() > typeParams.size()) {
      String errorMsg = ArcError.TOO_MANY_TYPE_ARGUMENTS.format(typeParams.size(), typeArgs.size());
      if (astStart.isPresent() && astEnd.isPresent()) {
        Log.error(errorMsg, astStart.get(), astEnd.get());
      } else {
        Log.error(errorMsg);
      }
    }
  }

  /**
   * @param astStart For error logging
   * @param astEnd For error logging
   */
  protected void checkTypeBounds(@NotNull TypeExprOfGenericComponent bindingTypeExpr,
                                 @NotNull Optional<SourcePosition> astStart,
                                 @NotNull Optional<SourcePosition> astEnd) {
    List<TypeVarSymbol> typeParams = bindingTypeExpr.getTypeInfo().getTypeParameters();

    for (TypeVarSymbol typeParam : typeParams) {
      Optional<SymTypeExpression> typeArg = bindingTypeExpr.getTypeBindingFor(typeParam);
      if (typeArg.isPresent()) {
        for (SymTypeExpression aBound : typeParam.getSuperTypesList()) {
          SymTypeExpression bound = aBound.deepClone();
          bound.replaceTypeVariables(bindingTypeExpr.getTypeVarBindings());

          if (!SymTypeRelations.isCompatible(bound, typeArg.get())) {
            String errorMsg = ArcError.TYPE_ARG_IGNORES_UPPER_BOUND.format(typeArg.get().print(), bound.print());
            if (astStart.isPresent() && astEnd.isPresent()){
              Log.error(errorMsg, astStart.get(), astEnd.get());
            } else {
              Log.error(errorMsg);
            }
          }
        }
      }
    }
  }

  protected static Optional<SourcePosition> astStartOf(@NotNull CompKindExpression compExpr) {
    Preconditions.checkNotNull(compExpr);
    return compExpr.getSourceNode().map(ASTNode::get_SourcePositionStart);
  }

  protected static Optional<SourcePosition> astEndOf(@NotNull CompKindExpression compExpr) {
    Preconditions.checkNotNull(compExpr);
    return compExpr.getSourceNode().map(ASTNode::get_SourcePositionEnd);
  }

  /**
   * If the list is non-empty, returns the start source position of the first element.
   */
  protected static Optional<SourcePosition> astStartOf(@NotNull List<? extends ASTNode> nodes) {
    Preconditions.checkNotNull(nodes);
    return nodes.isEmpty() ? Optional.empty()
      : Optional.of(nodes.get(0).get_SourcePositionStart());
  }

  /**
   * If the list is non-empty, returns the end source position of the last element.
   */
  protected static Optional<SourcePosition> astEndOf(@NotNull List<? extends ASTNode> nodes) {
    Preconditions.checkNotNull(nodes);
    return nodes.isEmpty() ? Optional.empty()
      : Optional.of(nodes.get(nodes.size() - 1).get_SourcePositionEnd());
  }
}
