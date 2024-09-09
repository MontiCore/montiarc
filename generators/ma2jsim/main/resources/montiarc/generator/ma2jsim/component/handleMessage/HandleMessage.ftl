<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/logging/CompLogging.ftl" as Log>

<#assign modeAutomatonOpt = helper.getModeAutomaton(ast)/>

<#if modeAutomatonOpt.isPresent() && helper.isEventBased(modeAutomatonOpt.get())>
@Override
public void handleMessageWithModeAutomaton(montiarc.rte.port.InPort<?> p) {
  <#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign portNameWithSuffix>${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}</#assign>
    <#assign eventClass>${ast.getName()}${suffixes.events()}<@Util.printTypeParameters ast false/></#assign>
    if (p == ${prefixes.port()}${portNameWithSuffix}) {
      getModeAutomaton().${prefixes.message()}${inPort.getName()}(${prefixes.portValueOf()}${portNameWithSuffix}());
    }
  </#list>
}
</#if>

@Override
public void handleMessageWithBehavior(montiarc.rte.port.InPort<?> p) {
  if (this.getBehavior() == null) throw new IllegalStateException();

  <#list ast.getSymbol().getAllIncomingPorts() as inPort>
    <#assign portNameWithSuffix>${inPort.getName()}${helper.portVariantSuffix(ast, inPort)}</#assign>
    <#assign eventClass>${ast.getName()}${suffixes.events()}<@Util.printTypeParameters ast false/></#assign>
    if (p == ${prefixes.port()}${portNameWithSuffix}) {
      this.getBehavior().${prefixes.message()}${portNameWithSuffix}(${prefixes.portValueOf()}${portNameWithSuffix}());
    }
  </#list>
}
