<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames>
protected void <@MethodNames.handleBufferImplementation portSym/>() {
  if(${prefixes.port()}${portSym.getName()}.isBufferEmpty()) return;

  if(this.isSync || ${prefixes.port()}${portSym.getName()}.isTickBlocked()) {
    <@MethodNames.handleTick/>();
    return;
  }

  if (!this.isSync) ((${ast.getName()}${suffixes.events()}) <@MethodNames.getBehavior/>()).${prefixes.message()}${portSym.getName()}();
}