<#-- (c) https://github.com/MontiCore/monticore -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

<#-- Calculates the java expression to get the port object of the port access -->
<#-- ASTPortAccess portAccess, ASTArcMode enclosingMode, ASTComponentType enclosingComp -->
<#macro calcPortAccessor portAccess enclosingMode enclosingComp>
  <#assign portSym = portAccess.getPortSymbol()>
  <#assign portAccessorName>${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(enclosingComp, portSym)}</#assign>
  <#assign modeSubComps = helper.getInstanceSymbolsFromMode(enclosingMode)>
  <#if !portAccess.isPresentComponent()>  <#-- Port is part of enclosing comp -->
    <#-- We directly access the field because we need to know that the port is an InOutPort -->
    ${portAccessorName}
  <#else>
    <#assign portOwner = portAccess.getComponentSymbol()>
    <#assign portOwnerName = portOwner.getName()>
    <#assign portOwnerVariantSuffix = helper.subcomponentVariantSuffix(enclosingComp, portAccess.getComponentSymbol())>
    <#if modeSubComps?seq_contains(portOwner)>  <#-- Port is part of mode defined component -->
      <#assign portOwnerName>${prefixes.subcomp()}${enclosingMode.getName()}_${portOwnerName}${portOwnerVariantSuffix}</#assign>
      ${portOwnerName}().${portAccessorName}()
    <#else>
      <#assign portOwnerName>${prefixes.subcomp()}${portOwnerName}${portOwnerVariantSuffix}</#assign>
      ${portOwnerName}().${portAccessorName}()
    </#if>
  </#if>
</#macro>

<#-- Prints: The activation behavior of the transitions. An if-/else-Block will activate the first possible transition. -->
<#-- ASTArcMode mode, ASTModeAutomaton modeAutomaton -->
<#macro transitioningBehavior transitions>
  <#if transitions?size != 0>
    <#list transitions as transition>
      <#assign guardExpre = helper.getGuard(transition)>
      <#assign guardPrinted><#if guardExpre.isPresent()>${prettyPrinter.prettyprint(guardExpre.get())} <#else>true</#if></#assign>

      if (${guardPrinted}) {
        <@Log.info log_aspects.modeChange() "this.compName">
          Mode.${transition.getSourceName()} + "->" + Mode.${transition.getTargetName()}
        </@Log.info>

        this.currentMode = Mode.${transition.getTargetName()};
        this.context.<@MethodNames.modeTeardown transition.getSourceNameSymbol()/>();
        this.context.<@MethodNames.modeSetup transition.getTargetNameSymbol()/>();
        this.context.<@MethodNames.modeInit transition.getTargetNameSymbol()/>();
      }
      <#sep>else </#sep>
    </#list>
  </#if>
</#macro>