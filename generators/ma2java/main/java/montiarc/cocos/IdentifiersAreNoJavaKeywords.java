/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcautomaton._cocos.StateNameIsNoReservedKeyword;
import arcbasis._cocos.*;
import genericarc._cocos.TypeParamNameIsNoReservedKeyword;

import java.util.Set;

public abstract class IdentifiersAreNoJavaKeywords {

  private IdentifiersAreNoJavaKeywords() {}

  /**
   * Immutable collection of java 19 (update this set for newer java versions in accordance with the corresponding
   * <a href="https://docs.oracle.com/javase/specs/">java language specification</a>)
   */
  static final Set<String> JAVA_KEYWORDS = Set.of(
    "abstract",   "continue",   "for",          "new",         "switch",
    "assert",     "default",    "if",           "package",     "synchronized",
    "boolean",    "do",         "goto",         "private",     "this",
    "break",      "double",     "implements",   "protected",   "throw",
    "byte",       "else",       "import",       "public",      "throws",
    "case",       "enum",       "instanceof",   "return",      "transient",
    "catch",      "extends",    "int",          "short",       "try",
    "char",       "final",      "interface",    "static",      "void",
    "class",      "finally",    "long",         "strictfp",    "volatile",
    "const",      "float",      "native",       "super",       "while",
    "_",

    "exports",      "opens",      "requires",     "uses",
    "module",       "permits",    "sealed",       "var",
    "non-sealed",   "provides",   "to",           "with",
    "open",         "record",     "transitive",   "yield"
  );

  public static class PortNamesAreNoJavaKeywords extends PortNameIsNoReservedKeyword {
    public PortNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }

  public static class ParameterNamesAreNoJavaKeywords extends ParameterNameIsNoReservedKeyword {
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

  public static class ComponentTypeNamesAreNoJavaKeywords extends ComponentTypeNameIsNoReservedKeyword {
    public ComponentTypeNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }

  public static class ComponentInstanceNamesAreNoJavaKeywords extends ComponentInstanceNameIsNoReservedKeyword {
    public ComponentInstanceNamesAreNoJavaKeywords() {
      super("Java", JAVA_KEYWORDS);
    }
  }
}
