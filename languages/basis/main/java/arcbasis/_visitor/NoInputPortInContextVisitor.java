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

import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

/**
 * Identifies and reports if an expression contains references to input ports
 * via name expressions. Indented to be used as visitor in the traversal of
 * the AST (context) where the values of input ports are not available.
 */
public class NoInputPortInContextVisitor implements ExpressionsBasisVisitor2 {

  // A human-readable description of the context in which this check is applied
  private final String context;

  /**
   * @param context a human-readable string that describes the context where
   *                the value of the input port is not available
   */
  public NoInputPortInContextVisitor(@NotNull String context) {
    this.context = Preconditions.checkNotNull(context);
  }

  @Override
  public void visit(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(expr.getEnclosingScope());
    Preconditions.checkArgument(expr.getEnclosingScope() instanceof IArcBasisScope);

    String name = expr.getName();

    IArcBasisScope scope = (IArcBasisScope) expr.getEnclosingScope();

    List<VariableSymbol> ports = scope.resolveVariableMany(name, this.getVariablePredicate());

    if (ports.size() == 1 && ports.get(0) instanceof Port2VariableAdapter
      && ((Port2VariableAdapter) ports.get(0)).getAdaptee().isIncoming()) {
      SourcePosition sourcePosition = expr.get_SourcePositionStart();
      Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(name, this.context), sourcePosition);
    }
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    return v -> true;
  }
}
