/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

/**
 * Identifies and reports name expressions that may reference a port whose
 * value is not available in the context of the traversed AST. A name
 * references a port in every configuration in which the name expression and
 * a port candidate of that name are present, unless a local variable shadows
 * the name. Reports an error if such a configuration exists and a warning if
 * its existence is unknown.
 */
public class NoInputPortInContext4FamilyVisitor implements ExpressionsBasisVisitor2 {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;
  protected final String context;
  protected final Predicate<PortSymbol> isUnavailable;

  /**
   * @param context       a human-readable string that describes the context
   *                      where the values of the matching ports are not
   *                      available
   * @param isUnavailable matches the ports whose values are not available in
   *                      the context
   */
  public NoInputPortInContext4FamilyVisitor(@NotNull WithinComponentResolver withinComponentResolver,
                                            @NotNull PresenceResolver presenceResolver,
                                            @NotNull String context,
                                            @NotNull Predicate<PortSymbol> isUnavailable) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
    this.context = Preconditions.checkNotNull(context);
    this.isUnavailable = Preconditions.checkNotNull(isUnavailable);
  }

  @Override
  public void visit(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Optional<ComponentTypeSymbol> enclosing = this.getEnclosingComponentIfNotShadowed(expr.getEnclosingScope(), expr.getName());
    if (enclosing.isEmpty()) return;

    ComponentTypeSymbol component = enclosing.get();
    List<PortSymbol> unavailablePort = this.withinComponentResolver.resolveMany(component, expr.getName(), PortSymbol.class).stream()
      .filter(this.isUnavailable)
      .toList();
    if (unavailablePort.isEmpty()) return;

    String message = IN_PORT_REF_IN_INVALID_CONTEXT.format(expr.getName(), this.context);
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr presenceOfExpr = this.presenceResolver.presenceOf(component, expr);
    List<BoolExpr> presenceOfUnavailablePorts = unavailablePort.stream().map(port -> this.presenceResolver.presenceOf(component, port)).toList();
    switch (solver.check(solver.and(presenceOfExpr, solver.or(presenceOfUnavailablePorts)))) {
      case SATISFIABLE -> Log.error(message, expr.get_SourcePositionStart(), expr.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(message, expr.get_SourcePositionStart(), expr.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }

  /**
   * @return the component type that encloses the scope, or empty if there is
   * none or if a variable of the given name is declared in a scope between
   */
  protected Optional<ComponentTypeSymbol> getEnclosingComponentIfNotShadowed(IScope scope, @NotNull String name) {
    while (scope != null) {
      if (scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof ComponentTypeSymbol component) {
        return Optional.of(component);
      }
      if (scope instanceof IBasicSymbolsScope basicScope
        && !basicScope.resolveVariableLocallyMany(false, name, AccessModifier.ALL_INCLUSION, variable -> true).isEmpty()) {
        return Optional.empty();
      }
      scope = scope.getEnclosingScope();
    }
    return Optional.empty();
  }
}
