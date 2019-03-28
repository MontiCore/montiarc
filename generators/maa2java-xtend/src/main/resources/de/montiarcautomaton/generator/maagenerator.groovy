package de.montiarcautomaton.generator

info("--------------------------------")
info("MAA Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("HWC dir        : " + handwrittenCode)
debug("enableCI       : " + enableComponentInstantiation)
debug("--------------------------------")
generate(modelPath, out, handwrittenCode, enableComponentInstantiation)
info("--------------------------------")
info("MAA Generator END")
info("--------------------------------")