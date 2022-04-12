<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Comments.ftl" as Comment>
<#import "/templates/Header.ftl" as Header>
<#-- Prints initializing of the decomposed structure -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="TemplateUtilities" -->
<#-- @ftlvariable name="chart" type="arcautomaton._ast.ASTArcStatechart" -->
<#-- @ftlvariable name="state" type="StateWrapper" -->
<#-- @ftlvariable name="transit" type="TransitionWrapper" -->
<#macro printSchedule>
      when (event) {
        is TickEvent -> tickOutputs()
        is ForwardEvent -> forward(event)
    <#if util.getModeTool().streamAutomata(component.getAstNode()).count() != 0>
        is ${getMessageType()} -> {
          guard.lastEvent = event
          reconfigure(modeAutomaton.update())
        }
    </#if>
      }
</#macro>
<#function getMessageType>
    <#if util.getTiming(component)?ends_with("timed")>
      <#return "SingleMessageEvent"/>
    </#if>
    <#return "SyncEvent"/>
</#function>