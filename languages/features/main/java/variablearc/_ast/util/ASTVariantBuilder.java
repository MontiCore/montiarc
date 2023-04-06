/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast.util;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.VariantComponentTypeSymbol;

public class ASTVariantBuilder implements ArcBasisHandler {

  protected ArcBasisTraverser traverser;
  protected ASTArcElement result;
  protected final VariantComponentTypeSymbol enclComponent;

  public ASTVariantBuilder(VariantComponentTypeSymbol enclComponent) {this.enclComponent = enclComponent;}

  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ArcBasisTraverser traverser) {
    this.traverser = traverser;
  }

  public ASTArcElement duplicate(ASTArcElement node) {
    result = node;
    traverser = VariableArcMill.traverser();
    traverser.setArcBasisHandler(this);
    node.accept(traverser);
    return result;
  }

  @Override
  public void handle(@NotNull ASTConnector node) {
    if (node == result) {
      ASTConnector result = ArcBasisMill.connectorBuilder().setSource(node.getSourceName())
        .setTargetList(node.getTargetsNames().toArray(String[]::new)).build();
      result.setEnclosingScope(node.getEnclosingScope());

      // duplicate source positions
      result.set_SourcePositionStart(node.get_SourcePositionStart());
      result.set_SourcePositionEnd(node.get_SourcePositionEnd());

      result.getSource().set_SourcePositionStart(node.getSource().get_SourcePositionStart());
      result.getSource().set_SourcePositionEnd(node.getSource().get_SourcePositionEnd());

      for (int i = 0; i < result.getTargetList().size(); i++) {
        result.getTargetList().get(i).set_SourcePositionStart(node.getTargetList().get(i).get_SourcePositionStart());
        result.getTargetList().get(i).set_SourcePositionEnd(node.getTargetList().get(i).get_SourcePositionEnd());
      }

      traverse(result);
      this.result = result;
    }
  }

  @Override
  public void handle(ASTPortAccess node) {
    if (node.isPresentComponent()) {
      enclComponent.getSubComponent(node.getComponent()).ifPresent(node::setComponentSymbol);
    }

    if (node.isPresentComponent()) {
      if (node.isPresentComponentSymbol() && node.getComponentSymbol().isPresentType() &&
        node.getComponentSymbol().getType().getTypeInfo() != null
      ) {
        node.getComponentSymbol().getType().getTypeInfo().getPort(node.getPort(), true).ifPresent(node::setPortSymbol);
      }
    } else {
      enclComponent.getPort(node.getPort(), true).ifPresent(node::setPortSymbol);
    }
  }
}
