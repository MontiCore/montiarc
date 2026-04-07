/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._ast.ASTMsgEvent;
import arcautomaton._symboltable.Port2EventDefAdapter;
import arcbasis._ast.ASTArcComponentType;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.scactions._ast.ASTSCABody;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCStateElement;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scevents._symboltable.SCEventDefSymbol;
import de.monticore.scstatehierarchy._ast.ASTSCHierarchyBody;
import de.monticore.scstatehierarchy._ast.ASTSCInternTransition;
import de.monticore.scstateinvariants._ast.ASTSCInvState;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import montiarc.MontiArcMill;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import variablearc._ast.ASTVariantArcComponentType;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class BehaviorHelper {

  public Optional<ASTArcStatechart> getAutomatonBehavior(ASTArcComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().streamArcElementsOfType(ASTArcStatechart.class).findFirst();
  }

  public Optional<ASTArcCompute> getComputeBehavior(ASTArcComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().streamArcElementsOfType(ASTArcCompute.class).findFirst();
  }

  public Optional<ASTArcInit> getComputeInit(ASTArcComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().streamArcElementsOfType(ASTArcInit.class).findFirst();
  }

  protected Map<PortSymbol, List<ASTSCTransition>> getTransitionsMappedToPortTriggers(ASTArcComponentType enclosingComponent, Stream<ASTSCTransition> transitions) {
    Map<PortSymbol, List<ASTSCTransition>> result = new HashMap<>();
    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty()) return;
      Optional<PortSymbol> trigger = getTriggeringPortSymbol(enclosingComponent, body.get());
      if (trigger.isEmpty()) return;
      if (result.containsKey(trigger.get())) result.get(trigger.get()).add(tr);
      else {
        ArrayList<ASTSCTransition> l = new ArrayList<>();
        l.add(tr);
        result.put(trigger.get(), l);
      }
    });
    return result;
  }


  public Optional<ASTExpression> getGuard(ASTSCTransition transition) {
    if (transition.getSCTBody() instanceof ASTTransitionBody) {
      ASTTransitionBody body = (ASTTransitionBody) transition.getSCTBody();
      return body.isPresentPre() ? Optional.of(body.getPre()) : Optional.empty();
    } else {
      return Optional.empty();
    }
  }


  public List<ASTSCTransition> getAllTransitionsForPortWithEventTrigger(ASTArcComponentType enclosingComponent, ASTArcStatechart sc, PortSymbol port) {
    return getAllTransitionsForPort(enclosingComponent, sc.streamTransitions(), port);
  }


  public List<ASTSCTransition> getAllTransitionsForPort(ASTArcComponentType enclosingComponent, Stream<ASTSCTransition> transitions, PortSymbol port) {
    List<ASTSCTransition> result = new ArrayList<>();

    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty()) return;
      Optional<PortSymbol> trigger = getTriggeringPortSymbol(enclosingComponent, body.get());
      if (trigger.isEmpty()) return;
      if (trigger.get().getName().equals(port.getName())) {
        result.add(tr);
      }
    });
    return result;
  }


  public List<ASTSCInternTransition> getAllInnerTransitionsForPortWithEventTrigger(ASTArcComponentType enclosingComponent, ASTArcStatechart sc, PortSymbol port) {
    List<ASTSCInternTransition> result = new ArrayList<>();

    List<ASTSCInternTransition> transitions = getInnerTransitions(sc);

    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty()) return;
      Optional<PortSymbol> trigger = getTriggeringPortSymbol(enclosingComponent, body.get());
      if (trigger.isEmpty()) return;

      if (trigger.get().getName().equals(port.getName())) {
        result.add(tr);
      }
    });
    return result;
  }


  public List<ASTSCInternTransition> getInnerTransitionsWithoutTrigger(ASTArcStatechart sc) {
    List<ASTSCInternTransition> transitions = this.getInnerTransitions(sc);

    ArrayList<ASTSCInternTransition> result = new ArrayList<>();
    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty() || !body.get().isPresentSCEvent()) {
        result.add(tr);
      }
    });
    return result;
  }


  public boolean isInnerTransition(ASTSCStateElement transition) {
    return MontiArcMill.typeDispatcher().isSCStateHierarchyASTSCInternTransition(transition);
  }


  public List<ASTSCInternTransition> getInnerTransitions(ASTArcStatechart sc) {
    List<ASTSCInternTransition> result = new ArrayList<>();
    for (ASTSCState state : sc.getStates()) {
      Optional<ASTSCHierarchyBody> bodyOpt = getBodyOfHierarchicalState(state);
      if (bodyOpt.isEmpty()) continue;

      for (ASTSCStateElement elem : bodyOpt.get().getSCStateElementList()) {
        if (MontiArcMill.typeDispatcher().isSCStateHierarchyASTSCInternTransition(elem)) {
          ASTSCInternTransition innerTr =
            MontiArcMill.typeDispatcher().asSCStateHierarchyASTSCInternTransition(elem);
          result.add(innerTr);
        }
      }
    }
    return result;
  }


  public Optional<ASTTransitionBody> getASTTransitionBody(ASTSCInternTransition transition) {
    if (transition.getSCTBody() instanceof ASTTransitionBody) {
      return Optional.of((ASTTransitionBody) transition.getSCTBody());
    }
    return Optional.empty();
  }


  public ASTSCState getInnerTransitionState(ASTArcStatechart sc, ASTSCInternTransition transition) {
    for (ASTSCState state : sc.getStates()) {
      Optional<ASTSCHierarchyBody> bodyOpt = getBodyOfHierarchicalState(state);
      if (bodyOpt.isEmpty()) continue;
      for (ASTSCStateElement elem : bodyOpt.get().getSCStateElementList()) {
        if (elem == transition) {
          return state;
        }
      }
    }
    return null;
  }

  /**
   * Get all transitions from the given statechart that are not triggered by any event.
   *
   * @param sc the statechart from which transitions should be extracted
   * @return all transitions without triggers
   */
  public List<ASTSCTransition> getTransitionsWithoutEvent(ASTArcStatechart sc) {
    return filterTransitionsWithoutTrigger(sc.streamTransitions());
  }


  public List<ASTSCTransition> filterTransitionsWithoutTrigger(Stream<ASTSCTransition> transitions) {
    ArrayList<ASTSCTransition> result = new ArrayList<>();
    transitions.forEach(tr -> {
      Optional<ASTTransitionBody> body = getASTTransitionBody(tr);
      if (body.isEmpty() || !body.get().isPresentSCEvent()) {
        result.add(tr);
      }
    });
    return result;
  }

  public Optional<ASTTransitionBody> getASTTransitionBody(ASTSCTransition transition) {
    if (transition.getSCTBody() instanceof ASTTransitionBody) {
      return Optional.of((ASTTransitionBody) transition.getSCTBody());
    }
    return Optional.empty();
  }

  public Optional<PortSymbol> getTriggeringPortSymbol(ASTArcComponentType componentType, ASTTransitionBody body) {
    Predicate<SCEventDefSymbol> predicate;
    if (componentType instanceof ASTVariantArcComponentType) {
      predicate = ((VariableArcVariantComponentTypeSymbol) componentType.getSymbol())::containsSymbol;
    } else {
      predicate = e -> true;
    }
    return Optional.of(body)
      .filter(ASTTransitionBody::isPresentSCEvent)
      .filter(bdy -> bdy.getSCEvent() instanceof ASTMsgEvent)
      .map(bdy -> (ASTMsgEvent) bdy.getSCEvent())
      .map(event -> event.getEnclosingScope().resolveSCEventDefMany(event.getName(), predicate).stream().findFirst())
      .filter(Optional::isPresent)
      .filter(sym -> sym.get() instanceof Port2EventDefAdapter)
      .map(sym -> ((Port2EventDefAdapter) sym.get()).getAdaptee());
  }


  public List<ASTSCState> getSubstates(ASTSCState state) {
    if (!state.isPresentSCSBody())
      return new ArrayList<>();

    List<ASTSCState> list = new ArrayList<>();
    for (ASTSCStateElement s : (((ASTSCHierarchyBody) state.getSCSBody()).getSCStateElementList())) {
      if (MontiArcMill.typeDispatcher().isSCBasisASTSCState(s))
        list.add(MontiArcMill.typeDispatcher().asSCBasisASTSCState(s));
    }
    return list;
  }

  public List<ASTSCStateElement> getInitialSubstates(ASTSCState state) {
    return getSubstates(state).stream().filter(s -> s.getSCModifier().isInitial()).collect(Collectors.toList());
  }

  public List<ASTSCState> getStates(ASTArcComponentType component) {
    Preconditions.checkNotNull(component);
    return this.getAutomatonBehavior(component).get().getStates();
  }

  public Optional<ASTMCStatement> getEntryAction(ASTSCState state) {
    for (ASTSCStateElement s : getHierarchyElementsOf(state)) {
      if (MontiArcMill.typeDispatcher().isSCActionsASTSCEntryAction(s)) {
        ASTSCABody actionBody = MontiArcMill.typeDispatcher()
          .asSCActionsASTSCEntryAction(s)
          .getSCABody();
        return getStatementOfAction(actionBody);
      }
    }
    return Optional.empty();
  }

  /**
   * New function getStateInvariant that gets a state as an input
   * and checks if an invariant is Present. If so the invariant is
   * printed and then returned to be used in the generation of States
   * via the States.ftl template. If no invariant is Present an empty
   * String is returned, which is interpreted as an invariant that only
   * consists of true in the entry-Action of each state.
   */
  public String getStateInvariant(ASTSCState state) {
    if (state instanceof ASTSCInvState) {
      ASTSCInvState invState = (ASTSCInvState) state;

      IndentPrinter printer = new IndentPrinter();
      MontiArcFullPrettyPrinter expPrinter = new MontiArcFullPrettyPrinter(printer, true);

      expPrinter.prettyprint(invState.getExpression());

      return printer.getContent();
    }
    return "";
  }

  public Optional<ASTMCStatement> getExitAction(ASTSCState state) {
    for (ASTSCStateElement s : getHierarchyElementsOf(state)) {
      if (MontiArcMill.typeDispatcher().isSCActionsASTSCExitAction(s)) {
        ASTSCABody actionBody = MontiArcMill.typeDispatcher()
          .asSCActionsASTSCExitAction(s)
          .getSCABody();
        return getStatementOfAction(actionBody);
      }
    }

    return Optional.empty();
  }

  public Optional<ASTMCStatement> getDoAction(ASTSCState state) {
    for (ASTSCStateElement s : getHierarchyElementsOf(state)) {
      if (MontiArcMill.typeDispatcher().isSCDoActionsASTSCDoAction(s)) {
        ASTSCABody actionBody = MontiArcMill.typeDispatcher()
          .asSCDoActionsASTSCDoAction(s)
          .getSCABody();
        return getStatementOfAction(actionBody);
      }
    }

    return Optional.empty();
  }

  /**
   * Hierarchy elements of the state are returned if it is an hierarchical state.
   * Else, the list is empty
   */
  private List<ASTSCStateElement> getHierarchyElementsOf(ASTSCState state) {
    return this.getBodyOfHierarchicalState(state)
      .map(ASTSCHierarchyBody::getSCStateElementList)
      .orElse(Collections.emptyList());
  }

  /** Return the body of a hierarchical state, given that the state body is an {@link ASTSCHierarchyBody}. */
  private Optional<ASTSCHierarchyBody> getBodyOfHierarchicalState(ASTSCState state) {
    if (state.isPresentSCSBody() && MontiArcMill.typeDispatcher().isSCStateHierarchyASTSCHierarchyBody(state.getSCSBody())) {
      return Optional.of(MontiArcMill.typeDispatcher().asSCStateHierarchyASTSCHierarchyBody(state.getSCSBody()));
    } else {
      return Optional.empty();
    }
  }

  /** Return the block statement, given that the action body is an {@link ASTTransitionAction}. */
  private Optional<ASTMCStatement> getStatementOfAction(ASTSCABody actionBody) {
    if (MontiArcMill.typeDispatcher().isSCTransitions4CodeASTTransitionAction(actionBody)) {
      ASTTransitionAction actualAction = MontiArcMill.typeDispatcher().asSCTransitions4CodeASTTransitionAction(actionBody);
      return Optional.of(actualAction.getMCStatement());
    } else {
      return Optional.empty();
    }
  }

  /** Prints the transition signature: Source, Target, Guard, and Event. But not the action. */
  public String printTransitionSignature(ASTSCTransition tr) {
    StringBuilder builder = new StringBuilder(tr.getSource().getName() + " -> " + tr.getTarget().getName());

    if (tr.getSCTBody() instanceof ASTTransitionBody) {
      IndentPrinter printer = new IndentPrinter();
      MontiArcFullPrettyPrinter pp = new MontiArcFullPrettyPrinter(printer, false);

      ASTTransitionBody body = (ASTTransitionBody) tr.getSCTBody();

      if (body.isPresentPre()) {
        builder
          .append(" [")
          .append(pp.prettyprint(((ASTTransitionBody) tr.getSCTBody()).getPre()))
          .append("]");
      }

      if (body.isPresentSCEvent()) {
        builder
          .append(" ")
          .append(pp.prettyprint(body.getSCEvent()));
      }
    }

    return builder.toString();
  }
}
