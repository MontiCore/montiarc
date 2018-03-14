package montiarc._ast;

import java.util.Optional;

public class ASTStereoValue extends ASTStereoValueTOP {
  
  /**
   * Constructor for montiarc._ast.ASTStereoValue
   */
  protected ASTStereoValue() {
    super();
  }
  
  /**
   * Constructor for montiarc._ast.ASTStereoValue
   * 
   * @param name
   * @param source
   */
  protected ASTStereoValue(String name, Optional<String> source) {
    super(name, source);
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
