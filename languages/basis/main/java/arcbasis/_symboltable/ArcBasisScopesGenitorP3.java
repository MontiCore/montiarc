/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTPortAccess;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcBasisScopesGenitorP3 implements ArcBasisVisitor2 {

  @Override
  public void visit(@NotNull ASTPortAccess node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());

    // If the port access has a component part,
    // then link the port access with the respective subcomponent
    if (node.isPresentComponent()) {
      node.getEnclosingScope()
        .resolveComponentInstanceMany(node.getComponent())
        .stream()
        .findFirst()
        .ifPresent(node::setComponentSymbol);
    }
    // Link the port access with the respective port
    // If the port access has a component part,
    // then the port belongs to a subcomponent
    if (node.isPresentComponent()) {
      if (node.isPresentComponentSymbol()
        && node.getComponentSymbol().isPresentType()
        && node.getComponentSymbol().getType().getTypeInfo() != null
        && node.getComponentSymbol()
        .getType().getTypeInfo().getEnclosingScope() != null) {
        node.getComponentSymbol()
          .getType()
          .getTypeInfo()
          .getSpannedScope()
          .resolvePortMany(node.getPort())
          .stream()
          .findFirst()
          .ifPresent(node::setPortSymbol);
      }
      // else the port belongs to this component
    } else {
      node.getEnclosingScope()
        .resolvePortMany(node.getPort())
        .stream()
        .findFirst()
        .ifPresent(node::setPortSymbol);
    }
  }
}
