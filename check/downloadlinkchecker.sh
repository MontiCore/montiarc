# (c) https://github.com/MontiCore/monticore
# expects private access token required for the download as input
curl --location --header "PRIVATE-TOKEN: $1" "https://git.rwth-aachen.de/api/v4/projects/monticore%2Fmdlinkchecker/jobs/artifacts/master/raw/target/libs/MDLinkCheckerCLI.jar?job=build" --output MDLinkCheckerCLI.jar
