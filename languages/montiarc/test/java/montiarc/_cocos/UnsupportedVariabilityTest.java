/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link UnsupportedVariability}.
 */
class UnsupportedVariabilityTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // empty component
    "component ValidComp1 { }",
    // composed component body (block, port, field, subcomponent, connector)
    "component ValidComp2 {" +
      "  {}" +
      "  port in int x;" +
      "  int y = 10;" +
      "" +
      "  component Inner {" +
      "    port in int x;" +
      "  }" +
      "  Inner inner;" +
      "  x -> inner.x;" +
      "}",
    // composed component body wrapped within braces
    "component ValidComp3 {" +
      "  {" +
      "  {}" +
      "  port in int x;" +
      "  int y = 10;" +
      "" +
      "  component Inner {" +
      "    port in int x;" +
      "  }" +
      "  Inner inner;" +
      "  x -> inner.x;" +
      "  }" +
      "}",
    // composed component body wrapped within a nested component
    "component ValidComp4 {" +
      "  component Nested {" +
      "  {}" +
      "  port in int x;" +
      "  int y = 10;" +
      "" +
      "  component Inner {" +
      "    port in int x;" +
      "  }" +
      "  Inner inner;" +
      "  x -> inner.x;" +
      "  }" +
      "}",
    // comfortable-arc elements (autoinstantiate, autoconnect, portComplete)
    "component ValidComp5 {" +
      "  port in int x;" +
      "" +
      "  component Inner {" +
      "    port in int x;" +
      "  }" +
      "" +
      "  autoinstantiate on;" +
      "  autoconnect port;" +
      "  portComplete;" +
      "}",
    // mode automaton
    "component ValidComp6 {" +
      "  mode automaton { }" +
      "}",
    // automaton behavior
    "component ValidComp7 {" +
      "  automaton { }" +
      "}",
    // init and compute behavior
    "component ValidComp8 {" +
      "  init { }" +
      "  compute { }" +
      "}",
    /*
    // assume-guarantee (not yet supported)
    "component ValidComp9 {" +
      "  guarantee : true;" +
      "  " +
      "  assume : true;" +
      "  guarantee : true;" +
      "}",
     */
    /*
    // pre-post (not yet supported)
    "component ValidComp10 {" +
      "  \n" +
      "  post : true;" +
      "  \n" +
      "  pre : true;" +
      "  post : true;" +
      "  \n" +
      "  trigger true;" +
      "  pre : true;" +
      "  post : true;" +
      "}",
     */
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UnsupportedVariability());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // varif directly in the component body
    "component InvalidComp1 { varif (true) port in int x; }",
    // varif nested in braces
    "component InvalidComp2 { { varif (true) port in int x; } }",
    // varif nested in an inner component
    "component InvalidComp3 { component Inner { varif (true) port in int x; } }",
    // varif nested in an inner component, in braces
    "component InvalidComp4 { component Inner { { varif (true) port in int x; } } }",
    // varif nested in a braces-contained inner component
    "component InvalidComp5 { { component Inner { varif (true) port in int x; } } }",
    // varif nested in a braces-contained inner component, in braces
    "component InvalidComp6 { { component Inner { { varif (true) port in int x; } } } }",
  })
  void shouldReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UnsupportedVariability());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsOnly(ArcError.UNSUPPORTED_MODEL_ELEMENT.getErrorCode());
  }
}
