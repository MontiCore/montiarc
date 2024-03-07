/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a state in a MontiArc automaton.
 */
public class State {

  public State(String name) {
    this.name = name;
    this.subStates = new ArrayList<>();
  }

  public State(String name, List<State> subStates, List<State> initialSubstates){
    this.name = name;
    this.subStates = subStates;
    if(initialSubstates.isEmpty()) {
      this.initialSubstates = subStates;
    }else {
      this.initialSubstates = initialSubstates;
    }
  }

  public State(String name, List<State> subStates){
    this.name = name;
    this.subStates = subStates;
  }

  String name;

  List<State> subStates = new ArrayList<>();

  List<State> initialSubstates = new ArrayList<>();

  public String name() {
    return this.name;
  }

  public void enter() { }
  public void enterWithSub(){
    enter();
    if(!initialSubstates.isEmpty())
      getInitialSubstate().enterWithSub();
  }

  public void exit() { }

  public void exitSub(){
    if(!initialSubstates.isEmpty())
      getInitialSubstate().enterWithSub();
    this.exit();
  }

  public void doAction(){}

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }

  public List<State> getSubStates(){
    return subStates;
  }

  public List<State> getInitialSubstates() {
    return initialSubstates;
  }
  public State getInitialSubstate() {
    if(initialSubstates.isEmpty())
      return this;
    return initialSubstates.get(0).getInitialSubstate();
  }

  public boolean isSubState(State current){
    if(this.getSubStates().contains(current)){
      return true;
    }else{
      for(State s: subStates){
        if (s.isSubState(current))
          return true;
      }
    }
    return false;
  }
}
