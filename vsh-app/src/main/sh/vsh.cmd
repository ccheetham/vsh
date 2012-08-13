@set BASEDIR=%~dp0..
@set VSH_JAR=%BASEDIR%\lib\@project.jar@

@call java -jar %VSH_JAR% %*
