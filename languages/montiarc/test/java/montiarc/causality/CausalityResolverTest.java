/* (c) https://github.com/MontiCore/monticore */
package montiarc.causality;

import arcbasis.context.WithinComponentResolver;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import montiarc.MontiArcTestBase;
import org.junit.jupiter.api.Test;
import variablearc.causality.CausalityResolver;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import static org.assertj.core.api.Assertions.assertThat;
import static variablearc.smt.FormulaSolver.Satisfiability.UNSATISFIABLE;

/**
 * The class under test is {@link CausalityResolver}.
 */
public class CausalityResolverTest extends MontiArcTestBase {

  protected static final String L = "package a; component L { port in int i; port out int o; feature ff; varif (!ff) { <<delayed>> compute { } } }";

  protected final WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
  protected final CausalityResolver causalityResolver =
    new CausalityResolver(this.withinComponentResolver, new PresenceResolver(this.withinComponentResolver));

  @Test
  public void shouldResolveDelay() {
    // Given
    ComponentTypeSymbol l = compile(L).getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();

    // When
    BoolExpr delay = this.causalityResolver.delayOf(l);

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(delay, z3.mkNot(z3.mkBoolConst("ff")))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldResolveEffectOfAtomicComponentsUnlessDelayed() {
    // Given
    ComponentTypeSymbol l = compile(L).getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();

    // When
    BoolExpr effect = this.causalityResolver.effectOf(solver, l, l.getPort("i").orElseThrow(), l.getPort("o").orElseThrow());

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(effect, z3.mkBoolConst("ff"))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldResolveEffectAlongConnectorsAndSubcomponents() {
    // Given
    compile(L);
    ComponentTypeSymbol w = compile("""
      package a;
      component W {
        feature g;
        port in int i;
        port out int o, p;
        a.L sub;
        i -> sub.i;
        varif (g) { sub.o -> o; }
      }""").getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();

    // When
    BoolExpr toO = this.causalityResolver.effectOf(solver, w, w.getPort("i").orElseThrow(), w.getPort("o").orElseThrow());
    BoolExpr toP = this.causalityResolver.effectOf(solver, w, w.getPort("i").orElseThrow(), w.getPort("p").orElseThrow());

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(toO, z3.mkAnd(z3.mkBoolConst("sub.ff"), z3.mkBoolConst("g")))))).isEqualTo(UNSATISFIABLE);
    assertThat(toP.isFalse()).isTrue();
  }
}
