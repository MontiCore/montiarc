package montiarc._ast;

import java.util.Optional;

public class ASTStereoValue extends ASTStereoValueTOP {
  
  public ASTStereoValue() {
    super();
  }
  
  public ASTStereoValue(String name, String source) {
    this.name = name;
    this.source = Optional.of(source);
  }

  public String getValue() {
    try {
      if (source.isPresent()) {
        return de.monticore.literals.LiteralsHelper.getInstance().decodeString(source.get());
      }
      else {
        return "";
      }
    }
    catch (Exception e) {
      return "";
    }
  }
  
  public void setValue(String value) {
    this.source = Optional.of('"' + value + '"');
  }
  
}

