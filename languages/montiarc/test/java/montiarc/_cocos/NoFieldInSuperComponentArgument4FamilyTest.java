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
import variablearc._cocos.NoFieldInSuperComponentArgument4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoFieldInSuperComponentArgument4Family}.
 */
class NoFieldInSuperComponentArgument4FamilyTest extends NoFieldInSuperComponentArgumentTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoFieldInSuperComponentArgument4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    ASTMACompilationUnit ast = compile(model);

    checker().checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    ASTMACompilationUnit ast = compile(model);

    checker().checkAll(ast);

    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // field in super component argument, dead variation point
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability1 extends ComponentTypeWithIntParameter(x) {
          varif (false) {
            int x = 1;
          }
        }
        """
      ),
      // field in super component argument, dead feature
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability2 extends ComponentTypeWithIntParameter(x) {
          feature f;
          varif (f) {
            int x = 1;
          }
          constraint (!f);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // field in super component argument, tautological variation point
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidCompWithVariability1 extends ComponentTypeWithIntParameter(x) {
          varif (true) {
            int x = 1;
          }
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // field in super component argument, optional feature
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidCompWithVariability2 extends ComponentTypeWithIntParameter(x) {
          feature f;
          varif (f) {
            int x = 1;
          }
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
