$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
& "$ScriptDir\apache-maven-3.9.6\bin\mvn.cmd" $args
