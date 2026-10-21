/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Variability aware execution of type checking context conditions. In a
 * component type with variability, a name may refer to different symbols in
 * different configurations, e.g., to same-named ports of different types in
 * mutually exclusive variation points. Hence, the context condition is
 * executed once for each combination of symbols the names of the checked
 * expressions may refer to, with each name resolved to its symbol of the
 * combination. Combinations that are absent in all configurations in which the
 * checked construct is present are skipped.
 * <p>
 * A finding that several combinations produce, i.e., the same message at the
 * same source position, is reported only once. It is reported as error if it
 * is an error in some combination that is present in some configuration, and
 * as warning if it is an error only in combinations whose presence cannot be
 * decided because it depends on values that cannot be evaluated statically.
 * Furthermore, a name is reported as missing if the construct is present in
 * some configuration in which none of the name's symbols is present, as error
 * or as warning likewise.
 * <p>
 * If the construct and the symbols are present in all configurations and each
 * name refers to a single symbol, the context condition is executed once
 * without solving any formulas.
 */
public class TypeCheck4Family {

  /**
   * The finding of MontiCore's type check for a name without symbol, which is
   * reported for the configurations in which no candidate is present, such
   * that it equals the finding of the type check if no candidate exists
   */
  protected static final String MISSING_SYMBOL = "0xFD118 could not find symbol for expression \"%s\"";

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  public TypeCheck4Family(@NotNull WithinComponentResolver withinComponentResolver,
                          @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  /**
   * @param construct the node whose presence guards the check, e.g., an if
   *                  statement or a transition
   * @param scope     the enclosing scope of the construct
   * @param typed     the parts of the construct the check types, e.g., its
   *                  condition
   * @param check     the type checking context condition on the construct
   */
  public void check(@NotNull ASTNode construct,
                    @NotNull IScope scope,
                    @NotNull Collection<? extends ASTNode> typed,
                    @NotNull Runnable check) {
    Preconditions.checkNotNull(construct);
    Preconditions.checkNotNull(scope);
    Preconditions.checkNotNull(typed);
    Preconditions.checkNotNull(check);
    Optional<ComponentTypeSymbol> component = this.withinComponentResolver.enclosingComponentOf(scope);
    if (component.isEmpty()) {
      check.run();
      return;
    }

    Map<ASTNameExpression, List<ISymbol>> candidates = this.candidatesOf(typed);
    if (this.isInvariable(component.get(), construct, candidates)) {
      check.run();
      return;
    }
    FormulaSolver solver = this.presenceResolver.solverFor(component.get());
    BoolExpr presence = this.presenceResolver.presenceOf(component.get(), construct);
    this.checkMissingNames(solver, component.get(), presence, candidates);
    this.checkCombinations(solver, component.get(), presence, new ArrayList<>(new LinkedHashSet<>(candidates.values())),
      typed, check);
  }

  /**
   * @return whether the construct and the symbols its names refer to are
   * present in every configuration and each name refers to a single symbol,
   * such that the check needs to run once and without a solver
   */
  protected boolean isInvariable(@NotNull ComponentTypeSymbol component,
                                 @NotNull ASTNode construct,
                                 @NotNull Map<ASTNameExpression, List<ISymbol>> candidates) {
    return this.presenceResolver.isUnconditional(component, construct) && candidates.values().stream()
      .allMatch(symbols -> symbols.size() == 1 && this.presenceResolver.isUnconditional(component, symbols.get(0)));
  }

  protected void checkMissingNames(@NotNull FormulaSolver solver,
                                   @NotNull ComponentTypeSymbol component,
                                   @NotNull BoolExpr presence,
                                   @NotNull Map<ASTNameExpression, List<ISymbol>> candidates) {
    for (Map.Entry<ASTNameExpression, List<ISymbol>> entry : candidates.entrySet()) {
      BoolExpr absent = solver.not(solver.or(entry.getValue().stream()
        .map(symbol -> this.presenceResolver.presenceOf(component, symbol))
        .toList()));
      ASTNameExpression name = entry.getKey();
      String message = String.format(MISSING_SYMBOL, name.getName());
      switch (solver.check(solver.and(presence, absent))) {
        case SATISFIABLE -> Log.error(message, name.get_SourcePositionStart(), name.get_SourcePositionEnd());
        case UNKNOWN -> Log.warn(message, name.get_SourcePositionStart(), name.get_SourcePositionEnd());
        case UNSATISFIABLE -> { }
      }
    }
  }

  protected void checkCombinations(@NotNull FormulaSolver solver,
                                   @NotNull ComponentTypeSymbol component,
                                   @NotNull BoolExpr presence,
                                   @NotNull List<List<ISymbol>> choices,
                                   @NotNull Collection<? extends ASTNode> typed,
                                   @NotNull Runnable check) {
    Map<FindingKey, Finding> reported = new LinkedHashMap<>();
    for (List<ISymbol> combination : this.combinationsOf(choices)) {
      BoolExpr present = solver.and(presence, solver.and(combination.stream()
        .map(symbol -> this.presenceResolver.presenceOf(component, symbol))
        .toList()));
      FormulaSolver.Satisfiability satisfiability = solver.check(present);
      if (satisfiability == FormulaSolver.Satisfiability.UNSATISFIABLE) continue;

      Set<ISymbol> excluded = Collections.newSetFromMap(new IdentityHashMap<>());
      choices.forEach(excluded::addAll);
      combination.forEach(excluded::remove);
      SymbolSelection selection = VariableArcTypeCheck.getSymbolSelection();
      List<Finding> findings = this.collectFindings(() -> selection.within(excluded, typed, check));
      for (Finding finding : findings) {
        if (satisfiability == FormulaSolver.Satisfiability.UNKNOWN && finding.isError()) {
          finding.setType(Finding.Type.WARNING);
        }
        reported.merge(FindingKey.of(finding), finding, (first, second) -> first.isError() ? first : second);
      }
    }
    Log.getFindings().addAll(reported.values());
  }

  protected record FindingKey(@NotNull String message,
                              @NotNull Optional<SourcePosition> start,
                              @NotNull Optional<SourcePosition> end) {

    protected static FindingKey of(@NotNull Finding finding) {
      return new FindingKey(finding.getMsg(), finding.getSourcePosition(), finding.getSourcePositionEnd());
    }
  }

  protected List<Finding> collectFindings(@NotNull Runnable run) {
    List<Finding> log = Log.getFindings();
    int before = log.size();
    run.run();
    List<Finding> collected = new ArrayList<>(log.subList(before, log.size()));
    log.subList(before, log.size()).clear();
    return collected;
  }

  protected List<List<ISymbol>> combinationsOf(@NotNull List<List<ISymbol>> choices) {
    List<List<ISymbol>> combinations = new ArrayList<>();
    combinations.add(List.of());
    for (List<ISymbol> choice : choices) {
      List<List<ISymbol>> extended = new ArrayList<>();
      for (List<ISymbol> combination : combinations) {
        for (ISymbol symbol : choice) {
          List<ISymbol> next = new ArrayList<>(combination);
          next.add(symbol);
          extended.add(next);
        }
      }
      combinations = extended;
    }
    return combinations;
  }

  /**
   * @return for each name expression the value symbols it may refer to,
   * excluding names without such symbols, which are left to the type check
   */
  protected Map<ASTNameExpression, List<ISymbol>> candidatesOf(@NotNull Collection<? extends ASTNode> typed) {
    List<ASTNameExpression> names = new ArrayList<>();
    VariableArcTraverser traverser = VariableArcMill.inheritanceTraverser();
    traverser.add4ExpressionsBasis(new ExpressionsBasisVisitor2() {
      @Override
      public void visit(ASTNameExpression node) {
        names.add(node);
      }
    });
    typed.forEach(node -> node.accept(traverser));

    Map<ASTNameExpression, List<ISymbol>> candidates = new LinkedHashMap<>();
    for (ASTNameExpression name : names) {
      List<ISymbol> symbols = this.withinComponentResolver.resolveValues(name.getEnclosingScope(), name.getName());
      if (!symbols.isEmpty()) {
        candidates.put(name, symbols);
      }
    }
    return candidates;
  }
}
