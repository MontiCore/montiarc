/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class PortHeritageTypeFits implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isEmptyParents()) {
      this.checkPorts(node.getSymbol());
    }
  }

  protected void checkPorts(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    // check all ports
    for (ArcPortSymbol port : component.getPorts()) {
      for (CompTypeExpression parent : component.getParentsList()) {
        this.checkPort(port, parent);
      }
    }
  }

  protected void checkPort(@NotNull ArcPortSymbol port, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    Optional<SymTypeExpression> inheritedPortType = parent.getTypeExprOfPort(port.getName());

    // if the parent has a port with the same name
    if (inheritedPortType.isPresent()) {
      // then check if their types fit
      if (port.isIncoming()) {
        if (!SymTypeRelations.isCompatible(inheritedPortType.get(), port.getType())) {
          Log.error(ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH.toString(), port.getSourcePosition());
        }
      } else {
        if (!SymTypeRelations.isCompatible(port.getType(), inheritedPortType.get())) {
          Log.error(ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH.toString(), port.getSourcePosition());
        }
      }

      // check direction fits
      Optional<ArcPortSymbol> inheritedPort = parent.getTypeInfo().getPort(port.getName(), true);
      if (inheritedPort.isPresent() && inheritedPort.get().isIncoming() != port.isIncoming()) {
        Log.error(ArcError.HERITAGE_PORT_DIRECTION_MISMATCH.toString(), port.getSourcePosition());
      }
    }
  }
}
