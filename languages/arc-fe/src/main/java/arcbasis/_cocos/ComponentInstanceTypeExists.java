/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

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
  public void check(ASTComponentInstantiation node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.getEnclosingScope() != null);

    String typeName = node.getMCType().printType(typePrinter);

    if(!node.getEnclosingScope().resolveComponentType(typeName).isPresent()) {
      String firstInstanceName = node.getComponentInstanceList().isEmpty() ?
        "?" : node.getComponentInstanceList().get(0).getName();

      Log.error(ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE.format(
        typeName, firstInstanceName, node.get_SourcePositionStart()));
    }
  }
}