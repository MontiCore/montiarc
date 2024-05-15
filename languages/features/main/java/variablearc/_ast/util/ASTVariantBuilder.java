/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast.util;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.VariantComponentTypeSymbol;

/**
 * A class that can duplicate AST elements or update symbols.
 * It partly duplicates the behavior of the symbol table completer but using a component variant when needed.
 */
public class ASTVariantBuilder implements ArcBasisHandler {

  protected ArcBasisTraverser traverser;
  protected ASTArcElement result;
  protected final VariantComponentTypeSymbol variant;

  public ASTVariantBuilder(VariantComponentTypeSymbol variant) {this.variant = variant;}

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

  /**
   * Duplicates a connector and then updates the component and ports of its port access'
   *
   * @param node the connector to be duplicated
   */
  @Override
  public void handle(@NotNull ASTConnector node) {
    Preconditions.checkNotNull(node);
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
        node.getComponentSymbol().getType().getTypeInfo() != null && VariableArcMill.typeDispatcher().isArcBasisComponentType(node.getComponentSymbol().getType().getTypeInfo())
      ) {
        ArcBasisMill.typeDispatcher().asArcBasisComponentType(node.getComponentSymbol().getType().getTypeInfo()).getArcPort(node.getPort(), true).ifPresent(node::setPortSymbol);
      }
    } else {
      variant.getArcPort(node.getPort(), true).ifPresent(node::setPortSymbol);
    }
  }

  /**
   * Set the timing and delay of a {@link variablearc._symboltable.VariantPortSymbol}
   *
   * @param node the port declaration used
   */
  @Override
  public void handle(@NotNull ASTPortDeclaration node) {
    Preconditions.checkNotNull(node);
    Timing timing = node.getTiming().orElse(null);

    for (ASTArcPort port : node.getArcPortList()) {
      if (variant.containsSymbol(port.getSymbol())) {
        variant.getArcPort(port.getName()).ifPresent(p -> p.setTiming(timing));
        if (node.hasDelay()) variant.getArcPort(port.getName()).ifPresent(p -> p.setDelayed(true));
      }
    }
  }
}
