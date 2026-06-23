/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arccompute.ArcComputeMill;
import arccompute._ast.ASTArcInit;
import arccompute._visitor.ArcComputeTraverser;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTArcInitCollector;
import montiarc._cocos.util.ASTNameCollector;
import montiarc._cocos.util.ASTVariableDeclaratorCollector;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.DuplicateElementsService;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static arccompute._cocos.NoInputPortsInInitialCompute.CONTEXT;
import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

public class NoInputPortsInInitialCompute4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if (DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    ExpressionSet constraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTArcInit, BoolExpr> initConditions;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    allFeatures = new ArrayList<>(mainFeatures);
    portConditions = new HashMap<>();
    initConditions = new HashMap<>();

    ArcComputeTraverser computeTraverser = ArcComputeMill.traverser();
    ASTArcInitCollector initCollector = new ASTArcInitCollector();
    computeTraverser.add4ArcCompute(initCollector);
    node.accept(computeTraverser);

    for (ASTArcInit init : initCollector.getExpressions()) {
      initConditions.put(init, ctx.mkTrue());
    }

    constraints = null;
    if (node instanceof ASTVariableArcFullVariantComponentType) {
      if (node.isPresentSymbol())
        constraints = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();

      List<VariableArcVariationPoint> variationPoints = ((ASTVariableArcFullVariantComponentType) node).getVariationPoints();
      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkAnd(expr.get());
        var arcInits = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcInit).map(k -> (ASTArcInit) k).toList();

        for (ASTArcInit arcInit : arcInits) {
          initConditions.put(arcInit, variationExpr);
        }
      }
    } else {
      if (node.isPresentSymbol())
        constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

      List<ASTArcPort> mainPorts = node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    for (Map.Entry<ASTArcInit, BoolExpr> transitionEntry : initConditions.entrySet()) {

      // Step 1: Check if init can be active
      List<BoolExpr> transitionEntryExpressionList = new ArrayList<>(List.of(featureConstraints, transitionEntry.getValue()));
      if (ExpressionSolverService.solve(transitionEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      transitionEntryExpressionList.clear();

      // Step 2: Get all nameExpressions from the init
      MontiArcTraverser nameTraverser = MontiArcMill.traverser();
      ASTNameCollector nameCollector = new ASTNameCollector();
      nameTraverser.add4ExpressionsBasis(nameCollector);
      transitionEntry.getKey().getMCStatement().accept(nameTraverser);
      List<ASTNameExpression> nameExpressions = nameCollector.getExpressions();

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      transitionEntry.getKey().getMCStatement().accept(variableTraverser);

      // Step 3: Check if there is a potential violation
      if (!nameExpressions.isEmpty()) {
        for (ASTNameExpression nameExpression : nameExpressions) {
          String variableName = nameExpression.getName();
          // Check if there are fields shadowing ports
          var shadowingFields = variableCollector.getDeclarators().stream().filter(e -> e.getDeclarator().getName().equals(variableName)).toList();
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
          possiblePorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isIncoming()).toList();
          for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
            transitionEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
            if (ExpressionSolverService.solve(transitionEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(variableName, CONTEXT),
                  nameExpression.get_SourcePositionStart(),
                  nameExpression.get_SourcePositionEnd()
                );
              }
            }
            transitionEntryExpressionList.clear();
          }
        }
      }
    }
  }
}
