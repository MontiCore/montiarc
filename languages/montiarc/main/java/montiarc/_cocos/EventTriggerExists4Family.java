/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTArcStatechartTOP;
import arcautomaton._ast.ASTMsgEvent;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scevents._symboltable.SCEventDefSymbol;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcAutomataError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventTriggerExists4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTMsgEvent, BoolExpr> messageEventConditions;
    Map<ASTArcPort, BoolExpr> portConditions;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    // Getting all features, constraints and ports from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new LinkedHashMap<>();
    messageEventConditions = new LinkedHashMap<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = null;
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      messageEventConditions = ((ASTVariableArcFullVariantComponentType) node).getMessageEventConditions();

    } else {
      ExpressionSet mainConstraintSet = null;
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      ArrayList<ASTMsgEvent> mainMessageEvents = (ArrayList<ASTMsgEvent>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).map(ASTArcStatechartTOP::getSCStatechartElementList).flatMap(List::stream).filter(n -> n instanceof ASTSCTransition).map(i -> (((ASTSCTransition) i))).map(o -> (ASTTransitionBody) o.getSCTBody()).filter(l -> l.isPresentSCEvent()).map(ASTTransitionBody::getSCEvent).filter(h -> h instanceof ASTMsgEvent).map(u -> (ASTMsgEvent) u).collect(Collectors.toList());

      for (ASTMsgEvent msgEvent : mainMessageEvents) {
        messageEventConditions.put(msgEvent, ctx.mkTrue());
      }

      ArrayList<ASTArcPort> mainPorts = (ArrayList<ASTArcPort>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).collect(Collectors.toList());
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTMsgEvent, BoolExpr> msgEvent : messageEventConditions.entrySet()) {

      // Step 1: Check if msgEvent can be active
      List<BoolExpr> msgEventExpressionList = new ArrayList<>(List.of(featureConstraints, msgEvent.getValue()));
      var connectorSatisfied = ExpressionSolverService.solve(msgEventExpressionList);
      if (connectorSatisfied == Status.UNSATISFIABLE)
        continue;

      msgEventExpressionList.clear();

      // Step 2: Check if the referenced Port is present
      var messagePortName = msgEvent.getKey().getName();
      if (messagePortName.equals("Tick"))
        continue;
      var messagePort = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(messagePortName)).findFirst();

      if (messagePort.isPresent()) {
        BoolExpr portCondition = messagePort.get().getValue();
        // Check if the port exists without variability
        if (portCondition.equals(ctx.mkTrue())) {
          checkIfPortSymbolExists(msgEvent.getKey());
        } else {

          msgEventExpressionList.addAll(List.of(featureConstraints, portCondition));
          // Check if msgEvent can be active without the referenced port (variable)
          if (ExpressionSolverService.solve(msgEventExpressionList) == Status.UNSATISFIABLE) {
            Log.error(ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL.format(), msgEvent.getKey().get_SourcePositionStart(), msgEvent.getKey().get_SourcePositionEnd());
          } else {
            // Add port to enclosing-scope, if port exists
            var msgPort = messagePort.get().getKey();
            if (msgPort.isPresentSymbol())
              msgEvent.getKey().getEnclosingScope().add(msgPort.getSymbol());
          }
        }
      } else {
        // Port could not be found from the Main-Component
        checkIfPortSymbolExists(msgEvent.getKey());
      }
    }
  }

  protected void checkIfPortSymbolExists(ASTMsgEvent msgEvent) {
    Optional<SCEventDefSymbol> optEventSym = msgEvent.getEnclosingScope()
      .resolveSCEventDefMany(msgEvent.getName(), getSymbolPredicate()).stream().findFirst();
    if (optEventSym.isEmpty()) {
      Log.error(ArcAutomataError.MSG_EVENT_WITHOUT_SYMBOL.format(), msgEvent.get_SourcePositionStart(), msgEvent.get_SourcePositionEnd());
    }
  }

  protected Predicate<SCEventDefSymbol> getSymbolPredicate() {
    return v -> true;
  }

}
