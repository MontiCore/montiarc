/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTArcStatechartTOP;
import arcautomaton._ast.ASTMsgEvent;
import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._ast.ASTPortDeclarationTOP;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.util.ASTFullVariantBuilder;
import variablearc._ast.util.ASTVariantBuilder;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcFullVariantComponentTypeSymbol;
import variablearc._symboltable.VariableArcSymbols2Json;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc._symboltable.VariableArcVariationPointDeSer;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;
import variablearc.evaluation.expressions.Expression;

import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ASTVariableArcFullVariantComponentType extends ASTArcComponentType {

  protected ASTArcComponentType parent;

  /**
   * @param parent        The component this variant originates from
   * @param variantSymbol The full variant (i.e. configuration) of this component
   */
  public ASTVariableArcFullVariantComponentType(@NotNull ASTArcComponentType parent, @NotNull VariableArcFullVariantComponentTypeSymbol variantSymbol) {
    Preconditions.checkNotNull(parent);
    Preconditions.checkNotNull(variantSymbol);
    List<ASTArcElement> additionalArcElementList = new ArrayList<>();
    this.parent = parent;
    this.name = parent.getName();
    this.enclosingScope = parent.getEnclosingScope();
    this.spannedScope = parent.getSpannedScope();
    this.symbol = Optional.of(variantSymbol);
    this.componentInstances = parent.getComponentInstanceList();
    this.head = parent.getHead();
    set_SourcePositionStart(parent.get_SourcePositionStart());
    set_SourcePositionEnd(parent.get_SourcePositionEnd());

    List<ASTArcElement> arcElementList = new ArrayList<>(parent.getBody().getArcElementList());
    arcElementList.addAll(additionalArcElementList);
    ASTFullVariantBuilder builder = new ASTFullVariantBuilder(variantSymbol);
    arcElementList = arcElementList.stream().map(builder::duplicate).collect(Collectors.toList());
    this.body = VariableArcMill.componentBodyBuilder().setArcElementsList(arcElementList).build();
  }

  private ArrayList<VariableArcVariationPoint> variationPoints;
  private Map<ASTArcPort, BoolExpr> portConditions;
  private Map<ASTConnector, BoolExpr> connectorConditions;
  private Map<ASTComponentInstance, BoolExpr> subcomponentConditions;
  private Map<ASTArcComponentType, BoolExpr> componentConditions;
  private Map<ASTArcBehaviorElement, BoolExpr> behaviorConditions;
  private Map<ASTArcField, BoolExpr> fieldConditions;
  private Map<ASTArcElement, BoolExpr> elementConditions;
  private Map<ASTArcStatechart, BoolExpr> statechartConditions;
  private Map<ASTMsgEvent, BoolExpr> messageeventConditions;
  private Map<PortSymbol, BoolExpr> portsymbolConditions;

  private static ASTArcPort findPortByProperties(Map<ASTArcPort, ?> map, PortSymbol symbol) {
    return map.keySet().stream()
      .filter(p -> p.getSymbol().getFullName().equals(symbol.getFullName()) && p.getSymbol().getType().print().equals(symbol.getType().print()) && p.getSymbol().isOutgoing() == symbol.isOutgoing())
      .findFirst()
      .orElse(null);
  }

  public ASTArcComponentType getOriginal() {
    return parent;
  }

  public ArrayList<VariableArcVariationPoint> getVariationPoints() {
    if (variationPoints == null) {
      variationPoints = new ArrayList<>();

      this.symbol.ifPresent(componentTypeSymbol -> variationPoints = new ArrayList<>(((VariableArcFullVariantComponentTypeSymbol) componentTypeSymbol).getIncludedVariationPoints()));
    }
    return variationPoints;
  }

  public Map<ASTArcPort, BoolExpr> getPortConditions() {

    if (portConditions == null) {
      portConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainBodyPorts = this.getOriginal().getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).collect(Collectors.toList());

      for (ASTArcPort mainPorts : mainBodyPorts) {
        portConditions.put(mainPorts, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationPorts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclarationTOP::getArcPortList).flatMap(List::stream).collect(Collectors.toList());
        for (ASTArcPort variationPort : variationPorts) {
          if (variationPort.isPresentSymbol() && variationPort.getSymbol().isPresentAstNode()) {
            ASTArcPort existingPort = findPortByProperties(portConditions, variationPort.getSymbol());

            if (existingPort != null) {
              BoolExpr merged = ctx.mkOr(portConditions.get(existingPort), variationExpr);
              portConditions.put(existingPort, merged);
            } else {
              portConditions.put(variationPort, variationExpr);
            }
          }
        }
      }
    }
    return portConditions;
  }

  public Map<ASTConnector, BoolExpr> getConnectorConditions() {
    if (connectorConditions == null) {
      connectorConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainBodyConnectors = this.getOriginal().getBody().getElementsOfType(ASTConnector.class);

      for (ASTConnector mainConnector : mainBodyConnectors) {
        connectorConditions.put(mainConnector, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationConnectors = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTConnector).map(v -> ((ASTConnector) v)).collect(Collectors.toList());
        for (ASTConnector variationConnector : variationConnectors) {
          connectorConditions.merge(variationConnector, variationExpr, ctx::mkOr);
        }
      }
    }
    return connectorConditions;
  }

  public Map<ASTComponentInstance, BoolExpr> getSubcomponentConditions() {
    if (subcomponentConditions == null) {
      subcomponentConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainBodySubcomponents = this.getOriginal().getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).collect(Collectors.toList());

      for (ASTComponentInstance mainBodySubcomponent : mainBodySubcomponents) {
        subcomponentConditions.put(mainBodySubcomponent, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationSubcomponents = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).collect(Collectors.toList());
        for (ASTComponentInstance variationSubcomponent : variationSubcomponents) {
          subcomponentConditions.merge(variationSubcomponent, variationExpr, ctx::mkOr);
        }
      }
    }
    return subcomponentConditions;
  }

  public Map<ASTArcComponentType, BoolExpr> getComponentConditions() {
    if (componentConditions == null) {
      componentConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      componentConditions.put(this.getOriginal(), ctx.mkTrue());

      var mainBodyComponents = this.getOriginal().getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcComponentType).map(l -> (ASTArcComponentType) l).collect(Collectors.toList());
      for (ASTArcComponentType mainBodyComponent : mainBodyComponents) {
        componentConditions.put(mainBodyComponent, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(variationPoint.getAllConditions()));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));

        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationComponents = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcComponentType).map(l -> (ASTArcComponentType) l).collect(Collectors.toList());
        for (ASTArcComponentType variationComponent : variationComponents) {
          componentConditions.merge(variationComponent, variationExpr, ctx::mkOr);
        }
      }
    }
    return componentConditions;
  }

  public Map<ASTArcBehaviorElement, BoolExpr> getBehaviorConditions() {
    if (behaviorConditions == null) {
      behaviorConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainBehaviors = this.getOriginal().getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcBehaviorElement).map(v -> ((ASTArcBehaviorElement) v)).collect(Collectors.toList());
      for (ASTArcBehaviorElement mainBehavior : mainBehaviors) {
        behaviorConditions.put(mainBehavior, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationBehaviors = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcBehaviorElement).map(v -> ((ASTArcBehaviorElement) v)).collect(Collectors.toList());
        for (ASTArcBehaviorElement variationBehavior : variationBehaviors) {
          behaviorConditions.merge(variationBehavior, variationExpr, ctx::mkOr);
        }
      }
    }
    return behaviorConditions;
  }

  public Map<ASTArcField, BoolExpr> getFieldConditions() {
    if (fieldConditions == null) {
      fieldConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainFields = this.getOriginal().getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFieldDeclaration).map(v -> ((ASTArcFieldDeclaration) v).getArcFieldList()).flatMap(List::stream).collect(Collectors.toList());
      for (ASTArcField mainField : mainFields) {
        fieldConditions.put(mainField, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationFields = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcFieldDeclaration).map(v -> ((ASTArcFieldDeclaration) v).getArcFieldList()).flatMap(List::stream).collect(Collectors.toList());
        for (ASTArcField variationField : variationFields) {
          fieldConditions.merge(variationField, variationExpr, ctx::mkOr);
        }
      }
    }
    return fieldConditions;
  }

  public Map<ASTArcElement, BoolExpr> getElementConditions() {
    if (elementConditions == null) {
      elementConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainElements = this.getOriginal().getBody().getArcElementList();
      for (ASTArcElement mainElement : mainElements) {
        elementConditions.put(mainElement, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationElements = variationPoint.getArcElements();
        for (ASTArcElement variationElement : variationElements) {
          elementConditions.merge(variationElement, variationExpr, ctx::mkOr);
        }
      }
    }
    return elementConditions;
  }

  public Map<ASTArcStatechart, BoolExpr> getStateChartConditions() {
    if (statechartConditions == null) {
      statechartConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainStateCharts = this.getOriginal().getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcStatechart).map(l -> (ASTArcStatechart) l).collect(Collectors.toList());
      for (ASTArcStatechart mainStateChart : mainStateCharts) {
        statechartConditions.put(mainStateChart, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationStateCharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(l -> (ASTArcStatechart) l).collect(Collectors.toList());
        for (ASTArcStatechart variationStateChart : variationStateCharts) {
          statechartConditions.merge(variationStateChart, variationExpr, ctx::mkOr);
        }
      }
    }
    return statechartConditions;
  }

  public Map<ASTMsgEvent, BoolExpr> getMessageEventConditions() {
    if (messageeventConditions == null) {
      messageeventConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainMessageEvents = this.getOriginal().getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).map(ASTArcStatechartTOP::getSCStatechartElementList).flatMap(List::stream).filter(n -> n instanceof ASTSCTransition).map(i -> (((ASTSCTransition) i))).map(o -> (ASTTransitionBody) o.getSCTBody()).filter(ASTTransitionBody::isPresentSCEvent).map(ASTTransitionBody::getSCEvent).filter(h -> h instanceof ASTMsgEvent).map(u -> (ASTMsgEvent) u).collect(Collectors.toList());
      for (ASTMsgEvent mainMessageEvent : mainMessageEvents) {
        messageeventConditions.put(mainMessageEvent, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationMessageEvents = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).map(ASTArcStatechartTOP::getSCStatechartElementList).flatMap(List::stream).filter(n -> n instanceof ASTSCTransition).map(i -> (((ASTSCTransition) i))).map(o -> (ASTTransitionBody) o.getSCTBody()).filter(ASTTransitionBody::isPresentSCEvent).map(ASTTransitionBody::getSCEvent).filter(h -> h instanceof ASTMsgEvent).map(u -> (ASTMsgEvent) u).collect(Collectors.toList());
        for (ASTMsgEvent variationMessageEvent : variationMessageEvents) {
          messageeventConditions.merge(variationMessageEvent, variationExpr, ctx::mkOr);
        }
      }
    }
    return messageeventConditions;
  }

  public Map<PortSymbol, BoolExpr> getPortSymbolConditions() {
    if (portsymbolConditions == null) {
      portsymbolConditions = new LinkedHashMap<>();

      ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
      Context ctx = ExpressionSolverService.getContext();

      List<VariableArcVariationPoint> variationPoints = getVariationPoints();

      var mainBodyPortSymbols = this.getOriginal().getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap((List::stream)).map(ASTArcPort::getSymbol).collect(Collectors.toList());

      for (PortSymbol mainPortSymbols : mainBodyPortSymbols) {
        portsymbolConditions.put(mainPortSymbols, ctx.mkTrue());
      }

      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        for (int i = 0; i < expr.get().length; i++) {
          expr.get()[i] = VariationConditionHelper.convertNotToEqualsFalse(expr.get()[i]);
        }

        BoolExpr variationExpr = ctx.mkAnd(expr.get());

        var variationPortSymbols = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclarationTOP::getArcPortList).flatMap(List::stream).map(ASTArcPort::getSymbol).collect(Collectors.toList());
        for (PortSymbol variationPortSymbol : variationPortSymbols) {
          portsymbolConditions.put(variationPortSymbol, variationExpr);
        }
      }
    }
    return portsymbolConditions;
  }
}
