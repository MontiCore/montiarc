package de.monticore.cd2pojo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.ModelingLanguageFamily;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * see README.md
 *
 * @author Robert Heim
 * 
 */
public class POJOGenerator {
  
  private Path outputDir;
  
  private TypeHelper typeHelper;
  
  private GeneratorSetup generatorSetup;
  
  private GeneratorEngine ge;
  
  private String _package = "";
  
  private CDSymbol cdSymbol;
  
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
    ModelingLanguageFamily fam = new ModelingLanguageFamily();
    fam.addModelingLanguage(lang);
    Scope st = new GlobalScope(new ModelPath(modelPath), fam);
    cdSymbol = st.<CDSymbol> resolve(modelName, CDSymbol.KIND).get();
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
  
  private void generate(CDTypeSymbol type) {
    String kind = type.isClass() ? "class" : (type.isEnum() ? "enum" : "interface");
    
    final StringBuilder _super = new StringBuilder();
    if (type.getSuperClass().isPresent()) {
      _super.append("extends ");
      _super.append(typeHelper.printType(type.getSuperClass().get()));
      _super.append(" ");
    } else if (type.isInterface() && !type.getInterfaces().isEmpty()){
      // Allows extending other interfaces
      _super.append("extends ");
      _super.append(typeHelper.printType(type.getInterfaces().get(0)));
      _super.append(" ");
    }
    if(!type.getInterfaces().isEmpty() && !type.isInterface()){
      _super.append(" ");
      _super.append("implements");
      type.getInterfaces().forEach(i -> {
        _super.append(" ");
        _super.append(typeHelper.printType(i));
        _super.append(",");
      });
      _super.deleteCharAt(_super.length() - 1);
    }

    Path filePath = Paths.get(Names.getPathFromPackage(typeHelper.printType(type)) + ".java");
    
    ge.generate("templates.type.ftl", filePath, type.getAstNode().get(), _package, kind,
        type, _super, typeHelper, cdSymbol.getImports());
  }
}
