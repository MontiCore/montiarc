/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

/**
 * Checks for each component type whether an inherited component type exists
 */
public class InheritedComponentTypeExists implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getHead().isPresentParent()) {
      return;
    }

    String type;
    if (node.getHead().getParent() instanceof ASTMCGenericType) {
      type = ((ASTMCGenericType) node.getHead().getParent()).printWithoutTypeArguments();
    } else {
      type = node.getHead().getParent().printType();
    }

    List<ComponentTypeSymbol> parent = node.getEnclosingScope().resolveComponentTypeMany(type);
    if (parent.isEmpty()) {
      Log.error(ArcError.MISSING_TYPE_OF_INHERITED_COMPONENT.format(type, node.getSymbol().getFullName()),
        node.get_SourcePositionStart());
    } else if (parent.size() > 1) {
      Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(type),
        node.get_SourcePositionStart());
    }
  }
}