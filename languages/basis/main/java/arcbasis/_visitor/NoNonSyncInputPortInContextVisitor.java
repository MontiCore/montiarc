/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.Port2VariableAdapter;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;
import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

/**
 * Identifies and reports if an expression contains references to
 * non-synchronous input ports via name expressions. Indented to be used as
 * visitor in the traversal of the AST (context) where the values of
 * non-synchronous input ports are not available.
 */
public class NoNonSyncInputPortInContextVisitor implements ExpressionsBasisVisitor2 {

  protected final String context;

  /**
   * @param context a human-readable string that describes the context where
   *                the value of the input port is not available
   */
  public NoNonSyncInputPortInContextVisitor(String context) {
    this.context = context;
  }

  @Override
  public void visit(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope() instanceof IArcBasisScope);

    String name = node.getName();

    IArcBasisScope scope = (IArcBasisScope) node.getEnclosingScope();

    List<VariableSymbol> ports = scope.resolveVariableMany(name, this.getVariablePredicate());

    if (!ports.isEmpty() && ports.get(0) instanceof Port2VariableAdapter) {
      PortSymbol port = ((Port2VariableAdapter) ports.get(0)).getAdaptee();
      if (port.isIncoming() && !port.getTiming().matches(TIMED_SYNC)) {
        SourcePosition sourcePosition = node.get_SourcePositionStart();
        Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(name, this.context), sourcePosition);
      }
    }
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    return v -> true;
  }
}
