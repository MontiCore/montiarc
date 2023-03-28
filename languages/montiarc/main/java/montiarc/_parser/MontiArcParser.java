/* (c) https://github.com/MontiCore/monticore */
package montiarc._parser;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Extends the {@link MontiArcParserTOP} with checks which check whether the name of the parsed
 * component model and its package conform to the file's name and package, respectively.
 */
public class MontiArcParser extends MontiArcParserTOP {

  /**
   * Parses the file behind the string argument as a MontiArc compilation unit. The string argument
   * must conform to the relative path, consisting a model path, package, and filename. The file
   * content must conform to the concrete syntax of MontiArc, as defined by the MontiArc grammar.
   * The parsed file content is returned as an abstract syntax tree with an {@code
   * ASTMACompilationUnit} as its root wrapped by an {@code Optional}. An empty {@code Optional} is
   * returned if a parse error is raised.
   *
   * <p> An exception of type {@code IOException} is thrown if an I/O exception of some sort
   * occurs. Error {@code COMPONENT_AND_FILE_NAME_DIFFER} is raised if component name and file root
   * differ. Error {@code COMPONENT_AND_FILE_PACKAGE_DIFFER} is raised if component package and file
   * package differ.
   *
   * @param file the {@code String} contained the relative path of the file to be parsed
   * @return an {@code Optional} of an abstract syntax tree representing the parsed file
   * @throws IOException if an I/O exception of some sort occurs
   */
  @Override
  public Optional<ASTMACompilationUnit> parseMACompilationUnit(@NotNull String file) throws IOException {
    Preconditions.checkNotNull(file);
    Optional<ASTMACompilationUnit> ast = super.parseMACompilationUnit(file);
    if (ast.isPresent()) {
      String fRoot = Files.getNameWithoutExtension(file);
      String mName = ast.get().getComponentType().getName();
      String fPkg = Names.getPathFromFilename(file);
      String mPkg = Names.constructQualifiedName(ast.get().isPresentPackage() ?
        ast.get().getPackage().getPartsList() : new ArrayList<>());
      if (!mName.equals(fRoot)) {
        Log.error(String.format(MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER.toString(), mName, fRoot),
          ast.get().getComponentType().get_SourcePositionStart()
        );
        setError(true);
      }
      if (!Names.getPackageFromPath(fPkg).endsWith(mPkg)) {
        Log.error(String.format(MontiArcError.COMPONENT_AND_FILE_PACKAGE_DIFFER.toString(), mPkg, fPkg),
          ast.get().isPresentPackage() ?
            ast.get().getPackage().get_SourcePositionStart() :
            ast.get().getComponentType().get_SourcePositionStart()
        );
        setError(true);
      }
    }
    if (hasErrors()) {
      return Optional.empty();
    }
    return ast;
  }

  @Override
  public Optional<ASTMACompilationUnit> parse(@NotNull String file) throws IOException {
    Preconditions.checkNotNull(file);
    return this.parse(Paths.get(file));
  }

  public Optional<ASTMACompilationUnit> parse(@NotNull Path file) throws IOException {
    Preconditions.checkNotNull(file);
    Preconditions.checkArgument(file.toFile().exists());
    Preconditions.checkArgument(file.toFile().isFile());

    return parseMACompilationUnit(file.toString());
  }
}
