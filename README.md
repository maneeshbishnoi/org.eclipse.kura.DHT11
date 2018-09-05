DHT11 OSGI plugin for Kura IOT gateway
===================================


SETTING THE ENVIRONMENT AND USAGE
---------------------------------

First Setting up the Kura Development Environment in Eclipse. Follow the below given link for complete instruction. https://eclipse.github.io/kura/dev/kura-setup.html

Try to implement the Hello World example given in Kura's official page(hit below given link) to check your local Kura dev environment is working properly or not.

Hello World Example :- https://eclipse.github.io/kura/dev/hello-example.html

Deploying Bundles(On Remote Target Device) :- https://eclipse.github.io/kura/dev/deploying-bundles.html

Here our Osgi Bundle is using Apache Felix Maven Bundle Plugin (BND) to build and create deployble Jar so it's better to get familiar with this tool. As you may need to change pom.xml according to your use case and all the configurtion for creating MANIFEST.MF(in case of using BND tool Manifest file will be created automatiically) file is present in build tag of pom. Please visit below link for more information. http://felix.apache.org/documentation/subprojects/apache-felix-maven-bundle-plugin-bnd.html

Now clone the "org.eclipse.kura.DHT11" project from the git repo https://github.com/maneeshbishnoi/org.eclipse.kura.DHT11.git

Navigate to the place where pom.xml file is present and run the following command. This builds a Deployable Jar(Kura Wire Component as Osgi Bundle) file with all the dependencies under your local maven repository (or path of bundled JAR will be given on terminal after successfully executing below command).

 mvn clean install       
Copy and save the downloaded jar into desktop or any other folder as you will need this to deploy on Kura.

Install/Deploy this bundle into your Remote target device through Eclipse or Kura Web Ui. Please follow below link for instruction on deploying on remote device through eclipse. https://eclipse.github.io/kura/dev/deploying-bundles.html#remote-target-device

Now the bundle can be run using below commands. Use the following Commands to start and stop the application. Once you deploy the bundle successfully it will be seen in list of existing wire components in Wires tab of Kura Web UI.

     a. First enter into Kura using ssh of Raspberry Pi.(in case of remote device) :-> ssh user@raspberry_pi_ip_address
     
     b. Then open Osgi terminal using below command. :-> telnet localhost 5002
     
     c. Now list all the installed bundle. :-> ss (you should see your installed bundles in last if it was installed successfully)
     
     d. Start and stop bundle with IDs. :-> start {Bundle_ID} or stop {Bundle_ID} 
     
     e. After starting your bundle check your bundle's state like Active/Registered/Unsatisfied. :-> ls {Bundle_ID} Note:  After this command if your bundle doesn't show the "Active" state means there is some problem with your bundle, you can check logs to figure out the exact issue.
                 
     f. Now you can navigate to the directory /var/log/ for checking logs and results coming from your bundle.
     
     g. Check kura.log file for your the results coming from your Bundle and also for investigating the issue in case of any error(you can check kura-console.log file which is present on same location in case of exception or issues like your bundle is not starting) while installing/starting that bundle.
