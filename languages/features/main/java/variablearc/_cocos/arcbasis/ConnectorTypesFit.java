/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.arcbasis;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * Override the default behavior of the ConnectorTypesFit coco to have a fixed enclosing component for a connector
 */
public class ConnectorTypesFit extends arcbasis._cocos.ConnectorTypesFit {

  protected ComponentTypeSymbol enclosingComponent;

  public ConnectorTypesFit(@NotNull ComponentTypeSymbol enclosingComponent) {
    Preconditions.checkNotNull(enclosingComponent);
    this.enclosingComponent = enclosingComponent;
  }

  @Override
  protected Optional<ComponentTypeSymbol> getEnclosingComponent(@NotNull ASTPortAccess portAccess) {
    Preconditions.checkNotNull(portAccess);
    return Optional.of(enclosingComponent);
  }
}
