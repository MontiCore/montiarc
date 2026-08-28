/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoComponentReferenceCycle;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.COMPONENT_REFERENCE_CYCLE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoComponentReferenceCycle}.
 */
class NoComponentReferenceCycleTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // component without nested types
    "component ValidComp1 { }",
    // component containing an instantiated nested type
    "component ValidComp2 { component Inner sub { } }",
    // two levels of nested types, no cycle
    "component ValidComp3 { component Middle sub { component Innermost sub { } } }",
    // two instances of a nested type, no cycle
    "component ValidComp4 { component Middle sub1, sub2 { component Innermost sub { } } }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoComponentReferenceCycle());

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
    checker.addCoCo(new NoComponentReferenceCycle());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // component directly containing itself as a subcomponent
      arg("""
        component InvalidComp1 {
          InvalidComp1 sub;
        }
        """,
        COMPONENT_REFERENCE_CYCLE
      ),
      // component containing two subcomponent instances of itself
      arg("""
        component InvalidComp2 {
          InvalidComp2 sub1;
          InvalidComp2 sub2;
        }
        """,
        COMPONENT_REFERENCE_CYCLE,
        COMPONENT_REFERENCE_CYCLE
      ),
      // nested types forming a 2-step cycle (each contains an instance of the other)
      arg("""
        component InvalidComp3A {
          component InvalidComp3B {
            InvalidComp3A subA;
          }
          InvalidComp3B subB;
        }""",
        COMPONENT_REFERENCE_CYCLE,
        COMPONENT_REFERENCE_CYCLE
      ),
      // nested type instantiated inline, forming a 2-step cycle back to the enclosing type
      arg("""
        component InvalidComp4A {
          component InvalidComp4B subB {
            InvalidComp4A subA;
          }
        }""",
        COMPONENT_REFERENCE_CYCLE,
        COMPONENT_REFERENCE_CYCLE
      ),
      // nested types forming a 3-step cycle
      arg("""
        component InvalidComp5A {
          component InvalidComp5B {
            component InvalidComp5C {
              InvalidComp5A subA;
            }
            InvalidComp5C subC;
          }
          InvalidComp5B subB;
        }""",
        COMPONENT_REFERENCE_CYCLE,
        COMPONENT_REFERENCE_CYCLE,
        COMPONENT_REFERENCE_CYCLE
      )
    );
  }
}
