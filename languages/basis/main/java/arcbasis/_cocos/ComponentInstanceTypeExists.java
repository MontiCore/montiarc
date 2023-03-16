/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

/**
 * Checks for each (AST) component instantiation whether its component type exists.
 */
public class ComponentInstanceTypeExists implements ArcBasisASTComponentInstantiationCoCo {

  protected MCBasicTypesFullPrettyPrinter typePrinter;

  public ComponentInstanceTypeExists() {
    this(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
  }

  public ComponentInstanceTypeExists(@NotNull MCBasicTypesFullPrettyPrinter typePrinter) {
    this.typePrinter = Preconditions.checkNotNull(typePrinter);
  }

  @Override
  public void check(@NotNull ASTComponentInstantiation node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());

    String typeName;
    if (node.getMCType() instanceof ASTMCGenericType) {
      typeName = ((ASTMCGenericType) node.getMCType()).printWithoutTypeArguments();
    } else {
      typeName = node.getMCType().printType(typePrinter);
    }

    List<ComponentTypeSymbol> compType = node.getEnclosingScope().resolveComponentTypeMany(typeName);
    if(compType.isEmpty()) {
      String firstInstanceName = node.getComponentInstanceList().isEmpty() ?
        "?" : node.getComponentInstanceList().get(0).getName();

      if (!node.getEnclosingScope().resolveTypeMany(typeName).isEmpty()) {
        Log.error(ArcError.MISSING_ASSIGNMENT_OF_ARC_FIELD.format(firstInstanceName, typeName),
                node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      } else {
        Log.error(ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE.format(
                typeName, firstInstanceName, node.get_SourcePositionStart()), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    } else if (compType.size() > 1){
      Log.error(ArcError.SYMBOL_TOO_MANY_FOUND.format(typeName), node.get_SourcePositionStart());
    }
  }
}