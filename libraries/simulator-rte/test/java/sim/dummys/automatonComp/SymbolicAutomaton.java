/* (c) https://github.com/MontiCore/monticore */
package sim.dummys.automatonComp;

import sim.automaton.ComponentState;
import sim.automaton.IAutomaton;
import sim.automaton.State;
import sim.automaton.Transition;
import sim.message.Message;

import java.util.*;

public class SymbolicAutomaton extends ComponentSymbolic implements IAutomaton {

  private State one;
  private State two;
  private State three;

  private SortedMap<Integer, List<Transition>> history;

  private int tID;

  @Override
  public void setupAutomaton() {
    one = new State("one");
    two = new State("two");
    three = new State("three");
    List<Transition> start = new ArrayList<>();
    start.add(Transition.of(one, one, null, this, null));
    history = new TreeMap<>();
    tID = 0;
    history.put(tID, start);
  }

  @Override
  public void treatIn(Integer message) {
    tID++;
    for (Integer output : findPossibility(message)) {
      InProcessed(Message.of(output));
    }

  }

  /**
   * Finds out which transition/s are triggered by the message
   *
   * @return boolean is True, if no transition is triggered by the message
   */
  public List<Integer> findPossibility(int m) {
    List<Integer> poss = new ArrayList<>();
    List<Transition> transitionstaken = new ArrayList<>();

    for (State current : findlastStates()) {
      if (current.equals(one)) {
        if (m > 0) {
          poss.add(2);
          transitionstaken.add(Transition.of(one, two, "m>0", this, null));
        }
        if (m > 1) {
          poss.add(3);
          transitionstaken.add(Transition.of(one, three, "m>1", this, null));

        }
      }
      if (current.equals(two)) {
        if (m > 0) {
          poss.add(1);
          transitionstaken.add(Transition.of(two, one, "m>0", this, null));
        }
        if (m <= 0) {
          poss.add(3);
          transitionstaken.add(Transition.of(two, three, "m<=0", this, null));
        }
      }
      if (current.equals(three)) {
        if (m > 0) {
          poss.add(1);
          transitionstaken.add(Transition.of(three, one, "m>0", this, null));
        }
        if (m <= 9) {
          poss.add(2);
          transitionstaken.add(Transition.of(three, two, "m>0", this, null));
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


  @Override
  public ComponentState getComponentState() {
    return null;
  }

  @Override
  public void setComponentState(ComponentState cs) {

  }
}

