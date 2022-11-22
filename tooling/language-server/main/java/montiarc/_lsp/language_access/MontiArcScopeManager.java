/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.language_access;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.resolver.CD4AnalysisResolver;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.MontiArcGlobalScope;

public class MontiArcScopeManager extends MontiArcScopeManagerTOP {
  private final CD4AnalysisResolver cd4AnalysisResolver = new CD4AnalysisResolver(CD4AnalysisMill.globalScope());
  private final OOClass2MCResolver ooClass2MCResolver = new OOClass2MCResolver();
  private final MontiArcTool tool = new MontiArcTool();

  @Override
  public void initGlobalScope(MCPath modelPath) {
    MontiArcMill.init();
    IMontiArcGlobalScope gs = MontiArcMill.globalScope();
    setGlobalScope((MontiArcGlobalScope) gs);
    ensureAdapterPresent(gs);
  }

  @Override
  public void clearGlobalScope() {
    syncAccessGlobalScope(gs -> {
      gs.clear();
      ensureAdapterPresent(gs);
    });
  }

  @Override
  public MontiArcArtifactScopeWithFindings createArtifactScope(ASTMACompilationUnit ast, IMontiArcArtifactScope oldArtifactScope) {
    MontiArcArtifactScopeWithFindings artifactScope = super.createArtifactScope(ast, oldArtifactScope);
    System.out.println("Complete symbol table!");
    tool.completeSymbolTable(ast);
    return artifactScope;
  }

  private void ensureAdapterPresent(IMontiArcGlobalScope gs) {
    if(!gs.containsAdaptedTypeSymbolResolver(cd4AnalysisResolver)) {
      gs.addAdaptedTypeSymbolResolver(cd4AnalysisResolver);
    }
    if(!gs.containsAdaptedTypeSymbolResolver(ooClass2MCResolver)){
      gs.addAdaptedTypeSymbolResolver(ooClass2MCResolver);
    }
    if(!gs.containsAdaptedOOTypeSymbolResolver(ooClass2MCResolver)){
      gs.addAdaptedOOTypeSymbolResolver(ooClass2MCResolver);
    }
  }
}
