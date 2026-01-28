@echo off
java -jar packr-all-4.0.0.jar ^
 --platform windows64 ^
 --jdk "C:\Program Files\Java\jdk-11.0.27" ^
 --executable CrawlerApp ^
 --classpath "target/program-prod.jar" ^
 --mainclass p.SearchPage ^
 --vmargs Xmx512m ^
 --minimizejre full ^
 --resources src/main/resources/goodtypes.properties ^
 --output target/program-output