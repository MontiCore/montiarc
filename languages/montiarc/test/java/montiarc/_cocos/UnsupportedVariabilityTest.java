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

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link UnsupportedVariability}.
 */
public class UnsupportedVariabilityTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // Nothing
    "component Comp1 { }",
    // Composed body
    "component Comp2 {" +
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
    // Wrapped within braces
    "component Comp3 {" +
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
    // Wrapped within a component
    "component Comp4 {" +
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
    // comfortable arc stuff
    "component Comp5 {" +
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
    // Mode automaton
    "component Comp6 {" +
      "  mode automaton { }" +
      "}",
    // Automaton behavior
    "component Comp7 {" +
      "  automaton { }" +
      "}",
    // Compute behavior
    "component Comp8 {" +
      "  init { }" +
      "  compute { }" +
      "}",
    /*
    // Assume-Guarantee
    "component Comp9 {" +
      "  guarantee : true;" +
      "  " +
      "  assume : true;" +
      "  guarantee : true;" +
      "}",
     */
    /*
    // Pre-Post
    "component Comp10 {" +
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
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UnsupportedVariability());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isZero();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // Direct
    "component Comp1 { varif (true) port in int x; }",
    // Nested in braces
    "component Comp2 { { varif (true) port in int x; } }",
    // Nested in inner component
    "component Comp3 { component Inner { varif (true) port in int x; } }",
    // Nested in inner component in braces
    "component Comp4 { component Inner { { varif (true) port in int x; } } }",
    // Nested in braces-contained component
    "component Comp5 { { component Inner { varif (true) port in int x; } } }",
    // Nested in braces-contained component + in braces
    "component Comp6 { { component Inner { { varif (true) port in int x; } } } }",
  })
  public void shouldReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UnsupportedVariability());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsOnly(ArcError.UNSUPPORTED_MODEL_ELEMENT.getErrorCode());
  }
}
