/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.types.check.ITypeRelations;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class InheritedPortsTypeCorrect implements ArcBasisASTComponentTypeCoCo {

  protected final ITypeRelations tr;

  public InheritedPortsTypeCorrect(@NotNull ITypeRelations tr) {
    this.tr = Preconditions.checkNotNull(tr);
  }

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (node.getSymbol().isPresentParent()) {
      this.checkPorts(node.getSymbol());
    }
  }

  protected void checkPorts(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    // check all ports
    for (PortSymbol port : component.getPorts()) {
      this.checkPort(port, component.getParent());
    }
  }

  protected void checkPort(@NotNull PortSymbol port, @NotNull CompTypeExpression parent) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    Optional<SymTypeExpression> inheritedPortType = parent.getTypeExprOfPort(port.getName());

    // if the parent has a port with the same name
    if (inheritedPortType.isPresent()) {
      // then check if their types fit
      if (port.isIncoming()) {
        if (!tr.compatible(port.getType(), inheritedPortType.get())) {
          Log.error(ArcError.INHERITED_INCOMING_PORT_TYPE_MISMATCH.format(port.getName(), port.getType().print(),
            inheritedPortType.get().print(), parent.getTypeInfo().getName()));
        }
      } else {
        if (!tr.compatible(inheritedPortType.get(), port.getType())) {
          Log.error(ArcError.INHERITED_OUTGOING_PORT_TYPE_MISMATCH.format(port.getName(), port.getType().print(),
            inheritedPortType.get().print(), parent.getTypeInfo().getName()));
        }
      }

      // check direction fits
      Optional<PortSymbol> inheritedPort = parent.getTypeInfo().getPort(port.getName(), true);
      if (inheritedPort.isPresent() && inheritedPort.get().isIncoming() != port.isIncoming()) {
        Log.error(ArcError.INHERITED_PORT_DIRECTION_MISMATCH.format(port.getName(),
          port.isIncoming() ? "incoming" : "outgoing", inheritedPort.get().isIncoming() ? "incoming" : "outgoing"));
      }
    }
  }
}
