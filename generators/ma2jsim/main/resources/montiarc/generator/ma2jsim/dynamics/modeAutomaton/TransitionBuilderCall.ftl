<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("transition" "shadowedInPorts")}
<#assign body = helper.getASTTransitionBody(transition)/>
new montiarc.rte.automaton.TransitionBuilder()
.setSource(${ast.getName()}${suffixes.modes()}.${prefixes.mode()}${transition.getSourceName()})
.setTarget(${ast.getName()}${suffixes.modes()}.${prefixes.mode()}${transition.getTargetName()})
.setGuard(() ->
<#if body.isPresent() && body.get().isPresentPre()>
    {
    ${tc.includeArgs("montiarc/generator/ma2jsim/behavior/ShadowInputs.ftl", [shadowedInPorts, false])}
    return ${prettyPrinter.prettyprint(body.get().getPre())};
    }
<#else>
    true
</#if>
)
.setAction(() -> {
    // disconnect connectors defined in source mode
  <#assign srcMode=transition.getSourceNameSymbol().getAstNode()/>
  <#list helper.getConnectors(srcMode) as sourceConn>
      <@disconnect sourceConn srcMode/>
  </#list>

    // connect connectors defined in target mode
  <#assign trgMode=transition.getTargetNameSymbol().getAstNode()/>
  <#list helper.getConnectors(trgMode) as targetConn>
      <@connect targetConn trgMode/>
  </#list>
})
.build()

<#macro connect conn mode>
    <#list conn.getTargetList() as target>
        <@idPortAccess conn.getSource() mode/>.connect(<@idPortAccess target mode/>);
    </#list>
</#macro>

<#macro disconnect conn mode>
    <#list conn.getTargetList() as target>
        <@idPortAccess conn.getSource() mode/>.disconnect(<@idPortAccess target mode/>);
    </#list>
</#macro>

<#macro idPortAccess pA mode>
    context.<#if pA.isPresentComponent()><#if helper.instanceInMode(pA, mode)>${prefixes.mode()}${mode.getName()}_</#if>${prefixes.subcomp()}${pA.getComponent()}().</#if>${prefixes.port()}${pA.getPort()}()
</#macro>
