package de.monticore.automaton.ioautomaton._symboltable;

import de.monticore.automaton.ioautomaton._symboltable.StateSymbolReference;
import de.monticore.automaton.ioautomaton._symboltable.TransitionSymbolTOP;

public class TransitionSymbol extends TransitionSymbolTOP {

  private StateSymbolReference source;
  private StateSymbolReference target;
  private boolean hasGuard;
  private boolean hasStimulus;
  private boolean hasReaction;

  public TransitionSymbol(String name) {
    super(name);
    this.hasGuard = false;
    this.hasStimulus = false;
    this.hasReaction = false;
  }
  
  public StateSymbolReference getSource() {
    return this.source;
  }

  public void setSource(StateSymbolReference source) {
    this.source = source;
  }

  public StateSymbolReference getTarget() {
    return this.target;
  }

  public void setTarget(StateSymbolReference target) {
    this.target = target;
  }

  public void setGuard(boolean b){
    this.hasGuard = b;
  }
  
  public boolean hasGuard(){
    return this.hasGuard;
  }
  
  public void setStimulus(boolean b){
    this.hasStimulus = b;
  }
  
  public boolean hasStimulus(){
    return this.hasStimulus;
  }
  
  public void setReaction(boolean b){
    this.hasReaction = b;
  }
  
  public boolean hasReaction(){
    return this.hasReaction;
  }
  
  @Override
  public String toString() {
    return getName();
  }
}
