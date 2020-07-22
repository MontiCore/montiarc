/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.ModelPath;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * see README.md
 *
 * @author Robert Heim
 */
public class POJOGenerator {
  
  private static final String[] primitiveTypes = { "boolean", "byte", "char", "double", "float",
    "int", "long", "short", "String" };
  
  private Path outputDir;

  private TypeHelper typeHelper;

  private GeneratorSetup generatorSetup;

  private GeneratorEngine ge;

  private String _package = "";

  private CDDefinitionSymbol cdSymbol;

  public POJOGenerator(Path outputDir, Path modelPath, String modelName) {
    this(outputDir, modelPath, modelName, Optional.empty());
  }

  public POJOGenerator(Path outputDir, Path modelPath, String modelName, String targetPackage) {
    this(outputDir, modelPath, modelName, Optional.of(targetPackage));
  }

  private POJOGenerator(
    Path outputDir,
    Path modelPath,
    String modelName,
    Optional<String> targetPackage) {
    this.outputDir = outputDir;

    CD4AnalysisLanguage lang = new CD4AnalysisLanguage();
    ICD4AnalysisScope st = new CD4AnalysisGlobalScope(new ModelPath(modelPath), lang);
    st = injectPrimitives(st);
    cdSymbol = st.resolveCDDefinition(modelName).orElse(null);
    _package = targetPackage.orElse(cdSymbol.getName().toLowerCase());
    
    this.typeHelper = new TypeHelper(_package);
    this.generatorSetup = new GeneratorSetup();
    this.generatorSetup.setOutputDirectory(this.outputDir.toFile());
    this.ge = new GeneratorEngine(this.generatorSetup);
  }

  public void generate() {
    cdSymbol.getTypes().forEach(t -> generate(t));
    Log.info("Done.", "Generator");
  }
  
  protected ICD4AnalysisScope injectPrimitives(ICD4AnalysisScope scope) {
    for (String primitive : primitiveTypes) {
      CDTypeSymbol primitiveCdType = new CDTypeSymbol(primitive);
      scope.add(primitiveCdType);
    }
    return scope;
  }

  private void generate(CDTypeSymbol type) {
    String kind = type.isIsClass() ? "class" : (type.isIsEnum() ? "enum" : "interface");

    final StringBuilder _super = new StringBuilder();
    if (type.isPresentSuperClass()) {
      _super.append("extends ");
      _super.append(typeHelper.printType(type.getSuperClass().getLoadedSymbol()));
      _super.append(" ");
    } else if (type.isIsInterface() && !type.getCdInterfaceList().isEmpty()) {
      // Allows extending other interfaces
      _super.append("extends ");
      _super.append(typeHelper.printType(type.getCdInterface(0).getLoadedSymbol()));
      _super.append(" ");
    }
    if (!type.getCdInterfaceList().isEmpty() && !type.isIsInterface()) {
      _super.append(" ");
      _super.append("implements");
      type.getCdInterfaceList().forEach(i -> {
        _super.append(" ");
        _super.append(typeHelper.printType(i.getLoadedSymbol()));
        _super.append(",");
      });
      _super.deleteCharAt(_super.length() - 1);
    }

    Path filePath = Paths.get(Names.getPathFromPackage(typeHelper.printType(type)) + ".java");

    // Hack to at least correctly generate java.lang.*
    // Will not work with packages that start with upper case letters
    List<String> imports = new ArrayList<>();
    for (String anImport : cdSymbol.getImports()) {
      final String[] split = anImport.split("\\.");
      if (split.length > 0) {
        final char firstOfLastElement = split[split.length - 1].charAt(0);
        if (Character.isLowerCase(firstOfLastElement)) {
          imports.add(anImport + ".*");
        } else {
          imports.add(anImport);
        }
      }
    }

    ge.generate("templates.type.ftl", filePath, type.getAstNode(), _package, kind,
      type, _super, typeHelper, imports);
  }
}
