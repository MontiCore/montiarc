/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.sched.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.Automaton.State;
import sim.Automaton.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BottomCompAut extends BottomComp {

  private State one;

  private SortedMap<Integer, List<Transition>> history;

  private int tID;

  public void setup(IScheduler s, ISimulationErrorHandler eh) {
    one = new State();
    history = new TreeMap<>();
    List<Transition> start = new ArrayList<>();
    start.add(Transition.of(one, one, null, this, null));
    tID = 0;
    history.put(tID, start);
    super.setup(s, eh);
  }

  @Override
  protected void timeStep() {

  }

  @Override
  public void treatbIn(Integer msg) {
    tID++;
    for (Boolean output : findPossIn(msg)) {
      sendbOut(output);
    }
  }

  public List<Boolean> findPossIn(int m) {
    List<Boolean> poss = new ArrayList<>();
    List<Transition> transitionstaken = new ArrayList<>();

    for (Transition t : history.get(history.lastKey())) {
      State current = t.getTargetState();
      if (current == one) {
        if (m > 0) {
          poss.add(true);
          transitionstaken.add(Transition.of(current, one, "m>0", this, null));
        }
        if (m <= 0) {
          poss.add(false);

          transitionstaken.add(Transition.of(current, one, "m<=0", this, null));
        }
      }
    }
    if (history.lastKey() < tID) {
      history.put(tID, transitionstaken);
    } else {
      history.get(tID).addAll(transitionstaken);
    }
    return poss;
  }
}
