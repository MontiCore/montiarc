/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._ast.ASTMsgEventTOP;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTAnteAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._ast.ASTArcFeatureDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Helper {
  
  public List<Object> asList(Object... args) {
    return List.of(args);
  }
  
  public <T> List<T> streamToList(Stream<T> stream) {
    return stream.collect(Collectors.toList());
  }
  
  public Optional<ASTArcStatechart> getAutomatonBehavior(ASTComponentType component) {
    Preconditions.checkNotNull(component);
    
    return component.getBody().getArcElementList().stream()
        .filter(el -> el instanceof ASTArcStatechart)
        .map(el -> (ASTArcStatechart) el)
        .findFirst();
  }
  
  /**
   * Get all transitions from the given statechart that are triggered by a message stimulus.
   * Messages are grouped by triggering port event name.
   *
   * @param sc the statechart from which transitions should be extracted
   *
   * @return event-triggered transitions grouped by triggering port
   */
  public Map<String, List<ASTSCTransition>> getTransitionsForPortEvents(ASTArcStatechart sc) {
    Map<String, List<ASTSCTransition>> result = new HashMap<>();
    sc.streamTransitions().forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if(body.isEmpty()) return;
      Optional<String> trigger = getTriggeringPortName(body.get());
      if(trigger.isEmpty()) return;
      if(result.containsKey(trigger.get())) result.get(trigger.get()).add(tr);
      else {
        ArrayList<ASTSCTransition> l = new ArrayList<>();
        l.add(tr);
        result.put(trigger.get(), l);
      }
    });
    return result;
  }
  
  /**
   * Get all transitions from the given statechart that are triggered by a tick event.
   *
   * @param sc the statechart from which transitions should be extracted
   *
   * @return all transitions triggered by a tick event
   */
  public List<ASTSCTransition> getTransitionsForTickEvent(ASTArcStatechart sc) {
    ArrayList<ASTSCTransition> result = new ArrayList<>();
    sc.streamTransitions().forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if(body.isEmpty() || !body.get().isPresentSCEvent()) return;
      if(!(body.get().getSCEvent() instanceof ASTMsgEvent)) return;
      if(ArcAutomatonMill.TICK.equals(((ASTMsgEvent) body.get().getSCEvent()).getName())) result.add(tr);
    });
    return result;
  }
  
  /**
   * Get all transitions from the given statechart that are not triggered by any event.
   *
   * @param sc the statechart from which transitions should be extracted
   *
   * @return all transitions without triggers
   */
  public List<ASTSCTransition> getTransitionsWithoutEvent(ASTArcStatechart sc) {
    ArrayList<ASTSCTransition> result = new ArrayList<>();
    sc.streamTransitions().forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty() || !body.get().isPresentSCEvent()) {
        result.add(tr);
      }
    });
    return result;
  }
  
  public Optional<ASTTransitionBody> getASTTransitionBody(ASTSCTransition transition) {
    if(transition.getSCTBody() instanceof ASTTransitionBody) {
      return Optional.of((ASTTransitionBody) transition.getSCTBody());
    }
    return Optional.empty();
  }
  
  public Optional<String> getTriggeringPortName(ASTTransitionBody body) {
    return Optional.of(body)
        .filter(ASTTransitionBody::isPresentSCEvent)
        .filter(bdy -> bdy.getSCEvent() instanceof ASTMsgEvent)
        .map(bdy -> (ASTMsgEvent) bdy.getSCEvent())
        .filter(event -> !ArcAutomatonMill.TICK.equals(event.getName()))
        .map(ASTMsgEventTOP::getName);
  }
  
  public boolean isEventBased(ASTArcStatechart sc) {
    return !sc.getTiming().matches(Timing.TIMED_SYNC);
  }
  
  public boolean isComponentInputTimeAware(ASTComponentType component) {
    return component.getSymbol()
        .getAllIncomingArcPorts().stream().findAny()
        .map(p -> p.getTiming() != Timing.UNTIMED).orElse(false);
  }
  
  public boolean isComponentOutputTimeAware(ASTComponentType component) {
    return component.getSymbol()
        .getAllOutgoingArcPorts().stream().findAny()
        .map(p -> p.getTiming() != Timing.UNTIMED).orElse(false);
  }
  
  public Map<String, ASTExpression> getArgNamesMappedToExpressions(ASTComponentInstance instance) {
    if(!instance.isPresentArcArguments()) return new HashMap<>();
    
    ComponentSymbol type = instance.getSymbol().getType().getTypeInfo();
    
    List<String> unsetParams = type.getParametersList().stream()
        .map(VariableSymbolTOP::getName).collect(Collectors.toList());
    
    Map<String, ASTExpression> result = new HashMap<>();
    instance.getArcArguments().streamArcArguments()
        .filter(ASTArcArgument::isPresentName)
        .forEach(arg -> {
          unsetParams.remove(arg.getName());
          result.put(arg.getName(), arg.getExpression());
        });
    instance.getArcArguments().forEachArcArguments(arg -> {
      if(!arg.isPresentName()) {
        result.put(unsetParams.remove(0), arg.getExpression());
      }
    });
    return result;
  }
  
  public List<?> getFeatures(@NotNull ASTComponentType ast) {
    Preconditions.checkNotNull(ast);
    
    return ast.getBody()
        .streamArcElementsOfType(ASTArcFeatureDeclaration.class)
        .flatMap(ASTArcFeatureDeclaration::streamArcFeatures)
        .collect(Collectors.toList());
  }
  
  public List<?> getConstraintExpressions(@NotNull ASTComponentType ast) {
    Preconditions.checkNotNull(ast);
    
    return ast.getBody()
        .streamArcElementsOfType(ASTArcConstraintDeclaration.class)
        .map(ASTArcConstraintDeclaration::getExpression)
        .collect(Collectors.toList());
  }
  
  public List<?> getNonPrimitiveParameters(@NotNull ASTComponentType ast) {
    return ast.getHead().streamArcParameters().filter(param -> !param.getSymbol().getType().isPrimitive()).collect(Collectors.toList());
  }
  
  public List<ASTMCBlockStatement> getAutomatonInitAction(ASTComponentType ast) {
    return getAutomatonBehavior(ast).flatMap(aut -> aut.streamInitialStates()
            .filter(init -> init.getSCSAnte() instanceof ASTAnteAction)
            .map(init -> (ASTAnteAction) init.getSCSAnte())
            .filter(anteAction -> anteAction.sizeMCBlockStatements() > 0)
            .findFirst()
        ).map(ASTAnteAction::getMCBlockStatementList)
        .orElse(new ArrayList<>());
  }
}