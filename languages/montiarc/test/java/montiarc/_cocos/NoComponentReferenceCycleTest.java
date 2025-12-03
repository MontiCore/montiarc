/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoComponentReferenceCycle;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoComponentReferenceCycle}.
 */
public class NoComponentReferenceCycleTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { }",
    "component Comp2 { component Comp1 sub { } }",
    "component Comp3 { component Comp2 sub { component Comp1 sub { } } }",
    "component Comp4 { component Comp3 sub1, sub2 { component Comp2 sub { } } }",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoComponentReferenceCycle());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(() -> Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoComponentReferenceCycle());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(() -> Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("""
        component Comp1 {
          Comp1 sub;
        }
        """,
        ArcError.COMPONENT_REFERENCE_CYCLE
      ),
      arg("""
        component Comp2 {
          Comp2 sub1;
          Comp2 sub2;
        }
        """,
        ArcError.COMPONENT_REFERENCE_CYCLE,
        ArcError.COMPONENT_REFERENCE_CYCLE
      ),
      arg("""
        component Comp3A {
          component Comp3B {
            Comp3A subA;
          }
          Comp3B subB;
        }""",
        ArcError.COMPONENT_REFERENCE_CYCLE,
        ArcError.COMPONENT_REFERENCE_CYCLE
      ),
      arg("""
        component Comp4A {
          component Comp4B subB {
            Comp4A subA;
          }
        }""",
        ArcError.COMPONENT_REFERENCE_CYCLE,
        ArcError.COMPONENT_REFERENCE_CYCLE
      ),
      arg("""
        component Comp5A {
          component Comp5B {
            component Comp5C {
              Comp5A subA;
            }
            Comp5C subC;
          }
          Comp5B subB;
        }""",
        ArcError.COMPONENT_REFERENCE_CYCLE,
        ArcError.COMPONENT_REFERENCE_CYCLE,
        ArcError.COMPONENT_REFERENCE_CYCLE
      )
    );
  }
}
