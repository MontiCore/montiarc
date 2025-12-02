/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast.util;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTVariantPortAccess;
import variablearc._symboltable.VariableArcFullVariantComponentTypeSymbol;

import java.util.stream.Collectors;

public class ASTFullVariantBuilder implements ArcBasisHandler {

  protected ArcBasisTraverser traverser;
  protected ASTArcElement result;
  protected final VariableArcFullVariantComponentTypeSymbol variant;

  public ASTFullVariantBuilder(VariableArcFullVariantComponentTypeSymbol variant) {
    this.variant = variant;
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ArcBasisTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * Does nothing, return a complete new version of the AST element, or update variant symbols of the enclComp.
   *
   * @param node the element the builder deals with
   * @return either {@param node} or a duplicated version of it
   */
  public ASTArcElement duplicate(@NotNull ASTArcElement node) {
    Preconditions.checkNotNull(node);
    result = node;
    traverser = VariableArcMill.traverser();
    traverser.setArcBasisHandler(this);
    node.accept(traverser);
    return result;
  }

  @Override
  public void handle(ASTArcComponentType node) {
    // Do nothing, don't traverse into inner components
  }

  /**
   * Duplicates a connector and then updates the component and ports of its port access'
   *
   * @param node the connector to be duplicated
   */
  @Override
  public void handle(@NotNull ASTConnector node) {
    Preconditions.checkNotNull(node);
    if (node == result) {
      ASTConnector result = ArcBasisMill.connectorBuilder()
        .setSource(new ASTVariantPortAccess(node.getSource()))
        .setTargetList(node.getTargetList().stream().map(ASTVariantPortAccess::new).collect(Collectors.toList()))
        .build();

      result.setEnclosingScope(node.getEnclosingScope());

      result.set_SourcePositionStart(node.get_SourcePositionStart());
      result.set_SourcePositionEnd(node.get_SourcePositionEnd());

      traverse(result);
      this.result = result;
    }
  }

  /**
   * Duplicates the behavior of the symbol table completer in respect to the enclosing variant
   *
   * @param node the port access to be updated
   */
  @Override
  public void handle(@NotNull ASTPortAccess node) {
    Preconditions.checkNotNull(node);
    node.setEnclosingScope(variant.getSpannedScope());
    if (node.isPresentComponent()) {
      variant.getSubcomponents(node.getComponent()).ifPresent(node::setComponentSymbol);
    }

    if (node.isPresentComponent()) {
      if (node.isPresentComponentSymbol() && node.getComponentSymbol().isTypePresent() &&
        node.getComponentSymbol().getType().getTypeInfo() != null
      ) {
        node.getComponentSymbol().getType().getTypeInfo().getPort(node.getPort(), true).ifPresent(node::setPortSymbol);
      }
    } else {
      variant.getPort(node.getPort(), true).ifPresent(node::setPortSymbol);
    }
  }

}
