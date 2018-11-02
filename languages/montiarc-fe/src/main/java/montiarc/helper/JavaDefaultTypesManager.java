package montiarc.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.java.symboltable.JavaSymbolFactory;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;


/**
 * TODO This class should be part of JavaDSL or a respective lib that provides
 * default types (e.g., primitives, java.lang.* and java.util.*). Copied from
 * MontiArc.
 */
public class JavaDefaultTypesManager {
  private final static JavaSymbolFactory jSymbolFactory = new JavaSymbolFactory();
  
  private static final String[] defaultImports = { "java.lang", "java.util" };
  
  private static final String[] primitiveTypes = { "boolean", "byte", "char", "double", "float",
      "int", "long", "short", "null" };
  
  public static void addJavaPrimitiveTypes(GlobalScope globalScope) {
    for (String primType : primitiveTypes) {
      JavaTypeSymbol jTypeSymbol = jSymbolFactory.createClassSymbol(primType);
      ArtifactScope spannedScope = new ArtifactScope("java.lang", new ArrayList<ImportStatement>());
      spannedScope.setResolvingFilters(globalScope.getResolvingFilters());
      spannedScope.setEnclosingScope(globalScope);
      jTypeSymbol.setEnclosingScope(globalScope);
      spannedScope.add(jTypeSymbol);
      globalScope.addSubScope(spannedScope);
    }
  }
  
  /**
   * @return primitivetypes
   */
  public static String[] getPrimitivetypes() {
    return primitiveTypes;
  }
  
  /**
   * Adds the default imports of the java language to make default types
   * resolvable without qualification (e.g., "String" instead of
   * "java.lang.String").
   *
   * @param imports
   */
  public static void addJavaDefaultImports(List<ImportStatement> imports) {
    List<String> importStrings = convertImportStatementsToStringList(imports);
    for (String i : defaultImports) {
      if (!importStrings.contains(i)) {
        imports.add(new ImportStatement(i, true));
      }
    }
  }
  
  private static List<String> convertImportStatementsToStringList(List<ImportStatement> imports) {
    return imports.stream().map(i -> i.getStatement()).collect(Collectors.toList());
  }
  
}
