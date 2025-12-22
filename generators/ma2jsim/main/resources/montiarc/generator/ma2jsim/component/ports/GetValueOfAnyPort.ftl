<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign inPorts = ast.getSymbol().getAllIncomingPorts()>

@Override
protected Object portValueOf(montiarc.rte.port.InPort<?> p) {
  <#list inPorts as inPort>
    <#assign portAccessor = "this." + prefixes.port() + inPort.getName() + helper.getVariantHelper().portVariantSuffix(ast, inPort)/>
    <#assign portValueGetter = prefixes.portValueOf() + inPort.getName() + helper.getVariantHelper().portVariantSuffix(ast, inPort)/>
    if (p == ${portAccessor}) {
      return ${portValueGetter}();
    } else
  </#list>
  throw new IllegalArgumentException("Port " + p.getQualifiedName() + " is not available in component " + getName());
}