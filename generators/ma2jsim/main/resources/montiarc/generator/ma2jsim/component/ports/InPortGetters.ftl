<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
@Override
public java.util.List${"<"}montiarc.rte.port.ITimeAwareInPort${"<?>>"} getAllInPorts() {
  return ${ast.getName()}${suffixes.context()}.super.getAllInPorts();
}
<#list ast.getSymbol().getAllIncomingPorts() as portSym>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.ports.PortGetter.ftl", helper.asList(portSym, ast.getSymbol().isAtomic()))}
</#list>