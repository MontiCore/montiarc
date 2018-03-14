
package montiarc._parser;

import java.io.IOException;
import java.util.Optional;

import com.google.common.io.Files;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;

public class MontiArcParser extends MontiArcParserTOP {
  
  /**
   * Besides parsing, this also checks that the filename equals the model name and the package
   * declaration equals the suffix of the package name of the model.
   */
  @Override
  public Optional<ASTMACompilationUnit> parseMACompilationUnit(String filename)
      throws IOException {
    Optional<ASTMACompilationUnit> ast = super.parseMACompilationUnit(filename);
    if (ast.isPresent()) {
      String simpleFileName = Files.getNameWithoutExtension(filename);
      String modelName = ast.get().getComponent().getName();
      String packageName = Names.getPackageFromPath(Names.getPathFromFilename(filename));
      String packageDeclaration = Names.getQualifiedName(ast.get().getPackageList());
      if (!modelName.equals(simpleFileName)) {
        Log.error("0xMA256 The name of the component " + modelName
            + " is not identical to the name of the file in file '" + filename
            + "' (without its fileextension).");
      }
      if (!packageName.endsWith(packageDeclaration)) {
        Log.error("0xMA257 The package declaration " + packageDeclaration
            + " of the component must not differ from the "
            + "package of the component file in file '" + filename + "'.");
      }
      
    }
    return ast;
  }
  
}
