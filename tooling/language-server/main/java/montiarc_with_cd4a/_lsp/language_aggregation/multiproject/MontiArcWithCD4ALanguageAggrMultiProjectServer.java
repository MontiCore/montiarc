/* (c) https://github.com/MontiCore/monticore */
package montiarc_with_cd4a._lsp.language_aggregation.multiproject;

import de.monticore.io.paths.MCPath;
import montiarc_with_cd4a._lsp.language_aggregation.modelpath.MontiArcGradleModelPathResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MontiArcWithCD4ALanguageAggrMultiProjectServer extends MontiArcWithCD4ALanguageAggrMultiProjectServerTOP {

  public MontiArcWithCD4ALanguageAggrMultiProjectServer(MCPath modelPath) {
    super(modelPath);
  }

  @Override
  protected void initResolver() {
    Path rootDir = Paths.get(rootUri);
    resolver = new MontiArcGradleModelPathResolver(rootDir);
  }
}
