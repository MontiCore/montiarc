/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorPortsExist;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
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

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConnectorPortsExist}.
 */
public class ConnectorPortsExistTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i; }");
    compile("package a.b; component C { port out int o; }");
    compile("package a.b; component D { port in int i; port out int o; }");
    compile("package a.b; component E { port in int i1, i2; port out int o; }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { }",
    "component Comp2 {" +
      "port in int i; " +
      "port out int o; " +
      "i -> o; " +
      "}",
    "component Comp3 { " +
      "port in int i; " +
      "port in int o; " +
      "a.b.D d; " +
      "i -> d.i; " +
      "d.o -> o; " +
      "}",
    "component Comp4 { " +
      "port in int i; " +
      "port out int o1, o2; " +
      "a.b.E e; " +
      "i -> e.i1, e.i2; " +
      "e.o -> o1, o2; " +
      "}",
    "component Comp5 { " +
      "port in int i; " +
      "port out int o; " +
      "component Inner {" +
      "port in int i; " +
      "port out int o; " +
      "} " +
      "Inner sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorPortsExist());

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
    checker.addCoCo(new ConnectorPortsExist());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.A a; " +
          "i -> a.i; " +
          "a.o -> o; " +
          "}",
        ArcError.MISSING_PORT,
        ArcError.MISSING_PORT),
      arg("component Comp2 { " +
          "port in int i; " +
          "port in int o; " +
          "a.b.B b; " +
          "i -> b.i; " +
          "b.o -> o; " +
          "}",
        ArcError.MISSING_PORT),
      arg("component Comp3 { " +
          "port in int i; " +
          "port in int o; " +
          "a.b.C c; " +
          "i -> c.i; " +
          "c.o -> o; " +
          "}",
        ArcError.MISSING_PORT),
      arg("component Comp4 { i -> o; }",
        ArcError.MISSING_PORT,
        ArcError.MISSING_PORT),
      arg("component Comp5 { " +
          "port in int i; " +
          "i -> o; " +
          "}",
        ArcError.MISSING_PORT),
      arg("component Comp6 { " +
          "port out int o; " +
          "i -> o; " +
          "}",
        ArcError.MISSING_PORT),
      arg("component Comp7 { " +
          "port in int i; " +
          "i -> b.i; " +
          "}",
        ArcError.MISSING_SUBCOMPONENT),
      arg("component Comp8 { " +
          "port out int o; " +
          "c.o -> o; " +
          "}",
        ArcError.MISSING_SUBCOMPONENT),
      arg("component Comp9 { " +
          "c.o -> b.i; " +
          "}",
        ArcError.MISSING_SUBCOMPONENT,
        ArcError.MISSING_SUBCOMPONENT),
      arg("component Comp10 { " +
          "port in int i; " +
          "i -> b1.i, b2.i; " +
          "}",
        ArcError.MISSING_SUBCOMPONENT,
        ArcError.MISSING_SUBCOMPONENT),
      arg("component Comp10 { " +
          "i -> d1.i, d2.i; " +
          "d1.o -> o1; " +
          "d2.o -> o2; " +
          "}",
        ArcError.MISSING_PORT,
        ArcError.MISSING_SUBCOMPONENT,
        ArcError.MISSING_SUBCOMPONENT,
        ArcError.MISSING_SUBCOMPONENT,
        ArcError.MISSING_PORT,
        ArcError.MISSING_SUBCOMPONENT,
        ArcError.MISSING_PORT)
    );
  }
}
