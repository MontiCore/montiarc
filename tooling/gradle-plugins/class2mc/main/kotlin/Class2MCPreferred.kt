/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.class2mc

import org.gradle.api.attributes.AttributeDisambiguationRule
import org.gradle.api.attributes.MultipleCandidatesDetails
import org.gradle.api.attributes.Usage

/**
 * Prefers a Class2MC API variant over a regular Java API variant when a
 * Class2MC consumer can use both.
 */
class Class2MCPreferred : AttributeDisambiguationRule<Usage> {

  override fun execute(details: MultipleCandidatesDetails<Usage>) = with(details) {
    if (consumerValue?.name == CLASS2MC_USAGE) {
      val candidates = candidateValues.filterNotNull()

      val class2mcApi = candidates.firstOrNull { it.name == CLASS2MC_USAGE }
      val javaApi = candidates.firstOrNull { it.name == Usage.JAVA_API }

      if (class2mcApi != null) {
        closestMatch(class2mcApi)
      } else if (javaApi != null) {
        closestMatch(javaApi)
      }
    }
  }
}
