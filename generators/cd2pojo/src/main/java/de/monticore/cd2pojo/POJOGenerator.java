/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.monticore.cd4code._symboltable.CD4CodeArtifactScope;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IGlobalScope;
import de.monticore.symboltable.IScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class POJOGenerator {
  
  protected static String FILE_EXTENSION = ".java";
  
  protected GeneratorEngine engine;
  protected MCPath hwcPath;
  protected File outputDirectory;
  
  public POJOGenerator(@NotNull GeneratorSetup setup) {
    Preconditions.checkNotNull(setup);
    this.engine = new GeneratorEngine(setup);
    this.hwcPath = setup.getHandcodedPath();
    outputDirectory = setup.getOutputDirectory();
  }
  
  public POJOGenerator(@NotNull Path targetDir, @NotNull Path hwcPath) {
    this(createGeneratorSetup(Preconditions.checkNotNull(targetDir), Preconditions.checkNotNull(hwcPath)));
    this.hwcPath = new MCPath(hwcPath);
  }
  
  protected static GeneratorSetup createGeneratorSetup(@NotNull Path targetDir, @NotNull Path hwcPath) {
    Preconditions.checkNotNull(targetDir);
    Preconditions.checkNotNull(hwcPath);
    GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir.toFile());
    setup.setHandcodedPath(new MCPath(hwcPath));
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

    String _package = CDWorkaroundsForCD2POJOGenerator.getFullNameWorkaround(typeSymbol)
      .substring(0, typeSymbol.getFullName().lastIndexOf("."));
    engine.generateNoA("templates.Type.ftl", getFileAsPath(typeSymbol),
      _package, kind, typeSymbol, _super, new TemplateHelper(), existsHWC);
  }

  /**
   * @return true if there is an handwritten version of this symbol
   */
  protected Boolean existsHWC(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    return hwcPath.find(getPathFor(typeSymbol, false).toString()).isPresent();
  }
  
  protected Path getFileAsPath(@NotNull CDTypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    return getPathFor(typeSymbol, existsHWC(typeSymbol));
  }

  /**
   *
   * @param typeSymbol type for which the path should be fetched
   * @param addTopExtension true if there should be a top-file-exension added
   * @return path where to locate the type's file
   */
  protected Path getPathFor(@NotNull CDTypeSymbol typeSymbol, boolean addTopExtension){
    Preconditions.checkNotNull(typeSymbol);
    return Paths.get(CDWorkaroundsForCD2POJOGenerator.getFullNameWorkaround(typeSymbol)
                         .replaceAll("\\.", "/") + (addTopExtension ? "TOP" : "") + FILE_EXTENSION);
  }
  
  // TODO remove when CD4A symbol table is fixed
  static class CDWorkaroundsForCD2POJOGenerator {
    
    public static void doAllWorkarounds(CDTypeSymbol typeSymbol) {
      setProperFullName(typeSymbol); //this also ensures that the proper package name is set
      fixSupertypes(typeSymbol);
    }
    
    /*
     * This is mostly copied from generated code with adaptations as needed.
     * It is only needed until CDTypeSymbolTOP::determineFullName works again.
     * determineFullName is probably broken because CD4CodeArtifactScope overrides getPackageName horribly.
     */
    public static String getFullNameWorkaround(@NotNull CDTypeSymbol typeSymbol) {
      Preconditions.checkNotNull(typeSymbol);
      if (typeSymbol.getFullName().contains(".")) {
        return typeSymbol.getFullName();
      }
      if (typeSymbol.getEnclosingScope() == null) {
        return typeSymbol.getName();
      }
      setProperFullName(typeSymbol);
      return typeSymbol.getFullName();
    }
  
    /*
     * This is mostly copied from generated code with adaptations as needed.
     * It is only needed until CDTypeSymbolTOP::determineFullName works again.
     * determineFullName is probably broken because CD4CodeArtifactScope overrides getPackageName horribly.
     */
    public static void setProperFullName(@NotNull CDTypeSymbol typeSymbol) {
      Preconditions.checkNotNull(typeSymbol);
      final Deque<String> nameParts = new ArrayDeque<>();
      nameParts.addFirst(typeSymbol.getName());
      
      IScope optCurrentScope = typeSymbol.getEnclosingScope();
      
      while (optCurrentScope != null) {
        final IScope currentScope = optCurrentScope;
        if (currentScope.isPresentSpanningSymbol()) {
          nameParts.addFirst(currentScope.getSpanningSymbol().getFullName());
          break;
        }
        
        if (!(currentScope instanceof IGlobalScope)) {
          if (currentScope instanceof IArtifactScope) {
            if (!getPackageNameWorkaround(typeSymbol).isEmpty()) {
              nameParts.addFirst(typeSymbol.getPackageName());
            }
          } else {
            if (currentScope.isPresentName()) {
              nameParts.addFirst(currentScope.getName());
            }
          }
          optCurrentScope = currentScope.getEnclosingScope();
        } else {
          break;
        }
      }
      String fullName = de.se_rwth.commons.Names.constructQualifiedName(nameParts);
      typeSymbol.setFullName(fullName);
    }
  
    /*
     * This is supposed to work around the horrible override
     * of getPackageName in CD4CArtifactScope
     */
    public static String getPackageNameWorkaround(@NotNull CDTypeSymbol typeSymbol) {
      Preconditions.checkNotNull(typeSymbol);
      //1. determine package name
      String packageName = "";
      IScope optCurrentScope = typeSymbol.getEnclosingScope();
      while (optCurrentScope != null) {
        final IScope currentScope = optCurrentScope;
        if (currentScope.isPresentSpanningSymbol()) {
          packageName = currentScope.getSpanningSymbol().getPackageName();
          break;
        } else if (currentScope instanceof CD4CodeArtifactScope) {
          packageName = ((CD4CodeArtifactScope) currentScope).getRealPackageName();
          break;
        }
        optCurrentScope = currentScope.getEnclosingScope();
      }
      
      //2. set proper package name (hopefully)
      typeSymbol.setPackageName(packageName);
      
      //3. return package name
      return packageName;
    }
    
    /*
     * I don't even know why but the supertypes of CDTypeSymbols were broken
     * (one could, for example, not differentiate between a superclass and an implemented interface)
     */
    protected static void fixSupertypes(@NotNull CDTypeSymbol typeSymbol) {
      Preconditions.checkNotNull(typeSymbol);
      List<SymTypeExpression> expressions = typeSymbol.getSuperTypesList().stream()
        .filter(t -> t.getTypeInfo() instanceof TypeSymbolSurrogate).collect(Collectors.toList());
      List<SymTypeExpression> fixed = new ArrayList<>();
      for (SymTypeExpression expression : expressions) {
        TypeSymbolSurrogate surrogate = (TypeSymbolSurrogate) expression.getTypeInfo();
        TypeSymbol sym = surrogate.lazyLoadDelegate();
        fixed.add(SymTypeExpressionFactory.createTypeExpression(sym));
      }
      List<SymTypeExpression> superTypes = typeSymbol.getSuperTypesList();
      superTypes.removeAll(expressions);
      superTypes.addAll(fixed);
      typeSymbol.setSuperTypesList(superTypes);
    }
  }
}