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
public class ConnectorTimingsFit implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol enclComponent = node.getSymbol();

    node.getConnectors().forEach(connector -> {
      if (!connector.getSource().isPresentPortSymbol(enclComponent)) return;
      ASTPortAccess source = connector.getSource();

      for (ASTPortAccess target : connector.getTargetList()) {
        if (target.isPresentPortSymbol(enclComponent) &&
          !source.getPortSymbol(enclComponent).getTiming().matches(target.getPortSymbol(enclComponent).getTiming())) {
          Log.error(ArcError.CONNECTOR_TIMING_MISMATCH.format(
              target.getPortSymbol(enclComponent).getTiming().getName(),
              source.getPortSymbol(enclComponent).getTiming().getName()
            ),
            target.get_SourcePositionStart(), target.get_SourcePositionEnd()
          );
        }
      }
    });
  }
}