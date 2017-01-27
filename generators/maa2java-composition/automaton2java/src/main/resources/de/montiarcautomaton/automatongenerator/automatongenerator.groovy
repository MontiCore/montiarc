package de.montiarcautomaton.automatongenerator

info("--------------------------------")
info("Automaton Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(modelPath, out)
info("--------------------------------")
info("Automaton Generator END")
info("--------------------------------")