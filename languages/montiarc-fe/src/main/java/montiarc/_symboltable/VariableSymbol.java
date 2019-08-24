/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import java.util.Optional;

import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import montiarc._ast.ASTValuation;

public class VariableSymbol extends CommonSymbol {
  
  public static final VariableKind KIND = new VariableKind();
  
  private JTypeReference<? extends JTypeSymbol> typeReference;
  
  private Optional<ASTValuation> valuation = Optional.empty();
  
  public VariableSymbol(String name) {
    super(name, KIND);
  }
  
  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }
  
  public void setValuation(Optional<ASTValuation> valuation) {
    this.valuation = valuation;
  }
  
  public Optional<ASTValuation> getValuation() {
    return this.valuation;
  }
  
  public JTypeReference<? extends JTypeSymbol> getTypeReference() {
    return this.typeReference;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("var ");
    if (null != typeReference) {
      sb.append(typeReference.getName());
      sb.append(" ");
    }
    sb.append(this.getName());
    return sb.toString();
  }
}
