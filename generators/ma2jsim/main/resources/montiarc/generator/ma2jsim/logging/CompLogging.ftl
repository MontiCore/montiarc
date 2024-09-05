<#-- (c) https://github.com/MontiCore/monticore -->

<#--
  -- 1. Nested arguments
  -- The provided logging macros utilize "nested" freemarker macros.
  -- This means that in the macro definition, the tag <#nested> may appear.
  -- This tag is a placeholder some content that is defined by the caller within the
  -- opening and closing tag of the macro call, e.g. <@info ...>messageToBeLogged</@log>.
  -- This "nested" parameter represents the message to be logged.
  --
  -- 2. argument "qualCompInstanceNameSupplier"
  -- When generating a code for a component type, we only know the component type name.
  -- But for logging purposes, we need the name of the actual instance which is not known
  -- before the runtime of the components. Therefore, we can only retrieve it at runtime.
  -- Hence, the macro argument for the component instance must be actual java code that
  -- supplies the component instance name. E.g. call the macro with
  -- <@info "StateChange" "this.getName()">...</@info>
  -->

<#-- For explication about how this macro works, see the beginning of the file -->
<#macro info aspect qualCompInstanceNameSupplier throwable=""> <#-- TODO: communicate supplier -->
  <@log "info" throwable "${qualCompInstanceNameSupplier} + \"#${aspect}\"">
    <#-- When <@info...>content</@info...> is called, the content will be placed into #nested,
        which by itself is the argument fro the <#nested> tag of the <@log> macro -->
    <#nested>
  </@log>
</#macro>

<#-- For explication about how this macro works, see the beginning of the file -->
<#macro trace aspect qualCompInstanceNameSupplier throwable="">
  <@log "trace" throwable "${qualCompInstanceNameSupplier}" + "\"#${aspect}\"">
    <#nested>  <#-- represents the msg content-->
  </@log>
</#macro>

<#-- For explication about how this macro works, see the beginning of the file -->
<#macro debug aspect qualCompInstanceNameSupplier throwable="">
  <@log "debug" throwable "${qualCompInstanceNameSupplier}" + "\"#${aspect}\"">
    <#nested>  <#-- represents the msg content-->
  </@log>
</#macro>

<#-- For explication about how this macro works, see the beginning of the file -->
<#macro error aspect qualCompInstanceNameSupplier throwable="">
  <@log "error" throwable>
    ${qualCompInstanceNameSupplier} + "#${aspect} - <#nested>"  <#-- #nested represents the msg content-->
  </@log>
</#macro>

<#-- For explication about how this macro works, see the beginning of the file -->
<#macro warn aspect qualCompInstanceNameSupplier throwable="">
  <@log "warn" throwable>
    ${qualCompInstanceNameSupplier} + "#${aspect} - <#nested>"  <#-- #nested represents the msg content-->
  </@log>
</#macro>

<#-- For explication about how this macro works, see the beginning of the file -->
<#macro log level throwable="" logName="">
  <#assign addLogName = level == "info" || level == "debug" || level == "trace">
  <#assign addThrowable = throwable != "">
  de.se_rwth.commons.logging.Log.${level}(
    <#nested>  <#-- represents the msg content-->
    <#if addThrowable>, ${throwable}</#if>
    <#if addLogName>, ${logName}</#if>
  );
</#macro>