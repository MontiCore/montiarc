/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.PortHeritageTimingFits4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.INVALID_PORT_TIMING_OVERRIDE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortHeritageTimingFits4Family}.
 */
class PortHeritageTimingFits4FamilyTest extends PortHeritageTimingFitsTest {

  @Override
  protected void setUpComponents() {
    super.setUpComponents();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    compile("package a.b; component V { feature f; varif (f) { port sync out int o; } else { port out int o; } }");
    compile("package a.b; component W { feature f; varif (f) { port in int i; } }");
  }

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortHeritageTimingFits4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @Override
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
  @Override
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  @Test
  void shouldWarnIfPresenceDependsOnUnknownValues() {
    // Given
    ASTMACompilationUnit ast = compile("""
      component UnknownComp(int n) extends a.b.A {
        varif (java.lang.Math.abs(n) > 3) {
          port out int os;
        }
      }""");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).singleElement()
      .matches(Finding::isWarning)
      .extracting(Finding::getMsg).asString()
      .startsWith(INVALID_PORT_TIMING_OVERRIDE.getErrorCode());
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // overriding an outgoing port non-sync only in the variant in which the inherited port is non-sync
      arg("""
        component ValidCompWithVariability1 extends a.b.V {
          varif (!f) {
            port out int o;
          }
        }"""
      ),
      // forbidden override excluded by a constraint
      arg("""
        component ValidCompWithVariability2 extends a.b.A {
          feature g;
          varif (g) {
            port out int os;
          }
          constraint (!g);
        }"""
      ),
      // forbidden override only in variants without the inherited port
      arg("""
        component ValidCompWithVariability3 extends a.b.W {
          varif (!f) {
            port sync in int i;
          }
        }"""
      )
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // overriding an outgoing port non-sync that is sync in some variant
      arg("""
        component InvalidCompWithVariability1 extends a.b.V {
          port out int o;
        }""",
        INVALID_PORT_TIMING_OVERRIDE
      ),
      // optional forbidden override
      arg("""
        component InvalidCompWithVariability2 extends a.b.A {
          feature g;
          varif (g) {
            port out int os;
          }
        }""",
        INVALID_PORT_TIMING_OVERRIDE
      ),
      // overriding an optional incoming port sync
      arg("""
        component InvalidCompWithVariability3 extends a.b.W {
          varif (f) {
            port sync in int i;
          }
        }""",
        INVALID_PORT_TIMING_OVERRIDE
      )
    );
  }
}
