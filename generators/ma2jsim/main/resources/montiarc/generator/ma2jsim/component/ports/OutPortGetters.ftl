<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign timed = helper.isComponentOutputTimeAware(ast)/>
@Override
public java.util.List${"<"}montiarc.rte.port.Time<#if timed>Aware<#else>Unaware</#if>OutPort${"<?>>"} getAllOutPorts() {
  return ${ast.getName()}${suffixes.context()}.super.getAllOutPorts();
}
<#list ast.getSymbol().getAllOutgoingPorts() as portSym>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.ports.PortGetter.ftl", helper.asList(portSym, ast.getSymbol().isAtomic()))}
</#list>