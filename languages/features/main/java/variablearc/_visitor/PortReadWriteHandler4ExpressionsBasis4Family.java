/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.Optional;

import static montiarc.util.ArcError.READ_FROM_OUTGOING_PORT;
import static montiarc.util.ArcError.WRITE_TO_INCOMING_PORT;

/**
 * Variability aware extension of {@link PortReadWriteHandler4ExpressionsBasis},
 * i.e., considers the presence conditions of elements.
 */
public class PortReadWriteHandler4ExpressionsBasis4Family extends PortReadWriteHandler4ExpressionsBasis {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public PortReadWriteHandler4ExpressionsBasis4Family(@NotNull ContextState context,
                                                      @NotNull WithinComponentResolver withinComponentResolver,
                                                      @NotNull PresenceResolver presenceResolver) {
    super(context);
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void handle(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(expr.getEnclosingScope());
    Preconditions.checkArgument(expr.getEnclosingScope() instanceof IArcBasisScope);

    IArcBasisScope scope = (IArcBasisScope) expr.getEnclosingScope();
    List<PortSymbol> ports = this.withinComponentResolver.resolvePorts(scope, expr.getName());
    if (ports.isEmpty()) return;

    Optional<ComponentTypeSymbol> enclosing = this.withinComponentResolver.enclosingComponentOf(scope);
    if (enclosing.isEmpty()) return;

    ComponentTypeSymbol component = enclosing.get();
    List<PortSymbol> writtenInPort = ports.stream().filter(port -> port.isIncoming() && this.context.isInWriteContext()).toList();
    List<PortSymbol> readOutPort = ports.stream().filter(port -> port.isOutgoing() && this.context.isInReadContext()).toList();
    if (writtenInPort.isEmpty() && readOutPort.isEmpty()) return;

    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr presenceOfExpr = this.presenceResolver.presenceOf(component, expr);
    List<BoolExpr> presenceOfWrittenInPorts = writtenInPort.stream().map(port -> this.presenceResolver.presenceOf(component, port)).toList();
    List<BoolExpr> presenceOfReadOutPorts = readOutPort.stream().map(port -> this.presenceResolver.presenceOf(component, port)).toList();
    if (!presenceOfWrittenInPorts.isEmpty()) {
      this.report(solver, solver.and(presenceOfExpr, solver.or(presenceOfWrittenInPorts)),
        WRITE_TO_INCOMING_PORT.format(expr.getName()), expr);
    }
    if (!presenceOfReadOutPorts.isEmpty()) {
      this.report(solver, solver.and(presenceOfExpr, solver.or(presenceOfReadOutPorts)),
        READ_FROM_OUTGOING_PORT.format(expr.getName()), expr);
    }
  }

  protected void report(@NotNull FormulaSolver solver,
                        @NotNull BoolExpr violation,
                        @NotNull String message,
                        @NotNull ASTNameExpression expr) {
    switch (solver.check(violation)) {
      case SATISFIABLE -> Log.error(message, expr.get_SourcePositionStart(), expr.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(message, expr.get_SourcePositionStart(), expr.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }
}
