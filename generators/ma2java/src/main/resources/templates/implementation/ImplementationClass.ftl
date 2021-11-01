<#-- (c) https://github.com/MontiCore/monticore -->
<#-- The implementation class for atomic components without specified behavior. -->
${tc.signature("comp", "compHelper", "identifier", "isTOPClass")}
<#assign comp=comp><#assign compHelper=compHelper><#assign identifier=identifier><#assign isTOPClass=isTOPClass>
<#import "/templates/util/Copyright.ftl" as Copyright>
<#import "/templates/util/Imports.ftl" as Imports>
<#import "/templates/implementation/ImplementationHeader.ftl" as Header>
<#import "/templates/implementation/ImplementationBody.ftl" as Body>
<@Copyright.defaultCopyrightComment/>

<#if comp.getPackageName() != "">
  package ${comp.getPackageName()};
</#if>

<@Imports.printImportsForImplementationClass comp=comp compHelper=compHelper/>

<@Header.printImplementationClassHeader comp=comp compHelper=compHelper isTOPClass=isTOPClass/> {

<@Body.printImplementationClassBody comp=comp compHelper=compHelper identifier=identifier isTOPClass=isTOPClass/>
}