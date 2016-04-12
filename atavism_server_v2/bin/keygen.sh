#!/bin/sh

MAIN_CLASS=atavism.server.util.SecureTokenUtil

if [ "X$AO_HOME" = "X" ]; then
    export AO_HOME=`dirname $0`/..
fi
AO_COMMON="$AO_HOME/config/common"

AOJAR="$AO_HOME/dist/lib/multiverse.jar"
MARSJAR="$AO_HOME/dist/lib/mars.jar"
GETOPT="$AO_HOME/other/java-getopt-1.0.11.jar"
LOG4J="$AO_HOME/other/log4j-1.2.14.jar"
BCEL="$AO_HOME/other/bcel-5.2.jar"
INJECTED_JAR=${INJECTED_JAR:-"${AO_HOME}/dist/lib/injected.jar"}

if [ $(uname -o) == "Cygwin" ]; then
    AO_CLASSPATH="$INJECTED_JAR;$AOJAR;$MARSJAR;$BCEL;$GETOPT;$LOG4J;$JAVA_HOME/lib/tools.jar"
else
    AO_CLASSPATH="$INJECTED_JAR:$AOJAR:$MARSJAR:$BCEL:$GETOPT:$LOG4J:$JAVA_HOME/lib/tools.jar"
fi

#echo $AO_CLASSPATH
DISABLE_LOG="-Datavism.disable_logs=true"

JAVA_PROPS="$DISABLE_LOG"

if [ "X$JAVA_HOME" = "X" ]; then
    java -cp $AO_CLASSPATH $JAVA_PROPS $MAIN_CLASS $MARSHALL_LIST "$@"
else
    "$JAVA_HOME/bin/java" -cp "$AO_CLASSPATH" $JAVA_PROPS $MAIN_CLASS "$@"
fi


