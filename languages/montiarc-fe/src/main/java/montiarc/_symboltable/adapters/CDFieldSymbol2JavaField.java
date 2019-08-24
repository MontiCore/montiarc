/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.SymbolAdapter;
import de.monticore.symboltable.types.JAttributeSymbolKind;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

// TODO required for resolving types of an expression that uses cd stuff. See
// de.monticore.automaton.ioautomaton.TypeCompatibilityChecker for further
// information.
public class CDFieldSymbol2JavaField extends JavaFieldSymbol
    implements SymbolAdapter<CDFieldSymbol> {
  private final CDFieldSymbol adaptee;
  
  private static JavaTypeSymbolReference createReference(CDTypeSymbolReference cdReference) {
    //case when its a reference to a cd type
    if (cdReference.existsReferencedSymbol()) {
      return new JavaTypeSymbolReference(cdReference.getName(), cdReference.getEnclosingScope(),
          cdReference.getDimension());
    }
    // case when its a java type
    else {
      return new JavaTypeSymbolReference(cdReference.getName(), cdReference.getAstNode().get().getEnclosingScopeOpt().get(),
          cdReference.getDimension());
    }
  }
  
  public CDFieldSymbol2JavaField(CDFieldSymbol adaptee) {
    super(adaptee.getName(), (JAttributeSymbolKind) adaptee.getKind(),
        createReference(adaptee.getType()));
    
    this.adaptee = adaptee;
  }
  
  @Override
  public CDFieldSymbol getAdaptee() {
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
  
}
