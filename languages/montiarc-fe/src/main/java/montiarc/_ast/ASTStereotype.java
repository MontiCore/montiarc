/* (c) https://github.com/MontiCore/monticore */
package montiarc._ast;

import java.util.List;

public class ASTStereotype extends ASTStereotypeTOP {
  
  /**
   * Constructor for montiarc._ast.ASTStereotype
   */
  protected ASTStereotype() {
    super();
  }
  
  /**
   * Constructor for montiarc._ast.ASTStereotype
   * 
   * @param valuess
   */
  protected ASTStereotype(List<ASTStereoValue> valuess) {
    super(valuess);
  }
  
  public boolean containsStereoValue(String name) {
    return (getStereoValue(name) != null);
  }
  
  public boolean containsStereoValue(String name, String value) {
    for (ASTStereoValue sv : getValuesList()) {
      if (sv.getName() != null && sv.getName().equals(name)
          && sv.getValue() != null && sv.getValue().equals(value)) {
        return true;
      }
    }
    return false;
  }
  
  public ASTStereoValue getStereoValue(String name) {
    for (ASTStereoValue sv : getValuesList()) {
      if (sv.getName() != null && sv.getName().equals(name)) {
        return sv;
      }
    }
    return null;
  }
  
}
