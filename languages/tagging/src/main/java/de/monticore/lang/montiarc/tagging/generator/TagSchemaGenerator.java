package de.monticore.lang.montiarc.tagging.generator;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.ITemplateControllerFactory;
import de.monticore.io.FileReaderWriter;
import de.monticore.lang.montiarc.tagging.helper.UnitKinds;
import de.monticore.lang.montiarc.tagschema._ast.ASTSimpleTagType;
import de.monticore.lang.montiarc.tagschema._ast.ASTTagSchemaUnit;
import de.monticore.lang.montiarc.tagschema._ast.ASTTagType;
import de.monticore.lang.montiarc.tagschema._ast.ASTValuedTagType;
import de.monticore.lang.montiarc.tagschema._parser.TagSchemaParser;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

/**
 * Created by Michael von Wenckstern on 09.06.2016.
 */
public class TagSchemaGenerator extends GeneratorEngine {

  public TagSchemaGenerator(GeneratorSetup generatorSetup, ITemplateControllerFactory templateControllerFactory, FileReaderWriter fileHandler) {
    super(generatorSetup, templateControllerFactory, fileHandler);
  }

  public TagSchemaGenerator(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }

  public static Path getPathFromRelativePath(Path relPath) throws Exception {
    return Paths.get(URLClassLoader.newInstance(new URL[] { relPath.toUri().toURL() }).getURLs()[0].toURI());
  }

  public static void generate(String tagSchemaLocation, String modelPath, String outputFolder) {

    try {

      GeneratorSetup setup = new GeneratorSetup(
          getPathFromRelativePath(Paths.get(outputFolder)).toFile());
      setup.setTracing(true);
      TagSchemaGenerator generator = new TagSchemaGenerator(setup);

      Map<String, String> symbolScopeMap = new LinkedHashMap<>();
      symbolScopeMap.put("Component", "NameScope");
      symbolScopeMap.put("Port", "NameScope");
      symbolScopeMap.put("ExpandedComponentInstance", "NameScope");
      symbolScopeMap.put("Connector", "ConnectorScope");

      List<String> list = Splitters.DOT.splitToList(tagSchemaLocation);
      Path pathTagschema = Paths.get(list.get(0));
      for (int i = 1; i < list.size(); i++) {
        pathTagschema = pathTagschema.resolve(list.get(i));
      }

      generator.generate(pathTagschema, Paths.get(modelPath), symbolScopeMap);

    } catch (Exception e) {
      Log.error("Error during TagSchemaGenerator", e);
    }
  }

      public void generate(Path tagSchemaLocation, Path modelPath, Map<String, String> symbolScopeMap) {
    try {
      _generate(tagSchemaLocation, modelPath, symbolScopeMap);
    }
    catch (Exception e) {
      Log.error("An error occured during the tag schema generation process", e);
    }
  }

  protected void _generate(Path tagSchemaLocation, Path modelPath, Map<String, String> symbolScopeMap)
      throws Exception {
    TagSchemaParser parser = new TagSchemaParser();
    ASTTagSchemaUnit tagSchemaUnit = Log.errorIfNull(parser.parse(
        modelPath.resolve(tagSchemaLocation).toString() +
            (tagSchemaLocation.endsWith(".tagschema") ? "" : ".tagschema")
    ).orElse(null),
        String.format("Could not load tagschema '%s'", tagSchemaLocation.toString()));

    List<String> tagTypeNames = new ArrayList<>();
    String packageName = Joiners.DOT.join(tagSchemaUnit.getPackage());
    List<ASTTagType> tagTypes = tagSchemaUnit.getTagTypes();
    for (ASTTagType tagType : tagTypes) {
      generateTagType(tagType, tagSchemaUnit, packageName, symbolScopeMap);
      tagTypeNames.add(tagType.getName());
    }

    generateTagSchema(tagSchemaUnit, packageName, tagTypeNames);
  }

