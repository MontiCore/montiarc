/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.IterablePath;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class POJOGenerator {
  
  protected static String FILE_EXTENSION = ".java";
  
  protected GeneratorEngine engine;
  protected IterablePath hwcPath;
  
  public POJOGenerator(@NotNull GeneratorEngine engine, @NotNull IterablePath hwcPaths) {
    Preconditions.checkNotNull(engine);
    Preconditions.checkNotNull(hwcPaths);
    this.engine = engine;
    this.hwcPath = hwcPaths;
  }
  
  public POJOGenerator(@NotNull GeneratorSetup setup) {
    this(new GeneratorEngine(Preconditions.checkNotNull(setup)), setup.getHandcodedPath());
  }
  
  public POJOGenerator(@NotNull Path targetDir, @NotNull Path hwcPath) {
    this(createGeneratorSetup(Preconditions.checkNotNull(targetDir), Preconditions.checkNotNull(hwcPath)));
    this.hwcPath = IterablePath.from(hwcPath.toFile(), "java");
  }
  
  protected static GeneratorSetup createGeneratorSetup(@NotNull Path targetDir, @NotNull Path hwcPath) {
    Preconditions.checkNotNull(targetDir);
    Preconditions.checkNotNull(hwcPath);
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir.toFile());
    setup.setHandcodedPath(IterablePath.from(hwcPath.toFile(), "java"));
    return setup;
  }
  
  public void generate(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    Boolean existsHWC = existsHWC(typeSymbol);
    String kind = "";
    if (typeSymbol.isIsClass()) {
      if (existsHWC) {
        kind = "abstract ";
      }
      kind += "class";
    } else if (typeSymbol.isIsEnum()) {
      kind = "enum";
    } else {
      kind = "interface";
    }
    
    final StringBuilder _super = new StringBuilder();

    if (typeSymbol.isPresentSuperClass()) {
      _super.append("extends ");
      _super.append(typeSymbol.getSuperClass().getTypeInfo().getFullName());
      _super.append(" ");
    } else if (typeSymbol.isIsInterface() && !typeSymbol.getInterfaceList().isEmpty()) {
      // Allows extending other interfaces
      _super.append("extends ");
      _super.append(typeSymbol.getInterfaceList().get(0).getTypeInfo().getFullName());
      _super.append(" ");
    }
    if (!typeSymbol.getInterfaceList().isEmpty() && !typeSymbol.isIsInterface()) {
      _super.append("implements ");
      _super.append(typeSymbol.getInterfaceList().stream()
        .map(i -> i.getTypeInfo().getFullName())
        .collect(Collectors.joining(", ")));
    }
    
    String _package = typeSymbol.getFullName().substring(0, typeSymbol.getFullName().lastIndexOf("."));
    engine.generateNoA("templates.Type.ftl", getFileAsPath(typeSymbol),
      _package, kind, typeSymbol, _super, new TemplateHelper(), existsHWC);
  }
  
  protected Boolean existsHWC(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    return hwcPath.exists(Paths.get(
      typeSymbol.getFullName().replaceAll("\\.", "/") + FILE_EXTENSION));
    
  }
  
  protected Path getFileAsPath(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    Boolean existsHWC = existsHWC(typeSymbol);
    return Paths.get(typeSymbol.getFullName().replaceAll("\\.", "/") + (existsHWC? "TOP":"") + FILE_EXTENSION);
  }
}