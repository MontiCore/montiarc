/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._cocos.ConnectorDirectionsFit;
import arcbasis._cocos.ConnectorPortsExist;
import arcbasis._cocos.ConnectorTimingsFit;
import arcbasis._cocos.ConnectorTypesFit;
import arcbasis._cocos.FeedbackStrongCausality;
import arcbasis._cocos.PortHeritageTypeFits;
import arcbasis._cocos.PortUniqueSender;
import arcbasis._cocos.PortsConnected;
import arcbasis._cocos.SubPortsConnected;
import arcbasis._cocos.UniqueIdentifier;
import com.google.common.base.Preconditions;
import de.monticore.types.check.TypeRelations;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._cocos.util.SingleASTVariantComponentTypeHandler;
import variablearc._cocos.util.IgnoreASTArcVarIfHandler;
import variablearc._symboltable.VariableComponentTypeSymbol;
import variablearc._symboltable.VariantComponentTypeSymbol;
import variablearc._visitor.VariableArcTraverser;

public class VariantCoCos implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    if (!(node.getSymbol() instanceof VariableComponentTypeSymbol)) return;
    VariableComponentTypeSymbol component = (VariableComponentTypeSymbol) node.getSymbol();

    for (VariantComponentTypeSymbol variant : component.getVariants()) {
      VariableArcTraverser traverser = getTraverser();
      variant.getAstNode().accept(traverser);
    }
  }

  protected VariableArcTraverser getTraverser() {
    VariableArcTraverser traverser = VariableArcMill.traverser();
    traverser.setArcBasisHandler(new SingleASTVariantComponentTypeHandler());
    traverser.setVariableArcHandler(new IgnoreASTArcVarIfHandler());

    // Add CoCos
    traverser.add4ArcBasis(new ConnectorDirectionsFit());
    traverser.add4ArcBasis(new ConnectorPortsExist());
    traverser.add4ArcBasis(new ConnectorTypesFit(new TypeRelations()));
    traverser.add4ArcBasis(new ConnectorTimingsFit());
    traverser.add4ArcBasis(new PortHeritageTypeFits(new TypeRelations()));
    traverser.add4ArcBasis(new PortsConnected());
    traverser.add4ArcBasis(new PortUniqueSender());
    traverser.add4ArcBasis(new SubPortsConnected());
    traverser.add4ArcBasis(new UniqueIdentifier());

    return traverser;
  }
}
