<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Comments.ftl" as Comment>
<#import "/templates/Header.ftl" as Header>
<#import "/templates/behavior/ComposedBehavior.ftl" as Events>
<#import "/templates/behavior/StatechartBehavior.ftl" as Chart>
<#-- Prints initializing of the decomposed structure -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="TemplateUtilities" -->
<#-- @ftlvariable name="chart" type="arcautomaton._ast.ASTArcStatechart" -->
<#-- @ftlvariable name="state" type="StateWrapper" -->
<#-- @ftlvariable name="transit" type="TransitionWrapper" -->
<#macro printSchedule>
  <#if util.getTiming(component) == "untimed">
      <@printAtomicBehavior/>
  <#else>
      when (event) {
        is TickEvent -> tickOutputs()
        is ${Events.getMessageType()} -> {
          <@printAtomicBehavior/>
        }
      }
  </#if>
</#macro>
<#macro printAtomicBehavior>
    <#if util.getStatechart(component).isPresent()>
        <@Chart.printStatechart/>
    <#else>
        <@printAlternative/>
    </#if>
</#macro>
<#macro printAlternative>
    <#list util.getComputes(component).toArray() as compute>
          // compute
        <@Comment.printOf node=compute/>
${util.printStatement(5, compute.getMCBlockStatement())}
    <#else>
          // delegate behavior
          Interface(this, event).let {behaviorImplementation.compute(it); it.pushAll()}
    </#list>

</#macro>