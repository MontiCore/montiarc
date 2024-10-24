<#-- (c) https://github.com/MontiCore/monticore -->

<#macro portSetup>setupPorts</#macro>

<#macro subCompSetup>setupSubComponents</#macro>

<#macro connectorSetup>setupDefaultConnectors</#macro>

<#macro behaviorSetup>setupBehavior</#macro>

<#macro sendTick>sendTick</#macro>

<#macro setupUnconnectedOutPorts>setupUnusedOutPorts</#macro>

<#macro handleModeAutomaton>handleModeAutomaton</#macro>
<#-- ArcModeSymbol mode -->
<#macro modeSetup mode>setupMode_${mode.getName()}</#macro>

<#-- ArcModeSymbol mode -->
<#macro modeTeardown mode>teardownMode_${mode.getName()}</#macro>

<#-- ArcModeSymbol mode -->
<#macro modeInit mode>initMode_${mode.getName()}</#macro>