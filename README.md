# Crossy Roads

This is the readme for Crossy Roads, try to keep it up to date with any information future-you will wish past-you
remembered to write down

## Project set up
This is a gradle project using JMonkey Engine and other java libraries

## How to run (for development)
You'll want a java 11 JDK installed on your machine (Your IDE may do this for you, IntelliJ does)

Open this application in your preferred IDE (IntelliJ and Eclipse will support Gradle by default, netbeans will support it with a plugin). The remaining instructions are for IntelliJ but the basic principle will be the same for any IDE)


### Development in IntelliJ
- Download the latest version of IntelliJ Community (IntelliJ Ultimate is a paid for version the features of which you may consider useful but are not essential for a JMonkey project)
- File > Open > select the top level folder of this project ( i.e. CrossyRoads) > Ok.
- The project will open with your project files on the left had side (IntelliJ may need to "think" for a couple of seconds before they appear)
- IntelliJ may say "No SDK set up" and prompt you to download one, follow its instructions and allow it to download a java 11 JDK. A JDK is used for compiling java applications, a JRE is used for running them.
- You can now add more java source files or assets to the project
- To run the project find CrossyRoads.java (which will be in under src/main/java/com/shchmax) and right click > Run 'CrossyRoads'

## How to package the game

### Distribute without a JRE

Either:

In your IDE execute the gradle task distZip (which you'll find under gradle > distributions > distZip)

Or:

In the command line open at the root of this project enter the following command: gradlew distZip

Then you will find a zip in the build/distributions folder. This zip will contain your game, all the libraries to run it and in the bin folder launch files (for windows and linux).

Note that the distribution does not contain a JRE, so java will need to be installed on the machine of anyone you give this distribution to. Alternatively you may wish to bundle a JRE with your game to remove this requirement.


### Distribute with a JRE

Distributing with a JRE means you'll need to provide an operating specific bundle for each OS you are
targeting (which is a disadvantage) but your end use will not have to have a JRE locally installed
(which is an advantage).

Either:

In your IDE execute the gradle task distZip (which you'll find under gradle > distributions > buildAllDistributions)

Or:

In the command line open at the root of this project enter the following command: gradlew buildAllDistributions

Then you will find a series of zip in the build/distributions folder. These zip will contain your game, all the libraries to run it and an
OS specific JRE. (The same files will also be available unzipped in a folder, which may be useful if distributing via steampipe or similar).
