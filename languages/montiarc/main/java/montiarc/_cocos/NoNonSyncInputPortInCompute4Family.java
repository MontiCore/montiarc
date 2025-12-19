/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arccompute._ast.ASTArcCompute;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTArcComputeCollector;
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

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;
import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

public class NoNonSyncInputPortInCompute4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if(DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTArcCompute, BoolExpr> computeConditions;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new HashMap<>();
    computeConditions = new HashMap<>();

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ASTArcComputeCollector computeCollector = new ASTArcComputeCollector();
    nodeTraverser.add4ArcCompute(computeCollector);
    node.accept(nodeTraverser);

    for (ASTArcCompute compute : computeCollector.getExpressions()) {
      computeConditions.put(compute, ctx.mkTrue());
    }

    ExpressionSet mainConstraintSet = null;
    if (node instanceof ASTVariableArcFullVariantComponentType) {
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();

      List<VariableArcVariationPoint> variationPoints = ((ASTVariableArcFullVariantComponentType) node).getVariationPoints();
      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkAnd(expr.get());
        var arcInits = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcCompute).map(k -> (ASTArcCompute) k).toList();

        for (ASTArcCompute arcCompute : arcInits) {
          computeConditions.put(arcCompute, variationExpr);
        }
      }
    } else {
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      List<ASTArcPort> mainPorts = node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints.getFirst(), allFeatures, expSolver);

    for (Map.Entry<ASTArcCompute, BoolExpr> computeEntry : computeConditions.entrySet()) {

      // Step 1: Check if compute can be active
      List<BoolExpr> computeEntryExpressionList = new ArrayList<>(List.of(featureConstraints, computeEntry.getValue()));
      if (ExpressionSolverService.solve(computeEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      computeEntryExpressionList.clear();

      // Step 2: Get all nameExpressions from the compute
      MontiArcTraverser nameTraverser = MontiArcMill.traverser();
      ASTNameCollector nameCollector = new ASTNameCollector();
      nameTraverser.add4ExpressionsBasis(nameCollector);
      computeEntry.getKey().accept(nameTraverser);
      List<String> variableNames = new ArrayList<>();

      for (ASTNameExpression nameExpression : nameCollector.getExpressions()) {
        variableNames.add(nameExpression.getName());
      }

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      computeEntry.getKey().accept(variableTraverser);

      // Step 3: Check if there is a potential violation
      if (!variableNames.isEmpty()) {
        for (String variableName : variableNames) {
          // Check if there are fields shadowing ports
          var shadowingFields = variableCollector.getDeclarators().stream().filter(e -> e.getDeclarator().getName().equals(variableName)).toList();
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
          possiblePorts = possiblePorts.stream().filter(e -> e.getKey().isPresentSymbol() && e.getKey().getSymbol().isIncoming() && !e.getKey().getSymbol().getTiming().matches(TIMED_SYNC)).toList();
          for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {

            computeEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
            if (ExpressionSolverService.solve(computeEntryExpressionList) == Status.SATISFIABLE) {
              if (shadowingFields.isEmpty()) {
                SourcePosition sourcePosition = computeEntry.getKey().get_SourcePositionStart();
                Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(variableName, computeEntry.getKey()), sourcePosition);
              }
            }
            computeEntryExpressionList.clear();
          }
        }
      }
    }
  }
}
