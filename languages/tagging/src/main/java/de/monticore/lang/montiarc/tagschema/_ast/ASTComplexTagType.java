package de.monticore.lang.montiarc.tagschema._ast;

/**
 * Created by MichaelvonWenckstern on 28.06.2016.
 */
public class ASTComplexTagType extends ASTComplexTagTypeTOP {
  public ASTComplexTagType() {
    super();
  }

  public ASTComplexTagType (String name, ASTScope scope, String complexTagDef) {
    super(name, scope, complexTagDef);
  }

  public void setComplexTag(String complexTag) {
    if (complexTag != null) {
      if (complexTag.startsWith("is")) {
        complexTag = complexTag.substring(2);
      }
      if (complexTag.endsWith(";")) {
        complexTag = complexTag.substring(0, complexTag.length() - 1);
      }
    }
    super.setComplexTag(complexTag);
  }
}
