/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.UniqueIdentifier;
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
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static montiarc.util.ArcError.UNIQUE_IDENTIFIER_NAMES;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link UniqueIdentifier}.
 */
class UniqueIdentifierTest extends MontiArcTestBase {

  private final static String TEST_DIR = "cocos";

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, TEST_DIR)));
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UniqueIdentifier());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @DisableIfDisplayName(contains = {
    "InvalidComp7",
    "InvalidComp14",
    "InvalidComp18",
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new UniqueIdentifier());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // single unique parameter
      arg("""
        component ValidComp1(int p) { }
        """
      ),
      // single unique port
      arg("""
        component ValidComp2 {
          port in int i;
        }
        """
      ),
      // single unique variable
      arg("""
        component ValidComp3 {
          int v = 0;
        }
        """
      ),
      // single unique inner component
      arg("""
        component ValidComp4 {
          component Inner { }
        }
        """
      ),
      // single unique subcomponent
      arg("""
        component ValidComp5 {
          CompType sub;
        }
        """
      ),
      // single unique type-parameter
      arg("""
        component ValidComp6<T> { }
        """
      ),
      // single unique feature
      arg("""
        component ValidComp7 {
          feature f;
        }
        """
      ),
      // unique parameter (inner component)
      arg("""
        component ValidComp8(int p) {
          component Inner(int p) { }
        }
        """
      ),
      // unique port (inner component)
      arg("""
        component ValidComp9 {
          port in int i;
          component Inner {
            port in int i;
          }
        }
        """
      ),
      // unique variable (inner component)
      arg("""
        component ValidComp10 {
          int v = 0;
          component Inner {
            int v = 0;
          }
        }
        """
      ),
      // unique inner component (inner component)
      arg("""
        component ValidComp11 {
          component Inner {
            component Inner { }
          }
        }
        """
      ),
      // unique subcomponent (inner component)
      arg("""
        component ValidComp12 {
          component Inner {
            CompType sub;
          }
          CompType sub;
        }
        """
      ),
      // unique type-parameter (inner component)
      arg("""
        component ValidComp13<T> {
          component Inner<T> { }
        }
        """
      ),
      // unique feature (inner component)
      arg("""
        component ValidComp14 {
          feature f;
          component Inner {
            feature f;
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // duplicate parameter
      arg("""
          component InvalidComp1(int p, int p) { }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate port
      arg("""
          component InvalidComp2 {
            port in int i;
            port in int i;
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate variable
      arg("""
          component InvalidComp3 {
            int v = 0;
            int v = 0;
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate inner component
      arg("""
          component InvalidComp4 {
            component Inner { }
            component Inner { }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate subcomponent
      arg("""
          component InvalidComp5 {
            CompType sub;
            CompType sub;
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate type-parameter
      arg("""
          component InvalidComp6<T, T> { }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate feature
      arg("""
          component InvalidComp7 {
            feature f;
            feature f;
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate parameter in inner component
      arg("""
          component InvalidComp8() {
            component Inner(int i, int i) { }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate port (inner component)
      arg("""
          component InvalidComp9 {
            component Inner {
              port in int i;
              port in int i;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate variable (inner component)
      arg("""
          component InvalidComp10 {
            component Inner {
              int v = 0;
              int v = 0;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate inner component (inner component)
      arg("""
          component InvalidComp11 {
            component Inner {
              component Inner { }
              component Inner { }
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate subcomponent (inner component)
      arg("""
          component InvalidComp12 {
            component Inner {
              CompType sub;
              CompType sub;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate type-parameter (inner component)
      arg("""
          component InvalidComp13 {
            component Inner<T, T> { }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // duplicate feature (inner component)
      arg("""
          component InvalidComp14 {
            component Inner {
              feature f;
              feature f;
            }
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // parameter and variable share name
      arg("""
          component InvalidComp15(int p) {
            int p = 0;
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // parameter and port share name
      arg("""
          component InvalidComp16(int p) {
            port in int p;
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // parameter and type-parameter share name
      arg("""
          component InvalidComp17<p> (int p) { }
          """,
        UNIQUE_IDENTIFIER_NAMES
      ),
      // parameter and feature share name
      arg("""
          component InvalidComp18(boolean p) {
            feature p;
          }
          """,
        UNIQUE_IDENTIFIER_NAMES
      )
    );
  }
}
