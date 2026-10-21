/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis.context.WithinComponentResolver;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class PortHeritageTypeFits implements ArcBasisASTArcComponentTypeCoCo {

  protected final WithinComponentResolver resolver;

  public PortHeritageTypeFits() {
    this(new WithinComponentResolver());
  }

  public PortHeritageTypeFits(@NotNull WithinComponentResolver resolver) {
    this.resolver = Preconditions.checkNotNull(resolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!node.getSymbol().isEmptySuperComponents()) {
      this.checkPorts(node.getSymbol());
    }
  }

  protected void checkPorts(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);

    // check all ports
    CompKindExpression self = this.resolver.kindOf(component);
    for (PortSymbol port : component.getPorts()) {
      for (CompKindExpression parent : this.resolver.getSuperComponents(self)) {
        this.checkPort(port, parent);
      }
    }
  }

  /**
   * @param parent a super component of the port's component type with the
   *               type parameters bound by that component type
   */
  protected void checkPort(@NotNull PortSymbol port, @NotNull CompKindExpression parent) {
    Preconditions.checkNotNull(port);
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(parent.getTypeInfo());

    for (PortSymbol inheritedPort : this.resolver.resolveMany(parent.getTypeInfo(), port.getName(), PortSymbol.class)) {
      Optional<SymTypeExpression> inheritedPortType = this.resolver.typeOf(parent, inheritedPort);
      if (inheritedPortType.isEmpty()) {
        continue;
      }

      // check if their types fit
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
      if (inheritedPort.isIncoming() != port.isIncoming()) {
        Log.error(ArcError.HERITAGE_PORT_DIRECTION_MISMATCH.toString(), port.getSourcePosition());
      }
    }
  }
}
