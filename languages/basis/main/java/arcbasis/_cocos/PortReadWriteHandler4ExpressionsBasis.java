/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * Input ports are read-only, and output ports are write-only. This handler detects illegal
 * operations on ports, i.e, reading from an output port or writing to an input port, and reports
 * these as errors.
 * <p>
 * This is the <i>ExpressionsBasis</i> fragment of a handler based implementation. Herein,
 * all {@link ASTNameExpression}s are treated as read accesses. If the name expression is used in
 * a context that changes this semantic, then the respective language's handler implementation
 * should stop traversal before reaching name expressions to avoid wrongful error logs. An example
 * could be the usage of a name expression on the right side of an assignment expression, where an
 * assignment is understood as sending a message, or any use of an expression as part of a statement.
 *
 * @see PortReadWriteHandler4AssignmentExpressions
 * @see PortReadWriteHandler4MCCommonStatements
 * @see PortReadWriteHandler4CommonExpressions
 */
public class PortReadWriteHandler4ExpressionsBasis implements ExpressionsBasisHandler {

  /**
   * Records the context of how variables in abstract syntax subtrees are accessed.
   * E.g.: <pre>(a.b.c) = 5 + d;</pre> Here:
   * <ul>
   *   <li>The right-hand side expression <pre>5 + d</pre> is a <i>pure read</i> operation.
   *       <pre>d</pre> will consequently just be read operation.
   *   </li>
   *   <li>The left-hand side expression <pre>(a.b.c)</pre> is a <i>write</i> operation.
   *       Consequently, there will be a value written to <pre>c</pre>.
   *       If <pre>a</pre> is a port, then there will be a value written to it.<br>
   *       Expression <pre>(a.b.c)</pre> can also be in the context of a <i>read and write</i>
   *       operation. For example, this is the case if the whole assignment expression appears
   *       in another expression: <pre>((a.b.c) = 4) / 2</pre>. This highlights that, while
   *       evaluating sub expressions, one cam switch from a <i>read</i> to a <i>read and write</i>
   *       context, but must not switch to a <i>pure write</i> context.
   *   </li>
   * </ul>
   * Lastly, the <i>neutral</i> context type is used in situations where neither a read nor a write
   * operation is expected. For example, this is the case for expression statements. which can
   * be used in the context of reading ta variable's value: <pre>foo(var)</pre>, or writing to
   * it: <pre>var = 3</pre>. Thus, when evaluating a subexpression, the context
   * may change freely from <i>neutral</i> to <i>read</i>, <i>write</i>, or <i>read and write</i>.
   */
  public enum ContextType {
    PURE_READ, PURE_WRITE, READ_AND_WRITE, NEUTRAL
  }

  /**
   * Shared state between different visitors and handlers that tracks the context in which
   * variables are accessed. The default context type is <i>pure read</i>, as most entry points
   * to expressions occur in a read context, for example, in loop conditions, if statements,
   * and variable initializations. In contrast, only a few entry points, such as expression
   * statements, begin in a <i>neutral</i> context.
   */
  public static final class ContextState {

    private ContextType type;

    public ContextState() {
      this.type = ContextType.PURE_READ;
    }

    public ContextState(@NotNull ContextType type) {
      this.type = Preconditions.checkNotNull(type);
    }

    public void setType(@NotNull ContextType type) {
      this.type = Preconditions.checkNotNull(type);
    }

    public ContextType getType() {
      return type;
    }

    public boolean isInReadContext() {
      return type == ContextType.PURE_READ || type == ContextType.READ_AND_WRITE || type == ContextType.NEUTRAL;
    }

    public boolean isInWriteContext() {
      return type == ContextType.PURE_WRITE || type == ContextType.READ_AND_WRITE;
    }
  }

  protected ExpressionsBasisTraverser traverser;

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull ExpressionsBasisTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  protected final ContextState context;

  public PortReadWriteHandler4ExpressionsBasis(@NotNull ContextState context) {
    this.context = Preconditions.checkNotNull(context);
  }

  @Override
  public void handle(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(expr.getEnclosingScope());
    Preconditions.checkArgument(expr.getEnclosingScope() instanceof IArcBasisScope);

    IArcBasisScope scope = (IArcBasisScope) expr.getEnclosingScope();

    List<VariableSymbol> ports = scope.resolveVariableMany(expr.getName(), getVariablePredicate());

    /*
     * An error should be logged by the type check if we can resolve no or more than one variable.
     * Therefore, we only continue if we resolve exactly one variable. If the resolved variable
     * is not a port, then we can skip further checks.
     */
    if (ports.size() == 1 && ports.get(0) instanceof Port2VariableAdapter) {
      PortSymbol port = ((Port2VariableAdapter) ports.get(0)).getAdaptee();

      if (port.isIncoming() && this.context.isInWriteContext()) {
        Log.error(ArcError.WRITE_TO_INCOMING_PORT.format(port.getName()),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
        );

      } else if (port.isOutgoing() && this.context.isInReadContext()) {
        Log.error(ArcError.READ_FROM_OUTGOING_PORT.format(port.getName()),
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
        );
      }
    }
  }

  protected Predicate<VariableSymbol> getVariablePredicate() {
    return v -> true;
  }
}
