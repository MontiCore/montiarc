/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcautomaton._cocos.StateNameIsNoReservedKeyword;
import arcbasis._cocos.SubcomponentNoReservedKeyword;
import arcbasis._cocos.ComponentNoReservedKeyword;
import arcbasis._cocos.FieldNameIsNoReservedKeyword;
import arcbasis._cocos.ParameterNoReservedKeyword;
import arcbasis._cocos.PortNoReservedKeyword;
import genericarc._cocos.TypeParamNameIsNoReservedKeyword;

import java.util.Set;

public abstract class IdentifiersAreNoJavaKeywords {

  private IdentifiersAreNoJavaKeywords() {}

  /**
   * Immutable collection of java 19 (update this set for newer java versions in accordance with the corresponding
   * <a href="https://docs.oracle.com/javase/specs/">java language specification</a>)
   */
  static final Set<String> JAVA_KEYWORDS = Set.of(
    "abstract", "continue", "for", "new", "switch",
    "assert", "default", "if", "package", "synchronized",
    "boolean", "do", "goto", "private", "this",
    "break", "double", "implements", "protected", "throw",
    "byte", "else", "import", "public", "throws",
    "case", "enum", "instanceof", "return", "transient",
    "catch", "extends", "int", "short", "try",
    "char", "final", "interface", "static", "void",
    "class", "finally", "long", "strictfp", "volatile",
    "const", "float", "native", "super", "while",
    "_"
  );

  public static class PortNoNamesAreNoJavaKeywords extends PortNoReservedKeyword {
    public PortNoNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }

  public static class ParameterNamesAreNoJavaKeywords extends ParameterNoReservedKeyword {
    public ParameterNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }

  public static class TypeParameterNamesAreNoJavaKeywords extends TypeParamNameIsNoReservedKeyword {
    public TypeParameterNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }

  public static class FieldNamesAreNoJavaKeywords extends FieldNameIsNoReservedKeyword {
    public FieldNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }

  public static class AutomatonStateNamesAreNoJavaKeywords extends StateNameIsNoReservedKeyword {
    public AutomatonStateNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }

  public static class ComponentTypeNamesAreNoJavaKeywords extends ComponentNoReservedKeyword {
    public ComponentTypeNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }

  public static class ComponentInstanceNamesAreNoJavaKeywords extends SubcomponentNoReservedKeyword {
    public ComponentInstanceNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }
}
