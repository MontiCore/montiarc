/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.fmu2arc.FMU2ARC_API_FILE_USAGE
import montiarc.gradle.fmu2arc.FMU2ARC_API_SYMBOL_USAGE
import org.gradle.api.attributes.AttributeDisambiguationRule
import org.gradle.api.attributes.MultipleCandidatesDetails
import org.gradle.api.attributes.Usage

/**
 * Using this rule, dependency variants with perfectly matching [Usage] attribute value [FMU2ARC_4_MONTIARC_USAGE] are
 * preferred over [FMU2ARC_API_SYMBOL_USAGE] and [FMU2ARC_API_FILE_USAGE] when we search for [Usage] values of [FMU2ARC_4_MONTIARC_USAGE].
 */
class FMU2ArcForMontiArcPreferred : AttributeDisambiguationRule<Usage> {
  override fun execute(t: MultipleCandidatesDetails<Usage>): Unit = with (t) {
    if (consumerValue != null && consumerValue!!.name == FMU2ARC_4_MONTIARC_USAGE) {

      val candidates = candidateValues.filterNotNull()
      val equalMatch = candidates.firstOrNull { it.name == FMU2ARC_4_MONTIARC_USAGE }
      val fromOrigLang1 = candidates.firstOrNull { it.name == FMU2ARC_API_FILE_USAGE }
      val fromOrigLang2 = candidates.firstOrNull { it.name == FMU2ARC_API_SYMBOL_USAGE }

      if (equalMatch != null) {
        closestMatch(equalMatch)
      } else if (fromOrigLang1 != null) {
        closestMatch(fromOrigLang1)
      } else if (fromOrigLang2 != null) {
        closestMatch(fromOrigLang2)
      }
    }
  }
}
