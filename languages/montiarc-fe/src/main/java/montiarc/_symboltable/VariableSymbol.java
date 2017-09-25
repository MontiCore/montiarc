package montiarc._symboltable;

import montiarc._ast.ASTValuation;
import de.monticore.symboltable.CommonSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

public class VariableSymbol extends CommonSymbol {
  
  public static final VariableKind KIND = VariableKind.INSTANCE;
  
  private JTypeReference<? extends JTypeSymbol> typeReference;
  
  private ASTValuation valuation;
  
  public VariableSymbol(String name) {
    super(name, KIND);
  }
  
  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }
  
  public void setValuation(ASTValuation valuation) {
    this.valuation = valuation;
  }

  public ASTValuation getValuation() {
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
