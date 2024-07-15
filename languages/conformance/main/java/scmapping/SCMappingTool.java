/* (c) https://github.com/MontiCore/monticore */
package scmapping;

import scmapping._ast.ASTSCMapping;
import scmapping._symboltable.ISCMappingArtifactScope;
import scmapping._symboltable.ISCMappingGlobalScope;

public class SCMappingTool extends SCMappingToolTOP {

  @Override
  public ISCMappingArtifactScope createSymbolTable(ASTSCMapping ast) {
    initGlobalScope();
    return super.createSymbolTable(ast);
  }

  public void initGlobalScope() {
    ISCMappingGlobalScope gs = SCMappingMill.globalScope();
    if (null == gs.getFileExt() || gs.getFileExt().isEmpty()) {
      gs.setFileExt("map");
    }
  }
}
