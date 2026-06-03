/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.statements.mccommonstatements.cocos.SwitchCaseTypesValid;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcMillTOP;
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

import static montiarc.MontiArcMillTOP.globalScope;
import static montiarc.MontiArcMillTOP.scope;
import static montiarc.util.MCError.SWITCH_CASE_INCOMPATIBLE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link SwitchCaseTypesValid} context condition.
 * Checks that case values in switch statements are compatible with the switch expression type.
 */
class SwitchCaseTypesValidTest extends MontiArcTestBase {

  private final static String TEST_DIR = "cocos";

  @BeforeEach
  @Override
  protected void init() {
    super.init();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, TEST_DIR)));
    setUpEnums();
  }

  void setUpEnums() {
    OOTypeSymbol onOffEnumType = MontiArcMillTOP.oOTypeSymbolBuilder()
      .setName("OnOff").setIsEnum(true).setIsPublic(true)
      .setSpannedScope(scope()).build();
    onOffEnumType.getSpannedScope().add(MontiArcMillTOP.fieldSymbolBuilder()
      .setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true)
      .setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    onOffEnumType.getSpannedScope().add(MontiArcMillTOP.fieldSymbolBuilder()
      .setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true)
      .setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
    globalScope().add(onOffEnumType);
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);
    // Given
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SwitchCaseTypesValid());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    // Given
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SwitchCaseTypesValid());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // No switch statement
      arg("""
        component ValidComp1 { }
        """),
      // Switch over int with numeric case
      arg("""
        component ValidComp2 {
          int v = 0;
          automaton {
            initial state S;
            S -> S / {
              switch (v) {
                case 1: break;
                case 2: break;
                default: break;
              }
            }
          }
        }
        """),
      // Switch over int with multiple numeric cases
      arg("""
        component ValidComp3 {
          int v = 0;
          automaton {
            initial state S;
            S -> S / {
              switch (v) {
                case 0:
                case 1:
                case 2: break;
                default: break;
              }
            }
          }
        }
        """),
      // Switch over char with character literal
      arg("""
        component ValidComp4 {
          char c = 'a';
          automaton {
            initial state S;
            S -> S / {
              switch (c) {
                case 'a': break;
                case 'b': break;
                default: break;
              }
            }
          }
        }
        """),
      // Switch over Enum with enum constant
      arg("""
        component ValidComp5 {
          OnOff e = OnOff.ON;
          automaton {
            initial state S;
            S -> S / {
              switch (e) {
                case OnOff.ON: break;
                case OnOff.OFF: break;
                default: break;
              }
            }
          }
        }
        """),
      // Switch over constant with variable case
      arg("""
        component ValidComp6 {
          OnOff e = OnOff.ON;
          automaton {
            initial state S;
            S -> S / {
              switch (OnOff.ON) {
                case e:
              }
            }
          }
        }
        """),
      // Switch over variable with expression case
      arg("""
        component ValidComp7 {
          int v = 0;
          automaton {
            initial state S;
            S -> S / {
              switch (v) {
                case 1*3:
              }
            }
          }
        }
        """),
      // Switch over String
      arg("""
        component ValidComp8 {
          String s = "abcd";
          automaton {
            initial state S;
            S -> S / {
              switch (s) {
                case "abcd":
              }
            }
          }
        }
        """),
      // Switch over longs
      arg("""
        component ValidComp9 {
          long l = 0L;
          automaton {
            initial state S;
            S -> S / {
              switch (l) {
                case 1L: break;
                default: break;
              }
            }
          }
        }
        """)
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // Switch over int with string case - INVALID
      arg("""
          component InvalidComp1 {
            int v = 0;
            automaton {
              initial state S;
              S -> S / {
                switch (v) {
                  case "hello": break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE),
      // Switch over int with boolean case - INVALID
      arg("""
          component InvalidComp2 {
            int v = 0;
            automaton {
              initial state S;
              S -> S / {
                switch (v) {
                  case true: break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE),
      // Switch over char with string case - INVALID
      arg("""
          component InvalidComp3 {
            char c = 'a';
            automaton {
              initial state S;
              S -> S / {
                switch (c) {
                  case "string": break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE),
      // Switch over Enum with numeric case - INVALID
      arg("""
          component InvalidComp4 {
            OnOff e = OnOff.ON;
            automaton {
              initial state S;
              S -> S / {
                switch (e) {
                  case 1: break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE),
      // Switch over Enum with expression case - INVALID
      arg("""
          component InvalidComp5 {
            OnOff e = OnOff.ON;
            int x = 1;
            automaton {
              initial state S;
              S -> S / {
                switch (e) {
                  case x + 1: break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE),
      // Switch over numeric with enum case - INVALID
      arg("""
          component InvalidComp6 {
            int v = 0;
            automaton {
              initial state S;
              S -> S / {
                switch (v) {
                  case OnOff.ON: break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE),
      // Multiple invalid cases
      arg("""
          component InvalidComp7 {
            int v = 0;
            automaton {
              initial state S;
              S -> S / {
                switch (v) {
                  case 1: break;
                  case "invalid": break;
                  case 2: break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE),
      // Switch over String with boolean case - INVALID
      arg("""
          component InvalidComp8 {
            String s = "hello";
            automaton {
              initial state S;
              S -> S / {
                switch (s) {
                  case true: break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE),
      // Switch over long with boolean case - INVALID
      arg("""
          component InvalidComp9 {
            long l = 0L;
            automaton {
              initial state S;
              S -> S / {
                switch (l) {
                  case true: break;
                  default: break;
                }
              }
            }
          }
          """,
        SWITCH_CASE_INCOMPATIBLE)
    );
  }
}
