/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.arcbasis;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.VariableArcTypeCheck;

import java.util.Optional;

/**
 * Override the default behavior of the ConnectorTypesFit coco to have a fixed enclosing component for a connector
 */
public class ConnectorTypesFit extends arcbasis._cocos.ConnectorTypesFit {

  public ConnectorTypesFit() {
    super();
  }

  @Override
  protected Optional<ArcComponentTypeSymbol> getEnclosingComponent(@NotNull ASTPortAccess portAccess) {
    Preconditions.checkNotNull(portAccess);
    if (VariableArcTypeCheck.getCurrentVariant().isPresent()) {
      return VariableArcTypeCheck.getCurrentVariant();
    } else {
      return super.getEnclosingComponent(portAccess);
    }
  }
}
