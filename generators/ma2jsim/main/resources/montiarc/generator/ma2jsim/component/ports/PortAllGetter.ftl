<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<@allInPortsGetter/>
<@allOutPortsGetter/>
<@allSyncedInPortsGetter/>
<@allMsgEventInPortsGetter/>

<#macro allInPortsGetter>
@Override
public java.util.List${"<"}montiarc.rte.port.InOutPort${"<?, ?>>"} getAllInPorts() {
  
    return java.util.List.of(
      <#list ast.getSymbol().getAllIncomingPorts() as port>
        this.${prefixes.port()}${port.getName()}<#sep>,
      </#list>
    );
  
}
</#macro>



<#macro allOutPortsGetter>
@Override
public java.util.List${"<"}montiarc.rte.port.OutPort${"<?>>"} getAllOutPorts() {
  
    return java.util.List.of(
      <#list ast.getSymbol().getAllOutgoingPorts() as port>
        this.${prefixes.port()}${port.getName()}() <#sep>, </#sep>
      </#list>
    );
  
}
</#macro>

<#macro allSyncedInPortsGetter>
@Override
public java.util.List${"<"}montiarc.rte.port.InOutPort${"<?, ?>>"} getAllSyncedInPorts() {
  
    return java.util.List.of(
    <#list helper.getComponentHelper().getSyncedInPortsOf(ast.getSymbol()) as port>
      this.${prefixes.port()}${port.getName()}<#sep>,
    </#list>
    );
  
}
</#macro>



<#macro allMsgEventInPortsGetter>
@Override
public java.util.List${"<"}montiarc.rte.port.InOutPort${"<?, ?>>"} getAllMsgEventInPorts() {
  
    return java.util.List.of(
    <#list helper.getComponentHelper().getMsgEventInPortsOf(ast.getSymbol()) as port>
      this.${prefixes.port()}${port.getName()}<#sep>, </#sep>
    </#list>
    );

  
}
</#macro>
