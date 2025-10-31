/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import modes._cocos.util.IgnoreASTArcModeHandler;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.MontiArcComponentTypeSymbol;
import montiarc.check.MontiArcTypeCheck;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.IgnoreASTArcVarIfHandler;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcFullVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;

import java.util.HashSet;
import java.util.stream.Collectors;

public class MontiArcFullVariantDispatch extends MontiArcCoCoChecker implements ArcBasisVisitor2 {

  public MontiArcFullVariantDispatch() {
    super();
    traverser.setArcBasisHandler(new SingleASTVariantComponentTypeHandler());
    traverser.setVariableArcHandler(new IgnoreASTArcVarIfHandler());
    traverser.setModesHandler(new IgnoreASTArcModeHandler());
  }

  @Override
  public void visit(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentSymbol()) return;

    if (node.getSymbol() instanceof MontiArcComponentTypeSymbol) {
      var nodeSymbol = (MontiArcComponentTypeSymbol) node.getSymbol();
      var variationPoints = new HashSet<>(nodeSymbol.getAllVariationPoints());
      var conditions = nodeSymbol.getConstraints();
      MontiArcTypeCheck.enterContext(node.getSymbol());
      VariableArcFullVariantComponentTypeSymbol fullVariantComponentTypeSymbol = new VariableArcFullVariantComponentTypeSymbol((IVariableArcComponentTypeSymbol) nodeSymbol.getTypeInfo(), variationPoints, conditions, nodeSymbol.getSuperComponentsList());
      ASTVariableArcFullVariantComponentType fullVariantComponentType = new ASTVariableArcFullVariantComponentType(node, fullVariantComponentTypeSymbol);
      fullVariantComponentType.accept(getTraverser());
      MontiArcTypeCheck.leaveContext();
    }
  }
}
