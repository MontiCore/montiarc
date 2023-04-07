/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoInputPortsInInitialOutputDeclaration;
import arcbasis._cocos.ComponentArgumentsOmitPortRef;
import arcbasis._cocos.FieldInitOmitPortReferences;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc.util.ArcAutomataError;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

public class StaticContextTest extends MontiArcAbstractTest {

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    IMontiArcArtifactScope as1 = MontiArcMill.artifactScope();
    OOTypeSymbol type1 = MontiArcMill.oOTypeSymbolBuilder()
      .setName("T")
      .setPackageName("i")
      .setIsEnum(false)
      .setIsPublic(true)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    as1.add(type1);
    as1.setPackageName("i");
    type1.setEnclosingScope(as1);
    FieldSymbol field1 = MontiArcMill.fieldSymbolBuilder()
      .setName("a")
      .setIsPublic(true)
      .setIsStatic(true)
      .setIsFinal(true)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .build();
    type1.getSpannedScope().add(field1);
    field1.setEnclosingScope(type1.getSpannedScope());

    IMontiArcArtifactScope as2 = MontiArcMill.artifactScope();
    OOTypeSymbol type2 = MontiArcMill.oOTypeSymbolBuilder()
      .setName("T")
      .setPackageName("sub.i")
      .setIsEnum(false)
      .setIsPublic(true)
      .setSpannedScope(MontiArcMill.scope())
      .build();
    as2.add(type2);
    as2.setPackageName("sub.i");
    type2.setEnclosingScope(as2);
    FieldSymbol field2 = MontiArcMill.fieldSymbolBuilder()
      .setName("a")
      .setIsPublic(true)
      .setIsStatic(true)
      .setIsFinal(true)
      .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
      .build();
    type2.getSpannedScope().add(field2);
    field2.setEnclosingScope(type2.getSpannedScope());

    MontiArcMill.globalScope().addSubScope(as1);
    MontiArcMill.globalScope().addSubScope(as2);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no ports, no expressions
    "component Comp1 { }",
    // no expressions
    "component Comp2 { port in int i; port out int o; }",
    // parameter (port like reference)
    //"component Comp3(int p = i.T.a) { port in int i; }",
    // inner with parameter (port like reference)
    //"component Comp4 { component Inner(int p = i.T.a) { port in int i; } }",
    // inner with parameter (outer port like reference)
    "component Comp5 { port in int i; component Inner(int p = i.T.a) { } }",
    // parameter (sub port like reference)
    "component Comp6(int p = sub.i.T.a) { component Inner { port in int i; } Inner sub; }",
    // inner with parameter (sub port like reference)
    "component Comp7 { component Inner(int p = sub.i.T.a) { port in int i; } Inner sub; }",
    // inner with parameter (outer sub port like reference)
    "component Comp8 { component Inner(int p = sub.i.T.a) { component Inner { port in int i; } Inner sub; } }",
    // field init (port like reference)
    //"component Comp9 { port in int i; int var = i.T.a; }",
    // field init (sub port like reference)
    "component Comp10 { component Inner { port in int i; } Inner sub; int var = sub.i.T.a; }",
    // state init (port line reference)
    //"component Comp11 { port in int i; automaton { initial { int var = i.T.a; } state s; } }",
    // state init (sub port line reference)
    "component Comp12 { component Inner { port in int i; } Inner sub; automaton { initial { int var = sub.i.T.a; } state s; } }"
  })
  public void shouldReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentArgumentsOmitPortRef());
    checker.addCoCo(new FieldInitOmitPortReferences());
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentArgumentsOmitPortRef());
    checker.addCoCo(new FieldInitOmitPortReferences());
    checker.addCoCo(new NoInputPortsInInitialOutputDeclaration());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // parameter (port reference)
      arg("component Comp1 { " +
          "port in int i; " +
          "component Inner(int p) { } " +
          "Inner sub(i); " +
          "}",
        ArcError.COMP_ARG_PORT_REF),
      // field init (port reference)
      arg("component Comp2 { " +
          "port in int i; " +
          "int var = i; " +
          "}",
        ArcError.PORT_REF_FIELD_INIT),
      // state init (port reference)
      arg("component Comp3 { " +
          "port in int i; " +
          "automaton { " +
          "initial { int var = i; } state s; " +
          "} " +
          "}",
        ArcAutomataError.INPUT_PORT_IN_INITIAL_OUT_DECL)
    );
  }
}
