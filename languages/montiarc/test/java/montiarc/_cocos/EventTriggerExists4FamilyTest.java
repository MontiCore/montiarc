/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.util.stream.Stream;

import static montiarc.util.ArcAutomataError.CANT_FIND_MSG_EVENT_SYMBOL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link EventTriggerExists4Family}.
 */
class EventTriggerExists4FamilyTest extends EvenTriggerExistsTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new EventTriggerExists4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidComp4",
    "InvalidComp5",
    "InvalidCompWithVariability1",
    "InvalidCompWithVariability2",
    "InvalidCompWithVariability3",
    "InvalidCompWithVariability4",
    "InvalidCompWithVariability5"
  })
  void shouldReportError(@NotNull String model, @NotNull Error... errors) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new EventTriggerExists4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // transition with msg-event, resolved port part of the static configuration
      arg("""
        component ValidCompWithVariability1 {
          port in int i;
          port out int o;
        
          feature f;
        
          varif(f) {
            automaton {
              initial state S;
              S -> S i;
            }
          }
        }
        """
      ),
      // transition with msg-event, resolved port defined in another feature
      arg("""
        component ValidCompWithVariability2 {
          feature f1, f2;
        
          varif(f1) {
            port in int i;
            port out int o;
          }
        
          varif(f2) {
            automaton {
              initial state S;
              S -> S i;
            }
          }
        
          constraint(f1 == f2);
        }
        """
      ),
      // transition with msg-event, port direction dependent on the feature
      arg("""
        component ValidCompWithVariability3 {
          feature f1, f2;
        
          varif(f1) {
            port in int io1;
            port out int io2;
          }
        
          varif(f2) {
            port out int io1;
            port in int io2;
          }
        
          varif(f1) {
            automaton {
              initial state S;
              S -> S io1;
            }
          }
        
          varif(f2) {
            automaton {
              initial state S;
              S -> S io2;
            }
          }
        
          constraint(f1 != f2);
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // transition with non-resolvable msg-event, port part of a feature not available in all configurations
      arg("""
          component InvalidCompWithVariability1 {
            feature f;
          
            varif(f) {
              port in int i;
              port out int o;
            }
          
            automaton {
              initial state S;
              S -> S i;
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // transition with non-resolvable msg-event, port und automaton defined in different features
      arg("""
          component InvalidCompWithVariability2 {
            feature f1, f2;
          
            varif(f1) {
              port in int i;
              port out int o;
            }
          
            varif(f2) {
              automaton {
                initial state S;
                S -> S i;
              }
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // two transitions with non-resolvable msg-event, ports und automata defined in different features
      arg("""
          component InvalidCompWithVariability3 {
            feature f1, f2, f3;
          
            varif(f1) {
              port in int i1, i2;
              port out int o;
            }
          
            varif(f2) {
              automaton {
                initial state S;
                S -> S i1;
              }
            }
          
            varif(f3) {
              automaton {
                initial state S;
                S -> S i2;
              }
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // nested transition with non-resolvable msg-event, port und automaton defined in different features
      arg("""
          component InvalidCompWithVariability4 {
            feature f1, f2;
          
            varif(f1) {
              port in int i;
              port out int o;
            }
          
            varif(f2) {
              automaton {
                initial state S {
                  S -> S i;
                }
              }
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      ),
      // inner transition with non-resolvable msg-event, port und automaton defined in different features
      arg("""
          component InvalidCompWithVariability5 {
            feature f1, f2;
          
            varif(f1) {
              port in int i;
              port out int o;
            }
          
            varif(f2) {
              automaton {
                initial state S {
                  -> i;
                }
              }
            }
          }""",
        CANT_FIND_MSG_EVENT_SYMBOL
      )
    );
  }
}