  public void generateTagSchema(ASTTagSchemaUnit tagSchemaUnit, String packageName, List<String> tagTypeNames) {
    generate("templates.de.monticore.lang.montiarc.tagschema.TagSchema",
        Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), tagSchemaUnit.getName() + ".java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), tagTypeNames);
  }

  protected void generateTagType(ASTTagType tagType, ASTTagSchemaUnit tagSchemaUnit, String packageName, Map<String, String> symbolScopeMap) {
    if (tagType instanceof ASTSimpleTagType) {
      generateSimpleTagType((ASTSimpleTagType) tagType, tagSchemaUnit, packageName, symbolScopeMap);
    }
    else if (tagType instanceof ASTValuedTagType) {
      generateValuedTagType((ASTValuedTagType) tagType, tagSchemaUnit, packageName, symbolScopeMap);
    }
  }

  protected void generateValuedTagType(ASTValuedTagType valuedTagType, ASTTagSchemaUnit tagSchemaUnit, String packageName, Map<String, String> symbolScopeMap) {
    String dataType = null;
    boolean isUnit = false;
    if (valuedTagType.getBoolean().isPresent()) {
      dataType = "Boolean";
    }
    else if (valuedTagType.getNumber().isPresent()) {
      dataType = "Number";
    }
    else if (valuedTagType.getString().isPresent()) {
      dataType = "String";
    }
    else if (valuedTagType.getUnitKind().isPresent()) {
      isUnit = true;
      dataType = valuedTagType.getUnitKind().get();
      if (!UnitKinds.contains(dataType)) {
        Log.error(String.format("Unit kind '%s' is not supported. Currently the following unit kinds are available '%s'",
            dataType, UnitKinds.available()), valuedTagType.get_SourcePositionStart());
        return;
      }
    }
    Log.errorIfNull(dataType, "Not supported data type in generator");
    generate("templates.de.monticore.lang.montiarc.tagschema.ValuedTagType",
        Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), valuedTagType.getName() + "Symbol.java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), valuedTagType.getName(), dataType, isUnit);
    String importSymbols = "de.monticore.lang.montiarc.montiarc._symboltable.*";
    String scopeSymbol = valuedTagType.getScope().get().getScopeIdentifiers().get(0).getScopeName();
    String nameScopeType = Log.errorIfNull(symbolScopeMap.get(scopeSymbol), String.format("For the scope symbol '%s' is no scope type defined.", scopeSymbol));
    generate("templates.de.monticore.lang.montiarc.tagschema.ValuedTagTypeCreator",
        Paths.get(createPackagePath(packageName).toString(),tagSchemaUnit.getName(),  valuedTagType.getName() + "SymbolCreator.java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), valuedTagType.getName(), importSymbols, scopeSymbol + "Symbol", nameScopeType, dataType, isUnit);
  }

  public static Path createPackagePath(String packageName) {
    List<String> parts = Splitters.DOT.splitToList(packageName);
    Path p = Paths.get(parts.get(0));
    for (int i = 1; i < parts.size(); i++) {
      p = Paths.get(p.toString(), parts.get(i));
    }
    return p;
  }

  protected void generateSimpleTagType(ASTSimpleTagType simpleTagType, ASTTagSchemaUnit tagSchemaUnit,
      String packageName, Map<String, String> symbolScopeMap) {
    generate("templates.de.monticore.lang.montiarc.tagschema.SimpleTagType",
        Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), simpleTagType.getName() + "Symbol.java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), simpleTagType.getName());
    String importSymbols = "de.monticore.lang.montiarc.montiarc._symboltable.*";
    String scopeSymbol = simpleTagType.getScope().get().getScopeIdentifiers().get(0).getScopeName();
    String nameScopeType = Log.errorIfNull(symbolScopeMap.get(scopeSymbol), String.format("For the scope symbol '%s' is no scope type defined.", scopeSymbol));
    generate("templates.de.monticore.lang.montiarc.tagschema.SimpleTagTypeCreator",
        Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), simpleTagType.getName() + "SymbolCreator.java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), simpleTagType.getName(), importSymbols, scopeSymbol + "Symbol", nameScopeType);
  }
}
