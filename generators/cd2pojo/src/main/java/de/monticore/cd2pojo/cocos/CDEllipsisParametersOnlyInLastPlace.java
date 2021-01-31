/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo.cocos;

import com.google.common.base.Preconditions;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._cocos.CD4CodeBasisASTCDMethodCoCo;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

public class CDEllipsisParametersOnlyInLastPlace implements CD4CodeBasisASTCDMethodCoCo {
  
  @Override
  public void check(@NotNull ASTCDMethod node) {
    Preconditions.checkNotNull(node);
    if(node.getCDParametersList().size() < 2) return;
    for(int i = node.getCDParametersList().size() - 2; i >= 0; i--) {
      if(node.getCDParameters(i).isEllipsis()) {
        Log.error(String.format("The parameter %s of the method %s is elliptic, " +
            "but is not the last parameter in the method definition." ,
          node.getCDParameters(i).getName(), node.getName()));
      }
    }
  }
}