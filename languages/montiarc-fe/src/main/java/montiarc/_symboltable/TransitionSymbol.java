package montiarc._symboltable;

import java.util.Optional;

import montiarc._ast.ASTBlock;
import montiarc._ast.ASTGuard;

public class TransitionSymbol extends TransitionSymbolTOP {
  
  private StateSymbolReference source;
  
  private StateSymbolReference target;
  
  private Optional<ASTGuard> guard;
  
  private Optional<ASTBlock> stimulus;
  
  private Optional<ASTBlock> reaction;
  
  public TransitionSymbol(String name) {
    super(name);
    this.guard = Optional.empty();
    this.stimulus = Optional.empty();
    this.reaction = Optional.empty();
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
  
  public void setGuardAST(Optional<ASTGuard> guard) {
    this.guard = guard;
  }
  
  public Optional<ASTGuard> getGuardAST() {
    return this.guard;
  }
  
  public void setStimulusAST(Optional<ASTBlock> block) {
    this.stimulus = block;
  }
  
  public Optional<ASTBlock> getStimulusAST() {
    return this.stimulus;
  }
  
  public void setReactionAST(Optional<ASTBlock> block) {
    this.reaction = block;
  }
  
  public Optional<ASTBlock> getReactionAST() {
    return this.reaction;
  }
  
  @Override
  public String toString() {
    return getName();
  }
}
