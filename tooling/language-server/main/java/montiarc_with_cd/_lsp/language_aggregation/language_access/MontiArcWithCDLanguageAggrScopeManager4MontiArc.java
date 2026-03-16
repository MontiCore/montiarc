/* (c) https://github.com/MontiCore/monticore */
package montiarc_with_cd._lsp.language_aggregation.language_access;

import de.mclsg.lsp.util.LanguageServerContext;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.symbols.basicsymbols._symboltable.ITypeSymbolResolver;
import de.monticore.symbols.oosymbols._symboltable.IFieldSymbolResolver;
import montiarc._symboltable.IMontiArcGlobalScope;

import java.util.List;
import java.util.stream.Collectors;

public class MontiArcWithCDLanguageAggrScopeManager4MontiArc
  extends MontiArcWithCDLanguageAggrScopeManager4MontiArcTOP {

  protected final ITypeSymbolResolver cd4aTypeResolver;
  protected final IFieldSymbolResolver cd4aFieldResolver;
  protected final List<String> primitiveTypes = List.of("void", "boolean", "char", "byte", "short", "int", "long", "float", "double");

  public MontiArcWithCDLanguageAggrScopeManager4MontiArc(
    LanguageServerContext montiArcContext,
    LanguageServerContext cD4CodeContext
  ) {
    super(montiArcContext, cD4CodeContext);
    cd4aTypeResolver = (foundSymbols, name, modifier, predicate) -> {
      try (var context = this.cD4CodeContext.open()) {
        //noinspect globalScopeAccess
        return primitiveTypes.contains(name) ? List.of() : CDTypeAsTypeAdapter.from(CD4AnalysisMill.globalScope().resolveCDTypeMany(foundSymbols, name, modifier, cdts -> predicate.test(new CDTypeAsTypeAdapter(cdts))));
      }
    };
    cd4aFieldResolver = (foundSymbols, name, modifier, predicate) -> {
      try (var context = this.cD4CodeContext.open()) {
        //noinspect globalScopeAccess
        return CD4AnalysisMill.globalScope().resolveFieldMany(foundSymbols, name, modifier).stream().filter(predicate).collect(Collectors.toList());
      }
    };
  }

  @Override
  protected void ensureAdapterPresent(IMontiArcGlobalScope gs) {
    super.ensureAdapterPresent(gs);
    if (!gs.containsAdaptedTypeSymbolResolver(cd4aTypeResolver)) {
      gs.addAdaptedTypeSymbolResolver(cd4aTypeResolver);
    }
    if (!gs.containsAdaptedFieldSymbolResolver(cd4aFieldResolver)) {
      gs.addAdaptedFieldSymbolResolver(cd4aFieldResolver);
    }
  }
}
