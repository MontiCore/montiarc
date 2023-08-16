<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ignore ast-->
${tc.signature("portSym", "atomic")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
@Override
public <@Util.getStaticPortClass portSym atomic/><<@Util.getTypeString portSym.getType()/>>
  ${prefixes.port()}${portSym.getName()}() {
  return this.${prefixes.port()}${portSym.getName()};
}