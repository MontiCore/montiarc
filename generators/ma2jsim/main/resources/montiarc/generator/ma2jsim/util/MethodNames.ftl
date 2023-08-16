<#-- (c) https://github.com/MontiCore/monticore -->

<#-- PortSymbol port -->
<#macro handleBufferImplementation port>handleMessageOn${port.getName()?cap_first}</#macro>

<#macro portSetup>setupPorts</#macro>

<#macro subCompSetup>setupSubComponents</#macro>

<#macro connectorSetup>setupDefaultConnectors</#macro>

<#macro behaviorSetup>setupBehavior</#macro>

<#macro handleTick>handleTickEvent</#macro>

<#macro getBehavior>getBehavior</#macro>

<#macro handleSyncComputation>handleComputationOnSyncPorts</#macro>

<#macro inputsTickBlocked>areAllInputsTickBlocked</#macro>

<#macro dropTickOnAll>dropTickOnAllInputs</#macro>

<#macro sendTickOnAll>sendTickOnAllOutputs</#macro>