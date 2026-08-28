/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.CircularInheritance;
import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.CIRCULAR_INHERITANCE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link CircularInheritance}.
 */
class CircularInheritanceTest extends MontiArcTestBase {

  private final static String TEST_DIR = "cocos/CircularInheritance";

  @BeforeEach
  protected void setUp() {
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, TEST_DIR)));
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CircularInheritance());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CircularInheritance());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // no inheritance
      arg("component ValidComp1 { }"),
      // extends an external supertype without its own supertype
      arg("component ValidComp2 extends ComponentWithoutSuperComponent { }"),
      // extends an external supertype that itself has a supertype
      arg("component ValidComp3 extends ComponentWithSuperComponent { }"),
      // multi-inheritance from two external supertypes
      arg("component ValidComp4 extends ComponentWithoutSuperComponent, ComponentWithSuperComponent { }"),
      // extends an external supertype that is itself part of an unrelated circular inheritance chain (not involving this component)
      arg("component ValidComp5 extends ComponentWithCircularInheritance1A { }"),
      // nested component type without inheritance
      arg("component ValidComp6 { component Inner { } }"),
      // nested component type extends an external supertype without its own supertype
      arg("component ValidComp7 { component Inner extends ComponentWithoutSuperComponent { } }"),
      // nested component type extends an external supertype that itself has a supertype
      arg("component ValidComp8 { component Inner extends ComponentWithSuperComponent { } }"),
      // nested component type with multi-inheritance from two external supertypes
      arg("component ValidComp9 { component Inner extends ComponentWithoutSuperComponent, ComponentWithSuperComponent { } }"),
      // nested component type extends an external supertype that is itself part of an unrelated circular inheritance chain
      arg("component ValidComp10 { component Inner extends ComponentWithCircularInheritance1A { } }")
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // component with directed circular inheritance
      arg(
        "component InvalidComp1 extends ComponentWithTestDependentSuperComponent1 { }",
        CIRCULAR_INHERITANCE
      ),
      // component with indirect circular inheritance
      arg(
        "component InvalidComp2 extends ComponentWithTestDependentSuperComponent2A { }",
        CIRCULAR_INHERITANCE
      ),
      // inner component with direct circular inheritance
      arg(
        """
          component InvalidComp3 {
            component Inner1 extends Inner2 { }
            component Inner2 extends Inner1 { }
          }
          """,
        CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE
      ),
      // inner component with indirect circular inheritance
      arg(
        """
          component InvalidComp4 {
            component Inner1 extends Inner2 { }
            component Inner2 extends Inner3 { }
            component Inner3 extends Inner1 { }
          }
          """,
        CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE
      ),
      // two inner components with circular inheritance
      arg(
        """
          component InvalidComp5 {
            component Inner1 extends Inner3 { }
            component Inner2 extends Inner4 { }
            component Inner3 extends Inner1 { }
            component Inner4 extends Inner2 { }
          }
          """,
        CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE,
        CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE
      ),
      // inner component extending two components introducing circular inheritance
      arg(
        """
          component InvalidComp6 {
            component Inner1 extends Inner2, Inner3 { }
            component Inner2 extends Inner1 { }
            component Inner3 extends Inner2 { }
          }
          """,
        CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE,
        CIRCULAR_INHERITANCE, CIRCULAR_INHERITANCE
      )
    );
  }
}
