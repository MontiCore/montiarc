/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
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

import static montiarc.util.MCError.SWITCH_SELECTOR_NOT_SWITCHABLE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link SwitchStatementValid}.
 */
class SwitchStatementValidTest extends MontiArcTestBase {

  private final static String TEST_DIR = "cocos";

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, TEST_DIR)));
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // component no behavior
      arg("""
        component ValidComp1 { }
        """
      ),
      // switch over int (primitive numeric type)
      arg("""
        component ValidComp2 {
          int v = 0;
          automaton {
            initial state S;
            S -> S / {
              switch (v) { }
            }
          }
        }
        """
      ),
      // switch over Integer (boxed numeric type)
      arg("""
        import java.lang.Integer;
        component ValidComp3 {
          Integer v = 0;
          automaton {
            initial state S;
            S -> S / {
              switch (v) { }
            }
          }
        }
        """
      ),
      // switch over primitive numeric types
      arg("""
        component ValidComp4(byte aByte, short aShort, char anChar, int anInt) {
          automaton {
            initial state S;
            S -> S / {
              switch (aByte) { }
              switch (aShort) { }
              switch (anChar) { }
              switch (anInt) { }
            }
          }
        }
        """
      ),
      // switch over boxed numeric types
      arg("""
        import java.lang.*;
        component ValidComp5(Byte aByte, Short aShort, Character anChar, Integer anInt) {
          automaton {
            initial state S;
            S -> S / {
              switch (aByte) { }
              switch (aShort) { }
              switch (anChar) { }
              switch (anInt) { }
            }
          }
        }
        """
      ),
      // switch over enum
      arg("""
        component ValidComp6 {
          Enum v = Enum.E1;
          automaton {
            initial state S;
            S -> S / {
              switch (v) { }
            }
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // switch over non-switchable primitive type
      arg("""
          component InvalidComp1 {
            boolean v = false;
            automaton {
              initial state S;
              S -> S / {
                switch (v) { }
              }
            }
          }
          """,
        SWITCH_SELECTOR_NOT_SWITCHABLE
      ),
      // switch over non-switchable boxed type
      arg("""
          import java.lang.Boolean;
          component InvalidComp2 {
            Boolean v = false;
            automaton {
              initial state S;
              S -> S / {
                switch (v) { }
              }
            }
          }
          """,
        SWITCH_SELECTOR_NOT_SWITCHABLE
      ),
      // switch over non-switchable generic type
      arg("""
          import java.util.List;
          component InvalidComp3 {
            List<int> v = [ ];
            automaton {
              initial state S;
              S -> S / {
                switch (v) { }
              }
            }
          }
          """,
        SWITCH_SELECTOR_NOT_SWITCHABLE
      )
    );
  }
}
