<#-- (c) https://github.com/MontiCore/monticore -->

<#-- ArcPortSymbol port -->
<#macro handleBufferImplementation port>handleMessageOn${port.getName()?cap_first}</#macro>

<#macro portSetup>setupPorts</#macro>

<#macro subCompSetup>setupSubComponents</#macro>

<#macro dynSubCompSetup>setupDynamicSubComponents</#macro>

<#macro connectorSetup>setupDefaultConnectors</#macro>

<#macro behaviorSetup>setupBehavior</#macro>

<#macro handleTick>handleTick</#macro>

<#macro getBehavior>getBehavior</#macro>

<#macro inputsTickBlocked>areAllInputsTickBlocked</#macro>

<#macro inputsTickBuffered>areAllInputsTickBuffered</#macro>

<#macro dropTickOnAll>dropTickOnAllInputs</#macro>

<#macro sendTick>sendTick</#macro>

<#macro sendTickOnAll>sendTickOnAllOutputs</#macro>

<#macro modes>modes</#macro>

<#macro handleModeAutomaton>handleModeAutomaton</#macro>

<#macro getModeAutomaton>getModeAutomaton</#macro>