/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

@Deprecated
public interface IMontiArcTool {

  /**
   * Parses the provided file as a montiarc model. Returns the compilation unit of that model if the file is parsed
   * successfully.
   *
   * @param file the file to parse as montiarc model
   * @return the compilation unit of the file if parsed successfully
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of montiarc model files.
   */
  Optional<ASTMACompilationUnit> parse(@NotNull Path file);

  /**
   * Loads the provided file as a serialized montiarc model. Returns the artifact scope of that model if the file is
   * deserialized successfully.
   *
   * @param file the file to load as a serialized montiarc model
   * @return the artifact scope of the file if deserialized successfully
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of serialized model files.
   */
  IMontiArcArtifactScope load(@NotNull Path file);

  /**
   * Parses the file to the provided file name as a montiarc model. Returns the compilation unit of that model if the
   * file is parsed successfully.
   *
   * @param fileName the name of the file to parse as montiarc model
   * @return the compilation of the file if parsed successfully
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of montiarc model files.
   */
  Optional<ASTMACompilationUnit> parse(@NotNull String fileName);

  /**
   * Loads the file to the provided file name as a serialized montiarc model. Returns the artifact scope of that model
   * if the file is deserialized successfully.
   *
   * @param fileName the name of the file to load as serialized montiarc model
   * @return the artifact scope of the file if deserialized successfully
   * @throws IllegalArgumentException if the provided file does not exist, is not a regular file, or if its file
   *                                  extension does not match the default file extension of serialized model files.
   */
  IMontiArcArtifactScope load(@NotNull String fileName);

  /**
   * Parses all montiarc model files in the provided directory and its subdirectories as montiarc models. Returns a
   * collection of compilation units of the successfully parsed models.
   *
   * @param directory the directory containing the files to parse
   * @return a possibly empty collection of compilation units of the successfully parsed montiarc model files
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  Collection<ASTMACompilationUnit> parseAll(@NotNull Path directory);

  /**
   * Loads all serialized model files in the provided directory and its subdirectories as serialized montiarc models.
   * Returns a collection of artifact scopes of the successfully deserialized models.
   *
   * @param directory the directory containing the files to load
   * @return a possibly empty collection of artifact scopes of the successfully deserialized model files
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  Collection<IMontiArcArtifactScope> loadAll(@NotNull Path directory);

  /**
   * Parses all models in the provided scope as montiarc models. Returns a collection of compilation units of the
   * successfully parsed models.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of compilation units of the successfully parsed models in the provided scope
   */
  Collection<ASTMACompilationUnit> parseAll(@NotNull IMontiArcGlobalScope scope);

  /**
   * Loads all models in the provided scope as serialized montiarc models. Returns a collection of artifact scopes of
   * the successfully deserialized models.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of artifact scopes the successfully deserialized models in the provided scope
   */
  Collection<IMontiArcArtifactScope> loadAll(@NotNull IMontiArcGlobalScope scope);

  /**
   * Creates the symbol table to the provided compilation unit and returns its artifact scope.
   *
   * @param ast the compilation unit whose symbol table is to be created
   * @return the created artifact scope to the provided compilation unit
   */
  IMontiArcScope createSymbolTable(@NotNull ASTMACompilationUnit ast);

  /**
   * Loads all models in the provided scope, creates their symbol table, and returns a collection of their artifact
   * scopes.
   *
   * @param scope the scope under consideration
   * @return a possibly empty collection of artifact scopes of models in the provided global scope
   */
  Collection<IMontiArcScope> createSymbolTable(@NotNull IMontiArcGlobalScope scope);

  /**
   * Loads all models in the provided scope, creates their symbol table, and returns a collection of their artifact
   * scopes.
   *
   * @param directory the directory containing the files to load
   * @return a possibly empty collection of artifact scopes of models in the provided directory
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  Collection<IMontiArcScope> createSymbolTable(@NotNull Path directory);

  /**
   * Creates a montiarc global scope with the provided directories as model path and default file ending ('arc').
   *
   * @param directories the directories that compose the model path
   * @return the constructed global scope
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  IMontiArcGlobalScope createMAGlobalScope(@NotNull Path... directories);

  /**
   * Creates types available without import and adds those to the global scope, including primitive types, Object,
   * String, and Integer.
   */
  void initializeBasicTypes();

  /**
   * Creates object types available without import and adds those to the global scope, including Object, String,
   * and Integer.
   */
  void initializeBasicOOTypes();

  /**
   * Checks default context conditions for the provided montiarc compilation unit and logs findings.
   *
   * @param ast the compilation unit whose context conditions are to be checked
   */
  void checkCoCos(@NotNull ASTMACompilationUnit ast);

  /**
   * Loads all model files in the provided scope, creates their symbol table, checks default context conditions for
   * these and logs findings.
   *
   * @param scope the scope under consideration
   */
  void processModels(@NotNull IMontiArcGlobalScope scope);

  /**
   * Creates a montiarc global scope with the provided directories as model path and default file ending ('arc), loads
   * all model files in that scope, creates their symbol table, checks default context conditions for these and logs
   * findings. Also loads serialized montiarc model files in the same model path.
   *
   * @param directories the directories that compose the model path
   * @return the created global scope
   * @throws IllegalArgumentException if the location to given path does not exist, or is not a directory.
   */
  IMontiArcGlobalScope processModels(@NotNull Path... directories);
}