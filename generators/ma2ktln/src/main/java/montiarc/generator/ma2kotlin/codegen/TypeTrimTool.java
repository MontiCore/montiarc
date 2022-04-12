/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.codegen;

import com.google.common.base.Preconditions;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeTrimTool {
  /**
   * contains imports statements for simple java-classes that have a kotlin equivalent and are thus not necessary in kotlin
   */
  public static Set<String> oddImports = Stream.of("Byte", "Short", "Integer", "Long", "Float", "Double", "Boolean", "Character", "String")
      .map(t -> "java.lang."+t).collect(Collectors.toSet());
  /**
   * maps javas primitive and boxed primitive types to an equivalent in kotlin
   */
  public static Map<String, String> java2Kotlin = Stream.of(
          "#byte",
          "#short",
          "#int",
          "#long",
          "#float",
          "#double",
          "#boolean",
          "#char",
          "*Byte",
          "*Short",
          "Integer=Int?",
          "*Long",
          "*Float",
          "*Double",
          "*Boolean",
          "Character=Char?"
      )
      .map(s -> s.startsWith("#")?s.substring(1)+'='+s.substring(1,2).toUpperCase()+s.substring(2):s)
      .map(s -> s.replaceAll("\\*(\\w+)", "$1=$1?"))
      .map(s -> s.split("\\s*=\\s*"))
      .collect(
          HashMap::new,
          (map, arr) -> map.put(arr[0], arr[1]),
          HashMap::putAll
      );

  /**
   * checks whether the given import is a justified import or if it should be omitted
   * @param statement any import
   * @return false, the import is covered by a default import of kotlin
   */
  public boolean isImport(ImportStatement statement){
    Preconditions.checkNotNull(statement);
    return !oddImports.contains(statement.getStatement());
  }

  /**
   * @param type the type-expression of a port, field parameter etc.
   * @return the given type, unless there is a better kotlin equivalent
   */
  public String printType(SymTypeExpression type){
    Preconditions.checkNotNull(type);
    String javaType = type.getTypeInfo().getName();
    return java2Kotlin.getOrDefault(javaType, javaType);
  }

}
