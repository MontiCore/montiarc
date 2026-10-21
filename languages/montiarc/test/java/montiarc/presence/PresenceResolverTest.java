/* (c) https://github.com/MontiCore/monticore */
package montiarc.presence;

import arcbasis.context.WithinComponentResolver;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import org.junit.jupiter.api.Test;
import variablearc._ast.ASTArcVarIf;
import variablearc._symboltable.IVariableArcScope;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.smt.FormulaSolver;
import variablearc.smt.exp2smt.IDeriveSMTExpr;
import variablearc.presence.PresenceResolver;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static variablearc.smt.FormulaSolver.Satisfiability.UNKNOWN;
import static variablearc.smt.FormulaSolver.Satisfiability.UNSATISFIABLE;

/**
 * The class under test is {@link PresenceResolver}.
 */
public class PresenceResolverTest extends MontiArcTestBase {

  protected static final String COMP = "package a; component Comp { feature A, B; varif (A) { port in int i; } constraint (B); }";
  protected static final String COMP2 = "package a; component Comp2 { feature C; varif (C) { a.Comp sub; } }";

  protected final WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
  protected final PresenceResolver presenceResolver = new PresenceResolver(this.withinComponentResolver);

  @Test
  public void shouldCombineVarIfConditionsAndConstraints() {
    // Given
    ComponentTypeSymbol comp = compile(COMP).getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();

    // When
    BoolExpr i = this.presenceResolver.presenceOf(comp, comp.getPort("i").orElseThrow());

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(i, z3.mkAnd(z3.mkBoolConst("B"), z3.mkBoolConst("A")))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldLiftPresenceIntoEnclosingComponent() {
    // Given
    ComponentTypeSymbol comp = compile(COMP).getArcComponentType().getSymbol();
    ComponentTypeSymbol comp2 = compile(COMP2).getArcComponentType().getSymbol();
    PortSymbol i = comp.getPort("i").orElseThrow();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();

    // When
    BoolExpr lifted = this.presenceResolver.presenceOf(comp2, comp2.getSubcomponents("sub").orElseThrow(), i);

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(lifted,
      z3.mkAnd(z3.mkBoolConst("C"), z3.mkBoolConst("sub.B"), z3.mkBoolConst("sub.A")))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldTranslateConstraintsInTheVocabularyOfTheType() {
    // Given
    ComponentTypeSymbol comp = compile("package a; component Comp { feature f; constraint (f); }").getArcComponentType().getSymbol();

    // When
    BoolExpr constraints = this.presenceResolver.presenceOf(comp);

    // Then
    assertThat(constraints.toString()).isEqualTo("f");
  }

  @Test
  public void shouldTranslateInconvertibleConditionsAsUnknowns() {
    // Given
    ComponentTypeSymbol comp = compile("package a; component Comp { double x = 0.0; constraint (x); }").getArcComponentType().getSymbol();

    // When
    BoolExpr constraints = this.presenceResolver.presenceOf(comp);

    // Then
    assertThat(IDeriveSMTExpr.isUnknown(constraints)).isTrue();
  }

  @Test
  public void shouldQualifyVariablesOfSubcomponents() {
    // Given
    ComponentTypeSymbol comp = compile("package a; component Comp { feature f; constraint (f); }").getArcComponentType().getSymbol();
    ComponentTypeSymbol outer = compile("package a; component Outer { a.Comp inner; }").getArcComponentType().getSymbol();
    ComponentTypeSymbol root = compile("package a; component Root { a.Outer o; }").getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    // When
    BoolExpr inOuter = this.presenceResolver.lift(outer, outer.getSubcomponents("inner").orElseThrow(),
      this.presenceResolver.presenceOf(comp));
    BoolExpr inRoot = this.presenceResolver.lift(root, root.getSubcomponents("o").orElseThrow(), inOuter);

    // Then
    assertThat(inOuter.toString()).isEqualTo("inner.f");
    assertThat(inRoot.toString()).isEqualTo("o.inner.f");
  }

  @Test
  public void shouldQualifyUnknownValuesOfSubcomponents() {
    // Given
    ComponentTypeSymbol comp = compile("package a; component Comp { double x = 0.0; constraint (x); }").getArcComponentType().getSymbol();
    ComponentTypeSymbol top = compile("package a; component Top { a.Comp sub; }").getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    // When
    BoolExpr lifted = this.presenceResolver.lift(top, top.getSubcomponents("sub").orElseThrow(),
      this.presenceResolver.presenceOf(comp));

    // Then
    assertThat(IDeriveSMTExpr.isUnknown(lifted)).isTrue();
    assertThat(lifted.toString()).startsWith("|sub." + IDeriveSMTExpr.UNKNOWN_MARKER);
  }

  @Test
  public void shouldLiftPresenceThroughNestedSubcomponents() {
    // Given
    ComponentTypeSymbol comp = compile(COMP).getArcComponentType().getSymbol();
    ComponentTypeSymbol comp2 = compile(COMP2).getArcComponentType().getSymbol();
    ComponentTypeSymbol comp3 = compile("package a; component Comp3 { a.Comp2 c; }").getArcComponentType().getSymbol();
    SubcomponentSymbol c = comp3.getSubcomponents("c").orElseThrow();
    SubcomponentSymbol sub = comp2.getSubcomponents("sub").orElseThrow();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();

    // When
    BoolExpr lifted = this.presenceResolver.lift(comp3, c,
      this.presenceResolver.presenceOf(comp2, sub, comp.getPort("i").orElseThrow()));

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(lifted,
      z3.mkAnd(z3.mkBoolConst("c.C"), z3.mkBoolConst("c.sub.B"), z3.mkBoolConst("c.sub.A")))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldResolvePresenceOfAnySymbolKind() {
    // Given
    ASTMACompilationUnit ast = compile("""
      package a;
      component K {
        feature F;
        varif (F) {
          feature G;
          int fld = 0;
          component Inner { }
          automaton { initial state S; }
        } else {
          port in int p;
        }
      }""");
    ComponentTypeSymbol k = ast.getArcComponentType().getSymbol();
    IVariableArcScope scope = (IVariableArcScope) k.getSpannedScope();
    ASTArcVarIf varIf = (ASTArcVarIf) ast.getArcComponentType().getBody().getArcElement(1);

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    BoolExpr f = z3.mkBoolConst("F");

    // When && Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(k, scope.resolveArcFeatureLocally("G").orElseThrow()), f)))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(k, scope.resolveVariableLocally("fld").orElseThrow()), f)))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(k, scope.resolveComponentTypeLocally("Inner").orElseThrow()), f)))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(k, k.getPort("p").orElseThrow()), z3.mkNot(f))))).isEqualTo(UNSATISFIABLE);
    assertThat(this.presenceResolver.presenceOf(k, scope.resolveArcFeatureLocally("F").orElseThrow()).isTrue()).isTrue();
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(k, varIf.getThen()), f)))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldShareFeaturesWithSuperComponents() {
    // Given
    ComponentTypeSymbol p = compile("package a; component P { feature F; varif (F) { port in int i; } }").getArcComponentType().getSymbol();
    ComponentTypeSymbol child = compile("package a; component Child extends a.P { constraint (F); }").getArcComponentType().getSymbol();
    ComponentTypeSymbol other = compile("package a; component Other extends a.P { constraint (!F); }").getArcComponentType().getSymbol();
    PortSymbol i = p.getPort("i").orElseThrow();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();

    // When && Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(child, i), z3.mkBoolConst("F"))))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(this.presenceResolver.presenceOf(other, i))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldLiftInheritedSubcomponents() {
    // Given
    ComponentTypeSymbol w = compile("package a; component W { feature F; varif (F) { port in int i; } }").getArcComponentType().getSymbol();
    ComponentTypeSymbol p = compile("package a; component P { a.W w; }").getArcComponentType().getSymbol();
    ComponentTypeSymbol child = compile("package a; component Child extends a.P { constraint (w.F); }").getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();

    // When
    BoolExpr lifted = this.presenceResolver.presenceOf(child, p.getSubcomponents("w").orElseThrow(), w.getPort("i").orElseThrow());

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(lifted, z3.mkBoolConst("w.F"))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldSubstituteParametersOfSubcomponents() {
    // Given
    ComponentTypeSymbol q = compile("package a; component Q(int n) { varif (n > 3) { port in int i; } }").getArcComponentType().getSymbol();
    ComponentTypeSymbol qd = compile("package a; component QD(int n = 5) { varif (n > 3) { port in int i; } }").getArcComponentType().getSymbol();
    ComponentTypeSymbol t = compile("package a; component T { a.Q q1(5); a.Q q2(1); a.QD q3; }").getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    // When && Then
    assertThat(this.presenceResolver.presenceOf(t, t.getSubcomponents("q1").orElseThrow(), q.getPort("i").orElseThrow()).simplify().isTrue()).isTrue();
    assertThat(this.presenceResolver.presenceOf(t, t.getSubcomponents("q2").orElseThrow(), q.getPort("i").orElseThrow()).simplify().isFalse()).isTrue();
    assertThat(this.presenceResolver.presenceOf(t, t.getSubcomponents("q3").orElseThrow(), qd.getPort("i").orElseThrow()).simplify().isTrue()).isTrue();
  }

  @Test
  public void shouldSubstituteParametersOfSuperComponents() {
    // Given
    ComponentTypeSymbol a = compile("package a; component A(int y) { varif (y > 3) { port in int i; } }").getArcComponentType().getSymbol();
    ComponentTypeSymbol b = compile("package a; component B(int x) extends a.A(x + 1) { constraint (x == 3); }").getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    IntExpr x = z3.mkIntConst("x");

    // When
    BoolExpr i = this.presenceResolver.presenceOf(b, a.getPort("i").orElseThrow());

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(i,
      z3.mkAnd(z3.mkEq(x, z3.mkInt(3)), z3.mkGt(z3.mkAdd(x, z3.mkInt(1)), z3.mkInt(3))))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldTreatCyclicDefaultValuesAsUnknown() {
    // Given
    ComponentTypeSymbol cy = compile("package a; component Cy(int a = b, int b = a) { varif (a > 0) { port in int i; } }").getArcComponentType().getSymbol();
    ComponentTypeSymbol u = compile("package a; component U { a.Cy c; }").getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    // When
    BoolExpr i = this.presenceResolver.presenceOf(u, u.getSubcomponents("c").orElseThrow(), cy.getPort("i").orElseThrow());

    // Then
    assertThat(solver.check(i)).isEqualTo(UNKNOWN);
  }

  @Test
  public void shouldIncludeConstraintsOfSharedSuperComponentsOnEveryPath() {
    // Given
    compile("package a; component D(int n) { constraint (n > 0); }");
    compile("package a; component L(int x) extends a.D(x) { }");
    compile("package a; component R(int y) extends a.D(y - 5) { }");
    ComponentTypeSymbol diamond = compile("package a; component Diamond(int z) extends a.L(z), a.R(z) { }").getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    IntExpr z = z3.mkIntConst("z");

    // When
    BoolExpr constraints = this.presenceResolver.presenceOf(diamond);

    // Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(constraints,
      z3.mkAnd(z3.mkGt(z, z3.mkInt(0)), z3.mkGt(z3.mkSub(z, z3.mkInt(5)), z3.mkInt(0))))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldPresentInheritedMembersWhereNotRedeclared() {
    // Given
    ComponentTypeSymbol p = compile("package a; component P { port in int i; }").getArcComponentType().getSymbol();
    ComponentTypeSymbol c = compile("package a; component C extends a.P { feature f; varif (f) { port in boolean i; } }").getArcComponentType().getSymbol();
    List<PortSymbol> i = this.withinComponentResolver.resolveMany(c, "i", PortSymbol.class);

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    BoolExpr f = z3.mkBoolConst("f");

    // When && Then
    assertThat(i.size()).isEqualTo(2);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(c, c.getPort("i").orElseThrow()), f)))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(c, p.getPort("i").orElseThrow()), z3.mkNot(f))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldConditionConstraintsOnVarIfConditions() {
    // Given
    ComponentTypeSymbol g = compile("package a; component G { feature D, E; varif (D) { port in int i; constraint (E); } varif (E) { port in int j; } }")
      .getArcComponentType().getSymbol();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    BoolExpr d = z3.mkBoolConst("D");
    BoolExpr e = z3.mkBoolConst("E");

    // When && Then
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(g, g.getPort("j").orElseThrow()),
      z3.mkAnd(z3.mkOr(z3.mkNot(d), e), e))))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(g, g.getPort("i").orElseThrow()),
      z3.mkAnd(z3.mkOr(z3.mkNot(d), e), d))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldDecideUnconditionalMembersWithoutSolver() {
    // Given
    ComponentTypeSymbol k = compile("package a; component K { feature F; port in int a; varif (F) { port in int b; } }").getArcComponentType().getSymbol();
    ComponentTypeSymbol comp = compile(COMP).getArcComponentType().getSymbol();

    // When && Then
    assertThat(this.presenceResolver.isUnconditional(k, k.getPort("a").orElseThrow())).isTrue();
    assertThat(this.presenceResolver.isUnconditional(k, k.getPort("b").orElseThrow())).isFalse();
    assertThat(this.presenceResolver.isUnconditional(comp, ((IVariableArcScope) comp.getSpannedScope()).resolveArcFeatureLocally("A").orElseThrow())).isFalse();
  }
}
