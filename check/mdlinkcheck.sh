linkCheckRes=$(java -jar MDLinkCheckerCLI.jar "$@")
echo "$linkCheckRes"
if [[ $linkCheckRes == *"ERROR"* ]]
then
  exit 1
fi
