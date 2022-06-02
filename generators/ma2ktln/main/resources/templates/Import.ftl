<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/templates/Comments.ftl" as Comment>
<#-- Prints import-statements -->
<#-- @ftlvariable name="component" type="arcbasis._symboltable.ComponentTypeSymbol" -->
<#-- @ftlvariable name="util" type="montiarc.generator.ma2kotlin.codegen.TemplateUtilities" -->
<#macro printImports>
<@printSimulatorImports/>
<@printComponentImports/>
</#macro>
<#macro printSimulatorImports>
  <#list ["comp", "conf", "log", "modeautomata", "msg", "port", "reactions", "sched.util", "sim"] as entry>
import dsim.${entry}.*
  </#list>
import dsim.sched.<#if component.isAtomic()>atomic<#else>decomposed</#if>.*
import openmodeautomata.runtime.*
import kotlinx.coroutines.flow.collect
</#macro>
<#macro printComponentImports>
  <#list getOuter(component).getAstNode().getEnclosingScope().getImportsList()as entry>
<#t>${util.getTypes().printImport(entry)}
  </#list>
</#macro>
<#function getOuter symbol>
    <#if symbol.getOuterComponent().isPresent()>
        <#return getOuter(symbol=symbol.getOuterComponent.get())>
    </#if>
    <#return symbol>
</#function>
