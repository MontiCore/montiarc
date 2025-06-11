/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

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
 * Identifies and reports if a name expressions references an input port,
 * expect for a name equal to the given event. Indented to be used as
 * visitor in the traversal of the AST (context) where only the port
 * of the given event is available.
 */
public class NoOtherInputPortInEventContextVisitor implements ExpressionsBasisVisitor2 {

  protected final String context;

  protected final String event;

  /**
   * @param event   the name of the event who's respective port may be referenced
   * @param context a human-readable string that describes the context where
   *                the value of other input ports are not available
   */
  public NoOtherInputPortInEventContextVisitor(@NotNull String event,
                                               @NotNull String context) {
    this.event = Preconditions.checkNotNull(event);
    this.context = Preconditions.checkNotNull(context);
  }

  @Override
  public void visit(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope() instanceof IArcBasisScope);

    String name = node.getName();

    if (node.getName().equals(this.event)) {
      return;
    }

    IArcBasisScope scope = (IArcBasisScope) node.getEnclosingScope();

    List<VariableSymbol> ports = scope.resolveVariableMany(name, this.getVariablePredicate());

    if (ports.size() == 1 && ports.get(0) instanceof Port2VariableAdapter
      && ((Port2VariableAdapter) ports.get(0)).getAdaptee().isIncoming()) {
      SourcePosition sourcePosition = node.get_SourcePositionStart();
      Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(name, this.context), sourcePosition);
    }
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    return v -> true;
  }
}
