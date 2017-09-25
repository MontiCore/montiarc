package montiarc.helper;

import java.util.List;

import de.monticore.java.symboltable.JavaSymbolFactory;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ImportStatement;

//XXX: https://git.rwth-aachen.de/montiarc/core/issues/43


/**
 * TODO This class should be part of JavaDSL or a respective lib that provides
 * default types (e.g., primitives, java.lang.* and java.util.*).
 * Copied from MontiArc.
 */
public class JavaHelper {
  private final static JavaSymbolFactory jSymbolFactory = new JavaSymbolFactory();
  
  // TODO this should be part of JavaDSL / JavaLib
  public static void addJavaPrimitiveTypes(GlobalScope globalScope) {
    globalScope.add(jSymbolFactory.createClassSymbol("boolean"));
    globalScope.add(jSymbolFactory.createClassSymbol("byte"));
    globalScope.add(jSymbolFactory.createClassSymbol("char"));
    globalScope.add(jSymbolFactory.createClassSymbol("double"));
    globalScope.add(jSymbolFactory.createClassSymbol("float"));
    globalScope.add(jSymbolFactory.createClassSymbol("int"));
    globalScope.add(jSymbolFactory.createClassSymbol("long"));
    globalScope.add(jSymbolFactory.createClassSymbol("short"));
  }
  
  /**
   * Adds the default imports of the java language to make default types
   * resolvable without qualification (e.g., "String" instead of
   * "java.lang.String").
   *
   * @param imports
   */
  // TODO should be part of JavaDSL / JavaLib
  public static void addJavaDefaultImports(List<ImportStatement> imports) {
    imports.add(new ImportStatement("java.lang", true));
    imports.add(new ImportStatement("java.util", true));
  }
}
