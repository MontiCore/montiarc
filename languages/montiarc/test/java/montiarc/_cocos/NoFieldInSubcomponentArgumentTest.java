/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.NoFieldInSubcomponentArgument;
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

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

class NoFieldInSubcomponentArgumentTest extends MontiArcTestBase {

  private static final String SYMBOLS_DIR = "symbols";

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR)));
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoFieldInSubcomponentArgument());

    checker.checkAll(ast);

    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoFieldInSubcomponentArgument());

    checker.checkAll(ast);

    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      arg("""
        component ValidComp1 { }
        """
      ),
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidComp2 {
          ComponentTypeWithIntParameter sub(1);
        }
        """
      ),
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component ValidComp3(int p) {
          ComponentTypeWithIntParameter sub(p);
        }
        """
      ),
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithFieldIO;
        component ValidComp4(OOTypeWithFieldIO p) {
          ComponentTypeWithIntParameter sub1(p.i);
          ComponentTypeWithIntParameter sub2(p.o);
        }
        """
      ),
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        import montiarc.test.OOTypeWithStaticFieldIO;
        component ValidComp5 {
          ComponentTypeWithIntParameter sub1(OOTypeWithStaticFieldIO.i);
          ComponentTypeWithIntParameter sub2(OOTypeWithStaticFieldIO.o);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp1 {
          int x = 1;
          ComponentTypeWithIntParameter sub(x);
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      arg("""
        import montiarc.test.ComponentTypeWithIntParameter;
        component InvalidComp2 {
          int x = 1;
          ComponentTypeWithIntParameter sub(x + 1);
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        component InvalidComp3 {
          int x = 1;
          ComponentTypeWithIntIOParameters sub(i = x);
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      arg("""
        import montiarc.test.ComponentTypeWithIntIOParameters;
        component InvalidComp4 {
          int x = 1;
          ComponentTypeWithIntIOParameters sub(i = x, o = x);
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT
      )
    );
  }
}
