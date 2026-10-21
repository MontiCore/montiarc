/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcComponentTypeBuilder;

/**
 * Builds {@link ASTVariableArcComponentType}s. As the generated builder
 * instantiates the node directly, building is replicated.
 */
public class ASTVariableArcComponentTypeBuilder extends ASTArcComponentTypeBuilder {

  @Override
  public ASTArcComponentType build() {
    if (!this.isValid()) {
      return super.build();
    }
    return this.uncheckedBuild();
  }

  @Override
  public ASTArcComponentType uncheckedBuild() {
    ASTVariableArcComponentType value = new ASTVariableArcComponentType();
    if (this.stereotype.isPresent()) {
      value.setStereotype(this.stereotype.get());
    } else {
      value.setStereotypeAbsent();
    }
    value.setName(this.name);
    value.setHead(this.head);
    value.setComponentInstanceList(this.componentInstances);
    value.setBody(this.body);
    if (this.sourcePositionEnd.isPresent()) {
      value.set_SourcePositionEnd(this.sourcePositionEnd.get());
    } else {
      value.set_SourcePositionEndAbsent();
    }
    if (this.sourcePositionStart.isPresent()) {
      value.set_SourcePositionStart(this.sourcePositionStart.get());
    } else {
      value.set_SourcePositionStartAbsent();
    }
    value.set_PreCommentList(this.precomments);
    value.set_PostCommentList(this.postcomments);
    return value;
  }
}
