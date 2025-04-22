/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.RefinementPortsMatch;
import com.google.common.base.Preconditions;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.MontiArcSymbols2Json;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RefinementPortsMatch}
 */
class RefinementPortsMatchTest extends MontiArcTestBase {

  private static final String TEST_RESOURCE = "test/resources";
  private static final String PACKAGE = "clitool/cocos";

  @BeforeEach
  protected void initSymbols() {
    loadOOTypes();
    setUpComponents();
  }

  protected void loadOOTypes() {
    Path ooTypesPath = Path.of(TEST_RESOURCE, PACKAGE, "OOTypeWithSuperType.sym");

    MontiArcSymbols2Json symbolDeser = new MontiArcSymbols2Json();
    MontiArcMill.globalScope().addSubScope(symbolDeser.load(ooTypesPath.toString()));
  }

  protected void setUpComponents() {
    compile(
      "component Restrictive {" +
        "  port      in  OOType i;" +
        "  port sync out SubType o;" +
        "}");
    compile(
      "component Liberal {" +
        "  port sync in  SubType i;" +
        "  port      out OOType o;" +
        "}");
    compile(
      "component RestrictiveMorePorts {" +
        "  port      in  OOType i1;" +
        "  port      in  OOType i2;" +
        "  port sync out SubType o1;" +
        "  port sync out SubType o2;" +
        "}");
    compile("component RestrictedChild extends Restrictive { }");
    // Also loading models with errors for robustness checks
    compile(
      "component DuplicatePortAllDifferent {" +
        "  port      in int a;" +
        "  port sync out long a;" +
        "}");
    compile(
      "component DuplicatePortSameDirection {" +
        "  port      in int a;" +
        "  port sync in long a;" +
        "}");
    compile(
      "component DuplicateSameDirectionTiming {" +
        "  port in int a;" +
        "  port in long a;" +
        "}");
    compile(
      "component DuplicateSameTimingTypes {" +
        "  port in OOType a;" +
        "  port out OOType a;" +
        "}");
    compile(
      "component  DuplicateEverythingSame {" +
        "  port in OOType a;" +
        "  port in OOType a;" +
        "}"
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // No refinement
    "component Comp1 {" +
      "  port      in OOType i;" +
      "  port sync out SubType o;" +
      "}",
    // No interface change
    "component Comp2 refines Liberal {" +
      "  port sync in SubType i;" +
      "  port      out OOType o;" +
      "}",
    // No interface change
    "component Comp3 refines Restrictive {" +
      "  port      in OOType i;" +
      "  port sync out SubType o;" +
      "}",
    // Changing the timing compatibly
    "component Comp4 refines Liberal {" +
      "  port      in SubType i;" +
      "  port sync out OOType o;" +
      "}",
    // Changing the types compatibly
    "component Comp5 refines Liberal {" +
      "  port sync in OOType i;" +
      "  port      out SubType o;" +
      "}",
    // Refining the liberal interface with time and type changes,
    // maintaining the restrictive interface
    "component Comp6 refines Liberal, Restrictive {" +
      "  port      in OOType i;" +
      "  port sync out SubType o;" +
      "}",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementPortsMatch());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementPortsMatch());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // Other in port name = 1 name removed, 1 name added
      arg("component Comp1 refines Restrictive { " +
          "  port      in OOType otherInPortName;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_PORT_NAME_MISMATCH,
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Other out port name = 1 name removed, 1 name added
      arg("component Comp2 refines Restrictive { " +
          "  port      in OOType i;" +
          "  port sync out SubType otherOutPortName;" +
          "}",
        ArcError.REFINEMENT_PORT_NAME_MISMATCH,
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // One port less (removed in port)
      arg("component Comp3 refines Restrictive { " +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // One port less (removed out port)
      arg("component Comp4 refines Restrictive { " +
          "  port in OOType i;" +
          "}",
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // One port added
      arg("component Comp5 refines Restrictive { " +
          "  port      in OOType i;" +
          "  port sync out SubType o;" +
          "  port      in OOType newInPort;" +
          "}",
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Too restrictive refined incoming port timing
      arg("component Comp6 refines Restrictive { " +
          "  port sync in OOType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // To weak refined outgoing port timing
      arg("component Comp7 refines Restrictive { " +
          "  port in OOType i;" +
          "  port out SubType o;" +
          "}",
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT),
      // Problematic timing changes on both ports
      arg("component Comp8 refines Restrictive { " +
          "  port sync in OOType i;" +
          "  port      out SubType o;" +
          "}",
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT),
      // Input port type is a sub type compared with before
      arg("component Comp9 refines Restrictive { " +
          "  port      in SubType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Output port type is a super type compared with before
      arg("component Comp10 refines Restrictive { " +
          "  port      in OOType i;" +
          "  port sync out OOType o;" +
          "}",
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH),
      // Input port type is a sub type compared with before and output port type is a super type
      arg("component Comp11 refines Restrictive { " +
          "  port      in SubType i;" +
          "  port sync out OOType o;" +
          "}",
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH),
      // Incoming port becomes outgoing port
      arg("component Comp12 refines Restrictive {" +
          "  port      out OOType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Outgoing port becomes incoming port
      arg("component Comp13 refines Restrictive {" +
        "  port      in OOType i;" +
        "  port sync in SubType o;" +
        "}",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Both ports change directions
      arg("component Comp14 refines Restrictive {" +
        "  port      out OOType i;" +
        "  port sync in SubType o;" +
        "}",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED,
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // 2 timing and subtyping changes
      arg("component Comp15 refines RestrictiveMorePorts {" +
        "  port sync in SubType i1;" +
        "  port sync in SubType i2;" +
        "  port      out OOType o1;" +
        "  port      out OOType o2;" +
        "}",
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT,
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT),
      // Changed port direction -> no sub typing error
      // (we do not know the direction of the sub type relationship)
      arg("component Comp16 refines Restrictive {" +
        "  port out SubType i;" +
        "  port sync out SubType o;" +
        "}",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Changed port direction -> no timing mismatch error
      // (we do not know which timing is allowed to be weaker)
      arg("component Comp17 refines Restrictive {" +
        "  port in OOType i;" +
        "  port in OOType o;" +
        "}",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Error for the second abstraction
      arg("component Comp18 refines Liberal, Restrictive {" +
        "  port in OOType i;" +
        "  port out OOType o;" +
        "}",
        ArcError.REFINEMENT_TIMING_MISMATCH_OUT,
        ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH)
    );
  }

  @ParameterizedTest
  @MethodSource("inheritanceModels")
  void shouldReportWithInheritedPorts(@NotNull String superModel,
                                      @NotNull String subModel,
                                      @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(superModel);
    Preconditions.checkNotNull(subModel);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    compile(superModel);
    ASTMACompilationUnit subAst = compile(subModel);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementPortsMatch());

    // When
    checker.checkAll(subAst);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> inheritanceModels() {
    return Stream.of(
      // Refining a component that has its port inherited
      arg("component Super1Ignored { }",
        "component Comp1 refines RestrictedChild {" +
        "  port      in OOType i;" +
        "  port sync out SubType o;" +
        "}"),
      // Refining a component validly with all ports inherited
      arg("component Super2 {" +
        "  port      in  OOType i;" +
        "  port sync out SubType o;" +
        "}",
        "component Comp2 extends Super2 refines Restrictive { }"),
      // Super comp misses one of the ports
      arg("component Super3 {" +
        "  port sync out SubType o;" +
        "}",
        "component Comp3 extends Super3 refines Restrictive { }",
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Super comp adds a port
      arg("component Super4 {" +
        "  port      in  OOType i;" +
        "  port sync out SubType o;" +
        "  port      in  OOType newInPort;" +
        "}",
        "component Comp4 extends Super4 refines Restrictive { }",
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Super comp changes direction of a port
      arg("component Super5 {" +
        "  port      out  OOType i;" +
        "  port sync out SubType o;" +
        "}",
        "component Comp5 extends Super5 refines Restrictive { }",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Super comp changes timing of a port
      arg("component Super6 {" +
        "  port sync in  OOType i;" +
        "  port sync out SubType o;" +
        "}",
        "component Comp6 extends Super6 refines Restrictive { }",
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // Super comp changes type of a port
      arg("component Super7 {" +
        "  port      in  SubType i;" +
        "  port sync out SubType o;" +
        "}",
        "component Comp7 extends Super7 refines Restrictive { }",
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH)
    );
  }

  @ParameterizedTest
  @MethodSource("robustnessTestModelProvider")
  void shouldReportErrorRobustly(@NotNull String model, @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new RefinementPortsMatch());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> robustnessTestModelProvider() {
    return Stream.of(
      // Ambiguous port direction in abstraction, so type / timing is ignored
      // But the presence of a similarly named port is checked
      arg("component Comp1 refines DuplicatePortAllDifferent {" +
        "  port in OOType a;" +
        "}"),
      // Missing port
      arg("component Comp2 refines DuplicatePortAllDifferent { }",
        ArcError.REFINEMENT_PORT_NAME_MISMATCH),
      // Ambiguous port timing -> own timing is not checked
      arg("component Comp3 refines DuplicatePortSameDirection {" +
        "  port in long a;" +
        "}"),
      // Ambiguous port timing -> own timing is not checked
      // But direction is unambiguous -> is checked
      arg("component Comp4 refines DuplicatePortSameDirection {" +
        "  port out long a;" +
        "}", ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Ambiguous port timing -> own timing is not checked
      // But direction is unambiguous -> types are checked
      arg("component Comp5 refines DuplicatePortSameDirection {" +
        "  port in int a;" +
        "}", ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Unambiguous abstraction port direction -> Timing and types are checked
      // Types are different -> Our type must be a subtype of both
      arg("component Comp6 refines DuplicateSameDirectionTiming {" +
        "  port in long a;" +
        "}"),
      // Unambiguous direction -> Timing and types are checked
      // Should find timing change
      arg("component Comp7 refines DuplicateSameDirectionTiming {" +
        "  port sync in long a;" +
        "}", ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // Unambiguous direction -> Timing and types are checked
      // Should find port type only extending one abstraction port type
      arg("component Comp8 refines DuplicateSameDirectionTiming {" +
        "  port in int a;" +
        "}", ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Ambiguous directions in abstraction's ports
      // -> Coco should not check timing nor port type
      arg("component Comp9 refines DuplicateSameTimingTypes {" +
        "  port in OOType a;" +
        "}"),
      // Ambiguous directions in abstraction's ports
      // -> Coco should not find timing change
      arg("component Comp10 refines DuplicateSameTimingTypes {" +
        "  port sync in OOType a;" +
        "}"),
      // Ambiguous directions in abstraction's ports
      // -> Coco should not find type change
      arg("component Comp11 refines DuplicateSameTimingTypes {" +
        "  port in SubType a;" +
        "}"),
      // Abstraction has a duplicate port, but the direction, type, and timing
      // is unambiguous -> should still find nothing, due to no error
      arg("component Comp12 refines DuplicateEverythingSame {" +
        "  port in OOType a;" +
        "}"),
      // Abstraction has a duplicate port, but the direction, type, and timing
      // is unambiguous -> should identify changed direction
      arg("component Comp13 refines DuplicateEverythingSame {" +
        "  port out OOType a;" +
        "}", ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Abstraction has a duplicate port, but the direction, type, and timing
      // is unambiguous -> should identify changed timing and type
      arg("component Comp14 refines DuplicateEverythingSame {" +
        "  port sync in SubType a;" +
        "}",
        // Two type errors, as SubType is not conform to neither of the
        // refinements a-ports
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // Interface is conform though -> No error
      arg("component Comp15 refines Restrictive {" +
        "  port      in  OOType i;" +
        "  port      in  OOType i;" +
        "  port sync out SubType o;" +
        "}"),
      // Abstraction is correct, but refinement has duplicate
      // Timing is changed once -> One error
      arg("component Comp16 refines Restrictive {" +
        "  port sync in  OOType i;" +
        "  port      in  OOType i;" +
        "  port sync out SubType o;" +
        "}",
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // Abstraction is correct, but refinement has duplicate
      // Timing is changed twice -> Two errors
      arg("component Comp17 refines Restrictive {" +
        "  port sync in  OOType i;" +
        "  port sync in  OOType i;" +
        "  port sync out SubType o;" +
        "}",
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_TIMING_MISMATCH_IN),
      // Abstraction is correct, but refinement has duplicate
      // Direction is changed once -> One error
      arg("component Comp18 refines Restrictive {" +
        "  port      out OOType i;" +
        "  port      in  OOType i;" +
        "  port sync out SubType o;" +
        "}",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Abstraction is correct, but refinement has duplicate
      // Direction is changed twice -> Two errors
      arg("component Comp19 refines Restrictive {" +
          "  port      out OOType i;" +
          "  port      out OOType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED,
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED),
      // Abstraction is correct, but refinement has duplicate
      // Type is changed once -> One error
      arg("component Comp20 refines Restrictive {" +
          "  port      in SubType i;" +
          "  port      in OOType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // Type is changed twice -> Two errors
      arg("component Comp21 refines Restrictive {" +
          "  port      in SubType i;" +
          "  port      in SubType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // Timing and type are changed separately
      arg("component Comp22 refines Restrictive {" +
          "  port sync in OOType i;" +
          "  port      in SubType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // Timing and type are changed simultaneously
      arg("component Comp23 refines Restrictive {" +
          "  port sync in SubType i;" +
          "  port      in OOType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_TIMING_MISMATCH_IN,
        ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH),
      // Abstraction is correct, but refinement has duplicate
      // One changes timing, the other type and direction (thus, type is not checked)
      arg("component Comp18 refines Restrictive {" +
          "  port      out SubType i;" +
          "  port sync in OOType i;" +
          "  port sync out SubType o;" +
          "}",
        ArcError.REFINEMENT_PORT_DIRECTION_CHANGED,
        ArcError.REFINEMENT_TIMING_MISMATCH_IN)
    );
  }
}
