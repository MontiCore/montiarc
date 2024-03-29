<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list ast.getSymbol().getAllIncomingPorts() as portSym>
    ${tc.includeArgs("montiarc.generator.ma2jsim.component.atomic.HandleMessageOn.ftl", [portSym])}
</#list>

protected montiarc.rte.behavior.IBehavior behavior;
protected boolean isSync;

@Override
public montiarc.rte.behavior.IBehavior <@MethodNames.getBehavior/>() { return behavior; }

${tc.include("montiarc.generator.ma2jsim.component.atomic.BehaviorSetup.ftl")}
