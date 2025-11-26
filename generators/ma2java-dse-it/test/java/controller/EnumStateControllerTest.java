/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.StateInfo;
import montiarc.rte.dse.StatesList;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class EnumStateControllerTest {

  EnumStateController<List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>, List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> controller;

  @BeforeEach
  void setUpMock() {
    controller = new EnumStateController<>();
    assertThat(controller).isNotNull();

    controller.init();

    assertThat(controller).isNotNull();
  }

  @Test
  public void testCompareStates() {
    Set<Pair<StatesList, Integer>> visitedStates = new LinkedHashSet<>();

    StateInfo stateInfoCSRwth = StateInfo.newStateInfo(EnumStateControllerTest.TestEnum2.COMPUTERSCIENCE,
            Arrays.asList("university : RWTH"), "testComponent2");
    StateInfo stateInfoAachen = StateInfo.newStateInfo(TestEnum1.AACHEN,
            Arrays.asList("country : Germany"), "testComponent1");

    StatesList infoRWTH = new StatesList(Arrays.asList(stateInfoCSRwth));
    StatesList infoAachen = new StatesList(Arrays.asList(stateInfoAachen));

    visitedStates = Set.of(Pair.of(infoRWTH, 2));

    assertThat(controller.compareStates(visitedStates, infoRWTH)).isEqualTo(Pair.of(infoRWTH, 2));

    assertThat(controller.compareStates(visitedStates, infoAachen)).isNull();
  }

  private enum TestEnum1 {
    AACHEN;
  }

  private enum TestEnum2 {
    COMPUTERSCIENCE;
  }
}
