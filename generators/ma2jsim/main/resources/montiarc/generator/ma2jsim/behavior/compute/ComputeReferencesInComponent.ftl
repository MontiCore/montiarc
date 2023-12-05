<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

protected ${ast.getName()}${suffixes.compute()}<@Util.printTypeParameters ast false/> compute;

@Override
public ${ast.getName()}${suffixes.compute()}<@Util.printTypeParameters ast false/> <@MethodNames.getBehavior/>() { return compute; }

protected void <@MethodNames.behaviorSetup/>() {
this.compute = new ${ast.getName()}${suffixes.compute()}<@Util.printTypeParameters ast false/>(this);
}


protected void <@MethodNames.handleSyncComputation/>() {
  if(getAllInPorts().stream().allMatch(montiarc.rte.port.IInPort::hasBufferedTick)) {
  <@MethodNames.getBehavior/>().tick();
  getAllInPorts().forEach(inP -> {
  while(inP.hasBufferedTick() && !inP.isTickBlocked()) de.se_rwth.commons.logging.Log.warn(
  "Component " + this.getName() +
  " has received more than one data message in a single time slice on port " + inP.getQualifiedName() +
  ". Dropped data: " + inP.pollBuffer());
  });
  <@MethodNames.handleTick/>();
  }
}
