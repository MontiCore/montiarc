<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>

<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)/>

@Override
public void handleMessage(montiarc.rte.port.IInPort<?> p) {
  <#if modeAutomatonOpt.isPresent() && helper.isEventBased(modeAutomatonOpt.get())>
  <#list ast.getSymbol().getAllIncomingPorts() as inPort>
  <#assign portName>${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}</#assign>
    if (p == ${portName}) {
      getModeAutomaton().${prefixes.message()}${inPort.getName()}();
    }
  </#list>
  </#if>

  if (!isAtomic) {
    ((montiarc.rte.port.TimeAwarePortForComposition) p).forward();
  } else if (<@MethodNames.getBehavior/>() == null) {
    p.pollBuffer();
  }

  <#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign portName>${prefixes.port()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}</#assign>
    else if (p == ${portName}) {
      if (isAtomic) {
        if (<@MethodNames.getBehavior/>() != null) {
          ((${ast.getName()}${suffixes.events()}) <@MethodNames.getBehavior/>()).${prefixes.message()}${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}();
        } else { ${portName}.pollBuffer(); } <#-- TODO: move pollBuffer logic out of behavior and into scheduler? -->
      } else {
        ((montiarc.rte.port.TimeAwarePortForComposition<?>) ${portName}()).forward();
      }
    }
  </#list>
}
