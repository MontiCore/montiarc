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

public class FinalCompAut extends FinalComp {

  private State one;

  private State two;

  private SortedMap<Integer, List<Transition>> history;

  private int tID;


  @Override
  public void setup(IScheduler s, ISimulationErrorHandler eh) {
    one = new State();
    two = new State();
    history = new TreeMap<>();
    List<Transition> start = new ArrayList<>();
    start.add(Transition.of(one, one, null, this, null));
    tID = 0;
    history.put(tID, start);
    super.setup(s, eh);
  }

  public List<Integer> findPossInInt(int m) {
    List<Integer> poss = new ArrayList<>();
    List<Transition> transitionstaken = new ArrayList<>();

    for (State t : findlastStates()) {
      if (t.equals(one)) {
        if (m >= 0) {
          poss.add(1);
          transitionstaken.add(Transition.of(t, one, "m>=0", this, poss));
        }
        if (m < 0) {
          poss.add(2);
          transitionstaken.add(Transition.of(t, one, "m<0", this, poss));
        }
      }
      if (t.equals(two)) {
        if (m > 0) {
          poss.add(3);
          transitionstaken.add(Transition.of(t, one, "m>0", this, poss));
        }
        if (m <= 0) {
          poss.add(4);

          transitionstaken.add(Transition.of(t, one, "m<=0", this, poss));
        }
      }
    }
    if (!transitionstaken.isEmpty()) {
      tID++;
      if (history.lastKey() < tID) {
        history.put(tID, transitionstaken);
      } else {
        history.get(tID).addAll(transitionstaken);
      }
    }
    return poss;
  }

  public List<Integer> findPossInBool(boolean m) {
    List<Integer> poss = new ArrayList<>();
    List<Transition> transitionstaken = new ArrayList<>();

    for (State t : findlastStates()) {

      if (t == one) {
        if (m == false) {
          transitionstaken.add(Transition.of(t, two, "m==false", this, null));
        }
      }
      if (t == two) {
        if (m == true) {
          transitionstaken.add(Transition.of(t, one, "m==true", this, null));
        }
      }
    }
    if (!transitionstaken.isEmpty()) {
      tID++;
      if (history.lastKey() < tID) {
        history.put(tID, transitionstaken);
      } else {
        history.get(tID).addAll(transitionstaken);
      }
    }
    return poss;
  }

  @Override
  protected void timeStep() {

  }

  @Override
  public void treatmInInt(Integer msg) {
    for (Integer output : findPossInInt(msg)) {
      sendmOut(output);
    }
  }

  @Override
  public void treatmInBool(Boolean msg) {
    for (Integer output : findPossInBool(msg)) {
      sendmOut(output);
    }
  }

  public Set<State> findlastStates() {
    Set<State> output = new HashSet<>();
    for (Transition t : history.get(tID)) {
      output.add(t.getTargetState());
    }
    return output;
  }
}
