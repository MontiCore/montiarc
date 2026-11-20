/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types3.util.PostTypeCheckNodeReplacer;
import de.se_rwth.commons.SourcePosition;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.context.VariableArcWithinComponentResolver;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Replaces qualified names that start with a value (port, variable, ...) or a subcomponent
 * with the corresponding field accesses, e.g., "sub.o" with the field access ".o" on the name "sub".
 * Qualified names that start with a type or package, e.g., "Color.RED", are kept.
 */
public class MAValueAccessTrafo implements UnaryOperator<ASTMACompilationUnit>, CommonExpressionsVisitor2 {

  private final MontiArcTraverser traverser;
  private final WithinComponentResolver resolver;

  public MAValueAccessTrafo() {
    this.traverser = MontiArcMill.inheritanceTraverser();
    this.traverser.add4CommonExpressions(this);
    this.resolver = new VariableArcWithinComponentResolver();
  }

  @Override
  public void visit(@NotNull ASTQualifiedNameExpression node) {
    List<String> parts = node.getMCQualifiedName().getPartsList();
    if (parts.size() > 1 && !resolver.resolveMany(node.getEnclosingScope(), parts.get(0),
      symbol -> resolver.isValue(symbol) || symbol instanceof SubcomponentSymbol).isEmpty()) {
      SourcePosition start = node.get_SourcePositionStart();
      ASTExpression result = MontiArcMill.nameExpressionBuilder().setName(parts.get(0))
        .set_SourcePositionStart(start)
        .set_SourcePositionEnd(endOf(start, parts.subList(0, 1)))
        .build();
      result.setEnclosingScope(node.getEnclosingScope());
      for (int i = 1; i < parts.size(); i++) {
        result = MontiArcMill.fieldAccessExpressionBuilder().setExpression(result).setName(parts.get(i))
          .set_SourcePositionStart(start)
          .set_SourcePositionEnd(endOf(start, parts.subList(0, i + 1)))
          .build();
        result.setEnclosingScope(node.getEnclosingScope());
      }
      PostTypeCheckNodeReplacer.addReplacement(node, result);
    }
  }

  /**
   * @return the end of the qualified name consisting of the given parts
   */
  protected SourcePosition endOf(@NotNull SourcePosition start, @NotNull List<String> parts) {
    return new SourcePosition(start.getLine(), start.getColumn() + String.join(".", parts).length(),
      start.getFileName().orElse(null));
  }

  @Override
  public ASTMACompilationUnit apply(@NotNull ASTMACompilationUnit cUnit) {
    Preconditions.checkNotNull(cUnit);
    traverser.handle(cUnit);
    PostTypeCheckNodeReplacer.replace(MontiArcMill::inheritanceTraverser, cUnit);

    return cUnit;
  }
}
