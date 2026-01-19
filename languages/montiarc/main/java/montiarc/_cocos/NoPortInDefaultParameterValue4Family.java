/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc._cocos.util.ExpressionBuildHelper;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.DuplicateElementsService;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;

public class NoPortInDefaultParameterValue4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if(DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    ExpressionSet constraints;
    List<ASTArcParameter> allParameters;

    Map<ASTArcPort, BoolExpr> portConditions;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    allFeatures = new ArrayList<>(mainFeatures);
    portConditions = new HashMap<>();
    allParameters = new ArrayList<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      constraints = null;
      if (node.isPresentSymbol())
        constraints = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();

    } else {
      constraints = null;
      if (node.isPresentSymbol())
        constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).toList();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    List<ASTArcParameter> mainParameters = node.getHead().getArcParameterList();
    if (!mainParameters.isEmpty())
      allParameters = new ArrayList<>(mainParameters);

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    for (ASTArcParameter param : allParameters) {
      if (!param.isPresentDefault())
        continue;

      var variableNames = ExpressionBuildHelper.getAllVariableOccurences(param.getDefault());
      if (!variableNames.isEmpty()) {
        for (String variableName : variableNames) {
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).toList();
          for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
            List<BoolExpr> portInDefaultParamExpressionList = new ArrayList<>(List.of(featureConstraints, entry.getValue()));
            if (ExpressionSolverService.solve(portInDefaultParamExpressionList) == Status.SATISFIABLE) {
              SourcePosition sourcePosition = param.get_SourcePositionStart();
              Log.error(PORT_REF_IN_STATIC_CONTEXT.format(entry.getKey().getName()), sourcePosition);
            }
          }
        }
      }
    }
  }
}
