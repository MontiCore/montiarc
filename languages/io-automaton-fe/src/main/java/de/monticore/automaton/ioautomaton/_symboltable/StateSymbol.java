package de.monticore.automaton.ioautomaton._symboltable;

import java.util.ArrayList;
import java.util.List;

import de.monticore.automaton.ioautomaton._symboltable.StateSymbolTOP;

public class StateSymbol extends StateSymbolTOP {

  private List<String> stereoValues; 
  private boolean isInitial;
  
  public StateSymbol(String name) {
    super(name);
    this.stereoValues = new ArrayList<>();
    this.isInitial = false;
  }
  
  public void setInitial(boolean initial) {
    isInitial = initial;
  }
  
  public boolean isInitial() {
    return isInitial;
  }

  public List<String> getStereoValues() {
    return this.stereoValues;
  }
  
  public void addStereoValue(String stereoValue) {
    this.stereoValues.add(stereoValue);   
  }
  
  @Override
  public String toString(){
    String stereo = "";
    String sep = "";
    if(this.getStereoValues() != null){
      for(String s : this.stereoValues){
        stereo += sep + s;
        sep = ",";
      }
      stereo = "<<" + stereo + ">> ";
      sep = "";
    }
    String initial = "";
    if(this.isInitial){
      initial = "initial ";      
    }    
    return stereo + initial + this.getName();
  }
}
