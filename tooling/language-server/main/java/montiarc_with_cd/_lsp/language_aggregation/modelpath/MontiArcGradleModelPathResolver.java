/* (c) https://github.com/MontiCore/monticore */
package montiarc_with_cd._lsp.language_aggregation.modelpath;

import de.mclsg.lsp.modelpath.ModelPathCache;
import de.mclsg.lsp.modelpath.ModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLSPConfigModelPathCache;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLanguageServerFileModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLanguageServerGradleModelPathResolver;
import de.mclsg.lsp.modelpath.multiproject.MultiProjectLayout;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MontiArcGradleModelPathResolver extends ModelPathResolver<MultiProjectLayout> {

  public MontiArcGradleModelPathResolver(Path rootDir) {
    super(
      rootDir,
      List.of(
        new MultiProjectLanguageServerGradleModelPathResolver(rootDir) {
          @Override
          protected CompletableFuture<MultiProjectLayout> loadModelPaths() {
            gradleVersion = "8.14.4";
            gradleTaskName = "aggregateAndProcessInfo";
            minimumJavaVersion = 21;
            maximumJavaVersion = 24;
            return super.loadModelPaths();
          }

          @Override
          protected Path createTempInitScript() {
            return super.createTempInitScript("/init.gradle", DEFAULT_DATA_STRUCTURE_RESOURCE);
          }
        },
        new MultiProjectLanguageServerFileModelPathResolver(rootDir)
      ),
      new MultiProjectLSPConfigModelPathCache(ModelPathCache.getDefaultCacheDir(rootDir), "montiarc-config.json")
    );
  }
}
