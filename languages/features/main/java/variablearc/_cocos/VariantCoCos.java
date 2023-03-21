/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorPortsExist;
import arcbasis._cocos.ConnectorTimingsFit;
import arcbasis._cocos.ConnectorTypesFit;
import arcbasis._cocos.PortHeritageTypeFits;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortsConnected;
import arcbasis._cocos.SubPortsConnected;
import arcbasis._cocos.UniqueIdentifier;
import com.google.common.base.Preconditions;
import de.monticore.types.check.TypeRelations;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.VariableComponentTypeSymbol;
import variablearc._symboltable.VariantComponentTypeSymbol;

import java.util.List;

public class VariantCoCos implements ArcBasisASTComponentTypeCoCo {

  List<ArcBasisASTComponentTypeCoCo> cocos = List.of(
    new ConnectorDirectionsFit(),
    new ConnectorPortsExist(),
    new ConnectorTypesFit(new TypeRelations()),
    new ConnectorTimingsFit(),
    new PortHeritageTypeFits(new TypeRelations()),
    new PortsConnected(),
    new PortUniqueSender(),
    new SubPortsConnected(),
    new UniqueIdentifier()
  );

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!(node.getSymbol() instanceof VariableComponentTypeSymbol)) return;
    VariableComponentTypeSymbol component = (VariableComponentTypeSymbol) node.getSymbol();

    for (VariantComponentTypeSymbol variant : component.getVariants()) {
      for (ArcBasisASTComponentTypeCoCo coco : cocos) {
        coco.check(variant.getAstNode());
      }
    }
  }
}
