<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSym", "hasAutomaton", "isEvent")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
protected void <@MethodNames.handleBufferImplementation portSym/>() {
  <#if hasAutomaton>
      <#if isEvent>
          if(${prefixes.port()}${portSym.getName()}.isBufferEmpty()) return;

          if(${prefixes.port()}${portSym.getName()}.isTickBlocked()) {
            <@MethodNames.handleTick/>();
            return;
          }

          <@MethodNames.getBehavior/>().${prefixes.message()}${portSym.getName()}();
      <#else>
          <@MethodNames.handleSyncComputation/>();
      </#if>
  <#else>
      <@MethodNames.handleSyncComputation/>();
  </#if>
}