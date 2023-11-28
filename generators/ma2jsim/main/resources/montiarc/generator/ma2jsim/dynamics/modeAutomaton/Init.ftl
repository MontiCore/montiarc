<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#assign initialMode=helper.getInitialModes(helper.getModeAutomaton(ast).get())?first/>
public void init() {
  <#list helper.getConnectors(initialMode) as conn>
      <#list conn.getTargetList() as target>
          <@idPortAccess conn.getSource() initialMode/>.connect(<@idPortAccess target initialMode/>);
      </#list>
  </#list>
}

<#macro idPortAccess pA mode>
    context.<#if pA.isPresentComponent()><#if helper.instanceInMode(pA, mode)>${prefixes.mode()}${mode.getName()}_</#if>${prefixes.subcomp()}${pA.getComponent()}().</#if>${prefixes.port()}${pA.getPort()}()
</#macro>