/* (c) https://github.com/MontiCore/monticore */
package montiarc_with_cd._lsp.language_aggregation.multiproject;

import de.monticore.io.paths.MCPath;
import montiarc_with_cd._lsp.language_aggregation.modelpath.MontiArcGradleModelPathResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MontiArcWithCDLanguageAggrMultiProjectServer extends MontiArcWithCDLanguageAggrMultiProjectServerTOP {

  public MontiArcWithCDLanguageAggrMultiProjectServer(MCPath modelPath) {
    super(modelPath);
  }

  @Override
  protected void initResolver() {
    Path rootDir = Paths.get(rootUri);
    resolver = new MontiArcGradleModelPathResolver(rootDir);
  }
}
