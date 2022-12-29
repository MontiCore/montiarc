/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis._lsp.language_access;

import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisArtifactScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;

public class CD4AnalysisScopeManager extends CD4AnalysisScopeManagerTOP {

  @Override
  public void initGlobalScope(MCPath modelPath) {
    BasicSymbolsMill.init();
    super.initGlobalScope(modelPath);
    ensurePrimitivesInitialized();
  }

  @Override
  public void clearGlobalScope() {
    super.clearGlobalScope();
    ensurePrimitivesInitialized();
  }

  private void ensurePrimitivesInitialized() {
    syncAccessGlobalScope(gs -> {
      if (gs.resolveType("int").isEmpty()) {
        BasicSymbolsMill.initializePrimitives();
      }
    });
  }

  @Override
  public CD4AnalysisArtifactScopeWithFindings createArtifactScope(ASTCDCompilationUnit ast, ICD4AnalysisArtifactScope oldArtifactScope) {
    CD4AnalysisArtifactScopeWithFindings res = super.createArtifactScope(ast, oldArtifactScope);
    ast.accept(new CD4AnalysisSymbolTableCompleter(ast).getTraverser());

    if (!Log.getFindings().isEmpty()) {
      res.findings.addAll(Log.getFindings());
    }

    return res;
  }
}
