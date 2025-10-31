/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorTypesFit;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectorTypesFit4FamilyTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B {port in boolean i;}");
    compile("package a.b; component C { port out boolean o; }");
    compile("package a.b; component D { port in int i; }");
    compile("package a.b; component E { port out int o; }");
    compile("package a.b; component F { port in java.lang.Integer i; }");
    compile("package a.b; component G { port in java.util.List<java.lang.Integer> i; }");
    compile("package a.b; component H { port out java.util.List<java.lang.Integer> o; }");
    compile("package a.b; component I { port in java.lang.Comparable<java.lang.Integer> i; }");
    compile("package a.b; component J { port out java.lang.Comparable<java.lang.Integer> o; }");
    compile("package a.b; component K<T> { port in T i; } ");
    compile("package a.b; component L<T> { port out T o; }");
  }

  private static Stream<Arguments> provideUniqueSenderModel() {
    List<Arguments> componentList = new ArrayList<>();
    Arguments simpleModel = arg("component Comp14<U, V> { " +
      "a.b.K<U> sub1; " +
      "a.b.L<V> sub2; " +
      "sub2.o -> sub1.i; " +
      "}");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcVariantCoCoChecker checker = new MontiArcVariantCoCoChecker();
    addCoCoAs(new ConnectorTypesFit4Family(), checker::addCoCo);

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
    // no connectors
    "component Comp1 { }",
    // no connectors with subcomponent
    "component Comp2 { " +
      "a.b.A sub; " +
      "}",
    // boolean -> boolean (in port forward)
    "component Comp3 { " +
      "port in boolean i; " +
      "a.b.B sub; " +
      "i -> sub.i; " +
      "}",
    // boolean -> boolean (out port forward)
    "component Comp4 { " +
      "port out boolean o; " +
      "a.b.C sub; " +
      "sub.o -> o; " +
      "}",
    // boolean -> boolean (hidden channel)
    "component Comp5 { " +
      "a.b.B sub1; " +
      "a.b.C sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // int -> int (in port forward)
    "component Comp6 { " +
      "port in int i; " +
      "a.b.D sub; " +
      "i -> sub.i; " +
      "}",
    // int -> int (out port forward)
    "component Comp7 { " +
      "port out int o; " +
      "a.b.E sub; " +
      "sub.o -> o; " +
      "}",
    // int -> int (hidden channel)
    "component Comp8 { " +
      "a.b.D sub1; " +
      "a.b.E sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // java.lang.Integer -> java.lang.Integer (in port forward)
    "component Comp9 { " +
      "port in java.lang.Integer i; " +
      "a.b.F sub; " +
      "i -> sub.i; " +
      "}",
    // java.util.List<java.lang.Integer> -> java.util.List<java.lang.Integer> (in port forward)
    "component Comp10 { " +
      "port in java.util.List<java.lang.Integer> i; " +
      "a.b.G sub; " +
      "i -> sub.i; " +
      "}",
    // java.util.List<java.lang.Integer> -> java.util.List<java.lang.Integer> (out port forward)
    "component Comp11 { " +
      "port in java.util.List<java.lang.Integer> o; " +
      "a.b.H sub; " +
      "sub.o -> o; " +
      "}",
    // java.util.List -> java.util.List<java.lang.Integer> (unchecked assignment)
    /*"component Comp12 { " +
      "port in java.util.List i; " +
      "a.b.G sub; " +
      "i -> sub.i; " +
      "}",*/
    // java.util.List<java.lang.Integer> -> java.util.List (type erasure)
    /*"component Comp13 { " +
      "port in java.util.List o; " +
      "a.b.H sub; " +
      "sub.o -> o; " +
      "}",*/
    // java.util.Integer -> java.lang.Comparable<java.lang.Integer> (in port forward,  super type conversion)
    "component Comp14 { " +
      "port in java.lang.Integer i; " +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "}",
    // java.lang.Comparable<java.lang.Integer> -> java.lang.Comparable<java.lang.Integer> (in port forward)
    "component Comp15 { " +
      "port in java.lang.Comparable<java.lang.Integer> i; " +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "}",
    // java.lang.Comparable<java.lang.Integer> -> java.lang.Comparable<java.lang.Integer> (out port forward)
    "component Comp16 { " +
      "port in java.lang.Comparable<java.lang.Integer> o; " +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "}",
    // java.lang.Integer -> java.lang.Integer (in port forward, type parameter)
    "component Comp17 { " +
      "port in java.lang.Integer i; " +
      "a.b.K<java.lang.Integer> sub; " +
      "i -> sub.i; " +
      "}",
    // java.lang.Integer -> java.lang.Integer (out port forward, type parameter)
    "component Comp18 { " +
      "port in java.lang.Integer o; " +
      "a.b.L<java.lang.Integer> sub; " +
      "o -> sub.o; " +
      "}",
    // S -> S (in port forward, type parameter)
    "component Comp19<S> { " +
      "port in S i; " +
      "a.b.K<S> sub; " +
      "i -> sub.i; " +
      "}",
    // S -> S (out port forward, type parameter)
    "component Comp20<S> { " +
      "port out S o; " +
      "a.b.L<S> sub; " +
      "sub.o -> o; " +
      "}",
    // T -> T (in port forward, type parameter)
    "component Comp21<T> { " +
      "port in T i; " +
      "a.b.K<T> sub; " +
      "i -> sub.i; " +
      "}",
    // T -> T (out port forward, type parameter)
    "component Comp22<T> { " +
      "port out T o; " +
      "a.b.L<T> sub; " +
      "sub.o -> o; " +
      "}",
    // S -> S (hidden channel, type parameter)
    "component Comp23<S> { " +
      "a.b.K<S> sub1; " +
      "a.b.L<S> sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // int -> int, int (in port forward, multiple targets)
    "component Comp24 { " +
      "port in int i; " +
      "a.b.D sub1, sub2; " +
      "i -> sub1.i, sub2.i; " +
      "}",
    // int -> int, int (out port forward, multiple targets)
    "component Comp25 { " +
      "port out int o1, o2; " +
      "a.b.E sub; " +
      "sub.o -> o1, o2; " +
      "}",
    // int -> int, int (hidden channel, multiple targets)
    "component Comp26 { " +
      "a.b.D sub1; " +
      "a.b.E sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // T -> T (inner component)
    "component Comp27<T> { " +
      "port in T i; " +
      "component Inner<T> { " +
      "port in T i; " +
      "} " +
      "Inner<T> sub; " +
      "i -> sub.i; " +
      "}",
    // S -> S (inner component)
    "component Comp28<S> { " +
      "port in S i; " +
      "component Inner<T> { " +
      "port in T i; " +
      "} " +
      "Inner<S> sub; " +
      "i -> sub.i; " +
      "}",
    // int -> int (inherited generic port)
    "component Comp29 extends a.b.K<int> { " +
      "a.b.D sub; " +
      "i -> sub.i; " +
      "}",
    // T -> T (with upper bound for T)
    "component Comp30<T extends int> { " +
      "port in T i; " +
      "a.b.K<T> sub; " +
      "i -> sub.i; " +
      "}",
    // java.lang.Boolean -> boolean
    "component Comp19 { " +
      "port in java.lang.Boolean i; " +
      "a.b.B sub; " +
      "i -> sub.i; " +
      "}",
    // boolean -> java.lang.Boolean
    "component Comp20 { " +
      "port out java.lang.Boolean o; " +
      "a.b.C sub; " +
      "sub.o -> o; " +
      "}",
    // java.lang.Integer -> int (in port forward)
    "component Comp21 { " +
      "port in java.lang.Integer i; " +
      "a.b.D sub; " +
      "i -> sub.i; " +
      "}",
    // int -> java.lang.Integer (out port forward)
    "component Comp22 { " +
      "port out java.lang.Integer o; " +
      "a.b.E sub; " +
      "sub.o -> o; " +
      "}",
    // int -> java.lang.Integer (in port forward)
    "component Comp23 { " +
      "port in int i; " +
      "a.b.F sub; " +
      "i -> sub.i; " +
      "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorTypesFit4Family());

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
    checker.addCoCo(new ConnectorTypesFit4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }


  @ParameterizedTest
  @MethodSource("invalidModelsWithVariability")
  public void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorTypesFit4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  @Test
  public void shouldNotReportConnectorTypeMismatchWhenPortTypeIsMissing() throws IOException {
    String model = "component MissingPortType {\n" +
      "  port in Missing i;\n" +
      "  port out Missing o;\n" +
      "\n" +
      "  component Inner {\n" +
      "    port in int i;\n" +
      "    port out int o;\n" +
      "  }\n" +
      "\n" +
      "  Inner sub;\n" +
      "\n" +
      "  i -> sub.i;\n" +
      "  sub.o -> o;\n" +
      "}";
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorTypesFit4Family());

    // When
    checker.checkAll(ast);

    // Then: Should only report missing type, NOT type mismatch
    assertThat(getLoggedErrorCodes())
      .doesNotContain(ArcError.CONNECTOR_TYPE_MISMATCH.getErrorCode());
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // int -> boolean (in port forward)
      arg("component Comp1 { " +
          "port in int i; " +
          "a.b.B sub; " +
          "i -> sub.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // boolean -> int (out port forward)
      arg("component Comp2 { " +
          "port out int o; " +
          "a.b.C sub; " +
          "sub.o -> o; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // boolean -> int (in port forward)
      arg("component Comp3 { " +
          "port in boolean i; " +
          "a.b.D sub; " +
          "i -> sub.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // int -> boolean (out port forward)
      arg("component Comp4 { " +
          "port out boolean o; " +
          "a.b.E sub; " +
          "sub.o -> o; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.lang.String -> java.lang.Integer (in port forward)
      arg("component Comp5 { " +
          "port in java.lang.String i; " +
          "a.b.F sub; " +
          "i -> sub.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.util.List<java.lang.String> -> java.util.List<java.lang.Integer> (in port forward)
      arg("component Comp6 { " +
          "port in java.util.List<java.lang.String> i; " +
          "a.b.G sub; " +
          "i -> sub.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.lang.Integer -> java.util.List<java.lang.Integer> (in port forward)
      arg("component Comp7 { " +
          "port in java.lang.Integer i; " +
          "a.b.G sub; " +
          "i -> sub.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.util.List<java.lang.Integer> -> java.util.List<java.lang.String> (out port forward)
      arg("component Comp8 { " +
          "port out java.util.List<java.lang.String> o; " +
          "a.b.H sub; " +
          "sub.o -> o; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.util.List<java.lang.Integer> -> java.lang.Integer (out port forward)
      arg("component Comp9 { " +
          "port out java.lang.Integer o; " +
          "a.b.H sub; " +
          "sub.o -> o; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.lang.Comparable<java.lang.Integer> -> java.lang.Integer (out port forward)
      arg("component Comp10 { " +
          "port out java.lang.Integer o; " +
          "a.b.J sub; " +
          "sub.o -> o; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // int -> T (in port forward, type parameter)
      arg("component Comp11 { " +
          "port in int i; " +
          "a.b.K sub; " +
          "i -> sub.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // T -> int (out port forward, type parameter)
      arg("component Comp12 { " +
          "port out int o; " +
          "a.b.L sub; " +
          "sub.o -> o; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // T -> T (hidden channel, type parameter)
      arg("component Comp13 { " +
          "a.b.K sub1; " +
          "a.b.L sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // V -> U (hidden channel, type parameter)
      arg("component Comp14<U, V> { " +
          "a.b.K<U> sub1; " +
          "a.b.L<V> sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // boolean -> int, int (in port forward, multiple targets)
      arg("component Comp15 { " +
          "port in boolean i; " +
          "a.b.D sub1, sub2; " +
          "i -> sub1.i, sub2.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // boolean -> int, int (out port forward, multiple targets)
      arg("component Comp16 { " +
          "port out int o1, o2; " +
          "a.b.C sub; " +
          "sub.o -> o1, o2; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // boolean -> int, int (hidden channel, multiple targets)
      arg("component Comp17 { " +
          "a.b.C sub1; " +
          "a.b.D sub2, sub3; " +
          "sub1.o -> sub2.i, sub3.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // T -> T (inner component, type parameter)
      arg("component Comp18<T> { " +
          "port in T i; " +
          "component Inner<T> { " +
          "port in T i; " +
          "} " +
          "Inner sub; " +
          "i -> sub.i; " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH)
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // int -> boolean (in port forward)
      arg("component Comp1 {" +
          "feature f1; " +
          "port in int i; " +
          "a.b.B sub;" +
          "varif(f1){ " +
          "i -> sub.i;} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // boolean -> int (out port forward)
      arg("component Comp2 { " +
          "feature f1,f2;" +
          "varif(f1){port out int o; }" +
          "a.b.C sub; " +
          "varif(f2){sub.o -> o; }" +
          "constraint(f1 && f2);" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // boolean -> int (in port forward)
      arg("component Comp3 { " +
          "feature f1;" +
          "port in boolean i; " +
          "a.b.D sub; " +
          "varif(f1){a.b.A a;}else{i -> sub.i;}" +
          "constraint(!f1); " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // int -> boolean (out port forward)
      arg("component Comp4 {" +
          "feature f1; " +
          "a.b.E sub;" +
          "varif(f1){ " +
          "sub.o -> o; " +
          "port out boolean o;" +
          "}" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.lang.String -> java.lang.Integer (in port forward)
      arg("component Comp5 { " +
          "feature f1,f2;" +
          "varif(f2){port in java.lang.String i;}else{ a.b.A a;} " +
          "a.b.F sub; " +
          "varif(f1){i -> sub.i; }" +
          "constraint(f2);" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.util.List<java.lang.String> -> java.util.List<java.lang.Integer> (in port forward)
      arg("component Comp6 { " +
          "feature f1,f2,f3;" +
          "varif(f1){" +
          "port in java.util.List<java.lang.String> i; " +
          "varif(f2){a.b.G sub; " +
          "varif(f3){i -> sub.i; }}}" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.lang.Integer -> java.util.List<java.lang.Integer> (in port forward)
      arg("component Comp7 { " +
          "feature f1;" +
          "port in java.lang.Integer i; " +
          "a.b.G sub; " +
          "varif(f1){i -> sub.i; }" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.util.List<java.lang.Integer> -> java.util.List<java.lang.String> (out port forward)
      arg("component Comp8 { " +
          "feature f1,f2;" +
          "varif(f1){"+
          "port out java.util.List<java.lang.String> o; " +
          "varif(f2){a.b.H sub; " +
          "sub.o -> o; }}" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.util.List<java.lang.Integer> -> java.lang.Integer (out port forward)
      arg("component Comp9 {" +
          "feature f1;" +
          "varif(f1){a.b.A a;}else{ " +
          "port out java.lang.Integer o; " +
          "a.b.H sub; " +
          "sub.o -> o; }" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // java.lang.Comparable<java.lang.Integer> -> java.lang.Integer (out port forward)
      arg("component Comp10 {" +
          "feature f1;" +
          "port out java.lang.Integer o; " +
          "a.b.J sub; " +
          "varif(f1){a.b.A a;}else{sub.o -> o; }" +
          "constraint(!f1);" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // int -> T (in port forward, type parameter)
      arg("component Comp11 { " +
          "feature f1,f2,f3;" +
          "varif(f1){port in int i; }" +
          "varif(f2){a.b.K sub; }" +
          "varif(f3){i -> sub.i; }" +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH)

    );
  }
}
