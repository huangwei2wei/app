#!/bin/bash
export AO_HOME=".."
MV_BIN=${MV_BIN:-"${AO_HOME}/bin"}

# Optional: Set DEFAULT_MV_PROPERTYFILE env. variable to set the default property file, otherwise use atavism.properties
# when no property file is specified as final argument on command line.
DEFAULT_MV_PROPERTYFILE=${DEFAULT_MV_PROPERTYFILE:-"atavism.properties"}
MV_PROPERTYFILE=${MV_PROPERTYFILE:-"${MV_BIN}/${DEFAULT_MV_PROPERTYFILE}"}

DELETE_LOGS_ON_STARTUP=${DELETE_LOGS_ON_STARTUP:-$(java -cp ${AO_HOME}/dist/lib/atavism.jar -Datavism.propertyfile=${MV_PROPERTYFILE} atavism.scripts.PropertyGetter atavism.delete_logs_on_startup)}
MV_JAR=${AO_HOME}/build
LOG4J=${LOG4J:-"${AO_HOME}/other/log4j-1.2.15.jar"}
BCEL=${BCEL:-"${AO_HOME}/other/bcel-5.2.jar"}
SHIRO=${SHIRO:-"${AO_HOME}/other/shiro-core-1.0-incubating-SNAPSHOT.jar"}
SLF4J=${SLF4J:-"${AO_HOME}/other/slf4j-api-1.5.6.jar"}
SLF4J_LOG4J=${SLF4J_LOG4J:-"${AO_HOME}/other/slf4j-log4j12-1.5.6.jar"}
MV_LOGS="${AO_HOME}/logs/inject"
MV_COMMON=${MV_COMMON:-"${AO_HOME}/config/common"}
if [ X"$1" != "X" ]; then
    MV_WORLDNAME=$1
else
    MV_WORLDNAME=${MV_WORLDNAME:-$(java -cp ${AO_HOME}/dist/lib/atavism.jar -Datavism.propertyfile=${MV_PROPERTYFILE} atavism.scripts.PropertyGetter atavism.worldname)}
fi
MV_WORLD=${MV_WORLD:-"$AO_HOME/config/$MV_WORLDNAME"}

if [ $(uname -o) == "Cygwin" ]; then
    MV_CLASSPATH="$MV_JAR;$LOG4J;$BCEL;$SLF4J;$SLF4J_LOG4J;$SHIRO;."
else
    MV_CLASSPATH="${MV_JAR}:${LOG4J}:${BCEL}:${SLF4J}:${SLF4J_LOG4J}:${SHIRO}:."
fi

JAVA_FLAGS="${JAVA_FLAGS} -cp ${MV_CLASSPATH} -Datavism.logs=${MV_LOGS}"

if [ ! -d "${MV_LOGS}" ]; then
	mkdir -p "${MV_LOGS}"
fi
if [ X$DELETE_LOGS_ON_STARTUP = "Xtrue" ]; then
	rm "${MV_LOGS}"/*.out*
fi

rm -rf "${AO_HOME}"/inject/*

echo -en "Starting batch injection of marshalling methods ...\n"
java -Datavism.log_level=0 $JAVA_FLAGS -ea atavism.server.marshalling.InjectClassFiles -m "${MV_COMMON}"/mvmarshallers.txt -m "${MV_WORLD}"/worldmarshallers.txt -t "${MV_COMMON}"/typenumbers.txt -i "${AO_HOME}"/build -o "${AO_HOME}"/inject/

echo -en "Finished batch injection of marshalling methods\n"
