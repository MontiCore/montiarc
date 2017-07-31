package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.lang.montiarc.montiarc._ast.ASTBlock;


public class StateSymbol extends StateSymbolTOP {

  private List<String> stereoValues; 
  private Optional<ASTBlock> initialReaction;
  private boolean isInitial;
  
  public StateSymbol(String name) {
    super(name);
    this.stereoValues = new ArrayList<>();
    this.initialReaction = Optional.empty();
    this.isInitial = false;
  }
  
  public void setInitial(boolean isInitial) {
    this.isInitial = isInitial;
  }
  
  public void setInitialReactionAST(Optional<ASTBlock> reaction) {
    initialReaction = reaction;
  }
  
  public boolean isInitial() {
    return isInitial;
  }
  
  public Optional<ASTBlock> getInitialReactionAST() {
    return initialReaction;
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
