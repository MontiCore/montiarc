/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import java.util.Optional;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import montiarc._ast.ASTBlock;
import montiarc._ast.ASTGuard;

public class TransitionSymbol extends CommonScopeSpanningSymbol {
  
  private StateSymbolReference source;
  
  private StateSymbolReference target;
  
  private Optional<ASTGuard> guard;
  
  private Optional<ASTBlock> reaction;
  
  public static final TransitionKind KIND = new TransitionKind();
  
  public TransitionSymbol(String name) {
    super(name, KIND);
    this.guard = Optional.empty();
    this.reaction = Optional.empty();
  }
  
  @Override
  protected TransitionScope createSpannedScope() {
    return new TransitionScope();
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
  
  public void setGuardAST(ASTGuard guard) {
    this.guard = Optional.of(guard);
  }
  
  public Optional<ASTGuard> getGuardAST() {
    return this.guard;
  }
  
  
  public void setReactionAST(ASTBlock block) {
    this.reaction = Optional.of(block);
  }
  
  public Optional<ASTBlock> getReactionAST() {
    return this.reaction;
  }
  
  @Override
  public String toString() {
    return getName();
  }
}
