/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable.adapters;

import de.monticore.ast.ASTNode;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.resolving.SymbolAdapter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import montiarc._symboltable.PortSymbol;

/**
 * Variable to field adapter for accessing variables/inputs/outputs as Java fields inside Java
 * expressions.
 * 
 */
public class Port2FieldAdapter extends JavaFieldSymbol implements SymbolAdapter<PortSymbol> {
  private final PortSymbol adaptee;
  
  private static JavaTypeSymbolReference createReference(
      JTypeReference<? extends JTypeSymbol> reference) {
    return new JavaTypeSymbolReference(reference.getName(), reference.getEnclosingScope(),
        reference.getDimension());
  }
  
  public Port2FieldAdapter(PortSymbol adaptee) {
    super(adaptee.getName(), JavaFieldSymbol.KIND, createReference(adaptee.getTypeReference()));
    this.adaptee = adaptee;
  }
  
  @Override
  public PortSymbol getAdaptee() {
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
