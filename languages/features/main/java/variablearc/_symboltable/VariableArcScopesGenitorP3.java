/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTConnector;
import arcbasis._symboltable.ArcBasisScopesGenitorP3;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.VariableArcElements;

import java.util.List;

/**
 * Fills the effect chains of component types such that they contain every
 * effect that exists in some configuration, i.e., including the connectors
 * and subcomponents of variation points. Component types loaded from symbol
 * files therefore never hide an effect; the exact conditions of effects are
 * only known for component types with AST.
 */
public class VariableArcScopesGenitorP3 extends ArcBasisScopesGenitorP3 {

  @Override
  protected boolean isAtomic(@NotNull ASTArcComponentType node) {
    return node.getSubComponents().isEmpty();
  }

  @Override
  protected boolean isDecomposed(@NotNull ASTArcComponentType node) {
    return !this.subcomponentsOf(node).isEmpty();
  }

  @Override
  protected List<ASTComponentInstance> subcomponentsOf(@NotNull ASTArcComponentType node) {
    return VariableArcElements.of(node, ASTComponentInstantiation.class).stream()
      .flatMap(instantiation -> instantiation.getComponentInstanceList().stream())
      .toList();
  }

  @Override
  protected List<ASTConnector> connectorsOf(@NotNull ASTArcComponentType node) {
    return VariableArcElements.of(node, ASTConnector.class);
  }
}
