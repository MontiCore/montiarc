/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;

public class NoFieldInStaticContextVisitor implements ExpressionsBasisVisitor2 {
  @Override
  public void visit(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(expr.getEnclosingScope());
    Preconditions.checkArgument(expr.getEnclosingScope() instanceof IArcBasisScope);

    String name = expr.getName();
    IArcBasisScope scope = (IArcBasisScope) expr.getEnclosingScope();

    List<VariableSymbol> vars =
      scope.resolveVariableMany(name, getVariablePredicate());

    // Mirror the port check semantics: only report if resolution is unambiguous.
    if (vars.size() == 1 && isField(vars.get(0))) {
      Log.error(
        FIELD_REF_IN_STATIC_CONTEXT.format(name),
        expr.get_SourcePositionStart(),
        expr.get_SourcePositionEnd()
      );
    }
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    return v -> true;
  }

  protected boolean isField(VariableSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return symbol.isPresentAstNode()
      && ArcBasisMill.typeDispatcher().isArcBasisASTArcField(symbol.getAstNode());
  }
}
