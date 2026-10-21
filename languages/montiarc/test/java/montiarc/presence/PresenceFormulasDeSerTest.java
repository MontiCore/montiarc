/* (c) https://github.com/MontiCore/monticore */
package montiarc.presence;

import arcbasis.context.WithinComponentResolver;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.MontiArcSymbols2Json;
import org.junit.jupiter.api.Test;
import variablearc._symboltable.IVariableArcScope;
import variablearc.causality.CausalityResolver;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceFormulasDeSer;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static variablearc.smt.FormulaSolver.Satisfiability.UNSATISFIABLE;

/**
 * The class under test is {@link PresenceFormulasDeSer}.
 */
public class PresenceFormulasDeSerTest extends MontiArcTestBase {

  protected final WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
  protected final PresenceResolver presenceResolver = new PresenceResolver(this.withinComponentResolver);

  protected static final String MODEL = """
    package a;
    component D {
      feature d, e;
      varif (d) {
        port in int i;
        constraint (e);
      } else {
        port in boolean i;
      }
      varif (e) {
        port in int j;
      }
    }""";

  @Test
  public void shouldPreserveConditionalConstraintsAndSymbolPresence() {
    // Given
    ASTMACompilationUnit ast = compile(MODEL);
    String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());
    MontiArcMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(json));

    ComponentTypeSymbol loaded = MontiArcMill.globalScope().resolveComponentType("a.D").orElseThrow();
    List<PortSymbol> i = this.withinComponentResolver.resolveMany(loaded, "i", PortSymbol.class);
    PortSymbol intI = i.stream().filter(p -> p.getType().printFullName().equals("int")).findFirst().orElseThrow();
    PortSymbol boolI = i.stream().filter(p -> p.getType().printFullName().equals("boolean")).findFirst().orElseThrow();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    BoolExpr d = z3.mkBoolConst("d");
    BoolExpr e = z3.mkBoolConst("e");
    BoolExpr constraints = z3.mkOr(z3.mkNot(d), e);

    // When && Then
    assertThat(json).contains(PresenceFormulasDeSer.PRESENCE);
    assertThat(json).contains(PresenceFormulasDeSer.CONDITIONAL_SYMBOLS);
    assertThat(loaded.isPresentAstNode()).isFalse();
    assertThat(this.presenceResolver.formulasOf(loaded)).isPresent();
    assertThat(i.size()).isEqualTo(2);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(loaded), constraints)))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(loaded, intI), z3.mkAnd(constraints, d))))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(loaded, boolI), z3.mkAnd(constraints, z3.mkNot(d)))))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(this.presenceResolver.presenceOf(loaded, loaded.getPort("j").orElseThrow()), z3.mkAnd(constraints, e))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldPreserveConditionalFieldsAndSubcomponents() {
    // Given
    compile("package a; component S { }");
    ASTMACompilationUnit ast = compile(
      "package a; component F { feature d; varif (d) { int v = 0; } else { a.S sub; } }");
    String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());
    MontiArcMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(json));

    ComponentTypeSymbol loaded = MontiArcMill.globalScope().resolveComponentType("a.F").orElseThrow();

    FormulaSolver solver = new FormulaSolver();
    // When && Then
    assertThat(this.presenceResolver.presenceOf(loaded, loaded.getSpannedScope().resolveVariableLocally("v").orElseThrow()).toString()).isEqualTo("d");
    assertThat(this.presenceResolver.presenceOf(loaded, loaded.getSubcomponents("sub").orElseThrow()).toString()).isEqualTo("(not d)");
  }

  @Test
  public void shouldResolveTypesOfConditionalSubcomponents() {
    // Given
    ASTMACompilationUnit s = compile("package a; component S { port in int x; }");
    ASTMACompilationUnit f = compile("package a; component F { feature d; varif (d) { a.S sub; } }");
    String sJson = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) s.getEnclosingScope());
    String fJson = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) f.getEnclosingScope());
    MontiArcMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(sJson));
    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(fJson));

    SubcomponentSymbol sub = MontiArcMill.globalScope().resolveComponentType("a.F").orElseThrow().getSubcomponents("sub").orElseThrow();

    // When && Then
    assertThat(sub.getType().getTypeInfo().getFullName()).isEqualTo("a.S");
    assertThat(sub.getType().getTypeInfo().getPorts().size()).isEqualTo(1);
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void shouldKeepBranchesOfConditionsWithUnknownValuesExclusive() {
    // Given
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    ASTMACompilationUnit ast = compile(
      "package a; component T(int n) { varif (java.lang.Math.abs(n) > 3) { port in int i; } else { port in int i; } }");
    String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());
    MontiArcMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(json));

    ComponentTypeSymbol loaded = MontiArcMill.globalScope().resolveComponentType("a.T").orElseThrow();
    List<PortSymbol> i = this.withinComponentResolver.resolveMany(loaded, "i", PortSymbol.class);

    // When && Then
    FormulaSolver solver = new FormulaSolver();
    assertThat(solver.check(solver.not(solver.or(
      this.presenceResolver.presenceOf(loaded, i.get(0)), this.presenceResolver.presenceOf(loaded, i.get(1)))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldSerializeAlwaysPresentSymbolsInTheSpannedScope() {
    // Given
    ASTMACompilationUnit ast = compile("package a; component N { feature d; port in int i; constraint (d); }");

    // When
    String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());

    // Then
    assertThat(json).contains(PresenceFormulasDeSer.PRESENCE);
    assertThat(json).doesNotContain(PresenceFormulasDeSer.CONDITIONAL_SYMBOLS);
  }

  @Test
  public void shouldPreserveDelayedBehaviors() {
    // Given
    ASTMACompilationUnit ast = compile(
      "package a; component L { port in int i; port out int o; feature ff; varif (!ff) { <<delayed>> compute { } } }");
    String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());
    MontiArcMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(json));

    ComponentTypeSymbol loaded = MontiArcMill.globalScope().resolveComponentType("a.L").orElseThrow();

    FormulaSolver solver = new FormulaSolver();
    Context z3 = solver.getContext();
    CausalityResolver causalityResolver = new CausalityResolver(this.withinComponentResolver, this.presenceResolver);

    // When && Then
    assertThat(json).contains(PresenceFormulasDeSer.DELAYED);
    assertThat(loaded.isPresentAstNode()).isFalse();
    assertThat(solver.check(z3.mkNot(z3.mkEq(causalityResolver.delayOf(loaded), z3.mkNot(z3.mkBoolConst("ff")))))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(z3.mkNot(z3.mkEq(causalityResolver.effectOf(solver, loaded, loaded.getPort("i").orElseThrow(),
      loaded.getPort("o").orElseThrow()), z3.mkBoolConst("ff"))))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  public void shouldPreserveConditionalFeaturesAndInnerComponentTypes() {
    // Given
    ASTMACompilationUnit ast = compile("""
      package a;
      component G {
        feature d;
        varif (d) {
          feature f;
          component Inner inner {
            feature g;
            varif (g) {
              port in int p;
            }
          }
        }
      }""");
    String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());
    MontiArcMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(json));

    ComponentTypeSymbol loaded = MontiArcMill.globalScope().resolveComponentType("a.G").orElseThrow();
    ComponentTypeSymbol inner = loaded.getSpannedScope().resolveComponentTypeLocally("Inner").orElseThrow();

    // When && Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.presenceResolver.presenceOf(loaded,
      ((IVariableArcScope) loaded.getSpannedScope()).resolveArcFeatureLocally("f").orElseThrow()).toString()).isEqualTo("d");
    assertThat(this.presenceResolver.presenceOf(loaded, inner).toString()).isEqualTo("d");
    assertThat(this.presenceResolver.presenceOf(loaded, loaded.getSubcomponents("inner").orElseThrow()).toString()).isEqualTo("d");
    assertThat(this.presenceResolver.presenceOf(inner, inner.getPort("p").orElseThrow()).toString()).isEqualTo("g");
  }
}
