/* (c) https://github.com/MontiCore/monticore */
package montiarc._lsp.language_access;

import arcbasis._symboltable.ArcBasisScopesGenitorP3;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.MontiArcGlobalScope;
import montiarc.check.MontiArcTypeCheck;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MontiArcScopeManager extends MontiArcScopeManagerTOP {

  private OOClass2MCResolver ooClass2MCResolver;
  private MontiArcTool tool;

  @Override
  public void initGlobalScope(MCPath symbolPath) {
    if (ooClass2MCResolver == null) {
      ooClass2MCResolver = new OOClass2MCResolver();
    }

    if (tool == null) {
      tool = new MontiArcTool();
    }

    IMontiArcGlobalScope gs = MontiArcMill.globalScope();
    setGlobalScope((MontiArcGlobalScope) gs);
    gs.setSymbolPath(new MCPath(symbolPath.getEntries().stream().filter(p -> p.toString().endsWith(".jar")).collect(Collectors.toList())));
    ensureAdapterPresent(gs);
    BasicSymbolsMill.initializePrimitives();
    MontiArcTypeCheck.init();
    SymTypeRelations.init();
    MCCollectionSymTypeRelations.init();
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

  protected void ensureAdapterPresent(IMontiArcGlobalScope gs) {
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
        tool.defaultImportTrafo(node, true);
        node.addImportStatement(MontiArcMill.mCImportStatementBuilder()
          .setMCQualifiedName(MontiArcMill.mCQualifiedNameBuilder()
            .setPartsList(List.of("montiarc", "lang"))
            .build())
          .setStar(true)
          .build());
        tool.runAfterParsingTrafos(node);
        var as = tool.createSymbolTable(node);
        res.put(node, new MontiArcArtifactScopeWithFindings(node, as, Log.getFindings()));
      }

      for (ASTMACompilationUnit node : astNodes) {
        Log.clearFindings();
        tool.runSymbolTablePhase2(node);
        tool.runAfterSymbolTablePhase2Trafos(node);
        if (res.containsKey(node)) {
          res.get(node).findings.addAll(Log.getFindings());
        }
      }

      for (ASTMACompilationUnit node : astNodes) {
        Log.clearFindings();
        tool.runSymbolTablePhase3(node);
        if (res.containsKey(node)) {
          res.get(node).findings.addAll(Log.getFindings());
        }
      }
    });

    return res;
  }
}
