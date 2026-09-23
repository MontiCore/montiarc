/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.util.stream.Stream;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoFieldInSubcomponentArgument4Family}.
 */
class NoFieldInSubcomponentArgument4FamilyTest extends NoFieldInSubcomponentArgumentTest {

  @ParameterizedTest
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoFieldInSubcomponentArgument4Family());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability1 ",
    "InvalidCompWithVariability2 ",
    "InvalidCompWithVariability3 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoFieldInSubcomponentArgument4Family());

    checker.checkAll(ast);

    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // field in subcomponent argument, dead variation point
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability1 {
          int x = 1;
          varif (false) {
            ComponentTypeWithIntParameter sub(x);
          }
        }
        """
      ),
      // field in subcomponent argument, dead feature
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability2 {
          feature f;
          int x = 1;
          varif (f) {
            ComponentTypeWithIntParameter sub(x);
          }
          constraint (!f);
        }
        """
      ),
      // conditional field in subcomponent argument cannot coexist with subcomponent
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            int x = 1;
          }
          varif (f2) {
            ComponentTypeWithIntParameter sub(x);
          }
          constraint (!(f1 && f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // field in subcomponent argument, tautological variation point
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidCompWithVariability1 {
          int x = 1;
          varif (true) {
            ComponentTypeWithIntParameter sub(x);
          }
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // field in subcomponent argument, core feature
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidCompWithVariability2 {
          feature f;
          int x = 1;
          varif (f) {
            ComponentTypeWithIntParameter sub(x);
          }
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // conditional field in subcomponent argument can coexist with subcomponent
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            int x = 1;
          }
          varif (f2) {
            ComponentTypeWithIntParameter sub(x);
          }
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
