/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.language_access;

import arcbasis._symboltable.ArcBasisScopesGenitorP3;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis.resolver.CD4AnalysisResolver;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.MontiArcGlobalScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

  /**
   * Since the structure of the MontiArc symbol table is complex and the creation has 3 phases, it has to be rebuild completely for every changed artifact.
   * In particular, the {@link ArcBasisScopesGenitorP3} will set port symbols of connectors of the current artifact based on components in other artifacts.
   * Rebuilding the artifact of the referenced component necessitates the rebuild of the artifact of the referencing component/connector.
   * If the performance of the full rebuild becomes a problem, a partial rebuild based on dependency analysis could be implemented.
   * To keep the complexity low, it will not be implemented for now.
   */
  @Override
  public boolean supportsIterativeScopeAppending() {
    return false;
  }

  @Override
  public MontiArcArtifactScopeWithFindings createArtifactScope(ASTMACompilationUnit ast, IMontiArcArtifactScope oldArtifactScope) {
    // see JavaDoc of supportsIterativeScopeAppending
    throw new IllegalStateException("Currently not supported for the complex symbol table of MontiArc");
  }

  private void ensureAdapterPresent(IMontiArcGlobalScope gs) {
    if (!gs.containsAdaptedTypeSymbolResolver(cd4AnalysisResolver)) {
      gs.addAdaptedTypeSymbolResolver(cd4AnalysisResolver);
    }
    if (!gs.containsAdaptedTypeSymbolResolver(ooClass2MCResolver)) {
      gs.addAdaptedTypeSymbolResolver(ooClass2MCResolver);
    }
    if (!gs.containsAdaptedOOTypeSymbolResolver(ooClass2MCResolver)) {
      gs.addAdaptedOOTypeSymbolResolver(ooClass2MCResolver);
    }
  }

  @Override
  public Map<ASTMACompilationUnit, MontiArcArtifactScopeWithFindings> createAllArtifactScopes(Collection<ASTMACompilationUnit> astNodes) {
    // Run completeSymbolTable after symbol table scaffolding for all components exist
    var res = new HashMap<ASTMACompilationUnit, MontiArcArtifactScopeWithFindings>();
    syncAccessGlobalScope(gs -> {
      for (ASTMACompilationUnit node : astNodes) {
        Log.clearFindings();
        var as = tool.createSymbolTable(node);
        res.put(node, new MontiArcArtifactScopeWithFindings(node, as, Log.getFindings()));
      }

      for (ASTMACompilationUnit node : astNodes) {
        Log.clearFindings();
        tool.runSymbolTablePhase2(node);
        tool.runSymbolTablePhase3(node);
        tool.runAfterSymbolTablePhase3Trafos(node);
        if (res.containsKey(node)) {
          res.get(node).findings.addAll(Log.getFindings());
        }
      }
    });

    return res;
  }
}
