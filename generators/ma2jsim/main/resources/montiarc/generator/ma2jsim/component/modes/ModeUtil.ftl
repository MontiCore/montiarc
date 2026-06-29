<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

<#-- Calculates the java expression to get the port object of the port access -->
<#-- ASTPortAccess portAccess, ASTArcMode enclosingMode, ASTArcComponentType enclosingComp -->
<#macro calcPortAccessor portAccess enclosingMode enclosingComp>
  <#assign portSym = portAccess.getPortSymbol()>
  <#assign portAccessorName>${prefixes.port()}${portSym.getName()}${helper.getVariantHelper().portVariantSuffix(enclosingComp, portSym)}</#assign>
  <#assign modeSubComps = helper.getModeHelper().getInstanceSymbolsFromMode(enclosingMode)>
  <#if !portAccess.isPresentComponent()>  <#-- Port is part of enclosing comp -->
    <#-- We directly access the field because we need to know that the port is an InOutPort -->
    ${portAccessorName}
  <#else>
    <#assign portOwner = portAccess.getComponentSymbol()>
    <#assign portOwnerName = portOwner.getName()>
    <#assign portOwnerVariantSuffix = helper.getVariantHelper().subcomponentVariantSuffix(enclosingComp, portAccess.getComponentSymbol())>
    <#if modeSubComps?seq_contains(portOwner)>  <#-- Port is part of mode defined component -->
      <#assign portOwnerName>${prefixes.subcomp()}${enclosingMode.getName()}_${portOwnerName}${portOwnerVariantSuffix}</#assign>
      ${portOwnerName}().${portAccessorName}()
    <#else>
      <#assign portOwnerName>${prefixes.subcomp()}${portOwnerName}${portOwnerVariantSuffix}</#assign>
      ${portOwnerName}().${portAccessorName}()
    </#if>
  </#if>
</#macro>

<#-- Prints: The activation behavior of the transitions. The oracle is queried to select an enabled one-->
<#-- List<ASTSCTransition>, ASTModeAutomaton -->
<#macro transitioningBehavior transitions, automaton>
  <#if transitions?size != 0>

    <#-- We identify each transition by the index in the order in which they are declared in the model.
      -- We query the oracle based on this identifier -->
    java.util.Map<Integer, montiarc.rte.modes.ModeTransition> enabledTransitions = new java.util.LinkedHashMap<>();

    <#list transitions as transition>
      <#assign guardExpre = helper.getBehaviorHelper().getGuard(transition)>
      <#assign guardPrinted><#if guardExpre.isPresent()>${javaPrinter.generateCode(guardExpre.get())} <#else>true</#if></#assign>
      <#assign transitionIndex = helper.getModeHelper().getTransitionIndex(transition, automaton)>

      // Transition: ${helper.getBehaviorHelper().printTransitionSignature(transition)}
      if (${guardPrinted}) {
        enabledTransitions.put(${transitionIndex}, this::transition_${transitionIndex});
      }
    </#list>

    if (!enabledTransitions.isEmpty()) {
      this.context.getOracle().decideAmong(enabledTransitions).execute();
    }
  </#if>
</#macro>

<#-- Logging related stuff -->

<#macro printAddedModeElements addedSubs addedConnectors>
  " Adding"
  <#if addedSubs?size != 0>
    + " subs = {<#list addedSubs as s>${s.getName()}<#sep>, </#list>}"
  </#if>
  <#if addedConnectors?size != 0>
    + " connectors = {<#list addedConnectors as c>${c.getSourceName()} -> <@formatStringList c.getTargetsNames()/><#sep>, </#list>}"
  </#if>
  + ";"
</#macro>

<#-- Put [square brackets] around list content, if content size != 1-->
<#macro formatStringList list><#if list?size != 1>[</#if>${list?join(", ")}<#if list?size != 1>]</#if></#macro>
