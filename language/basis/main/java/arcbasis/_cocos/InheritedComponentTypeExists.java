/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Checks for each component type whether an inherited component type exists
 */
public class InheritedComponentTypeExists implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    if (!node.getHead().isPresentParent()) {
      return;
    }

    String type;
    if (node.getHead().getParent() instanceof ASTMCGenericType) {
      type = ((ASTMCGenericType) node.getHead().getParent()).printWithoutTypeArguments();
    } else {
      type = node.getHead().getParent().printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter());
    }
    Optional<ComponentTypeSymbol> parent = node.getEnclosingScope().resolveComponentTypeMany(type).stream().findFirst();
    if (parent.isEmpty()) {
      Log.error(ArcError.MISSING_TYPE_OF_INHERITED_COMPONENT.format(parent, node.getSymbol().getFullName()),
        node.get_SourcePositionStart());
    }
  }
}