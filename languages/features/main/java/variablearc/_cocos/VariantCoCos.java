/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.*;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.VariableComponentTypeSymbol;
import variablearc._symboltable.VariantComponentTypeSymbol;

import java.util.List;

public class VariantCoCos implements ArcBasisASTComponentTypeCoCo {

  List<ArcBasisASTComponentTypeCoCo> cocos = List.of(
    new ConnectorSourceAndTargetDirectionsFit(),
    new ConnectorSourceAndTargetExist(),
    new ConnectorSourceAndTargetTypesFit(),
    new ConnectorSourceAndTargetTimingsFit(),
    new InheritedPortsTypeCorrect(),
    new PortsConnected(),
    new PortUniqueSender(),
    new SubPortsConnected(),
    new UniqueIdentifierNames()
  );

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' at '%s' has no symbol. Thus can not " +
        "check CoCo '%s'. Did you forget to run the scopes genitor and symbol table completer before checking the coco?",
      node.getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName());
    if (!(node.getSymbol() instanceof VariableComponentTypeSymbol)) return;
    VariableComponentTypeSymbol component = (VariableComponentTypeSymbol) node.getSymbol();

    for (VariantComponentTypeSymbol variant : component.getVariants()) {
      for (ArcBasisASTComponentTypeCoCo coco : cocos) {
        coco.check(variant.getAstNode());
      }
    }
  }
}
