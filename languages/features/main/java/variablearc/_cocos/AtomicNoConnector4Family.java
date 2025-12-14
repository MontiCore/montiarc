/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._ast.ASTConnector;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
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
import java.util.List;
import java.util.Map;

public class AtomicNoConnector4Family implements ArcBasisASTArcComponentTypeCoCo {

  private static Context ctx;

  private static boolean evaluateCondition(Context ctx, Model model, BoolExpr condition) {
    AtomicNoConnector4Family.ctx = ctx;
    try {
      Expr<?> eval = model.evaluate(condition, true);
      return eval.isTrue();
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    ctx = ExpressionSolverService.getContext();

    Map<ASTConnector, BoolExpr> connectorConditions = new HashMap<>();
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions = new HashMap<>();

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    // Getting all features, ports and variations from the Main-Component
    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    // Getting all features, variations, constraints, connectors and subcomponents from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      connectorConditions = ((ASTVariableArcFullVariantComponentType) node).getConnectorConditions();
      subcomponentConditions = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();

    } else {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      List<ASTConnector> mainConnectors = node.getConnectors();
      for (ASTConnector connector : mainConnectors) {
        connectorConditions.put(connector, ctx.mkTrue());
      }
      List<ASTComponentInstance> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).toList();
      for (ASTComponentInstance mainSubComp : mainSubComps) {
        subcomponentConditions.put(mainSubComp, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    // Adding all subcomponent-conditions
    List<BoolExpr> allSubcomponentConditions = new ArrayList<>();
    for (Map.Entry<ASTComponentInstance, BoolExpr> sub : subcomponentConditions.entrySet()) {
      allSubcomponentConditions.add(sub.getValue());
    }

    // Adding all connector-conditions
    List<BoolExpr> allConnectorConditions = new ArrayList<>();
    for (Map.Entry<ASTConnector, BoolExpr> connector : connectorConditions.entrySet()) {
      allConnectorConditions.add(connector.getValue());
    }

    // There exist a subcomponent, if any of the conditions hold
    BoolExpr subComponentsExists = ctx.mkOr(allSubcomponentConditions.toArray(new BoolExpr[0]));
    // There is a connector, if any of the conditions hold
    BoolExpr connectorsExist = ctx.mkOr(allConnectorConditions.toArray(new BoolExpr[0]));
    // If no SubComponent exist
    BoolExpr noSubComponents = ctx.mkNot(subComponentsExists);
    // In case there are no subcomponents, but connectors
    BoolExpr violation = ctx.mkAnd(noSubComponents, connectorsExist, featureConstraints);

    List<BoolExpr> connectorInAtomicExpressionList = new ArrayList<>();
    connectorInAtomicExpressionList.add(violation);

    if (ExpressionSolverService.solve(connectorInAtomicExpressionList) == Status.SATISFIABLE) {
      Model model = ExpressionSolverService.getModel();
      for (Map.Entry<ASTConnector, BoolExpr> connector : connectorConditions.entrySet()) {
        if (evaluateCondition(ctx, model, connector.getValue())) {
          Log.warn(ArcError.CONNECTORS_IN_ATOMIC.toString(), connector.getKey().get_SourcePositionStart(), connector.getKey().get_SourcePositionEnd());
        }
      }
    }
  }
}
