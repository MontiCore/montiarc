/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.generic.Configuration;
import sim.generic.IAutomaton;
import sim.generic.Message;
import sim.generic.State;

import java.util.List;
import java.util.Map;

public class DummyAutomaton extends ComponentAutomaton implements IAutomaton {

  private State one;
  private State two;
  private State three;
  private State currentState;

  @Override
  public void setupAutomaton() {
    one = new State();
    two = new State();
    three = new State();
    currentState = one;
  }

  @Override
  public Map<Integer, List<Configuration>> getHistory() {
    return null;
  }

  @Override
  public void treatIn(Message<?> message) {
    int m = (Integer) message.getData();
    if (one.equals(currentState)) {
      if (m > 0) {
        currentState = two;
        m = 2;
      } else if (m <= 0) {
        currentState = three;
        m = 3;
      }
    } else if (two.equals(currentState)) {
      if (m > 0) {
        currentState = one;
        m = 1;
      } else if (m <= 0) {
        currentState = three;
        m = 3;
      }
    } else if (three.equals(currentState)) {
      if (m > 0) {
        currentState = one;
        m = 1;

      } else if (m <= 0) {
        currentState = two;
        m = 2;
      }

    }
    InProcessed(Message.of(m));
  }

  @Override
  public void setSymbolicId(int i) {

  }
}
