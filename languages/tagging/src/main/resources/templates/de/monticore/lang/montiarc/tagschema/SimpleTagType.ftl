${tc.signature("packageName", "schemaName", "tagTypeName")}

package ${packageName}.${schemaName};

import de.monticore.lang.montiarc.tagging._symboltable.TagKind;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;

/**
 * Created by SimpleTagType.ftl
 */
public class ${tagTypeName}Symbol extends TagSymbol {
  public static final ${tagTypeName}Kind KIND = ${tagTypeName}Kind.INSTANCE;

  /**
   * is marker symbol so it has no value by itself
   */
  public ${tagTypeName}Symbol() {
    super(KIND);
  }

  protected ${tagTypeName}Symbol(${tagTypeName}Kind kind) {
    super(kind);
  }

  @Override
  public String toString() {
    return "${tagTypeName}";
  }

  public static class ${tagTypeName}Kind extends TagKind {
    public static final ${tagTypeName}Kind INSTANCE = new ${tagTypeName}Kind();

    protected ${tagTypeName}Kind() {
    }
  }
}