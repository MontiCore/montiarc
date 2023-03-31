/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCos;

public class MontiArcLspCoCoRunner extends MontiArcLspCoCoRunnerTOP {

  public MontiArcLspCoCoRunner(DocumentManager documentManager) {
    super(documentManager);
  }

  @Override
  public boolean needsSymbols() {
    return true;
  }

  @Override
  public void runAllCoCos(ASTMACompilationUnit ast) {
    MontiArcCoCos.afterSymTab().checkAll(ast);
  }
}
