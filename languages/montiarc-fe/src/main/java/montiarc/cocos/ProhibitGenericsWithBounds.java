/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._cocos.TypesASTTypeVariableDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * As of issue #241 we prohibit the use of generic type parameters which have
 * bounds. This was due to problems in generating executable code, as detailed
 * in the issue.
 */
public class ProhibitGenericsWithBounds implements TypesASTTypeVariableDeclarationCoCo {

  @Override
  public void check(ASTTypeVariableDeclaration astTypeVariableDeclaration) {
    if(astTypeVariableDeclaration.getUpperBoundList().size() > 0){
      Log.error("0xMA072 Generic type parameters with bounds are " +
                    "currently not allowed.");
    }
  }
}
