/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.mergeComp;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.generic.State;
import sim.generic.Transition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class TopCompAutUnderSpeci extends TopComp {

  private State one;

  private SortedMap<Integer, List<Transition>> history;

  private int tID;

  @Override
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
  public void treattIn(Integer msg) {
    tID++;
    for (Integer output : findPossIn(msg)) {
      sendtOut(output);
    }
  }

  public List<Integer> findPossIn(int m) {
    List<Integer> poss = new ArrayList<>();
    List<Transition> transitionstaken = new ArrayList<>();

    for (State t : findlastStates()) {
      if (t == one) {
        if (m > 0) {
          poss.add(-m);
          transitionstaken.add(Transition.of(t, one, "m>=0", this, null));
        }
        if (m > 1) {
          poss.add(m + 1);
          transitionstaken.add(Transition.of(t, one, "m>1", this, null));
        }
        if (m <= 0) {
          poss.add(m);

          transitionstaken.add(Transition.of(t, one, "m<0", this, null));
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

  public Set<State> findlastStates() {
    Set<State> output = new HashSet<>();
    for (Transition t : history.get(tID - 1)) {
      output.add(t.getTargetState());
    }
    return output;
  }
}
