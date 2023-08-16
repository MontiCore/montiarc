<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
protected void <@MethodNames.connectorSetup/>() {
    <#list ast.getConnectors() as conn>
        <#assign sourcePort><#if conn.getSource().isPresentComponent()>${prefixes.subcomp()}${conn.getSource().getComponent()}().</#if>${prefixes.port()}${conn.getSource().getPort()}()</#assign>
      <#list conn.getTargetList() as target>
          <#assign targetPort><#if target.isPresentComponent()>${prefixes.subcomp()}${target.getComponent()}().</#if>${prefixes.port()}${target.getPort()}()</#assign>
        ${sourcePort}.connect(${targetPort});
      </#list>
    </#list>
}