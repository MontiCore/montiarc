package de.monticore.lang.montiarc.montiarcautomaton.cdadapter;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.SymbolAdapter;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

//TODO required for resolving types of an expression that uses cd stuff. See
//de.monticore.automaton.ioautomaton.TypeCompatibilityChecker for further
//information.
public class CDTypeSymbol2JavaType extends JavaTypeSymbol implements SymbolAdapter<CDTypeSymbol> {
  
  private final CDTypeSymbol adaptee;
  
  public CDTypeSymbol2JavaType(CDTypeSymbol adaptee) {
    super(adaptee.getName());
    
    this.adaptee = adaptee;
  }
  
  @Override
  public CDTypeSymbol getAdaptee() {
    return adaptee;
  }
  
  @Override
  public String getFullName() {
    return adaptee.getFullName();
  }
  
  @Override
  public AccessModifier getAccessModifier() {
    return adaptee.getAccessModifier();
  }
  
  @Override
  public Optional<ASTNode> getAstNode() {
    return adaptee.getAstNode();
  }
  
  @Override
  public Scope getEnclosingScope() {
    return adaptee.getEnclosingScope();
  }
  
  @Override
  public SymbolKind getKind() {
    return adaptee.getKind();
  }
  
  @Override
  public String getPackageName() {
    return adaptee.getPackageName();
  }
  
  @Override
  public void setAstNode(ASTNode node) {
    adaptee.setAstNode(node);
  }
  
  @Override
  public void setFullName(String fullName) {
    adaptee.setFullName(fullName);
  }
  
  @Override
  public void setPackageName(String packageName) {
    adaptee.setPackageName(packageName);
  }
  
  @Override
  public String toString() {
    return adaptee.toString();
  }
  
  @Override
  public Scope getSpannedScope() {
    return adaptee.getSpannedScope();
  }
  
}
