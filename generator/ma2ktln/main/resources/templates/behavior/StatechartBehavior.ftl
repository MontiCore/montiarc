<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Comments.ftl" as Comment>
<#import "/templates/Header.ftl" as Header>
<#import "/templates/Ports.ftl" as Ports>
<#import "/templates/behavior/ComposedBehavior.ftl" as Event>
<#-- Prints initializing of the decomposed structure -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="montiarc.generator.ma2kotlin.codegen.TemplateUtilities" -->
<#-- @ftlvariable name="chart" type="arcautomaton._ast.ASTArcStatechart" -->
<#macro printStatechart>
    <#local tool = util.getStateTool()>
    <#local chart = tool.getStatechart(component).get()>
          state = run { when (state) {
    <#list util.getStateTool().getStates(chart) as state>
            State.${state.getName()} -> {
        <#list tool.getTransitions(chart)?filter(t -> tool.startsAt(state, t)) as transit>
              // ${state.getName()} -> ${transit.getTargetName()}
              if (<#rt>
            <#list tool.getTriggers(transit) as port>
                event.isFor(<@Ports.printAccess port=port/>) &&${" "}<#t>
            </#list>
            <#if tool.getGuard(transit).isPresent()>
                (${util.printExpression(tool.getGuard(transit).get())})!!<#t>
            <#else>
                true<#t>
            </#if>
            <#lt>) {
            <#if tool.getExitStatements(state)?has_content>
                exit${state.getName()}(event)
            </#if>
            <#if tool.getReaction(transit).isPresent()>
                <#lt>${util.printStatement(8, tool.getReaction(transit).get())}
            </#if>
            <#if tool.getEntryStatements(transit.getTargetNameDefinition())?has_content>
                enter${transit.getTargetName()}(event)
            </#if>
                return@run State.${transit.getTargetName()}
              }
        </#list>
        <#if tool.getDoStatements(state)?has_content>
              do${state.getName()}(event)
        </#if>
              return@run state
            }
    </#list>
          }}
</#macro>
<#macro printRequiredAttributes>
    <#local tool = util.getStateTool()>
    <#local chart = tool.getStatechart(component).get()>
    <@Comment.printOf node=chart/>
  enum class State {<#rt>
    <#list tool.getStates(chart) as state>
        ${state.getName()}<#sep>, </#sep><#t>
    </#list>
}
  var state = State.${chart.streamInitialStates().findFirst().get().getName()}
    <#list tool.getStates(chart) as state>
        <@printStateBehavior name="enter"+state.getName() actions=tool.getEntryStatements(state)/>
        <@printStateBehavior name="do"+state.getName() actions=tool.getDoStatements(state)/>
        <@printStateBehavior name="exit"+state.getName() actions=tool.getExitStatements(state)/>
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