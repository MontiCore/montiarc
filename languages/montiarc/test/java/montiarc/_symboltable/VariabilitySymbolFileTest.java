/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis.context.WithinComponentResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCoChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variablearc._cocos.ConnectorPortsExist4Family;
import variablearc._cocos.FeedbackStrongCausality4Family;
import variablearc._cocos.SubcomponentConstraintsImplied;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariableSymbolDeSer;
import variablearc.causality.CausalityResolver;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.List;

import static montiarc.util.ArcError.FEEDBACK_CAUSALITY;
import static montiarc.util.VariableArcError.PRESENCE_NOT_DESERIALIZABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static variablearc.smt.FormulaSolver.Satisfiability.UNSATISFIABLE;

/**
 * The classes under test are {@link MontiArcComponentTypeSymbolDeSer},
 * {@link MontiArcSubcomponentSymbolDeSer}, and
 * {@link VariableArcVariableSymbolDeSer}.
 */
public class VariabilitySymbolFileTest extends MontiArcTestBase {

  protected final WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
  protected final PresenceResolver presenceResolver = new PresenceResolver(this.withinComponentResolver);

  /**
   * Adds the enumeration {@code OnOff} to the global scope, which is not
   * serialized with the components
   */
  protected static void addEnumeration() {
    OOTypeSymbol onOff = MontiArcMill.oOTypeSymbolBuilder()
      .setName("OnOff").setIsEnum(true).setIsPublic(true)
      .setSpannedScope(MontiArcMill.scope()).build();
    for (String constant : List.of("ON", "OFF")) {
      onOff.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder()
        .setName(constant).setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true)
        .setType(SymTypeExpressionFactory.createTypeObject(onOff)).build());
    }
    MontiArcMill.globalScope().add(onOff);
  }

  @BeforeEach
  void loadLibraryFromSymbolFiles() {
    addEnumeration();
    List<ASTMACompilationUnit> library = new ArrayList<>();
    library.add(compile("package a; component B(boolean p) { varif (p) { port out int x; } }"));
    library.add(compile("package a; component C extends a.B(true) { }"));
    library.add(compile("package a; component W { a.B b(true); }"));
    library.add(compile("package a; component B2(boolean p, boolean q) { varif (p && !q) { port out int x; } }"));
    library.add(compile("package a; component W2 { a.B2 b(q = false, p = true); }"));
    library.add(compile("package a; component Sub(int n = 11) { constraint (n > 10); }"));
    library.add(compile("package a; component Q(int n = 5) { varif (n > 3) { port in int i; } }"));
    library.add(compile("package a; component Inner { port in int x; port out int y; compute { y = x; } }"));
    library.add(compile("package a; component F { feature f; port in int i; port out int o; a.Inner a; varif (f) { i -> a.x; a.y -> o; } }"));
    library.add(compile("package a; component G { feature f; constraint (f); }"));
    library.add(compile("package a; component E(OnOff s = OnOff.ON) { }"));
    library.add(compile("package a; component E2(OnOff s = OnOff.ON) { varif (s == OnOff.ON) { port in int i; } }"));
    library.getLast().getArcComponentType().getSpannedScope().resolveVariableMany("f");
    List<String> symbolFiles = library.stream()
      .map(ast -> new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope()))
      .toList();
    MontiArcMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    addEnumeration();
    symbolFiles.forEach(json -> MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(json)));
    Log.clearFindings();
  }

  @Test
  void shouldBindArgumentsOfSuperComponents() {
    // Given
    ASTMACompilationUnit ast = compile("component Top { port out int o; a.C c; c.x -> o; }");
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorPortsExist4Family(this.withinComponentResolver, this.presenceResolver));

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void shouldBindArgumentsOfSubcomponents() {
    // Given
    ComponentTypeSymbol w = MontiArcMill.globalScope().resolveComponentType("a.W").orElseThrow();
    ComponentTypeSymbol b = MontiArcMill.globalScope().resolveComponentType("a.B").orElseThrow();

    // When && Then
    FormulaSolver solver = new FormulaSolver();
    assertThat(solver.check(solver.not(this.presenceResolver.presenceOf(w,
      w.getSubcomponents("b").orElseThrow(), b.getPort("x").orElseThrow())))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  void shouldBindNamedArguments() {
    // Given
    ComponentTypeSymbol w = MontiArcMill.globalScope().resolveComponentType("a.W2").orElseThrow();
    ComponentTypeSymbol b = MontiArcMill.globalScope().resolveComponentType("a.B2").orElseThrow();

    // When && Then
    FormulaSolver solver = new FormulaSolver();
    assertThat(solver.check(solver.not(this.presenceResolver.presenceOf(w,
      w.getSubcomponents("b").orElseThrow(), b.getPort("x").orElseThrow())))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  void shouldPreserveDefaultValues() {
    // Given
    ComponentTypeSymbol sub = MontiArcMill.globalScope().resolveComponentType("a.Sub").orElseThrow();

    // When && Then
    assertThat(sub.isPresentAstNode()).isFalse();
    assertThat(((IVariableArcComponentTypeSymbol) sub).getParameterDefault("n")
      .map(e -> MontiArcMill.prettyPrint(e, false).trim()).orElseThrow()).isEqualTo("11");
  }

  @Test
  void shouldImplyConstraintsSatisfiedByDefaultValues() {
    // Given
    ASTMACompilationUnit ast = compile("component U { a.Sub s; }");
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentConstraintsImplied(this.withinComponentResolver, this.presenceResolver));

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void shouldDecidePresenceByDefaultValues() {
    // Given
    ComponentTypeSymbol t = compile("component T { a.Q q; }").getArcComponentType().getSymbol();
    ComponentTypeSymbol q = MontiArcMill.globalScope().resolveComponentType("a.Q").orElseThrow();

    // When && Then
    FormulaSolver solver = new FormulaSolver();
    assertThat(solver.check(solver.not(this.presenceResolver.presenceOf(t,
      t.getSubcomponents("q").orElseThrow(), q.getPort("i").orElseThrow())))).isEqualTo(UNSATISFIABLE);
  }

  @Test
  void shouldReportFeedbackThroughConnectorsInVariationPoints() {
    // Given
    ASTMACompilationUnit ast = compile("component Top { a.F w; w.o -> w.i; }");
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackStrongCausality4Family(this.withinComponentResolver, this.presenceResolver,
      new CausalityResolver(this.withinComponentResolver, this.presenceResolver)));

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).filteredOn(Finding::isError).extracting(Finding::getMsg)
      .singleElement().matches(msg -> msg.startsWith(FEEDBACK_CAUSALITY.getErrorCode()));
  }

  @Test
  void shouldPreserveEnumerationValues() {
    // Given
    ComponentTypeSymbol e = MontiArcMill.globalScope().resolveComponentType("a.E").orElseThrow();

    // When && Then
    assertThat(this.presenceResolver.formulasOf(e)).isPresent();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  void shouldDecidePresenceByEnumerationArguments() {
    // Given
    ComponentTypeSymbol t = compile("component T { a.E2 on; a.E2 off(OnOff.OFF); }").getArcComponentType().getSymbol();
    ComponentTypeSymbol e = MontiArcMill.globalScope().resolveComponentType("a.E2").orElseThrow();

    // When && Then
    FormulaSolver solver = new FormulaSolver();
    assertThat(solver.check(solver.not(this.presenceResolver.presenceOf(t,
      t.getSubcomponents("on").orElseThrow(), e.getPort("i").orElseThrow())))).isEqualTo(UNSATISFIABLE);
    assertThat(solver.check(this.presenceResolver.presenceOf(t,
      t.getSubcomponents("off").orElseThrow(), e.getPort("i").orElseThrow()))).isEqualTo(UNSATISFIABLE);
    assertThat(Log.getFindings()).isEmpty();
  }

  /**
   * Loads the symbol file of the model, in which the target is replaced, into
   * the cleared global scope.
   */
  protected void loadModified(String model, String target, String replacement) {
    ASTMACompilationUnit ast = compile(model);
    String json = new MontiArcSymbols2Json().serialize((IMontiArcArtifactScope) ast.getEnclosingScope());
    assertThat(json).contains(target);
    MontiArcMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addSubScope(new MontiArcSymbols2Json().deserialize(json.replace(target, replacement)));
  }

  @Test
  void shouldReportSymbolFilesOfOtherFormatVersions() {
    // When
    loadModified("package b; component V(boolean p) { varif (p) { port in int i; } }", "\"version\":1", "\"version\":0");

    // Then
    assertThat(Log.getFindings()).filteredOn(Finding::isError).extracting(Finding::getMsg)
      .singleElement().matches(msg -> msg.startsWith(PRESENCE_NOT_DESERIALIZABLE.getErrorCode()));
  }

  @Test
  void shouldReportMalformedPresenceFormulas() {
    // When
    loadModified("package b; component V(boolean p) { constraint (p); }", "\"constraints\":\"p\"", "\"constraints\":\"(and p\"");

    // Then
    assertThat(Log.getFindings()).filteredOn(Finding::isError).extracting(Finding::getMsg)
      .singleElement().matches(msg -> msg.startsWith(PRESENCE_NOT_DESERIALIZABLE.getErrorCode()));
  }

  @Test
  void shouldNotSerializeFeaturesAsFields() {
    // Given
    ComponentTypeSymbol g = MontiArcMill.globalScope().resolveComponentType("a.G").orElseThrow();

    // When && Then
    assertThat(g.getFields()).isEmpty();
    assertThat(g.getSpannedScope().resolveVariableMany("f").size()).isEqualTo(1);
  }
}
