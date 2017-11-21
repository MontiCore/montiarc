package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._cocos.MontiArcASTComponentCoCo;

public class AtMostOneInitBlock implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent node) {
    int initCount = 0;

    for (ASTElement element : node.getBody().getElements()) {
      if(element instanceof ASTJavaPInitializer) {
        if(initCount >= 1) {
          Log.error("0xMA078 There is more than one AJava init block in the component" + node.getName(), element.get_SourcePositionStart());
        } else {
          initCount++;
        }
      }
    }
  }
}
