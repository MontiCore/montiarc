<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign automaton = helper.getAutomatonBehavior(ast)/>
<#assign hasAutomaton = automaton.isPresent()/>
<#assign isEvent = hasAutomaton && helper.isEventBased(automaton.get())/>
<#assign inTimed = helper.isComponentInputTimeAware(ast)/>
<#assign outTimed = helper.isComponentOutputTimeAware(ast)/>

protected ${ast.getName()}${suffixes.automaton()} automaton;

@Override
public ${ast.getName()}${suffixes.automaton()} <@MethodNames.getBehavior/>() { return automaton; }

protected void <@MethodNames.behaviorSetup/>() {
    this.automaton = new ${ast.getName()}${suffixes.automaton()}${suffixes.builder()}(this)
    <#if !isEvent>
        .addDefaultTransitions()
    </#if>
      .addDefaultStates()
      .setDefaultInitial()
      .build();
}

<#if isEvent && inTimed>
    protected void <@MethodNames.handleTick/>() {
      if(<@MethodNames.inputsTickBlocked/>()) {
        <@MethodNames.dropTickOnAll/>();
        <@MethodNames.getBehavior/>().tick();
        <@MethodNames.sendTickOnAll/>();
        getAllInPorts().forEach(montiarc.rte.port.ITimeAwareInPort::continueAfterDroppedTick);
      }
    }
<#elseif !isEvent>
    protected void <@MethodNames.handleSyncComputation/>() {
      if(getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.AbstractInPort)
        .map(p -> (montiarc.rte.port.AbstractInPort<?>) p)
        .anyMatch(montiarc.rte.port.AbstractInPort::isBufferEmpty)) {
          return;
      }

      <#if inTimed>
          if (<@MethodNames.inputsTickBlocked/>()) {
          <@MethodNames.dropTickOnAll/>();
          <#if outTimed>
              <@MethodNames.sendTickOnAll/>();
          </#if>
          <@MethodNames.handleSyncComputation/>();
          return;
          }
      </#if>

      if (<@MethodNames.getBehavior/>().canExecuteTransition()) {
        <@MethodNames.getBehavior/>().executeAnyValidTransition();
      } else {
        getAllInPorts().stream()
          .filter(p -> p instanceof montiarc.rte.port.AbstractInPort)
          .map(p -> (montiarc.rte.port.AbstractInPort<?>) p)
          .forEach(montiarc.rte.port.AbstractInPort::pollBuffer);
      }
    }
</#if>