package de.monticore.lang.montiarc.montiarc._symboltable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static de.monticore.lang.montiarc.montiarc._symboltable.MontiArcLanguage.TAG_FILE_ENDING;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit;
import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.montiarc.tagging._parser.TaggingParser;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

/**
 * Created by Michael von Wenckstern on 30.05.2016.
 *
 * @author Michael von Wenckstern
 */
public class MontiArcModelLoader extends MontiArcModelLoaderTOP {

  public MontiArcModelLoader(MontiArcLanguage language) {
    super(language);
  }

  /**
   * this method should be implemented into the ModelPath class
   */
  private static Collection<Path> getEntriesFromModelPath(ModelPath modelPath) {
    String s = modelPath.toString().replace("[", "").replace("]", "").replace(" ", "");
    String ss[] = s.split(",");
    return Arrays.stream(ss).map(str -> Paths.get(URI.create(str))).collect(Collectors.toSet());
  }

  @Override
  public Collection<ASTMACompilationUnit> loadModelsIntoScope(final String qualifiedModelName,
      final ModelPath modelPath, final MutableScope enclosingScope,
      final ResolverConfiguration resolverConfiguration) {

    final Collection<ASTMACompilationUnit> asts = loadModels(qualifiedModelName, modelPath);

    for (ASTMACompilationUnit ast : asts) {
      createSymbolTableFromAST(ast, qualifiedModelName, enclosingScope, resolverConfiguration);

      // load tags of ast
      for (ASTTaggingUnit unit : loadTags(ast.getPackage(), modelPath)) {
        this.getModelingLanguage().getTagSymbolCreators().stream()
            .forEachOrdered(tc -> tc.create(unit, enclosingScope));
      }
    }

    return asts;
  }

  protected Collection<ASTTaggingUnit> loadTags(final List<String> packageName, final ModelPath modelPath) {
    // TODO use File.separator instead of "\\" or "/"
    String qualifiedModelName = Joiners.DOT.join(packageName);
    checkArgument(!isNullOrEmpty(qualifiedModelName));

    final Collection<ASTTaggingUnit> foundModels = new ArrayList<>();
    for (Path mp : getEntriesFromModelPath(modelPath)) {
      for (String pN : packageName) {
        final Path completePath = Paths.get(mp.toString(), pN);
        final File f = completePath.toFile();
        if (f != null && f.isDirectory()) {
          List<String> tagFiles = Arrays.stream(f.listFiles())
              .filter(s -> s.isFile())
              .map(s -> s.getPath())
              .filter(s -> s.endsWith(TAG_FILE_ENDING))
              .collect(Collectors.toList());

          tagFiles.stream().forEachOrdered(t -> {
            final TaggingParser parser = new TaggingParser();
            Optional<ASTTaggingUnit> ast = Optional.empty();
            try {
              ast = parser.parse(t);
            }
            catch (IOException e) {
              Log.error("could not open file " + t, e);
            }
            if (ast.isPresent()) {
              if (!completePath.endsWith(
                  ast.get().getPackage().stream().collect(Collectors.joining(File.separator)))) {
                Path p = Paths.get(t);
                String expectedPackage = mp.toUri().relativize(p.toUri()).getPath();
                if (p.getParent() != null) {
                  expectedPackage = mp.toUri().relativize(p.getParent().toUri()).getPath();
                }
                expectedPackage = expectedPackage.replace(File.separator, ".").replace("/", ".");
                if (expectedPackage.endsWith(".")) {
                  expectedPackage = expectedPackage.substring(0, expectedPackage.length() - 1);
                }
                Log.error(
                    String.format("0xAC050 package name in '%s' is wrong. package name is '%s' but should be '%s'",
                        t, Joiners.DOT.join(ast.get().getPackage()), expectedPackage));
              }
              else {
                foundModels.add(ast.get());
              }
            }
          });
        }
      }
    }
    return foundModels;
  }
}
