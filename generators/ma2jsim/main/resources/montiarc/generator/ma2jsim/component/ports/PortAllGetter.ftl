<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign hasOnlyOneVariant = helper.getVariants(ast)?size == 1>

@Override
protected java.util.List${"<"}montiarc.rte.port.InOutPort${"<?>>"} getAllInPorts() {
  final java.util.ArrayList${"<"}montiarc.rte.port.InOutPort${"<?>>"} allInPortList = new java.util.ArrayList<>();
  allInPortList.add(tickPort);

  <#if hasOnlyOneVariant>
    <#list ast.getSymbol().getAllIncomingPorts() as port>
      allInPortList.add(${prefixes.port()}${port.getName()});
    </#list>
  <#else>
    switch (this.variantID) {
      <#list helper.getVariants(ast) as variant>
        case ${helper.variantSuffix(variant)}:
        <#list variant.getAllIncomingPorts() as port>
          allInPortList.add(this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)});
        </#list>
          break;
      </#list>
      default: assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
    }
  </#if>

  return allInPortList;
}

@Override
public java.util.List${"<"}montiarc.rte.port.OutPort${"<?>>"} getAllOutPorts() {
  final java.util.ArrayList${"<"}montiarc.rte.port.OutPort${"<?>>"} allOutPortList = new java.util.ArrayList<>();

  <#if hasOnlyOneVariant>
    <#list ast.getSymbol().getAllOutgoingPorts() as port>
      allOutPortList.add(${prefixes.port()}${port.getName()}());
    </#list>
  <#else>
    switch (this.variantID) {
      <#list helper.getVariants(ast) as variant>
        case ${helper.variantSuffix(variant)}:
        <#list variant.getAllOutgoingPorts() as port>
          allOutPortList.add(this.${prefixes.port()}${port.getName()}${helper.portVariantSuffix(ast, port)}());
        </#list>
          break;
      </#list>
      default: assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
    }
  </#if>

  return allOutPortList;
}