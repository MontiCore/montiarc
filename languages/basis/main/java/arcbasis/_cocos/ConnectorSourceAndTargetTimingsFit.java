/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that the timing of the source port of a connector matches the timing of the target port.
 */
public class ConnectorSourceAndTargetTimingsFit implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' at '%s' has no symbol. Thus can not " +
        "check CoCo '%s'. Did you forget to run the scopes genitor and symbol table completer before checking the coco?",
      node.getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName());
    ComponentTypeSymbol enclComponent = node.getSymbol();

    node.getConnectors().forEach(connector -> {
      if (!connector.getSource().isPresentPortSymbol(enclComponent)) return;
      ASTPortAccess source = connector.getSource();

      for (ASTPortAccess target : connector.getTargetList()) {
        if (target.isPresentPortSymbol(enclComponent) &&
          !source.getPortSymbol(enclComponent).getTiming().matches(target.getPortSymbol(enclComponent).getTiming())) {
          Log.error(ArcError.SOURCE_AND_TARGET_TIMING_MISMATCH.format(
              target.getPortSymbol(enclComponent).getTiming().getName(),
              source.getPortSymbol(enclComponent).getTiming().getName()
            ),
            target.get_SourcePositionStart(),
            target.get_SourcePositionEnd()
          );
        }
      }
    });
  }
}
