package montiarc._ast;

import java.util.List;

public class ASTStereotype extends ASTStereotypeTOP {
  
  public ASTStereotype() {
    super();
  }
  
  public ASTStereotype(List<ASTStereoValue> values) {
    this.values = values;
  }
  
  public boolean containsStereoValue(String name) {
    return (getStereoValue(name) != null);
  }
  
  public boolean containsStereoValue(String name, String value) {
    for (ASTStereoValue sv : values) {
      if (sv.getName() != null && sv.getName().equals(name)
          && sv.getValue() != null && sv.getValue().equals(value)) {
        return true;
      }
    }
    return false;
  }
  
  public ASTStereoValue getStereoValue(String name) {
    for (ASTStereoValue sv : values) {
      if (sv.getName() != null && sv.getName().equals(name)) {
        return sv;
      }
    }
    return null;
  }
  
}
