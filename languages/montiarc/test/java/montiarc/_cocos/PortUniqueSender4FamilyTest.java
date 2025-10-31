/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.PortUniqueSender4Family;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PortUniqueSender4FamilyTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setupEnums();
    setupComponents();
  }

  protected void setupEnums() {
    OOTypeSymbol onOffEnumType = MontiArcMill.oOTypeSymbolBuilder().setIsEnum(true).setName("OnOff").setIsPublic(true).setSpannedScope(MontiArcMill.scope()).build();
    onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    MontiArcMill.globalScope().add(onOffEnumType);
  }

  protected void setupComponents() {
    compile("package a.b; component A { port in int i; }");
    compile("package a.b; component B { port out int o; }");
    compile("package a.b; component C { port in int i; port out int o; }");
    compile("package a.b; component Z { a.b.B b1; feature f1;  port in int i; port out int o; varif(f1){port out int k; o -> b1.o;} }");
  }

  private static Stream<Arguments> provideUniqueSenderModel() {
    List<Arguments> componentList = new ArrayList<>();
    Arguments simpleModel = arg("component Comp4 { " +
      "feature f1;" +
      "port out int o; " +
      "a.b.B b; " +
      "varif(f1){" +
      "a.b.A a;" +
      "}" +
      "else {" +
      "b.o -> o; " +
      "b.o -> o;" +
      "} " +
      "constraint(!f1);" +
      "}");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcVariantCoCoChecker checker = new MontiArcVariantCoCoChecker();
    addCoCoAs(new PortUniqueSender4Family(), checker::addCoCo);

    ASTMACompilationUnit mainAST = compile(model);
    checker.checkAll(mainAST);

    String[] test = getLoggedErrorCodes();

    // Then
    assertThat(Log.getErrorCount() == 0);
  }

  private static <T> void addCoCoAs(T coco, Consumer<T> consumer) {
    consumer.accept(coco);
  }



  @ParameterizedTest
  @ValueSource(strings = {
    // no ports or connectors
    "component Comp1 { }",
    // single in port forward
    "component Comp2 { " +
      "port in int i; " +
      "a.b.A a; " +
      "i -> a.i; " +
      "}",
    // multiple targets in port forward
    "component Comp3 { " +
      "port in int i; " +
      "a.b.A a1, a2; " +
      "i -> a1.i, a2.i; " +
      "}",
    // multiple in port forwards (different targets)
    "component Comp4 { " +
      "port in int i; " +
      "a.b.A a1, a2; " +
      "i -> a1.i;" +
      "i -> a2.i; " +
      "}",
    // single out port forward
    "component Comp5 { " +
      "port out int o; " +
      "a.b.B b; " +
      "b.o -> o; " +
      "}",
    // multiple targets out port forward
    "component Comp6 { " +
      "port out int o1, o2; " +
      "a.b.B b; " +
      "b.o -> o1, o2; " +
      "}",
    // multiple out port forwards (different targets)
    "component Comp7 { " +
      "port out int o1, o2; " +
      "a.b.B b1, b2; " +
      "b1.o -> o1;" +
      "b2.o -> o2; " +
      "}",
    // single hidden connector
    "component Comp8 { " +
      "a.b.A a;" +
      "a.b.B b; " +
      "b.o -> a.i; " +
      "}",
    // multiple targets hidden connector
    "component Comp9 { " +
      "a.b.A a1, a2;" +
      "a.b.B b; " +
      "b.o -> a1.i, a2.i; " +
      "}",
    // multiple hidden connectors (different targets)
    "component Comp10 { " +
      "a.b.A a1, a2;" +
      "a.b.B b1, b2; " +
      "b1.o -> a1.i; " +
      "b2.o -> a2.i; " +
      "}",
    // multiple connectors (different targets)
    "component Comp11 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.C c; " +
      "i -> c.i; " +
      "c.o -> o; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortUniqueSender4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no ports or connectors
    "component Comp1 { feature f1; }",
    // single in port forward
    "component Comp2 { " +
      "feature f1;" +
      "varif(f1){ port in int i; }" +
      "a.b.A a; " +
      "varif(f1){ i -> a.i; }" +
      "}",
    // multiple targets in port forward
    "component Comp3 { " +
      "feature f1;" +
      "varif(f1){ port in int i; }" +
      "a.b.A a1, a2; " +
      "varif(f1){ i -> a1.i, a2.i; }" +
      "constraint(f1);" +
      "}",
    // multiple in port forwards (different targets)
    "component Comp4 { " +
      "feature f1;" +
      "port in int i; " +
      "a.b.A a1, a2; " +
      "varif(f1){ i -> a1.i; i -> a1.i;}" +
      "else{" +
      "i -> a1.i;" +
      "i -> a2.i; " +
      "}" +
      "constraint(!f1);" +
      "}",
    // single out port forward
    "component Comp5 { " +
      "feature f1,f2;" +
      "varif(f1){ port out int o; }" +
      "a.b.B b; " +
      "varif(f2){ b.o -> o; }" +
      "constraint((f1 && f2)||(!f1 && !f2));" +
      "}",
    // multiple targets out port forward
    "component Comp6 { " +
      "feature f1;" +
      "port out int o1, o2; " +
      "a.b.B b; " +
      "varif(f1){b.o -> o1, o2; }" +
      "}",
    // multiple out port forwards (different targets)
    "component Comp7 { " +
      "feature f1,f2;" +
      "port out int o1, o2; " +
      "a.b.B b1, b2; " +
      "varif(f1){ b1.o -> o1; }" +
      "varif(f2){ b2.o -> o2; } " +
      "constraint(f1 || f2);" +
      "}",
    // single hidden connector
    "component Comp8 { " +
      "feature f1,f2;" +
      "varif(f1){ a.b.A a; }" +
      "varif(f2){ a.b.B b; }" +
      "b.o -> a.i; " +
      "constraint(f1 && f2);" +
      "}",
    // multiple targets hidden connector
    "component Comp9 { " +
      "a.b.A a1, a2;" +
      "a.b.B b; " +
      "b.o -> a1.i, a2.i; " +
      "}",
    // multiple hidden connectors (different targets)
    "component Comp10 { " +
      "feature f1,f2;" +
      "varif(f1){" +
      "a.b.A a1, a2;" +
      "a.b.B b1, b2; }" +
      "varif(f2){" +
      "b1.o -> a1.i; " +
      "b2.o -> a2.i; }" +
      "constraint(!f2 || (f1 && f2));" +
      "}",
    // multiple connectors (different targets)
    "component Comp11 { " +
      "feature f1,f2,f3,f4;" +
      "varif(f1){ port in int i; }" +
      "varif(f2){ port out int o; } " +
      "a.b.C c; " +
      "varif(f3){ i -> c.i; }" +
      "varif(f4){ c.o -> o; }" +
      "constraint((!f3 && !f4) || (f1 && f2));" +
      "}"
  })
  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new PortUniqueSender4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortUniqueSender4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // multiple in port forward (same target)
      arg("component Comp1 {" +
          "port in int i1, i2; " +
          "a.b.A a; " +
          "i1 -> a.i; " +
          "i2 -> a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple out port forward (same target)
      arg("component Comp2 {" +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "b1.o -> o; " +
          "b2.o -> o; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // redundant in port forward
      arg("component Comp3 { " +
          "port in int i; " +
          "a.b.A a; " +
          "i -> a.i; " +
          "i -> a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // redundant out port forward
      arg("component Comp4 { " +
          "port out int o; " +
          "a.b.B b; " +
          "b.o -> o; " +
          "b.o -> o; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple targets hidden connector (same target)
      arg("component Comp5 {" +
          "a.b.B b; " +
          "a.b.A a; " +
          "b.o -> a.i, a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple hidden connector (same target)
      arg("component Comp6 {" +
          "a.b.B b1, b2; " +
          "a.b.A a; " +
          "b1.o -> a.i; " +
          "b2.o -> a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // three in port forward (same target)
      arg("component Comp7 {" +
          "port in int i1, i2, i3; " +
          "a.b.A a; " +
          "i1 -> a.i; " +
          "i2 -> a.i; " +
          "i3 -> a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple out port forward (same target)
      arg("component Comp8 {" +
          "port out int o; " +
          "a.b.B b1, b2, b3; " +
          "b1.o -> o; " +
          "b2.o -> o; " +
          "b3.o -> o; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModelsWithVariability")
  public void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new PortUniqueSender4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // multiple in port forward (same target)
      arg("component Comp1 {" +
          "feature f1;" +
          "port in int i1, i2; " +
          "a.b.A a; " +
          "varif(f1){" +
          "i1 -> a.i; " +
          "i2 -> a.i; " +
          "}" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple out port forward (same target)
      arg("component Comp2 {" +
          "feature f1;" +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "varif(f1){" +
          "b1.o -> o; " +
          "b2.o -> o; " +
          "}" +
          "constraint(f1);" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // redundant in port forward
      arg("component Comp3 { " +
          "feature f1,f2;" +
          "port in int i; " +
          "a.b.A a; " +
          "varif(f1){ i -> a.i; }" +
          "varif(f2){ i -> a.i; }" +
          "constraint(f1 && f2);" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // redundant out port forward
      arg("component Comp4 { " +
          "feature f1;" +
          "port out int o; " +
          "a.b.B b; " +
          "varif(f1){" +
          "a.b.A a;" +
          "}" +
          "else {" +
          "b.o -> o; " +
          "b.o -> o;" +
          "} " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple targets hidden connector (same target)
      arg("component Comp5 {" +
          "feature f1;" +
          "a.b.B b; " +
          "a.b.A a; " +
          "varif(!f1){ a.b.A a2;  }" +
          "else { b.o -> a.i, a.i; }" +
          "constraint(f1);" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple hidden connector (same target)
      arg("component Comp6 {" +
          "feature f1,f2;" +
          "a.b.B b1, b2; " +
          "a.b.A a; " +
          "varif(f1){ b1.o -> a.i; }" +
          "varif(f2){ b2.o -> a.i; }" +
          "constraint((f1 && f2) || (!f1 && !f2));" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // three in port forward (same target)
      arg("component Comp7 {" +
          "feature f1,f2,f3;" +
          "port in int i1, i2, i3; " +
          "a.b.A a; " +
          "varif(f1){ i1 -> a.i; }" +
          "varif(f2){ i2 -> a.i; }" +
          "varif(f3){ i3 -> a.i; }" +
          "constraint(f1 && f2 && f3);" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple out port forward (same target)
      arg("component Comp8 {" +
          "feature f1,f2;" +
          "port out int o; " +
          "a.b.B b1, b2, b3; " +
          "varif(f1){ b1.o -> o; }" +
          "varif(f2){ b2.o -> o; " +
          "b3.o -> o; }" +
          "constraint(f1 && f2);" +
          "}",
        ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER)
    );
  }

}
