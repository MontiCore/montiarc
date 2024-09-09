<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign inPorts = ast.getSymbol().getAllIncomingPorts()>

@Override
protected Object portValueOf(montiarc.rte.port.InPort<?> p) {
  <#list inPorts as inPort>
    <#assign portAccessor = "this." + prefixes.port() + inPort.getName() + helper.portVariantSuffix(ast, inPort)/>
    <#assign portValueGetter = prefixes.portValueOf() + inPort.getName() + helper.portVariantSuffix(ast, inPort)/>
    if (p == ${portAccessor}) {
      return ${portValueGetter}();
    } else
  </#list>
  throw new IllegalArgumentException("Port " + p.getQualifiedName() + " is not available in component " + getName());
}