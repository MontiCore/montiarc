<!-- (c) https://github.com/MontiCore/monticore -->
# Setup MontiArc

MontiArc runs on any platform. In the following tutorials, we will use both the CLI and the Gradle plugin.

#### Gradle

If you only ever want to use the [Gradle plugin](./HelloGradle.md), the only thing you need is Java 21.
So if you haven't already, now is the right time to [install Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21).

#### Run the CLI Manually

To run the CLI manually, you need to have [Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21) installed.
Next, download the [MontiArc-7.9.0.jar](https://github.com/MontiCore/montiarc/releases/tag/snapshot).

You can then run the tool with:

```bash
java -jar MontiArc-7.10.0.jar
```

#### Install the CLI (BETA)

If you also want to use the [CLI](./HelloWorld.md), you can find installers for all platforms [here](https://github.com/MontiCore/montiarc/releases/tag/snapshot).

=== "Windows"
    Download the installer for Windows(.exe), extract, and run it.
    
=== "MacOS"
    1. Download the installer for MacOS(.pkg) 
    2. Run the installer and follow the instructions.
        - You will probably receive following message: ''Apple could not verify “MontiArc-[...].pkg” is free of malware that may harm your Mac or compromise your privacy.''
            1. Navigate to `System Settings`
            2. Navigate to `Privacy & Security`
            3. Navigate to `Security`
            4. Search for the message ''... was blocked from use because it is not from an identified developer''
            5. Click on **Open anyway**
    3. You are now able to use the MontiArc tool from anywhere.
        - When deinstalling MontiArc again, an orphan symlink is left behind at `/usr/local/bin/montiarc`. To remove this link follow these steps:
            1. Check if the symlink exists: `ls -l /usr/local/bin/montiarc`
                - If it exists, something like ''montiarc -> ...'' will be provided
                - Otherwise: ''No such file or directory''
            2. To remove the symlink run: `sudo rm /usr/local/bin/montiarc`

    !!! info 
        MontiArc can only be used via the terminal. Clicking on the MontiArc app icon displayed in your app overview will not work.

=== "Linux"
    1. Download the installer for Linux(.deb/.rpm) and run it.
    2. To be able to run the MontiArc command from anywhere, you will need to update your path variable:
        - You should already know how to add a program to your path?
        - If not, simply add `export PATH="/opt/montiarc/bin:$PATH"` to whatever shell's rc script you're using (likely `~/.bashrc`).

You can then run the tool by running:
```bash
MontiArc
```

When you intend to run the simulation using the CLI, you'll need [Java 21](https://www.oracle.com/de/java/technologies/downloads/#java21) anyway.

---

You can find detailed descriptions and available parameters in the [usage](../Usage/index.md) section.
