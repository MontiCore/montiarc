/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver.Satisfiability;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base of the context conditions that forbid references to certain members,
 * e.g., ports, in expressions of a static context. A name expression refers
 * to the members of its name in the innermost enclosing component type that
 * has a port or variable of that name. A reference is reported if the static
 * context and a forbidden member it may refer to are present together in some
 * configuration.
 */
public abstract class NoReferenceInStaticContext4Family {

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  protected NoReferenceInStaticContext4Family(@NotNull WithinComponentResolver withinComponentResolver,
                                              @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  /**
   * @return whether the member must not be referenced in the static context
   */
  protected abstract boolean isForbidden(@NotNull ISymbol member);

  /**
   * @return the message reported for a reference to a forbidden member
   */
  protected abstract String message(@NotNull String name);

  /**
   * @param context     the node of the static context, e.g., a field
   *                    declaration, whose presence guards the expressions
   * @param expressions the expressions of the static context
   */
  protected void checkReferences(@NotNull ASTNode context, @NotNull List<ASTExpression> expressions) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(expressions);
    List<ASTNameExpression> names = this.nameExpressionsOf(expressions);
    if (names.isEmpty()) return;
    FormulaSolver solver = this.withinComponentResolver.enclosingComponentOf(names.get(0).getEnclosingScope())
      .map(this.presenceResolver::solverFor)
      .orElseGet(this.presenceResolver::solver);
    for (ASTNameExpression name : names) {
      this.checkReference(solver, context, name);
    }
  }

  protected void checkReference(@NotNull FormulaSolver solver,
                                @NotNull ASTNode context,
                                @NotNull ASTNameExpression name) {
    Optional<ComponentTypeSymbol> enclosing = this.withinComponentResolver.enclosingComponentOf(name.getEnclosingScope());
    if (enclosing.isEmpty()) return;

    ComponentTypeSymbol component = enclosing.get();
    BoolExpr presenceOfContext = this.presenceResolver.presenceOf(component, context);
    List<BoolExpr> presenceOfEnclosingContexts = new ArrayList<>();
    List<ISymbol> candidates = this.candidates(component, name.getName());
    while (candidates.isEmpty()) {
      Optional<ComponentTypeSymbol> outer = this.withinComponentResolver.enclosingComponentOf(component.getEnclosingScope());
      if (outer.isEmpty()) return;
      presenceOfEnclosingContexts.add(presenceOfContext);
      presenceOfContext = this.presenceResolver.presenceOf(outer.get(), component);
      component = outer.get();
      candidates = this.candidates(component, name.getName());
    }

    ComponentTypeSymbol owner = component;
    List<BoolExpr> presenceOfForbiddenMembers = candidates.stream()
      .filter(this::isForbidden)
      .map(member -> this.presenceResolver.presenceOf(owner, member))
      .toList();
    if (presenceOfForbiddenMembers.isEmpty()) return;

    Satisfiability result = solver.check(solver.and(presenceOfContext, solver.or(presenceOfForbiddenMembers)));
    for (BoolExpr presenceOfEnclosingContext : presenceOfEnclosingContexts) {
      if (result == Satisfiability.UNSATISFIABLE) return;
      result = this.weakest(result, solver.check(presenceOfEnclosingContext));
    }
    switch (result) {
      case SATISFIABLE -> Log.error(this.message(name.getName()), name.get_SourcePositionStart(), name.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(this.message(name.getName()), name.get_SourcePositionStart(), name.get_SourcePositionEnd());
      case UNSATISFIABLE -> { }
    }
  }

  /**
   * @return the ports and variables of the component type with the given name
   */
  protected List<ISymbol> candidates(@NotNull ComponentTypeSymbol component, @NotNull String name) {
    List<ISymbol> candidates = new ArrayList<>();
    candidates.addAll(this.withinComponentResolver.resolveMany(component, name, PortSymbol.class));
    candidates.addAll(this.withinComponentResolver.resolveMany(component, name, VariableSymbol.class));
    return candidates;
  }

  protected Satisfiability weakest(@NotNull Satisfiability first, @NotNull Satisfiability second) {
    if (first == Satisfiability.UNSATISFIABLE || second == Satisfiability.UNSATISFIABLE) {
      return Satisfiability.UNSATISFIABLE;
    } else if (first == Satisfiability.UNKNOWN || second == Satisfiability.UNKNOWN) {
      return Satisfiability.UNKNOWN;
    }
    return Satisfiability.SATISFIABLE;
  }

  protected List<ASTNameExpression> nameExpressionsOf(@NotNull List<ASTExpression> expressions) {
    NameExpressionCollector collector = new NameExpressionCollector();
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.add4ExpressionsBasis(collector);
    expressions.forEach(expression -> expression.accept(traverser));
    return collector.getNameExpressions();
  }

  protected static class NameExpressionCollector implements ExpressionsBasisVisitor2 {

    protected final List<ASTNameExpression> nameExpressions = new ArrayList<>();

    @Override
    public void visit(ASTNameExpression node) {
      this.nameExpressions.add(node);
    }

    protected List<ASTNameExpression> getNameExpressions() {
      return this.nameExpressions;
    }
  }
}
