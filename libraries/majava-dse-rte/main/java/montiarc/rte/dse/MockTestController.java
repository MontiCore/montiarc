/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockTestController implements TestControllerI {
  protected static Context context;
  protected static Integer transition = 0;

  public static void init() {
    MockTestController result = new MockTestController();
    TestController.init(result);

    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");

    context = new Context(cfg);
  }

  public void setTransition(Integer transition) {
    this.transition = transition;
  }

  public boolean shouldEndRun() {
    return false;
  }

  public boolean getIfOracle(String branchId) {
    if (transition > 1) {
      transition--;
      return false;
    }
    else {
      return true;
    }
  }

  public boolean getIf(BoolExpr condition, boolean result, String branchID) {
    return result;
  }

  public Context getCtx() {
    System.err.println("getCTX");
    return context;
  }

  public void selectTransition(List<Pair<Runnable, String>> possibleTransitions) {
    for (int i = 0; i < possibleTransitions.size(); i++) {
      montiarc.rte.log.Log.comment("Transitions:" + possibleTransitions.get(i).toString());
    }
    if (possibleTransitions.size() == 1) {
      possibleTransitions.get(0).getKey().run();
    }

    if (possibleTransitions.size() > 1) {
      int transition = 0;
      while (transition < possibleTransitions.size() && !getIfOracle("possibleTransitions")) {
        transition++;
      }
      montiarc.rte.log.Log.comment("Transitions:" + possibleTransitions.get(transition).toString());

      possibleTransitions.get(transition).getKey().run();
    }
  }
}
