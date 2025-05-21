/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis.check.TypeExprOfGenericComponent;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Issues a warning if, in a refinement declaration, the refined component is
 * generic, but no type arguments are presented.
 */
public class RefinementRawType implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType astCompType) {
    Preconditions.checkNotNull(astCompType);
    Preconditions.checkArgument(astCompType.isPresentSymbol());

    for (CompKindExpression compExpr : astCompType.getSymbol().getRefinementsList()) {

      boolean typeIsGeneric = compExpr.getTypeInfo().hasTypeParameter();
      boolean typeExprIsRaw = !(compExpr instanceof TypeExprOfGenericComponent);

      if (typeIsGeneric && typeExprIsRaw) {

        String warnMsg = ArcError.RAW_USE_OF_PARAMETRIZED_TYPE.format(compExpr.getTypeInfo().getName());
        Optional<SourcePosition> srcStart = astStartOf(compExpr).isPresent() ?
          astStartOf(compExpr) : astStartOf(astCompType.getHead().getSpecList());
        Optional<SourcePosition> srcEnd = astEndOf(compExpr).isPresent() ?
          astEndOf(compExpr) : astEndOf(astCompType.getHead().getSpecList());

        if (srcStart.isPresent() && srcEnd.isPresent()) {
          Log.warn(warnMsg, srcStart.get(), srcEnd.get());
        } else {
          Log.warn(warnMsg);
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
