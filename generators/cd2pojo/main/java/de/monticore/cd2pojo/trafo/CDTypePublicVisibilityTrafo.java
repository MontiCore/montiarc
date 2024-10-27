/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo.trafo;

import com.google.common.base.Preconditions;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Transforms all classdiagram types to have public visibility. Use in an
 * inheritance traverser as cdbasis visitor.
 */
public class CDTypePublicVisibilityTrafo implements CDBasisVisitor2 {

  @Override
  public void visit(@NotNull ASTCDType node) {
    Preconditions.checkNotNull(node);
    node.getModifier().setPublic(true);
    node.getModifier().setProtected(false);
    node.getModifier().setPrivate(false);
  }
}
