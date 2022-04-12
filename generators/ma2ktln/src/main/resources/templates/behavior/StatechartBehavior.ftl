<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Comments.ftl" as Comment>
<#import "/templates/Header.ftl" as Header>
<#import "/templates/Ports.ftl" as Ports>
<#import "/templates/behavior/ComposedBehavior.ftl" as Event>
<#-- Prints initializing of the decomposed structure -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="TemplateUtilities" -->
<#-- @ftlvariable name="chart" type="arcautomaton._ast.ASTArcStatechart" -->
<#-- @ftlvariable name="state" type="StateWrapper" -->
<#-- @ftlvariable name="transit" type="TransitionWrapper" -->
<#macro printStatechart>
    <#local chart = util.getStatechart(component).get()>
          state = run { when (state) {
    <#list chart.streamStates().map(util.transformState()).toArray() as state>
            State.${state.getName()} -> {
        <#list chart.streamTransitions().map(util.transformTransition()).toArray()?filter(t -> t.startsAt(state)) as transit>
              // ${state.getName()} -> ${transit.getTarget().getName()}
              if (<#rt>
            <#list transit.getTriggers() as port>
                event.isFor(<@Ports.printAccess port=port/>) &&${" "}<#t>
            </#list>
            <#if transit.getGuard().isPresent()>
                ${util.printExpression(transit.getGuard().get())}<#t>
            <#else>
                true<#t>
            </#if>
            <#lt>) {
            <#if state.getExitStatements()?has_content>
                exit${state.getName()}(event)
            </#if>
            <#if transit.getReaction().isPresent()>
                <#lt>${util.printStatement(8, transit.getReaction().get().getMCBlockStatement())}
            </#if>
            <#if transit.getTarget().getEntryStatements()?has_content>
                enter${transit.getTargetName()}(event)
            </#if>
                return@run State.${transit.getTarget().getName()}
              }
        </#list>
        <#if state.getDoStatements()?has_content>
              do${state.getName()}(event)
        </#if>
              return@run state
            }
    </#list>
          }}
</#macro>
<#macro printRequiredAttributes>
    <#local chart = util.getStatechart(component).get()>
    <@Comment.printOf node=chart/>
  enum class State {<#rt>
    <#list chart.streamStates().toArray() as state>
        ${state.getName()}<#sep>, </#sep><#t>
    </#list>
}
  var state = State.${chart.streamInitialStates().findFirst().get().getName()}
    <#list chart.streamStates().map(util.transformState()).toArray() as state>
        <@printStateBehavior name="enter"+state.getName() actions=state.getEntryStatements()/>
        <@printStateBehavior name="do"+state.getName() actions=state.getDoStatements()/>
        <@printStateBehavior name="exit"+state.getName() actions=state.getExitStatements()/>
    </#list>
</#macro>
<#macro printStateBehavior name actions>
    <#list actions>

  private suspend fun ${name}(event: ${Event.getMessageType()}) {
        <#items as statement>
            <#lt>${util.printStatement(2, statement)}
        </#items>
  }
    </#list>
</#macro>