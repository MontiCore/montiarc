package montiarc._ast;

import de.monticore.types.types._ast.ASTQualifiedName;

import java.util.List;
import java.util.Optional;

public class ASTConnector extends ASTConnectorTOP {
  
  public ASTConnector() {
    super();
  }
  
  protected  ASTConnector (Optional<ASTStereotype> stereotype, ASTQualifiedName source, List<ASTQualifiedName> targetss){
    super(stereotype,source, targetss);
  }
  
  public String toString(){
    StringBuilder stringBuilder =  new StringBuilder();
    stringBuilder.append(this.getSource().toString());
    stringBuilder.append(" ->");
    this.getTargetsList().stream().map(ASTQualifiedName::toString).forEach(name -> stringBuilder.append(" ").append(name));
    return stringBuilder.toString();
  }
  
}
