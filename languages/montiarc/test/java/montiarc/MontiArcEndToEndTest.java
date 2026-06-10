/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcautomaton._cocos.NoInputPortInEntryAction;
import arcautomaton._cocos.NoInputPortInExitAction;
import arcautomaton._cocos.NoNonSyncInputPortInDoAction;
import arcautomaton._cocos.NoNonSyncInputPortInEpsilonTransition;
import arcautomaton._cocos.NoOtherInputPortInMsgTransition;
import arccompute._cocos.NoInputPortsInInitialCompute;
import arccompute._cocos.NoNonSyncInputPortInCompute;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.io.File;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static de.se_rwth.commons.logging.Finding.Type.ERROR;
import static de.se_rwth.commons.logging.Finding.Type.WARNING;
import static montiarc.util.ArcAutomataError.CANT_FIND_MSG_EVENT_SYMBOL;
import static montiarc.util.ArcComputeError.INIT_BLOCK_WITHOUT_COMPUTE;
import static montiarc.util.ArcComputeError.MULTIPLE_INIT;
import static montiarc.util.ArcError.CIRCULAR_FIELDS_DEPENDENCY;
import static montiarc.util.ArcError.CIRCULAR_INHERITANCE;
import static montiarc.util.ArcError.COMPONENT_LOWER_CASE;
import static montiarc.util.ArcError.COMPONENT_REFERENCE_CYCLE;
import static montiarc.util.ArcError.CONNECTOR_TIMING_MISMATCH;
import static montiarc.util.ArcError.CONNECTOR_TYPE_MISMATCH;
import static montiarc.util.ArcError.FIELD_INIT_TYPE_MISMATCH;
import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static montiarc.util.ArcError.FIELD_UPPER_CASE;
import static montiarc.util.ArcError.INVALID_CONTEXT_ASSIGNMENT;
import static montiarc.util.ArcError.IN_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;
import static montiarc.util.ArcError.IN_PORT_UNUSED;
import static montiarc.util.ArcError.MISSING_PORT;
import static montiarc.util.ArcError.MISSING_SUBCOMPONENT;
import static montiarc.util.ArcError.MULTIPLE_BEHAVIOR;
import static montiarc.util.ArcError.OPTIONAL_PARAMS_LAST;
import static montiarc.util.ArcError.OUT_PORT_NOT_CONNECTED;
import static montiarc.util.ArcError.OUT_PORT_UNUSED;
import static montiarc.util.ArcError.PARAMETER_UPPER_CASE;
import static montiarc.util.ArcError.PARAM_DEFAULT_TYPE_MISMATCH;
import static montiarc.util.ArcError.PORT_MULTIPLE_SENDER;
import static montiarc.util.ArcError.PORT_UPPER_CASE;
import static montiarc.util.ArcError.SOURCE_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.SUBCOMPONENT_UPPER_CASE;
import static montiarc.util.ArcError.TARGET_DIRECTION_MISMATCH;
import static montiarc.util.ArcError.TYPE_ARG_IGNORES_UPPER_BOUND;
import static montiarc.util.ArcError.TYPE_PARAMETER_UPPER_CASE;
import static montiarc.util.ArcError.UNIQUE_IDENTIFIER_NAMES;
import static montiarc.util.ArcError.UNSUPPORTED_MODEL_ELEMENT;
import static montiarc.util.ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT;
import static montiarc.util.ComfortableArcError.MULTIPLE_AUTOCONNECTS;
import static montiarc.util.MCError.CANT_FIND_SYMBOL;
import static montiarc.util.MCError.CANT_FIND_SYMBOL_IN_EXPRESSION;
import static montiarc.util.MCError.DUPLICATE_VAR_IN_SCOPE;
import static montiarc.util.MCError.FOR_EACH_EXPR_NOT_ITERABLE;
import static montiarc.util.MCError.FOR_EACH_TYPE_MISMATCH;
import static montiarc.util.MCError.MISSING_COMPONENT;
import static montiarc.util.MCError.SWITCH_CASE_INCOMPATIBLE;
import static montiarc.util.ModesError.MODE_AUTOMATON_CONTAINS_STATE;
import static montiarc.util.ModesError.MODE_CONTAINS_PORT_DEFINITION;
import static montiarc.util.MontiArcError.IMPORTED_SYMBOL_MISSING;
import static montiarc.util.SCError.CANT_FIND_SOURCE;
import static montiarc.util.SCError.CANT_FIND_TARGET;
import static montiarc.util.SCError.DUPLICATE_STATE;
import static montiarc.util.SCError.MISSING_INITIAL_STATE;
import static montiarc.util.VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE;
import static montiarc.util.VariableArcError.FEATURE_UPPER_CASE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class groups the end-to-end tests for MontiArc. These tests mainly
 * ensure that the integration of the parser, symbol table, and cocos produces
 * the correct error messages for invalid models. That is, they verify that the
 * appropriate errors are raised, with properly formatted messages, at the
 * correct source positions.
 */
public class MontiArcEndToEndTest extends MontiArcTestBase {

  private final static String TEST_DIR = "endtoend";

  private final static String PKG_AUT = "automata";

  private final static String PKG_COMP = "components";

  private final static String PKG_CPOS = "composition";

  private final static String PKG_CB = "compute";

  private final static String PKG_STMT = "statements";

  private final static String PKG_VARI = "variability";

  private final static String PKG_MODES = "modes";

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidModelAndErrorProvider")
  @MethodSource("invalidModelAndErrorNoVariabilityProvider")
  @DisableIfDisplayName(contains = {
    "NameClash",
    "SelfReferentialComponentWithCompositionTest",
    "CircularInheritanceWithCompositionTest3"
  })
  void invalidModelsShouldFailEndToEnd(@NotNull String name,
                                       @NotNull String modelPath,
                                       @NotNull Finding[] findings) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(modelPath);
    Preconditions.checkNotNull(findings);
    Preconditions.checkArgument(!name.isBlank());

    String symbolPath = Paths.get(TEST_RESOURCE, TEST_DIR).toString();

    // Given
    String[] args = new String[]{"-i", modelPath, "-path", symbolPath, "-novar", "-c2mc"};
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(args);

    // Then
    assertThat(Log.getFindings())
      .containsExactlyInAnyOrder(findings);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("invalidModelAndErrorProvider")
  @MethodSource("invalidModelAndError4VariabilityProvider")
  @DisableIfDisplayName(contains = {
    "CircularInheritanceTest7",
    "NameClash",
    "SelfReferentialComponentWithCompositionTest",
    "PortMultipleSender",
    "ConnectorMismatchDirectionTest4",
    "MissingPortInConnectorTest1",
    "MissingSubcomponentInConnectorTest",
    "CircularInheritanceWithCompositionTest",
    "MissingEventInTransitionTest3",
    "MissingEventInTransitionTest4",
    "MissingEventInTransitionTest6",
    "MissingSymbolsInGuardTest3",
    "MissingSymbolsInTransitionActionTest",
    "MissingSymbolsInEntryAction",
    "MissingSymbolsInExitAction",
    "MissingSymbolsInConstraint",
    "MissingSymbolsInVarIfTest",
    "MissingSymbolsInVarIfWithCompositionTest",
    "MoreThanOneBehaviorTest",
    "MoreThanOneBehaviorWithVariabilityTest",
    "ForEachExpressionNotIterableTest",
    "ForEachTypeMismatchTest"
  })
  void invalidModelsShouldFailEndToEnd4Variability(@NotNull String name,
                                                   @NotNull String modelPath,
                                                   @NotNull Finding[] findings) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(modelPath);
    Preconditions.checkNotNull(findings);
    Preconditions.checkArgument(!name.isBlank());

    String symbolPath = Paths.get(TEST_RESOURCE, TEST_DIR).toString();

    // Given
    String[] args = new String[]{"-i", modelPath, "-path", symbolPath, "-c2mc"};
    MontiArcTool tool = new MontiArcTool();

    // When
    tool.run(args);

