/* (c) https://github.com/MontiCore/monticore */
package montiarc._parser;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;

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
   * @param relativeFilePath the {@code String} contained the relative path of the file to be parsed
   * @return an {@code Optional} of an abstract syntax tree representing the parsed file
   * @throws IOException if an I/O exception of some sort occurs
   */
  @Override
  public Optional<ASTMACompilationUnit> parseMACompilationUnit(@NotNull String relativeFilePath)
    throws IOException {
    Preconditions.checkNotNull(relativeFilePath);
    Optional<ASTMACompilationUnit> optAst = super.parseMACompilationUnit(relativeFilePath);
    if (optAst.isPresent()) {
      String fileRoot = Files.getNameWithoutExtension(relativeFilePath);
      String modelName = optAst.get().getComponentType().getName();
      String packageOfFile = Names.getPathFromFilename(relativeFilePath);
      String packageOfModel = Names.constructQualifiedName(optAst.get().isPresentPackage() ?
        optAst.get().getPackage().getPartsList() : new ArrayList<>());
      if (!modelName.equals(fileRoot)) {
        Log.error(String.format(MontiArcError.COMPONENT_AND_FILE_NAME_DIFFER.toString(),
            modelName, fileRoot),
          optAst.get().isPresentPackage() ? optAst.get().getPackage().get_SourcePositionStart()
            : optAst.get().get_SourcePositionStart());
        setError(true);
      }
      if (!Names.getPackageFromPath(packageOfFile).endsWith(packageOfModel)) {
        Log.error(String.format(MontiArcError.COMPONENT_AND_FILE_PACKAGE_DIFFER.toString(),
            packageOfModel, packageOfFile),
          optAst.get().isPresentPackage() ? optAst.get().getPackage().get_SourcePositionStart()
            : optAst.get().get_SourcePositionStart());
        setError(true);
      }
    }
    if (hasErrors()) {
      return Optional.empty();
    }
    return optAst;
  }

  /**
   * @see MontiArcParser#parseMACompilationUnit(String)
   */
  @Override
  public Optional<ASTMACompilationUnit> parse(@NotNull String relativeFilePath) throws IOException {
    Preconditions.checkNotNull(relativeFilePath);
    String nonUriPath = relativeFilePath.replaceAll("/", Matcher.quoteReplacement(File.separator));
    return parseMACompilationUnit(nonUriPath);
  }

}
