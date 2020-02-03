/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc._symboltable;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.SymbolKind;
import dynamicmontiarc._ast.ASTModeTransition;
import montiarc._ast.ASTGuard;

import java.util.Optional;

public class ModeTransitionSymbol extends CommonScopeSpanningSymbol {

  public static ModeTransitionKind KIND = new ModeTransitionKind();

  public ModeTransitionSymbol(String name) {
    this(name, KIND);
  }

  public ModeTransitionSymbol(String name, SymbolKind kind) {
    super(name, kind);
  }

  public Optional<ASTGuard> getGuard(){
    if(!getAstNode().isPresent()){
      return Optional.empty();
    }
    final ASTModeTransition node = (ASTModeTransition) getAstNode().get();

    return node.getGuardOpt();
  }
}
