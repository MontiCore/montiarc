package de.monticore.automaton.ioautomatonjava._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.resolving.SymbolAdapter;

public class Variable2FieldAdapter extends JavaFieldSymbol implements SymbolAdapter<VariableSymbol> {
  private VariableSymbol adaptee;

  public Variable2FieldAdapter(VariableSymbol adaptee) {
    super(adaptee.getName(), JavaFieldSymbol.KIND, (JavaTypeSymbolReference) adaptee.getTypeReference());
    this.adaptee = adaptee;
  }

  @Override
  public VariableSymbol getAdaptee() {
    return adaptee;
  }
  
  @Override
  public void setType(JavaTypeSymbolReference type) {
    adaptee.setTypeReference(type);
    super.setType(type);
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
