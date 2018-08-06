package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTComponentBody;
import montiarc._cocos.MontiArcASTComponentBodyCoCo;

//TODO("Remove this coco when inner Components should be allowed again")
public class NoInnerComponents implements MontiArcASTComponentBodyCoCo {
@Override
public void check(ASTComponentBody node) {
  node.streamElements().forEach(element -> {
    if (element instanceof ASTComponent) Log.error("0xMA093 Inner Components are forbidden in this Version" , element.get_SourcePositionStart());
  });
  
}
}
