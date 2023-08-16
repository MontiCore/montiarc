<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSym", "hasAutomaton", "isEvent", "isTimed")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#assign automaton = helper.getAutomatonBehavior(ast)/>
protected void handleMessageOn${portSym.getName()?cap_first}() {
  <#if hasAutomaton>
      <#if isEvent>
          if(${prefixes.port()}${portSym.getName()}.isBufferEmpty()) return;

          <#if isTimed>
              if(${prefixes.port()}${portSym.getName()}.isTickBlocked()) {
                <@MethodNames.handleTick/>();
                return;
              }
          </#if>

          <@MethodNames.getBehavior/>().msg_${portSym.getName()}();
      <#else>
          <@MethodNames.handleSyncComputation/>();
      </#if>
  </#if>
}