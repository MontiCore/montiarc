/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PortHeritageTypeFits4Family implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    Map<ASTArcPort, BoolExpr> portConditions = new HashMap<>();
    Map<BoolExpr, ArcError> violationExprToError = new LinkedHashMap<>();

    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();
    List<String> allFeatures;
    List<ExpressionSet> allConstraints;
    List<ASTArcPort> allPorts;

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    allPorts = new ArrayList<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);
      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      allPorts.addAll(portConditions.keySet());

    } else {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);
      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).toList();
      for (ASTArcPort mainPort : mainPorts) {
        portConditions.put(mainPort, ctx.mkTrue());
      }
      allPorts.addAll(mainPorts);
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints.getFirst(), allFeatures, expSolver);

    List<BoolExpr> portExpressionList = new ArrayList<>();

    for (ASTArcPort port : allPorts) {

      portExpressionList.add(featureConstraints);
      BoolExpr portActive = portConditions.get(port);

      for (CompKindExpression parent : node.getSymbol().getSuperComponentsList()) {
        Optional<SymTypeExpression> inheritedTypeOptional = parent.getTypeOfPort(port.getName());

        if (inheritedTypeOptional.isPresent()) {
          SymTypeExpression inheritedType = inheritedTypeOptional.get();
          SymTypeExpression ownType = port.getSymbol().getType();

          // Mismatch of incoming type
          if (port.getSymbol().isIncoming() && !SymTypeRelations.isCompatible(inheritedType, ownType)) {
            BoolExpr mismatch = ctx.mkAnd(portActive, ctx.mkBoolConst("mismatch_in_type_port " + port.getName()));
            portExpressionList.add(mismatch);
            violationExprToError.put(mismatch, ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH);
          }

          // Mismatch of outgoing type
          if (!port.getSymbol().isIncoming() && !SymTypeRelations.isCompatible(ownType, inheritedType)) {
            BoolExpr mismatch = ctx.mkAnd(portActive, ctx.mkBoolConst("mismatch_out_type_port " + port.getName()));
            portExpressionList.add(mismatch);
            violationExprToError.put(mismatch, ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH);
          }

          // Direction mismatch
          Optional<PortSymbol> inheritedPortOptional = parent.getTypeInfo().getPort(port.getName());
          if (inheritedPortOptional.isPresent() && inheritedPortOptional.get().isIncoming() != port.getSymbol().isIncoming()) {
            BoolExpr mismatch = ctx.mkAnd(portActive, ctx.mkBoolConst("mismatch_direction_port " + port.getName()));
            portExpressionList.add(mismatch);
            violationExprToError.put(mismatch, ArcError.HERITAGE_PORT_DIRECTION_MISMATCH);
          }

        }
      }

      if (ExpressionSolverService.solve(portExpressionList) == Status.SATISFIABLE) {
        Model model = ExpressionSolverService.getModel();
        for (Map.Entry<BoolExpr, ArcError> entry : violationExprToError.entrySet()) {
          if (model.evaluate(entry.getKey(), false).isTrue()) {
            var error = entry.getValue();
            Log.error(error.toString(), port.getSymbol().getSourcePosition());
          }
        }
      }
      portExpressionList.clear();
    }

  }
}
