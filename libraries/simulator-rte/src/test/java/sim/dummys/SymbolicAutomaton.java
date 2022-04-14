/* (c) https://github.com/MontiCore/monticore */
package sim.dummys;

import sim.generic.*;

import java.util.*;

public class SymbolicAutomaton extends ComponentSymbolic implements IAutomaton {

  private State one;
  private State two;
  private State three;
  private int symbolicId;

  private Map<Integer, List<Configuration>> configList;

  @Override
  public void setupAutomaton() {
    one = new State();
    two = new State();
    three = new State();
    configList = new HashMap<>();
    List<Configuration> startState = new ArrayList<>();
    startState.add(Configuration.of(one, null, null, this));
    symbolicId = 0;
    configList.put(symbolicId, startState);
  }

  @Override
  public void treatIn(Message<?> message) {
    //message is symbolic
    if (message.getSymbolic() == true) {

      if (findSymbolicPossibility()) {

        for (Configuration conf : configList.get(symbolicId)) {
          conf.setId(symbolicId);
          Transitionpath tp = (Transitionpath) message.getData();
          tp.add(conf);
          Message out = Message.of(tp);
          out.setSymbolic(true);
          InProcessed(out);
        }
      }

      //message is not symbolic, but might create symbolic messages
    } else {

      //check if a transition can be taken
      if (findPossibility((Integer) message.getData())) {

        //if only one transition can be taken, the message is not symbolic
        if (configList.get(symbolicId).size() == 1) {
          int m = (Integer) configList.get(symbolicId).get(0).getValue();
          InProcessed(Message.of(m));

          //more than one transition can be taken
        } else {
          for (Configuration conf : configList.get(symbolicId)) {
            conf.setId(symbolicId);
            Transitionpath tp = new Transitionpath();
            tp.add(conf);
            Message out = Message.of(tp);
            out.setSymbolic(true);
            InProcessed(out);
          }
        }
      }
    }
  }

  /**
   * Finds out which transition/s are triggered by the message
   *
   * @return boolean is True, if no transition is triggered by the message
   */
  public boolean findPossibility(int m) {

    boolean taken;
    List<Configuration> currentList = new ArrayList<>();
    for (Configuration conf : (configList.get(symbolicId))) {
      if (conf.getState().equals(one)) {
        if (m > 0) {
          currentList.add(Configuration.of(two, "m>0", 2, this));
        }
        if (m > 1) {
          currentList.add(Configuration.of(three, "m>1", 3, this));
        }
      }
      if (conf.getState().equals(two)) {
        if (m > 0) {
          currentList.add(Configuration.of(one, "m>0", 1, this));
        }
        if (m <= 0) {
          currentList.add(Configuration.of(three, "m<=0", 3, this));
        }
      }
      if (conf.getState().equals(three)) {
        if (m > 0) {
          currentList.add(Configuration.of(one, "m>0", 1, this));
        }
        if (m <= 9) {
          currentList.add(Configuration.of(one, "m<=9", 3, this));
        }
      }
    }
    symbolicId++;
    if (currentList.isEmpty()) {
      System.out.println("No Transition taken in this step");
      configList.put(symbolicId, configList.get(symbolicId - 1));
      taken = false;
    } else {
      configList.put(symbolicId, currentList);
    }
    return true;
  }

  /**
   * Finds out all Transitions that the symbolic message triggers
   *
   * @return boolean is True, if no transition is triggered by the message
   */
  public boolean findSymbolicPossibility() {

    boolean taken;
    List<Configuration> currentList = new ArrayList<>();
    for (Configuration conf : (configList.get(symbolicId))) {
      if (conf.getState().equals(one)) {
        currentList.add(Configuration.of(two, "m>0", 2, this));
        currentList.add(Configuration.of(three, "m>1", 3, this));
      }
      if (conf.getState().equals(two)) {
        currentList.add(Configuration.of(one, "m>0", 1, this));
        currentList.add(Configuration.of(three, "m<=0", 3, this));
      }
      if (conf.getState().equals(three)) {
        currentList.add(Configuration.of(one, "m>0", 1, this));
        currentList.add(Configuration.of(one, "m<=9", 3, this));
      }
    }
    symbolicId++;
    if (currentList.isEmpty()) {
      System.out.println("No Transition taken in this step, last Message Symbolic");
      configList.put(symbolicId, configList.get(symbolicId - 1));
      taken = false;
    } else {
      configList.put(symbolicId, currentList);
    }
    return true;
  }

  @Override
  public Map<Integer, List<Configuration>> getHistory() {return configList;}

  @Override
  public void setSymbolicId(int symbolicId) {
    this.symbolicId = symbolicId;
  }
}

