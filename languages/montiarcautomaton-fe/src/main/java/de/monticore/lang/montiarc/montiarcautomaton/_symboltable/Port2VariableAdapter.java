package de.monticore.lang.montiarc.montiarcautomaton._symboltable;

import org.antlr.v4.parse.ATNBuilder.subrule_return;

import de.monticore.ast.ASTNode;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.resolving.SymbolAdapter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

public class Port2VariableAdapter extends VariableSymbol implements SymbolAdapter<PortSymbol> {
  private final PortSymbol adaptee;

  public Port2VariableAdapter(PortSymbol adaptee) {
    super(adaptee.getName());
    
    // set variable symbol attributes
    super.setTypeReference((JTypeReference<JTypeSymbol>) adaptee.getTypeReference());
    super.setDirection(adaptee.isIncoming() ? Direction.Input : Direction.Output);
    // TODO is this correct?
//    super.setEnclosingScope((MutableScope) adaptee.getEnclosingScope());
//    super.setAstNode(adaptee.getAstNode().orElse(null));
//    super.setFullName(adaptee.getFullName());
//    super.setPackageName(adaptee.getPackageName());
    
    this.adaptee = adaptee;
  }

  @Override
  public PortSymbol getAdaptee() {
    return adaptee;
  }
  
  @Override
  public void setDirection(Direction direction) {
    if (direction == Direction.Input) {
      adaptee.setDirection(true);
    } else if (direction == Direction.Output) {
      adaptee.setDirection(false);
    } else {
      Log.error("0xABF00 Variable not supported.");
    }
    super.setDirection(direction);
  }
  
  @Override
  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    adaptee.setTypeReference(typeReference);
    super.setTypeReference(typeReference);
  }
  
  @Override
  public void setEnclosingScope(MutableScope scope) {
    adaptee.setEnclosingScope(scope);
    super.setEnclosingScope(scope);
  }
  
  @Override
  public void setFullName(String fullName) {
    adaptee.setFullName(fullName);
    super.setFullName(fullName);
  }
  
  @Override
  public void setPackageName(String packageName) {
    adaptee.setPackageName(packageName);
    super.setPackageName(packageName);
  }
  
  @Override
  public void setAstNode(ASTNode node) {
    adaptee.setAstNode(node);
    super.setAstNode(node);
  }
}
