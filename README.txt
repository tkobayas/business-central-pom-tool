## business-central-pom-tool


/home/tkobayas/usr/local/bin/pomfullchecker

~~~
java -cp /home/tkobayas/usr/git/tkobayas/business-central-pom-tool/target/business-central-pom-tool-1.0.0.jar com.redhat.gss.support.PomFullChecker
~~~

 -> Run under /home/tkobayas/usr/gitengssh/BxMS64/RHBRMS-3095
    Checks any missing <version> modification

/home/tkobayas/usr/local/bin/pomupdater

~~~
java -cp /home/tkobayas/usr/git/tkobayas/business-central-pom-tool/target/business-central-pom-tool-1.0.0.jar com.redhat.gss.support.PomTool
~~~

 -> Run under each project
 -> Generates pom.xml.new so safe.
 -> then diff with pom.xml
 