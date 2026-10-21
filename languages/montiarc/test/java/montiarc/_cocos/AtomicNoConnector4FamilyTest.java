/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.AtomicNoConnector4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.CONNECTORS_IN_ATOMIC;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link AtomicNoConnector4Family}.
 */
class AtomicNoConnector4FamilyTest extends AtomicNoConnectorTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicNoConnector4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          port out int o;
          feature f1;
          varif(f1) { A a; i -> o; }
        }
        """
      ),
      arg("""
        component ValidCompWithVariability2 {
          port in int i;
          port out int o;
          feature f1, f2;
          varif(f1) { A a; }
          varif(f2) { i -> o; }
          constraint(!(f1 ^ f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // atomic component with connector (configuration f1)
      arg("""
        component InvalidCompWithVariability1 {
          port in int i;
          port out int o;
          feature f1;
          varif(f1) { i -> o; }
        }
        """,
        CONNECTORS_IN_ATOMIC
      ),
      // atomic component with two connectors (configuration f1 && f2)
      arg("""
        component InvalidCompWithVariability2 {
          feature f1,f2;
          port in int i;
          port out int o;
          varif(f1) { i -> o; }
          varif(f2) { i -> o; }
        }
        """,
        CONNECTORS_IN_ATOMIC, CONNECTORS_IN_ATOMIC
      )
    );
  }
}
