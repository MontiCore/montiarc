/* (c) https://github.com/MontiCore/monticore */
package mceffect._parser;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import mceffect._ast.ASTMCEffect;

public class MCEffectParser extends MCEffectParserTOP {
  boolean _checkFileAndPackageName = false;

  public static void checkFileAndPackageName(String fileName, ASTMCEffect ast) {
    String pathName = Paths.get(fileName).toString();

    String packageName = Names.getPackageFromPath(Names.getPathFromFilename(pathName));
    String packageDeclaration;
    if (ast.isPresentMCPackageDeclaration()) {
      packageDeclaration = ast.getMCPackageDeclaration().getMCQualifiedName().getQName();
    } else {
      packageDeclaration = "";
    }

    if (!packageName.endsWith(packageDeclaration)) {
      Log.error(
          String.format(
              "0xEFF001: The package declaration %s"
                  + " of the diagram (%s) must not differ from the"
                  + " package of the diagram file.",
              packageDeclaration, fileName),
          ast.isPresentMCPackageDeclaration()
              ? ast.getMCPackageDeclaration().get_SourcePositionStart()
              : ast.get_SourcePositionStart());
    }
  }

  @Override
  public Optional<ASTMCEffect> parse(String fileName) throws IOException {

    final Optional<ASTMCEffect> parse = super.parse(fileName);

    if (_checkFileAndPackageName) {
      parse.ifPresent(p -> checkFileAndPackageName(fileName, p));
    }
    return parse;
  }
}
