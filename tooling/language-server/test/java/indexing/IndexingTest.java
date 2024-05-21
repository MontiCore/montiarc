/* (c) https://github.com/MontiCore/monticore */
package indexing;

import de.mclsg.lsp.util.AsyncUtilWithSyncExec;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import montiarc._lsp.MontiArcLanguageServerBuilder;
import org.eclipse.lsp4j.InitializedParams;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import util.MockLanguageClient;

import static org.junit.jupiter.api.Assertions.*;

public class IndexingTest {

  @Test
  public void testTypesAfterIndexing(){
    AsyncUtilWithSyncExec.init();
    var languageServer = new MontiArcLanguageServerBuilder()
        .modelPath(new MCPath("test/resources/indexing/")).build();

    languageServer.connect(new MockLanguageClient());
    languageServer.initialized(new InitializedParams());

    languageServer.getLanguageAccess().getScopeManager().syncAccessGlobalScope(globalScope -> {
      assertEquals(2, globalScope.getSubScopes().size());
      var subscopeOpt = globalScope.getSubScopes().stream().filter(s -> "A".equals(s.getName())).findFirst();
      assertTrue(subscopeOpt.isPresent());
      var subscope = subscopeOpt.get();

      SubcomponentSymbol instanceSymbol = subscope.getSubScopes().get(0).getLocalSubcomponentSymbols().get(0);
      assertEquals("b", instanceSymbol.getName());
      assertTrue(instanceSymbol.isTypePresent());
      assertEquals("B", instanceSymbol.getType().getTypeInfo().getName());
    });

  }
}
