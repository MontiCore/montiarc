/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.Port2VariableAdapter;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;

/**
 * Identifies and reports if an expression contains references to a port
 * via name expressions. Indented to be used as visitor in the traversal of
 * the AST (static context) where the values of ports are not available.
 */
public class NoPortInStaticContextVisitor implements ExpressionsBasisVisitor2 {

  @Override
  public void visit(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(expr.getEnclosingScope());
    Preconditions.checkArgument(expr.getEnclosingScope() instanceof IArcBasisScope);

    String name = expr.getName();

    IArcBasisScope scope = (IArcBasisScope) expr.getEnclosingScope();

    List<VariableSymbol> ports = scope.resolveVariableMany(name, getVariablePredicate());

    if (ports.size() == 1 && ports.get(0) instanceof Port2VariableAdapter) {
      SourcePosition sourcePosition = expr.get_SourcePositionStart();
      Log.error(PORT_REF_IN_STATIC_CONTEXT.format(name), sourcePosition);
    }
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
      return v -> true;
  }
}