    // Then
    assertThat(Log.getFindings())
      .containsExactlyInAnyOrder(findings);
  }

  protected static Stream<Arguments> invalidModelAndErrorProvider() {
    return Stream.of(
      arg("DuplicateStatesTest1",
        mpk(PKG_AUT, "DuplicateStates1.arc"),
        fn(ERROR, PKG_AUT, "DuplicateStates1.arc", 13, 5, 13, 14, DUPLICATE_STATE, "s2")
      ),
      arg("DuplicateStatesTest2",
        mpk(PKG_AUT, "DuplicateStates2.arc"),
        fn(ERROR, PKG_AUT, "DuplicateStates2.arc", 16, 7, 18, 8, DUPLICATE_STATE, "s3")
      ),
      arg("InputPortInEntryActionTest",
        mpk(PKG_AUT, "InputPortInEntryAction.arc"),
        fn(ERROR, PKG_AUT, "InputPortInEntryAction.arc", 13, 25, 13, 26, IN_PORT_REF_IN_INVALID_CONTEXT, "i", NoInputPortInEntryAction.CONTEXT)
      ),
      arg("InputPortInExitActionTest",
        mpk(PKG_AUT, "InputPortInExitAction.arc"),
        fn(ERROR, PKG_AUT, "InputPortInExitAction.arc", 13, 24, 13, 25, IN_PORT_REF_IN_INVALID_CONTEXT, "i", NoInputPortInExitAction.CONTEXT)
      ),
      arg("MissingEventInTransitionTest1",
        mpk(PKG_AUT, "MissingEventInTransition1.arc"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition1.arc", 11, 12, 11, 15, CANT_FIND_MSG_EVENT_SYMBOL, "msg")
      ),
      arg("MissingEventInTransitionTest2",
        mpk(PKG_AUT, "MissingEventInTransition2.arc"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition2.arc", 12, 12, 12, 16, CANT_FIND_MSG_EVENT_SYMBOL, "msg1"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition2.arc", 13, 12, 13, 16, CANT_FIND_MSG_EVENT_SYMBOL, "msg2")
      ),
      arg("MissingEventInTransitionTest3",
        mpk(PKG_AUT, "MissingEventInTransition3.arc"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition3.arc", 12, 14, 12, 17, CANT_FIND_MSG_EVENT_SYMBOL, "msg")
      ),
      arg("MissingEventInTransitionTest4",
        mpk(PKG_AUT, "MissingEventInTransition4.arc"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition4.arc", 12, 10, 12, 13, CANT_FIND_MSG_EVENT_SYMBOL, "msg")
      ),
      arg("MissingEventInTransitionTest5",
        mpk(PKG_AUT, "MissingEventInTransition5.arc"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition5.arc", 19, 12, 19, 13, CANT_FIND_MSG_EVENT_SYMBOL, "o"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition5.arc", 20, 12, 20, 13, CANT_FIND_MSG_EVENT_SYMBOL, "v"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition5.arc", 21, 12, 21, 13, CANT_FIND_MSG_EVENT_SYMBOL, "p"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition5.arc", 22, 12, 22, 13, CANT_FIND_MSG_EVENT_SYMBOL, "T"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition5.arc", 23, 12, 23, 13, CANT_FIND_MSG_EVENT_SYMBOL, "S")
      ),
      arg("MissingEventInTransitionTest6",
        mpk(PKG_AUT, "MissingEventInTransition6.arc"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition6.arc", 12, 16, 12, 20, CANT_FIND_MSG_EVENT_SYMBOL, "msg1"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition6.arc", 16, 16, 16, 20, CANT_FIND_MSG_EVENT_SYMBOL, "msg2"),
        fn(ERROR, PKG_AUT, "MissingEventInTransition6.arc", 18, 18, 18, 22, CANT_FIND_MSG_EVENT_SYMBOL, "msg3")
      ),
      arg("MissingStateInTransitionTest1",
        mpk(PKG_AUT, "MissingStateInTransition1.arc"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition1.arc", 12, 5, 12, 6, CANT_FIND_SOURCE, "M")
      ),
      arg("MissingStateInTransitionTest2",
        mpk(PKG_AUT, "MissingStateInTransition2.arc"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition2.arc", 12, 10, 12, 11, CANT_FIND_TARGET, "M")
      ),
      arg("MissingStateInTransitionTest3",
        mpk(PKG_AUT, "MissingStateInTransition3.arc"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition3.arc", 12, 5,  12, 7,  CANT_FIND_SOURCE, "M1"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition3.arc", 12, 11, 12, 13, CANT_FIND_TARGET, "M2")
      ),
      arg("MissingStateInTransitionTest4",
        mpk(PKG_AUT, "MissingStateInTransition4.arc"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition4.arc", 12, 5,  12, 7,  CANT_FIND_SOURCE, "M1"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition4.arc", 13, 10, 13, 12, CANT_FIND_TARGET, "M2")
      ),
      arg("MissingStateInTransitionTest5",
        mpk(PKG_AUT, "MissingStateInTransition5.arc"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition5.arc", 12, 7,  12, 9,  CANT_FIND_SOURCE, "M1"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition5.arc", 12, 13, 12, 15, CANT_FIND_TARGET, "M2"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition5.arc", 16, 7,  16, 9,  CANT_FIND_SOURCE, "M3"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition5.arc", 16, 13, 16, 15, CANT_FIND_TARGET, "M4"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition5.arc", 18, 9,  18, 11, CANT_FIND_SOURCE, "M5"),
        fn(ERROR, PKG_AUT, "MissingStateInTransition5.arc", 18, 15, 18, 17, CANT_FIND_TARGET, "M6")
      ),
      arg("MissingSymbolsInEntryActionTest1",
        mpk(PKG_AUT, "MissingSymbolsInEntryAction1.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction1.arc", 12, 15, 12, 16, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInEntryActionTest2",
        mpk(PKG_AUT, "MissingSymbolsInEntryAction2.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction2.arc", 12, 17, 12, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInEntryActionTest3",
        mpk(PKG_AUT, "MissingSymbolsInEntryAction3.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction3.arc", 12, 17, 12, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction3.arc", 13, 17, 13, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2")
      ),
      arg("MissingSymbolsInEntryActionTest4",
        mpk(PKG_AUT, "MissingSymbolsInEntryAction4.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction4.arc", 12, 17, 12, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction4.arc", 16, 17, 16, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInEntryAction4.arc", 18, 19, 18, 21, CANT_FIND_SYMBOL_IN_EXPRESSION, "a3")
      ),
      arg("MissingSymbolsInExitActionTest1",
        mpk(PKG_AUT, "MissingSymbolsInExitAction1.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInExitAction1.arc", 12, 14, 12, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInExitActionTest2",
        mpk(PKG_AUT, "MissingSymbolsInExitAction2.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInExitAction2.arc", 12, 16, 12, 17, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInExitActionTest3",
        mpk(PKG_AUT, "MissingSymbolsInExitAction3.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInExitAction3.arc", 12, 16, 12, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInExitAction3.arc", 13, 16, 13, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2")
      ),
      arg("MissingSymbolsInExitActionTest4",
        mpk(PKG_AUT, "MissingSymbolsInExitAction4.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInExitAction4.arc", 12, 16, 12, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInExitAction4.arc", 16, 16, 16, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInExitAction4.arc", 18, 18, 18, 20, CANT_FIND_SYMBOL_IN_EXPRESSION, "a3")
      ),
      arg("MissingSymbolsInGuardTest1",
        mpk(PKG_AUT, "MissingSymbolsInGuard1.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard1.arc", 12, 13, 12, 14, CANT_FIND_SYMBOL_IN_EXPRESSION, "g")
      ),
      arg("MissingSymbolsInGuardTest2",
        mpk(PKG_AUT, "MissingSymbolsInGuard2.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard2.arc", 12, 13, 12, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "g1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard2.arc", 13, 13, 13, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "g2")
      ),
      arg("MissingSymbolsInGuardTest3",
        mpk(PKG_AUT, "MissingSymbolsInGuard3.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard3.arc", 12, 11, 12, 12, CANT_FIND_SYMBOL_IN_EXPRESSION, "g")
      ),
      arg("MissingSymbolsInGuardTest4",
        mpk(PKG_AUT, "MissingSymbolsInGuard4.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard4.arc", 12, 17, 12, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "g1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard4.arc", 16, 17, 16, 19, CANT_FIND_SYMBOL_IN_EXPRESSION, "g2"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInGuard4.arc", 18, 19, 18, 21, CANT_FIND_SYMBOL_IN_EXPRESSION, "g3")
      ),
      arg("MissingSymbolsInTransitionActionTest1",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction1.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction1.arc", 12, 14, 12, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInTransitionActionTest2",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction2.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction2.arc", 12, 16, 12, 17, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInTransitionActionTest3",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction3.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction3.arc", 12, 16, 12, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction3.arc", 13, 16, 13, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2")
      ),
      arg("MissingSymbolsInTransitionActionTest4",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction4.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction4.arc", 12, 12, 12, 13, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInTransitionActionTest5",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction5.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction5.arc", 12, 14, 12, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "a")
      ),
      arg("MissingSymbolsInTransitionActionTest6",
        mpk(PKG_AUT, "MissingSymbolsInTransitionAction6.arc"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction6.arc", 12, 20, 12, 22, CANT_FIND_SYMBOL_IN_EXPRESSION, "a1"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction6.arc", 16, 20, 16, 22, CANT_FIND_SYMBOL_IN_EXPRESSION, "a2"),
        fn(ERROR, PKG_AUT, "MissingSymbolsInTransitionAction6.arc", 18, 22, 18, 24, CANT_FIND_SYMBOL_IN_EXPRESSION, "a3")
      ),
      arg("NoInitialStateTest1",
        mpk(PKG_AUT, "NoInitialState1.arc"),
        fn(ERROR, PKG_AUT, "NoInitialState1.arc", 8, 3, 8, 16, MISSING_INITIAL_STATE)
      ),
      arg("NoInitialStateTest2",
        mpk(PKG_AUT, "NoInitialState2.arc"),
        fn(ERROR, PKG_AUT, "NoInitialState2.arc", 9, 5, 9, 18, MISSING_INITIAL_STATE)
      ),
      arg("NonSyncInputPortInDoActionTest",
        mpk(PKG_AUT, "NonSyncInputPortInDoAction.arc"),
        fn(ERROR, PKG_AUT, "NonSyncInputPortInDoAction.arc", 13, 17, 13, 18, IN_PORT_REF_IN_INVALID_CONTEXT, "i", NoNonSyncInputPortInDoAction.CONTEXT)
      ),
      arg("NonSyncInputPortInEpsilonTransitionTest",
        mpk(PKG_AUT, "NonSyncInputPortInEpsilonTransition.arc"),
        fn(ERROR, PKG_AUT, "NonSyncInputPortInEpsilonTransition.arc", 13, 15, 13, 16, IN_PORT_REF_IN_INVALID_CONTEXT, "i", NoNonSyncInputPortInEpsilonTransition.CONTEXT)
      ),
      arg("NonMsgInputPortInMsgTransitionTest",
        mpk(PKG_AUT, "NonMsgInputPortInMsgTransition.arc"),
        fn(ERROR, PKG_AUT, "NonMsgInputPortInMsgTransition.arc", 13, 15, 13, 17, IN_PORT_REF_IN_INVALID_CONTEXT, "i2", NoOtherInputPortInMsgTransition.CONTEXT)
      ),
      arg("CircularFieldDependencyTest1",
        mpk(PKG_COMP, "CircularFieldDependency1.arc"),
        fn(ERROR, PKG_COMP, "CircularFieldDependency1.arc", 7, 1, 10, 2, CIRCULAR_FIELDS_DEPENDENCY, "a, b")
      ),
      arg("CircularFieldDependencyTest2",
        mpk(PKG_COMP, "CircularFieldDependency2.arc"),
        fn(ERROR, PKG_COMP, "CircularFieldDependency2.arc", 7, 1, 11, 2, CIRCULAR_FIELDS_DEPENDENCY, "a, b, c")
      ),
      arg("CircularFieldDependencyTest3",
        mpk(PKG_COMP, "CircularFieldDependency3.arc"),
        fn(ERROR, PKG_COMP, "CircularFieldDependency3.arc", 7, 1, 10, 2, CIRCULAR_FIELDS_DEPENDENCY, "a, b")
      ),
      arg("CircularInheritanceTest1",
        mpk(PKG_COMP, "CircularInheritance1.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance1.arc", 7, 32, 7, 60, CIRCULAR_INHERITANCE, "CircularInheritance1")
      ),
      arg("CircularInheritanceTest2",
        mpk(PKG_COMP, "CircularInheritance2A.arc", "CircularInheritance2B.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance2A.arc", 8, 33, 8, 62, CIRCULAR_INHERITANCE, "CircularInheritance2A"),
        fn(ERROR, PKG_COMP, "CircularInheritance2B.arc", 8, 33, 8, 62, CIRCULAR_INHERITANCE, "CircularInheritance2B")
      ),
      arg("CircularInheritanceTest3",
        mpk(PKG_COMP, "CircularInheritance3.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance3.arc", 8, 19, 8, 32, CIRCULAR_INHERITANCE, "Inner")
      ),
      arg("CircularInheritanceTest4",
        mpk(PKG_COMP, "CircularInheritance4.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance4.arc", 9, 20, 9, 34, CIRCULAR_INHERITANCE, "Inner1"),
        fn(ERROR, PKG_COMP, "CircularInheritance4.arc", 10, 20, 10, 34, CIRCULAR_INHERITANCE, "Inner2")
      ),
      arg("CircularInheritanceTest5",
        mpk(PKG_COMP, "CircularInheritance5.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance5.arc", 10, 26, 10, 44, CIRCULAR_INHERITANCE, "InnerInner")
      ),
      arg("CircularInheritanceTest6",
        mpk(PKG_COMP, "CircularInheritance6.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance6.arc", 10, 27, 10, 46, CIRCULAR_INHERITANCE, "InnerInner1"),
        fn(ERROR, PKG_COMP, "CircularInheritance6.arc", 11, 27, 11, 46, CIRCULAR_INHERITANCE, "InnerInner2")
      ),
      arg("CircularInheritanceTest7",
        mpk(PKG_COMP, "CircularInheritance7A.arc"),
        fn(ERROR, PKG_COMP, "CircularInheritance7A.arc", 8, 33, 8, 62, CIRCULAR_INHERITANCE, "CircularInheritance7A")
      ),
      arg("MissingComponentTest1",
        mpk(PKG_COMP, "MissingComponent1.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent1.arc", 9, 3, 9, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest2",
        mpk(PKG_COMP, "MissingComponent2.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent2.arc", 9, 3, 9, 14, MISSING_COMPONENT, "a.b.Missing")
      ),
      arg("MissingComponentTest3",
        mpk(PKG_COMP, "MissingComponent3.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent3.arc", 9, 3, 9, 10, MISSING_COMPONENT, "Missing"),
        fn(ERROR, PKG_COMP, "MissingComponent3.arc", 10, 3, 10, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest4",
        mpk(PKG_COMP, "MissingComponent4.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent4.arc", 10, 5, 10, 12, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest5",
        mpk(PKG_COMP, "MissingComponent5.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent5.arc", 14, 5, 14, 12, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest7",
        mpk(PKG_COMP, "MissingComponent7.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent7.arc", 10, 3, 10, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingComponentTest8",
        mpk(PKG_COMP, "MissingComponent8.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent8.arc", 9, 3, 9, 10, MISSING_COMPONENT, "boolean")
      ),
      arg("MissingComponentTest9",
        mpk(PKG_COMP, "MissingComponent9.arc"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 9, 3, 9, 10, MISSING_COMPONENT, "boolean"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 10, 3, 10, 7, MISSING_COMPONENT, "byte"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 11, 3, 11, 8, MISSING_COMPONENT, "short"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 12, 3, 12, 6, MISSING_COMPONENT, "int"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 13, 3, 13, 7, MISSING_COMPONENT, "long"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 14, 3, 14, 8, MISSING_COMPONENT, "float"),
        fn(ERROR, PKG_COMP, "MissingComponent9.arc", 15, 3, 15, 9, MISSING_COMPONENT, "double")
      ),
      arg("MissingPortTypeTest1",
        mpk(PKG_COMP, "MissingPortType1.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType1.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest2",
        mpk(PKG_COMP, "MissingPortType2.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType2.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest3",
        mpk(PKG_COMP, "MissingPortType3.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType3.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_COMP, "MissingPortType3.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest4",
        mpk(PKG_COMP, "MissingPortType4.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType4.arc", 14, 13, 14, 20, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_COMP, "MissingPortType4.arc", 15, 14, 15, 21, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest9",
        mpk(PKG_COMP, "MissingPortType9.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType9.arc", 11, 11, 11, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest10",
        mpk(PKG_COMP, "MissingPortType10.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType10.arc", 11, 11, 11, 18, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest11",
        mpk(PKG_COMP, "MissingPortType11.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType11.arc", 12, 12, 12, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeTest12",
        mpk(PKG_COMP, "MissingPortType12.arc"),
        fn(ERROR, PKG_COMP, "MissingPortType12.arc", 11, 11, 11, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_COMP, "MissingPortType12.arc", 12, 12, 12, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MoreThanOneBehaviorTest1",
        mpk(PKG_COMP, "MoreThanOneBehavior1.arc"),
        fn(ERROR, PKG_COMP, "MoreThanOneBehavior1.arc", 10, 3, 10, 33, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorTest2",
        mpk(PKG_COMP, "MoreThanOneBehavior2.arc"),
        fn(ERROR, PKG_COMP, "MoreThanOneBehavior2.arc", 10, 3, 10, 14, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorTest3",
        mpk(PKG_COMP, "MoreThanOneBehavior3.arc"),
        fn(ERROR, PKG_COMP, "MoreThanOneBehavior3.arc", 11, 3, 11, 14, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorTest4",
        mpk(PKG_COMP, "MoreThanOneBehavior4.arc"),
        fn(ERROR, PKG_COMP, "MoreThanOneBehavior4.arc", 12, 5, 12, 35, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorTest5",
        mpk(PKG_COMP, "MoreThanOneBehavior5.arc"),
        fn(ERROR, PKG_COMP, "MoreThanOneBehavior5.arc", 10, 3, 10, 33, MULTIPLE_BEHAVIOR),
        fn(ERROR, PKG_COMP, "MoreThanOneBehavior5.arc", 11, 3, 11, 33, MULTIPLE_BEHAVIOR)
      ),
      arg("NamesCapitalizationTest1",
        mpk(PKG_COMP, "namesCapitalization1.arc"),
        fn(WARNING, PKG_COMP, "namesCapitalization1.arc", 7, 1, 7, 35, COMPONENT_LOWER_CASE)
      ),
      arg("NamesCapitalizationTest2",
        mpk(PKG_COMP, "NamesCapitalization2.arc"),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 7, 40, 7, 46, PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 7, 48, 7, 54, PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 7, 32, 7, 34, TYPE_PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 7, 36, 7, 38, TYPE_PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 8, 15, 8, 17, PORT_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 9, 16, 9, 18, PORT_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 10, 15, 10, 17, PORT_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 11, 16, 11, 18, PORT_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 13, 7, 13, 13, FIELD_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 15, 3, 30, 4, COMPONENT_LOWER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 15, 23, 15, 29, PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 15, 19, 15, 21, TYPE_PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 16, 16, 16, 18, PORT_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 16, 20, 16, 22, PORT_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 17, 17, 17, 19, PORT_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 19, 9, 19, 15, FIELD_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 32, 14, 32, 26, SUBCOMPONENT_UPPER_CASE),
        fn(WARNING, PKG_COMP, "NamesCapitalization2.arc", 32, 28, 32, 35, SUBCOMPONENT_UPPER_CASE)
      ),
      arg("OptionalBeforeMandatoryParameterTest",
        mpk(PKG_COMP, "OptionalBeforeMandatoryParameter.arc"),
        fn(ERROR, PKG_COMP, "OptionalBeforeMandatoryParameter.arc", 9, 56, 9, 62, OPTIONAL_PARAMS_LAST, "p2", "p1")
      ),
      arg("PortUnusedTest1",
        mpk(PKG_COMP, "PortUnused1.arc"),
        fn(WARNING, PKG_COMP, "PortUnused1.arc", 9, 15, 9, 17, IN_PORT_UNUSED, "i1")
      ),
      arg("PortUnusedTest2",
        mpk(PKG_COMP, "PortUnused2.arc"),
        fn(WARNING, PKG_COMP, "PortUnused2.arc", 9, 19, 9, 21, IN_PORT_UNUSED, "i2")
      ),
      arg("PortUnusedTest3",
        mpk(PKG_COMP, "PortUnused3.arc"),
        fn(WARNING, PKG_COMP, "PortUnused3.arc", 10, 16, 10, 18, OUT_PORT_UNUSED, "o1")
      ),
      arg("PortUnusedTest4",
        mpk(PKG_COMP, "PortUnused4.arc"),
        fn(WARNING, PKG_COMP, "PortUnused4.arc", 10, 20, 10, 22, OUT_PORT_UNUSED, "o2")
      ),
      arg("PortUnusedTest5",
        mpk(PKG_COMP, "PortUnused5.arc"),
        fn(WARNING, PKG_COMP, "PortUnused5.arc", 14, 17, 14, 19, IN_PORT_UNUSED, "i1"),
        fn(WARNING, PKG_COMP, "PortUnused5.arc", 14, 21, 14, 23, IN_PORT_UNUSED, "i2"),
        fn(WARNING, PKG_COMP, "PortUnused5.arc", 15, 18, 15, 20, OUT_PORT_UNUSED, "o1"),
        fn(WARNING, PKG_COMP, "PortUnused5.arc", 15, 22, 15, 24, OUT_PORT_UNUSED, "o2")
      ),
      arg("SelfReferentialComponentTest1",
        mpk(PKG_COMP, "SelfReferentialComponent1.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent1.arc", 10, 29, 10, 32, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent1", "SelfReferentialComponent1 -> SelfReferentialComponent1")
      ),
      arg("SelfReferentialComponentTest2",
        mpk(PKG_COMP, "SelfReferentialComponent2.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent2.arc", 10, 29, 10, 33, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent2", "SelfReferentialComponent2 -> SelfReferentialComponent2"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent2.arc", 11, 29, 11, 33, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent2", "SelfReferentialComponent2 -> SelfReferentialComponent2")
      ),
      arg("SelfReferentialComponentTest3",
        mpk(PKG_COMP, "SelfReferentialComponent3A.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent3A.arc", 14, 30, 14, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent3A", "SelfReferentialComponent3A -> SelfReferentialComponent3B -> SelfReferentialComponent3A"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent3A.arc", 17, 32, 17, 36, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent3B", "SelfReferentialComponent3B -> SelfReferentialComponent3A -> SelfReferentialComponent3B")
      ),
      arg("SelfReferentialComponentTest4",
        mpk(PKG_COMP, "SelfReferentialComponent4A.arc", "SelfReferentialComponent4B.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent4A.arc", 12, 30, 12, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent4A", "SelfReferentialComponent4A -> SelfReferentialComponent4B -> SelfReferentialComponent4A"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent4B.arc", 12, 30, 12, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent4B", "SelfReferentialComponent4B -> SelfReferentialComponent4A -> SelfReferentialComponent4B")
      ),
      arg("SelfReferentialComponentTest5",
        mpk(PKG_COMP, "SelfReferentialComponent5A.arc"),
        fn(ERROR, PKG_COMP, "SelfReferentialComponent5A.arc", 13, 30, 13, 34, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponent5A", "SelfReferentialComponent5A -> SelfReferentialComponent5B -> SelfReferentialComponent5A")
      ),
      arg("TypeMismatchOfDefaultParameterValueTest",
        mpk(PKG_COMP, "TypeMismatchOfDefaultParameterValue.arc"),
        fn(ERROR, PKG_COMP, "TypeMismatchOfDefaultParameterValue.arc", 7, 59, 7, 73, PARAM_DEFAULT_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("AutoconnectInAtomicTest1",
        mpk(PKG_CPOS, "AutoconnectInAtomic1.arc"),
        fn(ERROR, PKG_CPOS, "AutoconnectInAtomic1.arc", 9, 3, 9, 20, AUTOCONNECT_IN_ATOMIC_COMPONENT)
      ),
      arg("AutoconnectInAtomicTest2",
        mpk(PKG_CPOS, "AutoconnectInAtomic2.arc"),
        fn(ERROR, PKG_CPOS, "AutoconnectInAtomic2.arc", 9, 3, 9, 20, AUTOCONNECT_IN_ATOMIC_COMPONENT)
      ),
      arg("AutoconnectInAtomicTest3",
        mpk(PKG_CPOS, "AutoconnectInAtomic3.arc"),
        fn(ERROR, PKG_CPOS, "AutoconnectInAtomic3.arc", 9, 3, 9, 19, AUTOCONNECT_IN_ATOMIC_COMPONENT)
      ),
      arg("AutoconnectInAtomicTest4",
        mpk(PKG_CPOS, "AutoconnectInAtomic4.arc"),
        fn(ERROR, PKG_CPOS, "AutoconnectInAtomic4.arc", 11, 3, 11, 20, AUTOCONNECT_IN_ATOMIC_COMPONENT)
      ),
      arg("AutoconnectInAtomicTest5",
        mpk(PKG_CPOS, "AutoconnectInAtomic5.arc"),
        fn(ERROR, PKG_CPOS, "AutoconnectInAtomic5.arc", 9, 3, 9, 20, AUTOCONNECT_IN_ATOMIC_COMPONENT),
        fn(ERROR, PKG_CPOS, "AutoconnectInAtomic5.arc", 10, 3, 10, 20, MULTIPLE_AUTOCONNECTS, "2")
      ),
      arg("AutoconnectInAtomicTest6",
        mpk(PKG_CPOS, "AutoconnectInAtomic6.arc"),
        fn(ERROR, PKG_CPOS, "AutoconnectInAtomic6.arc", 10, 5, 10, 22, AUTOCONNECT_IN_ATOMIC_COMPONENT)
      ),
      arg("CircularInheritanceWithCompositionTest1",
        mpk(PKG_CPOS, "CircularInheritanceWithComposition1.arc"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition1.arc", 8, 47, 8, 90, CIRCULAR_INHERITANCE, "CircularInheritanceWithComposition1"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition1.arc", 13, 20, 13, 34, CIRCULAR_INHERITANCE, "Inner1"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition1.arc", 18, 20, 18, 34, CIRCULAR_INHERITANCE, "Inner2")
      ),
      arg("CircularInheritanceWithCompositionTest2",
        mpk(PKG_CPOS, "CircularInheritanceWithComposition2A.arc", "CircularInheritanceWithComposition2B.arc", "CircularInheritanceWithComposition2C.arc"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition2B.arc", 7, 48, 7, 92, CIRCULAR_INHERITANCE, "CircularInheritanceWithComposition2B"),
        fn(ERROR, PKG_CPOS, "CircularInheritanceWithComposition2C.arc", 7, 48, 7, 92, CIRCULAR_INHERITANCE, "CircularInheritanceWithComposition2C")
      ),
      arg("CircularInheritanceWithCompositionTest3",
        mpk(PKG_CPOS, "CircularInheritanceWithComposition3A.arc"),
        new Finding[]{}
      ),
      arg("ConnectorMismatchDirectionTest1",
        mpk(PKG_CPOS, "ConnectorMismatchDirection1.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection1.arc", 25, 3, 25, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection1.arc", 25, 8, 25, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection1.arc", 26, 3, 26, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection1.arc", 26, 12, 26, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchDirectionTest2",
        mpk(PKG_CPOS, "ConnectorMismatchDirection2.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 27, 3, 27, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 27, 8, 27, 14, TARGET_DIRECTION_MISMATCH, "sub.o1"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 27, 16, 27, 22, TARGET_DIRECTION_MISMATCH, "sub.o2"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 28, 3, 28, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 28, 12, 28, 14, TARGET_DIRECTION_MISMATCH, "i1"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection2.arc", 28, 16, 28, 18, TARGET_DIRECTION_MISMATCH, "i2")
      ),
      arg("ConnectorMismatchDirectionTest3",
        mpk(PKG_CPOS, "ConnectorMismatchDirection3A.arc", "ConnectorMismatchDirection3B.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection3A.arc", 20, 3, 20, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection3A.arc", 20, 8, 20, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection3A.arc", 21, 3, 21, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection3A.arc", 21, 12, 21, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchDirectionTest4",
        mpk(PKG_CPOS, "ConnectorMismatchDirection4A.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection4A.arc", 21, 3, 21, 4, SOURCE_DIRECTION_MISMATCH, "o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection4A.arc", 21, 8, 21, 13, TARGET_DIRECTION_MISMATCH, "sub.o"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection4A.arc", 22, 3, 22, 8, SOURCE_DIRECTION_MISMATCH, "sub.i"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchDirection4A.arc", 22, 12, 22, 13, TARGET_DIRECTION_MISMATCH, "i")
      ),
      arg("ConnectorMismatchTimingTest1",
        mpk(PKG_CPOS, "ConnectorMismatchTiming1.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming1.arc", 27, 8, 27, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming1.arc", 29, 13, 29, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming1.arc", 31, 13, 31, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest2",
        mpk(PKG_CPOS, "ConnectorMismatchTiming2.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 27, 8, 27, 15, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 27, 17, 27, 24, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 29, 13, 29, 20, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 29, 22, 29, 29, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 31, 13, 31, 15, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming2.arc", 31, 17, 31, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest3",
        mpk(PKG_CPOS, "ConnectorMismatchTiming3A.arc", "ConnectorMismatchTiming3B.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming3A.arc", 22, 8, 22, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming3A.arc", 24, 13, 24, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming3A.arc", 26, 13, 26, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTimingTest4",
        mpk(PKG_CPOS, "ConnectorMismatchTiming4A.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming4A.arc", 23, 8, 23, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming4A.arc", 25, 13, 25, 19, CONNECTOR_TIMING_MISMATCH, "sync", "timed"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchTiming4A.arc", 27, 13, 27, 14, CONNECTOR_TIMING_MISMATCH, "sync", "timed")
      ),
      arg("ConnectorMismatchTypeTest1",
        mpk(PKG_CPOS, "ConnectorMismatchType1.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType1.arc", 27, 8, 27, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType1.arc", 29, 13, 29, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType1.arc", 31, 13, 31, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest2",
        mpk(PKG_CPOS, "ConnectorMismatchType2.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 33, 8, 33, 15, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 33, 17, 33, 24, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 35, 13, 35, 20, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 35, 22, 35, 29, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 37, 13, 37, 15, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType2.arc", 37, 17, 37, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest3",
        mpk(PKG_CPOS, "ConnectorMismatchType3A.arc", "ConnectorMismatchType3B.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType3A.arc", 22, 8, 22, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType3A.arc", 24, 13, 24, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType3A.arc", 26, 13, 26, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("ConnectorMismatchTypeTest4",
        mpk(PKG_CPOS, "ConnectorMismatchType4A.arc"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType4A.arc", 23, 8, 23, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType4A.arc", 25, 13, 25, 19, CONNECTOR_TYPE_MISMATCH, "int", "boolean"),
        fn(ERROR, PKG_CPOS, "ConnectorMismatchType4A.arc", 27, 13, 27, 14, CONNECTOR_TYPE_MISMATCH, "int", "boolean")
      ),
      arg("MissingComponentInConnectorTest",
        mpk(PKG_CPOS, "MissingComponentInConnector.arc"),
        fn(ERROR, PKG_CPOS, "MissingComponentInConnector.arc", 13, 3, 13, 10, MISSING_COMPONENT, "Missing")
      ),
      arg("MissingPortInConnectorTest1",
        mpk(PKG_CPOS, "MissingPortInConnector1.arc"),
        fn(ERROR, PKG_CPOS, "MissingPortInConnector1.arc", 17, 3, 17, 4, MISSING_PORT, "i"),
        fn(ERROR, PKG_CPOS, "MissingPortInConnector1.arc", 18, 12, 18, 13, MISSING_PORT, "o")
      ),
      arg("MissingPortInConnectorTest2",
        mpk(PKG_CPOS, "MissingPortInConnector2.arc"),
        fn(ERROR, PKG_CPOS, "MissingPortInConnector2.arc", 19, 8, 19, 13, MISSING_PORT, "sub.i"),
        fn(ERROR, PKG_CPOS, "MissingPortInConnector2.arc", 20, 3, 20, 8, MISSING_PORT, "sub.o")
      ),
      arg("MissingPortInConnectorTest3",
        mpk(PKG_CPOS, "MissingPortInConnector3A.arc", "MissingPortInConnector3B.arc"),
        fn(ERROR, PKG_CPOS, "MissingPortInConnector3A.arc", 17, 8, 17, 13, MISSING_PORT, "sub.i"),
        fn(ERROR, PKG_CPOS, "MissingPortInConnector3A.arc", 18, 3, 18, 8, MISSING_PORT, "sub.o")
      ),
      arg("MissingPortInConnectorTest4",
        mpk(PKG_CPOS, "MissingPortInConnector4A.arc"),
        fn(ERROR, PKG_CPOS, "MissingPortInConnector4A.arc", 18, 8, 18, 13, MISSING_PORT, "sub.i"),
        fn(ERROR, PKG_CPOS, "MissingPortInConnector4A.arc", 19, 3, 19, 8, MISSING_PORT, "sub.o")
      ),
      arg("MissingPortTypeInConnectorTest1",
        mpk(PKG_CPOS, "MissingPortTypeInConnector1.arc"),
        fn(ERROR, PKG_CPOS, "MissingPortTypeInConnector1.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_CPOS, "MissingPortTypeInConnector1.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeInConnectorTest2",
        mpk(PKG_CPOS, "MissingPortTypeInConnector2.arc"),
        fn(ERROR, PKG_CPOS, "MissingPortTypeInConnector2.arc", 15, 13, 15, 20, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_CPOS, "MissingPortTypeInConnector2.arc", 16, 14, 16, 21, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeInConnectorTest3",
        path(mpk(PKG_CPOS, "MissingPortTypeInConnector3.arc"), mpk(PKG_COMP, "MissingPortType3.arc")),
        fn(ERROR, PKG_COMP, "MissingPortType3.arc", 10, 11, 10, 18, CANT_FIND_SYMBOL, "Missing"),
        fn(ERROR, PKG_COMP, "MissingPortType3.arc", 11, 12, 11, 19, CANT_FIND_SYMBOL, "Missing")
      ),
      arg("MissingPortTypeInConnectorTest4",
        mpk(PKG_CPOS, "MissingPortTypeInConnector4A.arc"),
        fn(ERROR, PKG_CPOS, "MissingPortTypeInConnector4A.arc", 19, 8, 19, 13, CONNECTOR_TYPE_MISMATCH, "Missing", "int"),
        fn(ERROR, PKG_CPOS, "MissingPortTypeInConnector4A.arc", 20, 12, 20, 13, CONNECTOR_TYPE_MISMATCH, "int", "Missing")
      ),
      arg("MissingSubcomponentInConnectorTest",
        mpk(PKG_CPOS, "MissingSubcomponentInConnector.arc"),
        fn(ERROR, PKG_CPOS, "MissingSubcomponentInConnector.arc", 17, 8, 17, 14, MISSING_SUBCOMPONENT, "subA"),
        fn(ERROR, PKG_CPOS, "MissingSubcomponentInConnector.arc", 18, 3, 18, 9, MISSING_SUBCOMPONENT, "subA")
      ),
      arg("PortMultipleSenderTest1",
        mpk(PKG_CPOS, "PortMultipleSender1.arc"),
        fn(ERROR, PKG_CPOS, "PortMultipleSender1.arc", 22, 13, 22, 14, PORT_MULTIPLE_SENDER, "o")
      ),
      arg("PortMultipleSenderTest2",
        mpk(PKG_CPOS, "PortMultipleSender2.arc"),
        fn(ERROR, PKG_CPOS, "PortMultipleSender2.arc", 21, 15, 21, 16, PORT_MULTIPLE_SENDER, "o")
      ),
      arg("PortMultipleSenderTest3",
        mpk(PKG_CPOS, "PortMultipleSender3.arc"),
        fn(ERROR, PKG_CPOS, "PortMultipleSender3.arc", 21, 9, 21, 14, PORT_MULTIPLE_SENDER, "sub.i")
      ),
      arg("PortNotConnectedTest1",
        mpk(PKG_CPOS, "PortNotConnected1.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected1.arc", 18, 9, 18, 12, IN_PORT_NOT_CONNECTED, "sub.i1")
      ),
      arg("PortNotConnectedTest2",
        mpk(PKG_CPOS, "PortNotConnected2.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected2.arc", 18, 9, 18, 12, IN_PORT_NOT_CONNECTED, "sub.i2")
      ),
      arg("PortNotConnectedTest3",
        mpk(PKG_CPOS, "PortNotConnected3.arc"),
        fn(WARNING, PKG_CPOS, "PortNotConnected3.arc", 18, 9, 18, 12, OUT_PORT_NOT_CONNECTED, "sub.o1")
      ),
      arg("PortNotConnectedTest4",
        mpk(PKG_CPOS, "PortNotConnected4.arc"),
        fn(WARNING, PKG_CPOS, "PortNotConnected4.arc", 18, 9, 18, 12, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("PortNotConnectedTest5",
        mpk(PKG_CPOS, "PortNotConnected5.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected5.arc", 24, 22, 24, 26, IN_PORT_NOT_CONNECTED, "sub2.i1"),
        fn(ERROR, PKG_CPOS, "PortNotConnected5.arc", 24, 22, 24, 26, IN_PORT_NOT_CONNECTED, "sub2.i2"),
        fn(WARNING, PKG_CPOS, "PortNotConnected5.arc", 24, 22, 24, 26, OUT_PORT_NOT_CONNECTED, "sub2.o1"),
        fn(WARNING, PKG_CPOS, "PortNotConnected5.arc", 24, 22, 24, 26, OUT_PORT_NOT_CONNECTED, "sub2.o2")
      ),
      arg("PortNotConnectedTest6",
        mpk(PKG_CPOS, "PortNotConnected6A.arc", "PortNotConnected6B.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected6A.arc", 12, 22, 12, 25, IN_PORT_NOT_CONNECTED, "sub.i1"),
        fn(ERROR, PKG_CPOS, "PortNotConnected6A.arc", 12, 22, 12, 25, IN_PORT_NOT_CONNECTED, "sub.i2"),
        fn(WARNING, PKG_CPOS, "PortNotConnected6A.arc", 12, 22, 12, 25, OUT_PORT_NOT_CONNECTED, "sub.o1"),
        fn(WARNING, PKG_CPOS, "PortNotConnected6A.arc", 12, 22, 12, 25, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("PortNotConnectedTest7",
        mpk(PKG_CPOS, "PortNotConnected7A.arc"),
        fn(ERROR, PKG_CPOS, "PortNotConnected7A.arc", 13, 22, 13, 25, IN_PORT_NOT_CONNECTED, "sub.i1"),
        fn(ERROR, PKG_CPOS, "PortNotConnected7A.arc", 13, 22, 13, 25, IN_PORT_NOT_CONNECTED, "sub.i2"),
        fn(WARNING, PKG_CPOS, "PortNotConnected7A.arc", 13, 22, 13, 25, OUT_PORT_NOT_CONNECTED, "sub.o1"),
        fn(WARNING, PKG_CPOS, "PortNotConnected7A.arc", 13, 22, 13, 25, OUT_PORT_NOT_CONNECTED, "sub.o2")
      ),
      arg("SelfReferentialComponentWithCompositionTest",
        mpk(PKG_CPOS, "SelfReferentialComponentWithComposition.arc"),
        fn(ERROR, PKG_CPOS, "SelfReferentialComponentWithComposition.arc", 13, 39, 13, 43, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponentWithComposition", "SelfReferentialComponentWithComposition -> SelfReferentialComponentWithComposition"),
        fn(ERROR, PKG_CPOS, "SelfReferentialComponentWithComposition.arc", 13, 45, 13, 49, COMPONENT_REFERENCE_CYCLE, "SelfReferentialComponentWithComposition", "SelfReferentialComponentWithComposition -> SelfReferentialComponentWithComposition")
      ),
      arg("InputPortInInitialComputeTest",
        mpk(PKG_CB, "InputPortInInitialCompute.arc"),
        fn(ERROR, PKG_CB, "InputPortInInitialCompute.arc", 12, 13, 12, 14, IN_PORT_REF_IN_INVALID_CONTEXT, "i", NoInputPortsInInitialCompute.CONTEXT)
      ),
      arg("NonSyncInputPortInComputeTest",
        mpk(PKG_CB, "NonSyncInputPortInCompute.arc"),
        fn(ERROR, PKG_CB, "NonSyncInputPortInCompute.arc", 11, 13, 11, 14, IN_PORT_REF_IN_INVALID_CONTEXT, "i", NoNonSyncInputPortInCompute.CONTEXT)
      ),
      arg("ConstraintNoAssignmentExpressionTest",
        mpk(PKG_VARI, "ConstraintNoAssignmentExpression.arc"),
        fn(ERROR, PKG_VARI, "ConstraintNoAssignmentExpression.arc", 8, 14, 8, 22, INVALID_CONTEXT_ASSIGNMENT)
      ),
      arg("MissingSymbolsInConstraintTest1",
        mpk(PKG_VARI, "MissingSymbolsInConstraint1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInConstraint1.arc", 10, 14, 10, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("MissingSymbolsInConstraintTest2",
        mpk(PKG_VARI, "MissingSymbolsInConstraint2.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInConstraint2.arc", 10, 14, 10, 16, CANT_FIND_SYMBOL_IN_EXPRESSION, "e1"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInConstraint2.arc", 11, 14, 11, 16, CANT_FIND_SYMBOL_IN_EXPRESSION, "e2")
      ),
      arg("MissingSymbolsInConstraintTest3",
        mpk(PKG_VARI, "MissingSymbolsInConstraint3.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInConstraint3.arc", 11, 16, 11, 17, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("MissingSymbolsInConstraintWithCompositionTest1",
        mpk(PKG_VARI, "MissingSymbolsInConstraintWithComposition1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInConstraintWithComposition1.arc", 15, 16, 15, 17, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("MissingSymbolsInConstraintWithCompositionTest2",
        mpk(PKG_VARI, "MissingSymbolsInConstraintWithComposition2.arc", "MissingSymbolsInConstraint1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInConstraint1.arc", 10, 14, 10, 15, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("NameClashParamParamTest",
        mp("NameClashParamParam.arc"),
        fn(ERROR, "NameClashParamParam.arc", 7, 42, 7, 43, UNIQUE_IDENTIFIER_NAMES, "p")
      ),
      arg("NameClashParamPortTest",
        mp("NameClashParamPort.arc"),
        fn(ERROR, "NameClashParamPort.arc", 9, 15, 9, 16, UNIQUE_IDENTIFIER_NAMES, "p")
      ),
      arg("NameClashParamVarTest",
        mp("NameClashParamVar.arc"),
        fn(ERROR, "NameClashParamVar.arc", 12, 7, 12, 8, UNIQUE_IDENTIFIER_NAMES, "p")
      ),
      arg("NameClashPortPortTest1",
        mp("NameClashPortPort1.arc"),
        fn(ERROR, "NameClashPortPort1.arc", 10, 15, 10, 16, UNIQUE_IDENTIFIER_NAMES, "i")
      ),
      arg("NameClashPortPortTest2",
        mp("NameClashPortPort2.arc"),
        fn(ERROR, "NameClashPortPort2.arc", 11, 16, 11, 17, UNIQUE_IDENTIFIER_NAMES, "o")
      ),
      arg("NameClashPortVarTest",
        mp("NameClashPortVar.arc"),
        fn(ERROR, "NameClashPortVar.arc", 12, 7, 12, 8, UNIQUE_IDENTIFIER_NAMES, "i")
      ),
      arg("NameClashTypeParamTest",
        mp("NameClashTypeParam.arc"),
        fn(ERROR, "NameClashTypeParam.arc", 7, 33, 7, 34, UNIQUE_IDENTIFIER_NAMES, "T")
      ),
      arg("NameClashVarPortTest",
        mp("NameClashVarPort.arc"),
        fn(ERROR, "NameClashVarPort.arc", 11, 15, 11, 16, UNIQUE_IDENTIFIER_NAMES, "v")
      ),
      arg("NameClashVarVarTest",
        mp("NameClashVarVar.arc"),
        fn(ERROR, "NameClashVarVar.arc", 13, 7, 13, 8, UNIQUE_IDENTIFIER_NAMES, "v")
      ),
      arg("NameClashVarVarTest",
        mp("NameClashVarVar.arc"),
        fn(ERROR, "NameClashVarVar.arc", 13, 7, 13, 8, UNIQUE_IDENTIFIER_NAMES, "v")
      ),
      arg("FieldInitTypeMismatchTest",
        mpk(PKG_COMP, "FieldInitTypeMismatch.arc"),
        fn(ERROR, PKG_COMP, "FieldInitTypeMismatch.arc", 10, 11, 10, 18, FIELD_INIT_TYPE_MISMATCH, "boolean", "int")
      ),
      arg("ImportSymbolNotFound1",
        mpk(PKG_COMP, "ImportSymbolNotFound1A.arc"),
        fn(WARNING, PKG_COMP, "ImportSymbolNotFound1A.arc", 5, 1, 5, 25, IMPORTED_SYMBOL_MISSING, "unknown.symbol.C")
      ),
      arg("ForEachExpressionNotIterableTest",
        mpk(PKG_STMT, "ForEachExpressionNotIterable.arc"),
        fn(ERROR, PKG_STMT, "ForEachExpressionNotIterable.arc", 14, 20, 14, 21, FOR_EACH_EXPR_NOT_ITERABLE, "int")
      ),
      arg("ForEachTypeMismatchTest",
        mpk(PKG_STMT, "ForEachTypeMismatch.arc"),
        fn(ERROR, PKG_STMT, "ForEachTypeMismatch.arc", 19, 12, 19, 21, FOR_EACH_TYPE_MISMATCH, "java.lang.Boolean", "java.lang.Integer")
      ),
      arg("NoDuplicateVariableDeclarationsTest1",
        mpk(PKG_STMT, "NoDuplicateVariableDeclarations1.arc"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations1.arc", 12, 11, 12, 16, DUPLICATE_VAR_IN_SCOPE, "i"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations1.arc", 13, 11, 13, 16, DUPLICATE_VAR_IN_SCOPE, "i")
      ),
      arg("NoDuplicateVariableDeclarationsTest2",
        mpk(PKG_STMT, "NoDuplicateVariableDeclarations2.arc"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations2.arc", 12, 13, 12, 18, DUPLICATE_VAR_IN_SCOPE, "i"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations2.arc", 13, 13, 13, 18, DUPLICATE_VAR_IN_SCOPE, "i")
      ),
      arg("NoDuplicateVariableDeclarationsTest3",
        mpk(PKG_STMT, "NoDuplicateVariableDeclarations3.arc"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations3.arc", 12, 13, 12, 18, DUPLICATE_VAR_IN_SCOPE, "i"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations3.arc", 13, 13, 13, 18, DUPLICATE_VAR_IN_SCOPE, "i")
      ),
      arg("NoDuplicateVariableDeclarationsTest4",
        mpk(PKG_STMT, "NoDuplicateVariableDeclarations4.arc"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations4.arc", 10, 9, 10, 14, DUPLICATE_VAR_IN_SCOPE, "i"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations4.arc", 11, 9, 11, 14, DUPLICATE_VAR_IN_SCOPE, "i")
      ),
      arg("NoDuplicateVariableDeclarationsTest5",
        mpk(PKG_STMT, "NoDuplicateVariableDeclarations5.arc"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations5.arc", 10, 9, 10, 14, DUPLICATE_VAR_IN_SCOPE, "i"),
        fn(ERROR, PKG_STMT, "NoDuplicateVariableDeclarations5.arc", 11, 9, 11, 14, DUPLICATE_VAR_IN_SCOPE, "i")
      ),
      arg("StateInModeAutomatonTest1",
        mpk(PKG_MODES, "StateInModeAutomaton1.arc"),
        fn(ERROR, PKG_MODES, "StateInModeAutomaton1.arc", 9, 3, 12, 4, MODE_AUTOMATON_CONTAINS_STATE)
      ),
      arg("StateInModeAutomatonTest2",
        mpk(PKG_MODES, "StateInModeAutomaton2.arc"),
        fn(ERROR, PKG_MODES, "StateInModeAutomaton2.arc", 9, 3, 13, 4, MODE_AUTOMATON_CONTAINS_STATE)
      ),
      arg("StateInModeAutomatonTest3",
        mpk(PKG_MODES, "StateInModeAutomaton3.arc"),
        fn(ERROR, PKG_MODES, "StateInModeAutomaton3.arc", 11, 5, 14, 6, MODE_AUTOMATON_CONTAINS_STATE)
      ),
      arg("PortInModeTest1",
        mpk(PKG_MODES, "PortInMode1.arc"),
        fn(ERROR, PKG_MODES, "PortInMode1.arc",11, 19, 11, 22, MODE_CONTAINS_PORT_DEFINITION, "pIn")
      ),
      arg("PortInModeTest2",
        mpk(PKG_MODES, "PortInMode2.arc"),
        fn(ERROR, PKG_MODES, "PortInMode2.arc", 11, 19, 11, 22, MODE_CONTAINS_PORT_DEFINITION, "pIn"),
        fn(ERROR, PKG_MODES, "PortInMode2.arc", 14, 23, 14, 27, MODE_CONTAINS_PORT_DEFINITION, "pOut")
      ),
      arg("PrimitiveTypeParameterBoundsTest",
        mpk(PKG_COMP, "PrimitiveTypeParameterBounds.arc"),
        fn(ERROR, PKG_COMP, "PrimitiveTypeParameterBounds.arc", 13, 26, 13, 39, TYPE_ARG_IGNORES_UPPER_BOUND, "boolean", "int"),
        fn(ERROR, PKG_COMP, "PrimitiveTypeParameterBounds.arc", 16, 26, 16, 39, TYPE_ARG_IGNORES_UPPER_BOUND, "Boolean", "Integer")
      )
    );
  }

  protected static Stream<Arguments> invalidModelAndError4VariabilityProvider() {
    return Stream.of(
      arg("ConstraintSmtConvertibleTest",
        mpk(PKG_VARI, "ConstraintSmtConvertible.arc"),
        fn(WARNING, PKG_VARI, "ConstraintSmtConvertible.arc", 8, 14, 8, 29, EXPRESSION_NOT_SMT_CONVERTIBLE, "obj.isPresent()", "method calls are not supported in this context")
      ),
      arg("FieldReferenceInStaticContextVariabilityTest",
        mpk(PKG_VARI, "FieldReferenceInStaticContext.arc", "ParameterizedSuperComponent.arc"),
        fn(ERROR, PKG_VARI, "FieldReferenceInStaticContext.arc", 10, 14, 10, 15, FIELD_REF_IN_STATIC_CONTEXT, "x"),
        fn(ERROR, PKG_VARI, "FieldReferenceInStaticContext.arc", 11, 9, 11, 10, FIELD_REF_IN_STATIC_CONTEXT, "x")
      ),
      arg("MissingSymbolsInVarIfTest1",
        mpk(PKG_VARI, "MissingSymbolsInVarIf1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf1.arc", 10, 9, 10, 10, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("MissingSymbolsInVarIfTest2",
        mpk(PKG_VARI, "MissingSymbolsInVarIf2.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf2.arc", 10, 30, 10, 31, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("MissingSymbolsInVarIfTest3",
        mpk(PKG_VARI, "MissingSymbolsInVarIf3.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf3.arc", 12, 11, 12, 12, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("MissingSymbolsInVarIfTest4",
        mpk(PKG_VARI, "MissingSymbolsInVarIf4.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf4.arc", 10, 9, 12, 11, CANT_FIND_SYMBOL_IN_EXPRESSION, "e1"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf4.arc", 11, 11, 11, 13, CANT_FIND_SYMBOL_IN_EXPRESSION, "e2"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf4.arc", 12, 16, 12, 18, CANT_FIND_SYMBOL_IN_EXPRESSION, "e3")
      ),
      arg("MissingSymbolsInVarIfWithCompositionTest1",
        mpk(PKG_VARI, "MissingSymbolsInVarIfWithComposition1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIfWithComposition1.arc", 16, 11, 16, 12, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("MissingSymbolsInVarIfWithCompositionTest2",
        mpk(PKG_VARI, "MissingSymbolsInVarIfWithComposition2.arc", "MissingSymbolsInVarIf1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf1.arc", 10, 9, 10, 10, CANT_FIND_SYMBOL_IN_EXPRESSION, "e")
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest1",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability1.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability1.arc", 13, 15, 13, 45, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest2",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability2.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability2.arc", 13, 15, 13, 26, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest3",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability3.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability3.arc", 13, 15, 13, 26, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest4",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability4.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability4.arc", 13, 17, 13, 47, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest5",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability5.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability5.arc", 12, 15, 12, 45, MULTIPLE_BEHAVIOR),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability5.arc", 13, 15, 13, 45, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest6",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability6.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability6.arc", 14, 5, 14, 35, MULTIPLE_BEHAVIOR),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability6.arc", 18, 5, 18, 35, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest7",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability7.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability7.arc", 14, 5, 14, 35, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest8",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability8.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability8.arc", 14, 5, 14, 35, MULTIPLE_BEHAVIOR),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability8.arc", 19, 5, 19, 35, MULTIPLE_BEHAVIOR)
      ),
      arg("MoreThanOneBehaviorWithVariabilityTest9",
        mpk(PKG_VARI, "MoreThanOneBehaviorWithVariability9.arc"),
        fn(ERROR, PKG_VARI, "MoreThanOneBehaviorWithVariability9.arc", 18, 5, 18, 35, MULTIPLE_BEHAVIOR)
      ),
      arg("NamesCapitalizationWithVariabilityTest",
        mpk(PKG_VARI, "NamesCapitalizationWithVariability.arc"),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 9, 11, 9, 12, FEATURE_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 12, 17, 12, 18, PORT_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 13, 9, 13, 14, FIELD_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 14, 5, 16, 6, COMPONENT_LOWER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 14, 25, 14, 30, PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 14, 22, 14, 23, TYPE_PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 15, 19, 15, 20, PORT_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 17, 17, 17, 23, SUBCOMPONENT_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 20, 17, 20, 18, PORT_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 21, 9, 21, 14, FIELD_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 22, 5, 24, 6, COMPONENT_LOWER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 22, 25, 22, 30, PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 22, 22, 22, 23, TYPE_PARAMETER_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 23, 19, 23, 20, PORT_UPPER_CASE),
        fn(WARNING, PKG_VARI, "NamesCapitalizationWithVariability.arc", 25, 17, 25, 23, SUBCOMPONENT_UPPER_CASE)
      )
    );
  }

  protected static Stream<Arguments> invalidModelAndErrorNoVariabilityProvider() {
    return Stream.of(
      arg("UnsupportedModelElementVarifTest1",
        mpk(PKG_VARI, "MissingSymbolsInVarIf1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf1.arc", 10, 3, 10, 15, UNSUPPORTED_MODEL_ELEMENT, "varif")
      ),
      arg("UnsupportedModelElementVarifTest2",
        mpk(PKG_VARI, "MissingSymbolsInVarIf2.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf2.arc", 10, 3, 10, 18, UNSUPPORTED_MODEL_ELEMENT, "varif"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf2.arc", 10, 24, 10, 36, UNSUPPORTED_MODEL_ELEMENT, "varif")
      ),
      arg("UnsupportedModelElementVarifTest3",
        mpk(PKG_VARI, "MissingSymbolsInVarIf3.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf3.arc", 12, 5, 12, 17, UNSUPPORTED_MODEL_ELEMENT, "varif")
      ),
      arg("UnsupportedModelElementVarifTest4",
        mpk(PKG_VARI, "MissingSymbolsInVarIf4.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf4.arc", 10, 3, 12, 4, UNSUPPORTED_MODEL_ELEMENT, "varif"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf4.arc", 11, 5, 11, 18, UNSUPPORTED_MODEL_ELEMENT, "varif"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf4.arc", 12, 10, 12, 23, UNSUPPORTED_MODEL_ELEMENT, "varif")
      ),
      arg("UnsupportedModelElementWithCompositionTest1",
        mpk(PKG_VARI, "MissingSymbolsInVarIfWithComposition1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIfWithComposition1.arc", 16, 5, 16, 17, UNSUPPORTED_MODEL_ELEMENT, "varif"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIfWithComposition1.arc", 21, 3, 21, 15, UNSUPPORTED_MODEL_ELEMENT, "varif")
      ),
      arg("UnsupportedModelElementWithCompositionTest2",
        mpk(PKG_VARI, "MissingSymbolsInVarIfWithComposition2.arc", "MissingSymbolsInVarIf1.arc"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIfWithComposition2.arc", 17, 3, 17, 15, UNSUPPORTED_MODEL_ELEMENT, "varif"),
        fn(ERROR, PKG_VARI, "MissingSymbolsInVarIf1.arc", 10, 3, 10, 15, UNSUPPORTED_MODEL_ELEMENT, "varif")
      ),
      arg("FieldReferenceInStaticContextNoVariabilityTest",
        mpk(PKG_VARI, "FieldReferenceInStaticContext.arc", "ParameterizedSuperComponent.arc"),
        fn(ERROR, PKG_VARI, "FieldReferenceInStaticContext.arc", 4, 49, 4, 50, FIELD_REF_IN_STATIC_CONTEXT, "x"),
        fn(ERROR, PKG_VARI, "FieldReferenceInStaticContext.arc", 4, 88, 4, 89, FIELD_REF_IN_STATIC_CONTEXT, "x"),
        fn(ERROR, PKG_VARI, "FieldReferenceInStaticContext.arc", 8, 13, 8, 14, FIELD_REF_IN_STATIC_CONTEXT, "x"),
        fn(ERROR, PKG_VARI, "FieldReferenceInStaticContext.arc", 10, 14, 10, 15, FIELD_REF_IN_STATIC_CONTEXT, "x"),
        fn(ERROR, PKG_VARI, "FieldReferenceInStaticContext.arc", 11, 3, 11, 19, UNSUPPORTED_MODEL_ELEMENT, "varif")
      ),
      arg("InitWithoutComputeTest1",
        mpk(PKG_COMP, "InitWithoutCompute1.arc"),
        fn(ERROR, PKG_COMP, "InitWithoutCompute1.arc", 10, 3, 10, 22, INIT_BLOCK_WITHOUT_COMPUTE)
      ),
      arg("InitWithoutComputeTest2",
        mpk(PKG_COMP, "InitWithoutCompute2.arc"),
        fn(ERROR, PKG_COMP, "InitWithoutCompute2.arc", 11, 5, 11, 24, INIT_BLOCK_WITHOUT_COMPUTE)
      ),
      arg("InitWithoutComputeTest3",
        mpk(PKG_COMP, "InitWithoutCompute3.arc"),
        fn(ERROR, PKG_COMP, "InitWithoutCompute3.arc", 8, 3, 8, 22, INIT_BLOCK_WITHOUT_COMPUTE)
      ),
      arg("MaxOneInitTest1",
        mpk(PKG_COMP, "MaxOneInit1.arc"),
        fn(ERROR, PKG_COMP, "MaxOneInit1.arc", 12, 3, 12, 11, MULTIPLE_INIT)
      ),
      arg("MaxOneInitTest2",
        mpk(PKG_COMP, "MaxOneInit2.arc"),
        fn(ERROR, PKG_COMP, "MaxOneInit2.arc", 16, 5, 16, 13, MULTIPLE_INIT)
      ),
      arg("MaxOneInitTest3",
        mpk(PKG_COMP, "MaxOneInit3.arc"),
        fn(ERROR, PKG_COMP, "MaxOneInit3.arc", 12, 3, 12, 11, MULTIPLE_INIT)
      ),
      arg("SwitchCaseIncompatibleTest1",
        mpk(PKG_STMT, "SwitchCaseIncompatible1.arc"),
        fn(ERROR, PKG_STMT, "SwitchCaseIncompatible1.arc", 10, 14, 10, 15, SWITCH_CASE_INCOMPATIBLE, "int", "boolean")
      ),
      arg("SwitchCaseIncompatibleTest2",
        mpk(PKG_STMT, "SwitchCaseIncompatible2.arc"),
        fn(ERROR, PKG_STMT, "SwitchCaseIncompatible2.arc", 14, 14, 14, 15, SWITCH_CASE_INCOMPATIBLE, "int", "OnOff")
      ),
      arg("SwitchCaseIncompatibleTest3",
        mpk(PKG_STMT, "SwitchCaseIncompatible3.arc"),
        fn(ERROR, PKG_STMT, "SwitchCaseIncompatible3.arc", 14, 14, 14, 18, SWITCH_CASE_INCOMPATIBLE, "long", "boolean")
      ),
      arg("SwitchCaseIncompatibleTest4",
        mpk(PKG_STMT, "SwitchCaseIncompatible4.arc"),
        fn(ERROR, PKG_STMT, "SwitchCaseIncompatible4.arc", 15, 14, 15, 18, SWITCH_CASE_INCOMPATIBLE, "double", "java.lang.Long"),
        fn(ERROR, PKG_STMT, "SwitchCaseIncompatible4.arc", 19, 14, 19, 18, SWITCH_CASE_INCOMPATIBLE, "double", "long")
      ),
      arg("SwitchCaseIncompatibleStringTest5",
        mpk(PKG_STMT, "SwitchCaseIncompatible5.arc"),
        fn(ERROR, PKG_STMT, "SwitchCaseIncompatible5.arc", 14, 14, 14, 17, SWITCH_CASE_INCOMPATIBLE, "int", "java.lang.String")
      ),
      arg("SwitchCaseIncompatibleStringTest6",
        mpk(PKG_STMT, "SwitchCaseIncompatible6.arc"),
        fn(ERROR, PKG_STMT, "SwitchCaseIncompatible6.arc", 14, 14, 14, 19, SWITCH_CASE_INCOMPATIBLE, "R\"hello\"", "int")
      )
    );
  }

  private static String mp(@NotNull String... models) {
    Preconditions.checkNotNull(models);
    String[] parts = new String[models.length];
    for (int i = 0; i < models.length; i++) {
      parts[i] = Paths.get(TEST_RESOURCE, TEST_DIR, models[i]).toString();
    }
    return path(parts);
  }

  private static String mpk(@NotNull String pkg, @NotNull String... models) {
    Preconditions.checkNotNull(models);
    String[] parts = new String[models.length];
    for (int i = 0; i < models.length; i++) {
      parts[i] = Paths.get(TEST_RESOURCE, TEST_DIR, pkg, models[i]).toString();
    }
    return path(parts);
  }

  private static String path(@NotNull String... parts) {
    return String.join(File.pathSeparator, parts);
  }

  private static Finding fn(Finding.Type type,
                            String model,
                            int lineStart,
                            int columnStart,
                            int lineEnd,
                            int columnEnd,
                            Error error,
                            String... args) {
    return new Finding(type, String.format(error.toString(), (Object[]) args),
      sourcePos(model, lineStart, columnStart),
      sourcePos(model, lineEnd, columnEnd)
    );
  }

  private static Finding fn(Finding.Type type,
                            String pkg,
                            String model,
                            int lineStart,
                            int columnStart,
                            int lineEnd,
                            int columnEnd,
                            Error error,
                            String... args) {
    return new Finding(type, String.format(error.toString(), (Object[]) args),
      sourcePos(pkg, model, lineStart, columnStart),
      sourcePos(pkg, model, lineEnd, columnEnd)
    );
  }

  private static SourcePosition sourcePos(@NotNull String model, int line, int column) {
    // columns in monticore are parsed with a -1 offset, workaround until fixed
    int columnFix = column - 1;
    return new SourcePosition(line, columnFix, Paths.get(TEST_RESOURCE, TEST_DIR, model).toAbsolutePath().toString());
  }

  private static SourcePosition sourcePos(@NotNull String pkg, @NotNull String model, int line, int column) {
    // columns in monticore are parsed with a -1 offset, workaround until fixed
    int columnFix = column - 1;
    return new SourcePosition(line, columnFix, Paths.get(TEST_RESOURCE, TEST_DIR, pkg, model).toAbsolutePath().toString());
  }
}
