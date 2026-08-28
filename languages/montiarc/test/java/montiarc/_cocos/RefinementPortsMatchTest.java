/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.RefinementPortsMatch;
import com.google.common.base.Preconditions;
import de.monticore.io.paths.MCPath;
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

import static montiarc.util.ArcError.REFINEMENT_IN_PORT_TYPE_MISMATCH;
import static montiarc.util.ArcError.REFINEMENT_OUT_PORT_TYPE_MISMATCH;
import static montiarc.util.ArcError.REFINEMENT_PORT_DIRECTION_CHANGED;
import static montiarc.util.ArcError.REFINEMENT_PORT_NAME_MISMATCH;
import static montiarc.util.ArcError.REFINEMENT_TIMING_MISMATCH_IN;
import static montiarc.util.ArcError.REFINEMENT_TIMING_MISMATCH_OUT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link RefinementPortsMatch}.
 */
class RefinementPortsMatchTest extends MontiArcTestBase {

  private static final String TEST_DIR = "cocos/RefinementPortsMatch";

  @BeforeEach
  protected void setUp() {
    MontiArcMill.globalScope().setSymbolPath(new MCPath(Paths.get(TEST_RESOURCE, TEST_DIR)));
  }

  @ParameterizedTest
  @MethodSource("validModels")
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

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // no refinement
      arg("""
        component ValidComp1 {
          port      in OOType i;
          port sync out SubType o;
        }
        """),
      // no interface change
      arg("""
        component ValidComp2 refines Liberal {
          port sync in SubType i;
          port      out OOType o;
        }
        """),
      // no interface change
      arg("""
        component ValidComp3 refines Restrictive {
          port      in OOType i;
          port sync out SubType o;
        }
        """),
      // changing the timing compatibly
      arg("""
        component ValidComp4 refines Liberal {
          port      in SubType i;
          port sync out OOType o;
        }
        """),
      // changing the types compatibly
      arg("""
        component ValidComp5 refines Liberal {
          port sync in OOType i;
          port      out SubType o;
        }
        """),
      // refining the liberal interface with timing and type changes, maintaining the restrictive interface
      arg("""
        component ValidComp6 refines Liberal, Restrictive {
          port      in OOType i;
          port sync out SubType o;
        }
        """),
      // ambiguous port direction in abstraction, so type/timing is ignored, but presence of a similarly named port is checked
      arg("""
        component ValidComp7 refines DuplicatePortAllDifferent {
          port in OOType a;
        }
        """),
      // ambiguous port timing in abstraction -> own timing is not checked
      arg("""
        component ValidComp8 refines DuplicatePortSameDirection {
          port in long a;
        }
        """),
      // unambiguous abstraction port direction -> timing and types are checked; type must be a subtype of both abstraction ports
      arg("""
        component ValidComp9 refines DuplicateSameDirectionTiming {
          port in long a;
        }
        """),
      // ambiguous directions in abstraction's ports -> CoCo should not check timing nor port type
      arg("""
        component ValidComp10 refines DuplicateSameTimingTypes {
          port in OOType a;
        }
        """),
      // ambiguous directions in abstraction's ports -> CoCo should not find a timing change
      arg("""
        component ValidComp11 refines DuplicateSameTimingTypes {
          port sync in OOType a;
        }
        """),
      // ambiguous directions in abstraction's ports -> CoCo should not find a type change
      arg("""
        component ValidComp12 refines DuplicateSameTimingTypes {
          port in SubType a;
        }
        """),
      // abstraction has a duplicate port, but direction/type/timing are unambiguous and conform -> no error
      arg("""
        component ValidComp13 refines DuplicateEverythingSame {
          port in OOType a;
        }
        """),
      // abstraction is correct, refinement has a duplicate port, interface is still conform -> no error
      arg("""
        component ValidComp14 refines Restrictive {
          port      in  OOType i;
          port      in  OOType i;
          port sync out SubType o;
        }
        """),
      // refining a component whose ports are inherited (rather than declared directly)
      arg("""
        component ValidComp15 refines RestrictedChild {
          port      in OOType i;
          port sync out SubType o;
        }
        """),
      // refining validly with all ports inherited from a distinct supertype
      arg("component ValidComp16 extends Super2 refines Restrictive { }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // other in port name = 1 name removed, 1 name added
      arg("""
        component InvalidComp1 refines Restrictive {
          port      in OOType otherInPortName;
          port sync out SubType o;
        }
        """,
        REFINEMENT_PORT_NAME_MISMATCH,
        REFINEMENT_PORT_NAME_MISMATCH
      ),
      // other out port name = 1 name removed, 1 name added
      arg("""
        component InvalidComp2 refines Restrictive {
          port      in OOType i;
          port sync out SubType otherOutPortName;
        }
        """,
        REFINEMENT_PORT_NAME_MISMATCH,
        REFINEMENT_PORT_NAME_MISMATCH
      ),
      // one port less (removed in port)
      arg("""
        component InvalidComp3 refines Restrictive {
          port sync out SubType o;
        }
        """,
        REFINEMENT_PORT_NAME_MISMATCH
      ),
      // one port less (removed out port)
      arg("""
        component InvalidComp4 refines Restrictive {
          port in OOType i;
        }
        """,
        REFINEMENT_PORT_NAME_MISMATCH
      ),
      // one port added
      arg("""
        component InvalidComp5 refines Restrictive {
          port      in OOType i;
          port sync out SubType o;
          port      in OOType newInPort;
        }
        """,
        REFINEMENT_PORT_NAME_MISMATCH
      ),
      // too restrictive refined incoming port timing
      arg("""
        component InvalidComp6 refines Restrictive {
          port sync in OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_IN
      ),
      // too weak refined outgoing port timing
      arg("""
        component InvalidComp7 refines Restrictive {
          port in OOType i;
          port out SubType o;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_OUT
      ),
      // problematic timing changes on both ports
      arg("""
        component InvalidComp8 refines Restrictive {
          port sync in OOType i;
          port      out SubType o;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_IN,
        REFINEMENT_TIMING_MISMATCH_OUT
      ),
      // input port type is a subtype compared with before
      arg("""
        component InvalidComp9 refines Restrictive {
          port      in SubType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      ),
      // output port type is a supertype compared with before
      arg("""
        component InvalidComp10 refines Restrictive {
          port      in OOType i;
          port sync out OOType o;
        }
        """,
        REFINEMENT_OUT_PORT_TYPE_MISMATCH
      ),
      // input port type is a subtype and output port type is a supertype
      arg("""
        component InvalidComp11 refines Restrictive {
          port      in SubType i;
          port sync out OOType o;
        }
        """,
        REFINEMENT_IN_PORT_TYPE_MISMATCH,
        REFINEMENT_OUT_PORT_TYPE_MISMATCH
      ),
      // incoming port becomes outgoing port
      arg("""
        component InvalidComp12 refines Restrictive {
          port      out OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // outgoing port becomes incoming port
      arg("""
        component InvalidComp13 refines Restrictive {
          port      in OOType i;
          port sync in SubType o;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // both ports change directions
      arg("""
        component InvalidComp14 refines Restrictive {
          port      out OOType i;
          port sync in SubType o;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // 2 timing and subtyping changes
      arg("""
        component InvalidComp15 refines RestrictiveMorePorts {
          port sync in SubType i1;
          port sync in SubType i2;
          port      out OOType o1;
          port      out OOType o2;
        }
        """,
        REFINEMENT_IN_PORT_TYPE_MISMATCH,
        REFINEMENT_TIMING_MISMATCH_IN,
        REFINEMENT_IN_PORT_TYPE_MISMATCH,
        REFINEMENT_TIMING_MISMATCH_IN,
        REFINEMENT_OUT_PORT_TYPE_MISMATCH,
        REFINEMENT_TIMING_MISMATCH_OUT,
        REFINEMENT_OUT_PORT_TYPE_MISMATCH,
        REFINEMENT_TIMING_MISMATCH_OUT
      ),
      // changed port direction -> no subtyping error (we do not know the direction of the subtype relationship)
      arg("""
        component InvalidComp16 refines Restrictive {
          port out SubType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // changed port direction -> no timing mismatch error (we do not know which timing is allowed to be weaker)
      arg("""
        component InvalidComp17 refines Restrictive {
          port in OOType i;
          port in OOType o;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // error for the second abstraction
      arg("""
        component InvalidComp18 refines Liberal, Restrictive {
          port in OOType i;
          port out OOType o;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_OUT,
        REFINEMENT_OUT_PORT_TYPE_MISMATCH
      ),
      // missing port
      arg("component InvalidComp19 refines DuplicatePortAllDifferent { }",
        REFINEMENT_PORT_NAME_MISMATCH
      ),
      // ambiguous port timing -> own timing not checked, but direction is unambiguous -> is checked
      arg("""
        component InvalidComp20 refines DuplicatePortSameDirection {
          port out long a;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // ambiguous port timing -> own timing not checked, but direction is unambiguous -> types are checked
      arg("""
        component InvalidComp21 refines DuplicatePortSameDirection {
          port in int a;
        }
        """,
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      ),
      // unambiguous direction -> timing and types are checked; should find timing change
      arg("""
        component InvalidComp22 refines DuplicateSameDirectionTiming {
          port sync in long a;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_IN
      ),
      // unambiguous direction -> timing and types are checked; should find port type only extending one abstraction port type
      arg("""
        component InvalidComp23 refines DuplicateSameDirectionTiming {
          port in int a;
        }
        """,
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      ),
      // abstraction has a duplicate port, unambiguous -> should identify changed direction
      arg("""
        component InvalidComp24 refines DuplicateEverythingSame {
          port out OOType a;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // abstraction has a duplicate port, unambiguous -> should identify changed timing and type;
      // two type errors, as SubType is not conform to either of the abstraction's a-ports
      arg("""
        component InvalidComp25 refines DuplicateEverythingSame {
          port sync in SubType a;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_IN,
        REFINEMENT_IN_PORT_TYPE_MISMATCH,
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      ),
      // abstraction is correct, refinement has a duplicate; timing is changed once -> one error
      arg("""
        component InvalidComp26 refines Restrictive {
          port sync in  OOType i;
          port      in  OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_IN
      ),
      // abstraction is correct, refinement has a duplicate; timing is changed twice -> two errors
      arg("""
        component InvalidComp27 refines Restrictive {
          port sync in  OOType i;
          port sync in  OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_IN,
        REFINEMENT_TIMING_MISMATCH_IN
      ),
      // abstraction is correct, refinement has a duplicate; direction is changed once -> one error
      arg("""
        component InvalidComp28 refines Restrictive {
          port      out OOType i;
          port      in  OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // abstraction is correct, refinement has a duplicate; direction is changed twice -> two errors
      arg("""
        component InvalidComp29 refines Restrictive {
          port      out OOType i;
          port      out OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED,
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // abstraction is correct, refinement has a duplicate; type is changed once -> one error
      arg("""
        component InvalidComp30 refines Restrictive {
          port      in SubType i;
          port      in OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      ),
      // abstraction is correct, refinement has a duplicate; type is changed twice -> two errors
      arg("""
        component InvalidComp31 refines Restrictive {
          port      in SubType i;
          port      in SubType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_IN_PORT_TYPE_MISMATCH,
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      ),
      // abstraction is correct, refinement has a duplicate; timing and type are changed separately
      arg("""
        component InvalidComp32 refines Restrictive {
          port sync in OOType i;
          port      in SubType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_IN,
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      ),
      // abstraction is correct, refinement has a duplicate; timing and type are changed simultaneously
      arg("""
        component InvalidComp33 refines Restrictive {
          port sync in SubType i;
          port      in OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_TIMING_MISMATCH_IN,
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      ),
      // abstraction is correct, refinement has a duplicate; one changes timing, the other type and direction (thus type is not checked)
      arg("""
        component InvalidComp34 refines Restrictive {
          port      out SubType i;
          port sync in  OOType i;
          port sync out SubType o;
        }
        """,
        REFINEMENT_PORT_DIRECTION_CHANGED,
        REFINEMENT_TIMING_MISMATCH_IN
      ),
      // super comp misses one of the ports
      arg("component InvalidComp35 extends Super3 refines Restrictive { }",
        REFINEMENT_PORT_NAME_MISMATCH
      ),
      // super comp adds a port
      arg("component InvalidComp36 extends Super4 refines Restrictive { }",
        REFINEMENT_PORT_NAME_MISMATCH
      ),
      // super comp changes direction of a port
      arg("component InvalidComp37 extends Super5 refines Restrictive { }",
        REFINEMENT_PORT_DIRECTION_CHANGED
      ),
      // super comp changes timing of a port
      arg("component InvalidComp38 extends Super6 refines Restrictive { }",
        REFINEMENT_TIMING_MISMATCH_IN
      ),
      // super comp changes type of a port
      arg("component InvalidComp39 extends Super7 refines Restrictive { }",
        REFINEMENT_IN_PORT_TYPE_MISMATCH
      )
    );
  }
}
