/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

public interface IMontiArcTool {

  /**
   * Parses the provided file as a montiarc model. Returns the compilation unit of that model if the file is parsed
   * successfully and else returns an empty optional.
   *
   * @param file the file to parse as montiarc model
   * @return an optional of the compilation unit of the file if parsed successfully, or else {@link Optional#empty()}.
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of montiarc model files.
   */
  Optional<ASTMACompilationUnit> parseMAModel(@NotNull Path file);

  /**
   * Parses the provided file as a class diagram. Returns the compilation unit of that diagram if the file is parsed
   * successfully and else returns an empty optional.
   *
   * @param file the file to parse as class diagram
   * @return an optional of the compilation unit of the file if parsed successfully, or else {@link Optional#empty()}.
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of class diagram model files.
   */
  Optional<ASTCDCompilationUnit> parseCDModel(@NotNull Path file);

  /**
   * Parses the file to the provided file name as a montiarc model. Returns the compilation unit of that model if the
   * file is parsed successfully and else returns an empty optional.
   *
   * @param fileName the name of the file to be parsed as montiarc model
   * @return an optional of the compilation of the file if parsed successfully, or else {@link Optional#empty()}.
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of montiarc model files.
   */
  Optional<ASTMACompilationUnit> parseMAModel(@NotNull String fileName);

  /**
   * Parses the file to the provided file name as a class diagram. Returns the compilation unit of that diagram if the
   * file is parsed successfully and else returns an empty optional.
   *
   * @param fileName the name of the file to parse as class diagram
   * @return an optional of the compilation of the file if parsed successfully, or else {@link Optional#empty()}.
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of class diagram model files.
   */
  Optional<ASTCDCompilationUnit> parseCDModel(@NotNull String fileName);

  /**
   * Parses all montiarc model files in the provided directory and its subdirectories as montiarc models. Returns a
   * collection of compilation units of the successfully parsed models.
   *
   * @param directory the directory containing the files to parse
   * @return a possibly empty collection of compilation units of the successfully parsed montiarc model files
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  Collection<ASTMACompilationUnit> parseMAModels(@NotNull Path directory);

  /**
   * Parses all class diagram model files in the provided directory and its subdirectories as class diagrams. Returns a
   * collection of compilation units of the successfully parsed models.
   *
   * @param directory the directory containing the files to parse
   * @return a possibly empty collection of compilation units of the successfully parsed class diagram model files
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  Collection<ASTCDCompilationUnit> parseCDModels(@NotNull Path directory);

  /**
   * Parses all models in the provided scope as montiarc models. Returns a collection of compilation units of the
   * successfully parsed models.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of compilation units of the successfully parsed files in the provided scope
   */
  Collection<ASTMACompilationUnit> parseModels(@NotNull IMontiArcGlobalScope scope);

  /**
   * Parses all models in the provided scope as class diagrams. Returns a collection of compilation units of the
   * successfully parsed models.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of compilation units of the successfully parsed files in the provided scope
   */
  Collection<ASTCDCompilationUnit> parseModels(@NotNull ICD4CodeGlobalScope scope);

  /**
   * Creates the symbol table to the provided compilation unit and returns its artifact scope.
   *
   * @param ast the compilation unit whose symbol table is to be created
   * @return the created artifact scope to the provided compilation unit
   */
  IMontiArcScope createSymbolTable(@NotNull ASTMACompilationUnit ast);

  /**
   * Creates the symbol table to the provided compilation unit and returns its artifact scope.
   *
   * @param ast the compilation unit whose symbol table is to be created
   * @return the created artifact scope to the provided compilation unit
   */
  ICD4CodeScope createSymbolTable(@NotNull ASTCDCompilationUnit ast);

  /**
   * Parses all models in the provided scope, creates their symbol table, and returns a collection of their artifact
   * scopes.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of artifact scopes of models in the provided global scope
   */
  Collection<IMontiArcScope> createSymbolTable(@NotNull IMontiArcGlobalScope scope);


  /**
   * Parses all models in the provided scope, creates their symbol table, and returns a collection of their artifact
   * scopes.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of artifact scopes of models in the provided global scope
   */
  Collection<ICD4CodeScope> createSymbolTable(@NotNull ICD4CodeGlobalScope scope);

  /**
   * Creates a montiarc global scope with the provided directories as model path and default file ending ('arc'). Also
   * initializes resolving delegates for symbols of class diagrams with default file ending ('cd') and under the
   * assumption that montiarc models and class diagrams share the same model path.
   *
   * @param directories the directories that compose the model path
   * @return the constructed global scope
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  IMontiArcGlobalScope createMAGlobalScope(@NotNull Path... directories);

  /**
   * Creates a class diagram global scope with the provided directories as model path and default file ending ('cd').
   *
   * @param directories the directories that compose the model path
   * @return the constructed global scope
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  ICD4CodeGlobalScope createCDGlobalScope(@NotNull Path... directories);

  /**
   * Creates a montiarc global scope with the provided directories as model path and default file ending ('arc'). Also
   * initializes resolving delegates for symbols of class diagrams for the provided scope.
   *
   * @param cdScope     the scope of class diagram model files
   * @param directories the directories that compose the model path
   * @return the constructed global scope
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  IMontiArcGlobalScope createMAGlobalScope(@NotNull ICD4CodeGlobalScope cdScope, @NotNull Path... directories);

  /**
   * Adds basic types {@link de.monticore.types.check.DefsTypeBasic} to the provided scope.
   *
   * @param scope to scope to be extended with basic types
   */
  void addBasicTypes(@NotNull IMontiArcScope scope);

  /**
   * Checks default context conditions for the provided montiarc compilation unit and logs findings.
   *
   * @param ast the compilation unit whose context conditions are to be checked
   */
  void checkCoCos(@NotNull ASTMACompilationUnit ast);

  /**
   * Checks default context conditions for the provided class diagram compilation unit and logs findings.
   *
   * @param ast the compilation unit whose context conditions are to be checked
   */
  void checkCoCos(@NotNull ASTCDCompilationUnit ast);

  /**
   * Parses all model files in the provided scope, creates their symbol table, checks default context conditions for
   * these and logs findings.
   *
   * @param scope the scope under consideration
   */
  void processModels(@NotNull IMontiArcGlobalScope scope);

  /**
   * Parses all model files in the provided scope, creates their symbol table, checks default context conditions for
   * these and logs findings.
   *
   * @param scope the scope under consideration
   */
  void processModels(@NotNull ICD4CodeGlobalScope scope);

  /**
   * Creates a montiarc global scope with the provided directories as model path and default file ending ('arc), parsers
   * all model files in that scope, creates their symbol table, checks default context conditions for these and logs
   * findings. Also process class diagram model files for the same model path and default file ending.
   *
   * @param directories the directories that compose the model path
   * @return the created global scope
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  IMontiArcGlobalScope processModels(@NotNull Path... directories);
}