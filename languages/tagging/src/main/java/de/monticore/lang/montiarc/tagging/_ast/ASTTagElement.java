package de.monticore.lang.montiarc.tagging._ast;

public class ASTTagElement extends ASTTagElementTOP {
  protected  ASTTagElement (String name, String tagValue) {
    super(name, tagValue);
  }

  protected ASTTagElement () {
    super();
  }

  public void setTagValue(String tagValue) {
    if (tagValue != null) {
      if (tagValue.startsWith("=")) {
        tagValue = tagValue.substring(1);
      }
      if (tagValue.endsWith(";")) {
        tagValue = tagValue.substring(0, tagValue.length() - 1);
      }
      tagValue = tagValue.trim();
    }
    super.setTagValue(tagValue);
  }
}


