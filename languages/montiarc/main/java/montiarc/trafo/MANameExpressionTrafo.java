/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.types3.util.PostTypeCheckNodeReplacer;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.function.UnaryOperator;

/**
 * Replaces single names that are parsed as qualified names with name expressions,
 * e.g., the feature f in "constraint (f);".
 * Qualified names consisting of multiple names, e.g., "sub.f", are kept.
 */
public class MANameExpressionTrafo implements UnaryOperator<ASTMACompilationUnit>, CommonExpressionsVisitor2 {

  private final MontiArcTraverser traverser;

  public MANameExpressionTrafo() {
    this.traverser = MontiArcMill.inheritanceTraverser();
    this.traverser.add4CommonExpressions(this);
  }

  @Override
  public void visit(@NotNull ASTQualifiedNameExpression node) {
    if (node.getMCQualifiedName().sizeParts() == 1) {
      PostTypeCheckNodeReplacer.addReplacement(node, MontiArcMill.nameExpressionBuilder()
        .setName(node.getMCQualifiedName().getParts(0))
        .set_SourcePositionStart(node.get_SourcePositionStart())
        .set_SourcePositionEnd(node.get_SourcePositionEnd())
        .build());
    }
  }

  @Override
  public ASTMACompilationUnit apply(@NotNull ASTMACompilationUnit cUnit) {
    this.applyTo(cUnit);
    return cUnit;
  }

  /**
   * Applies the transformation to the given node, e.g., a parsed expression.
   *
   * @return the transformed node, which is a replacement of the given node,
   *     if the given node is a single name
   */
  public ASTNode applyTo(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);
    node.accept(traverser);
    return PostTypeCheckNodeReplacer.replace(MontiArcMill::inheritanceTraverser, node);
  }
}
