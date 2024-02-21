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

protected void <@MethodNames.handleSyncComputation/>() {
    if (this.behavior != null) {
        if(isSync && getAllInPorts().stream().allMatch(montiarc.rte.port.IInPort::hasBufferedTick)) {
        <@MethodNames.getBehavior/>().tick();
        getAllInPorts().forEach(inP -> {
        while(inP.hasBufferedTick() && !inP.isTickBlocked()) de.se_rwth.commons.logging.Log.warn(
        "Component " + this.getName() +
        " has received more than one data message in a single time slice on port " + inP.getQualifiedName() +
        ". Dropped data: " + inP.pollBuffer());
        });
        <@MethodNames.handleTick/>();
        }
    } else {
    <#-- No Behvaior behavior -->
        if(getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.AbstractInPort)
        .map(p -> (montiarc.rte.port.AbstractInPort<?>) p)
        .anyMatch(montiarc.rte.port.AbstractInPort::isBufferEmpty)) {
        return;
        }

        if (<@MethodNames.inputsTickBlocked/>()) {
        <@MethodNames.dropTickOnAll/>();
        <@MethodNames.sendTickOnAll/>();
        <@MethodNames.handleSyncComputation/>();
        return;
        }

        getAllInPorts().stream()
        .filter(p -> p instanceof montiarc.rte.port.AbstractInPort)
        .map(p -> (montiarc.rte.port.AbstractInPort<?>) p)
        .filter(p -> !(((montiarc.rte.port.ITimeAwareInPort<?>) p).isTickBlocked()))
        .forEach(montiarc.rte.port.AbstractInPort::pollBuffer);
    }
}
