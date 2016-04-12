#!/bin/sh
(shopt -s igncr) 2>/dev/null && shopt -s igncr; # Workaround Cygwin line-ending issue

# This batch file runs the Atavism Online sever processes on Linux in bash shell or on Windows/Cygwin
# You must have installed Java, a database, JDBC driver, and the Atavism Online servers
# Copyright 2013 Neojac Entertainment, Inc.
# Thanks to Judd-MGT for contributions.

# Optional: Set AO_HOME env. variable to be able to run this script from an arbitrary directory.
# Otherwise, this script assumes it is being run from AO_HOME/bin working directory
AO_HOME=${AO_HOME:-".."}

# Optional: Set DEFAULT_AO_PROPERTYFILE env. variable to set the default property file, otherwise use world.properties
# when no property file is specified as final argument on command line.
DEFAULT_AO_PROPERTYFILE=${DEFAULT_AO_PROPERTYFILE:-"world.properties"}

function kill_process () {
    if [ $verbose -gt 0 ]; then
        echo -en "stopping $1:    \t"
    fi
    kill $2 > /dev/null 2>&1
    result=$?
    if [ $verbose -gt 0 ]; then
        if [ $result = 0 ]; then
            echo STOPPED
        else
            echo NOT RUNNING
        fi
    fi
}

function check_process () {
    ps aho pid | grep $2 > /dev/null 2>&1
    result=$?
    if [ $result = 1 ]; then
        echo FAILED
    else
        echo SUCCESS
    fi
}

function status_process () {
    ps aho pid | grep $2 > /dev/null 2>&1
    result=$?
    if [ $result = 1 ]; then
        echo -e $1":     \t NOT RUNNING"
    else
        echo -e $1":     \t RUNNING"
    fi
}

function start_server () {
	# Use these flags for profiling
        HPROF_FLAGS="-agentlib:hprof=heap=sites,depth=8"
	GCDETAILS_FLAGS="-XX:-PrintGC -XX:-PrintGCDetails"

	echo "*** Starting world $AO_WORLDNAME ***"
	if [ ! -d "${AO_RUN}" ]
	  then
		  mkdir -p "${AO_RUN}"
	fi

	if [ ! -d "${AO_LOGS}" ]
	  then
		  mkdir -p "${AO_LOGS}"
	fi

	if [ $DELETE_LOGS_ON_STARTUP = "true" ]; then
		rm "${AO_LOGS}"/*.out*
	fi

	if [ $verbose -gt 0 ]; then
		echo AO_HOME is $AO_HOME
		echo Using property file $AO_PROPERTYFILE  
		echo Using world file $AOW_FILENAME
		echo Using world script directory $AO_WORLD
		echo Using log directory $AO_LOGS
		echo Using common directory $AO_COMMON, bin directory $AO_BIN     
		echo Dual World Manager Flag = $DUALWMGRS
		echo "JAVA_FLAGS=\"${JAVA_FLAGS}\""
		echo -en "Starting message server: \t"
	fi

	JIT_FLAGS="-XX:+CITime -XX:+PrintCompilation"

	echo java \
		${JAVA_FLAGS} \
		-Datavism.loggername=domain \
		atavism.msgsys.DomainServer \
        -m "${AO_COMMON}"/typenumbers.txt "$@"
	java \
		${JAVA_FLAGS} \
		-Datavism.loggername=domain \
		atavism.msgsys.DomainServer \
        -t "${AO_COMMON}"/typenumbers.txt "$@"

	echo "pid" $! 

}

function stop_server () {
	echo "*** Stopping world $AO_WORLDNAME ***"
	kill_process "login server" $(cat "${AO_RUN}"/login_manager.pid)
	kill_process "animation server" $(cat "${AO_RUN}"/anim.pid)
	kill_process "combat server" $(cat "${AO_RUN}"/combat.pid)
	kill_process "object manager" $(cat "${AO_RUN}"/objmgr.pid)
	kill_process "world manager 1" $(cat "${AO_RUN}"/wmgr1.pid)
  if [ $DUALWMGRS -gt 0 ]; then
	    kill_process "world manager 2" $(cat "${AO_RUN}"/wmgr2.pid)
	fi
	kill_process "proxy server" $(cat "${AO_RUN}"/proxy.pid)
	kill_process "mob server" $(cat "${AO_RUN}"/mobserver.pid)
	kill_process "message server" $(cat "${AO_RUN}"/msgsvr.pid)
	kill_process "world reader" $(cat "${AO_RUN}"/worldreader.pid)
kill_process "startup plugin" $(cat "${AO_RUN}"/startup.pid)
}

function status_server () {
	down=0
	status_process "message server" $(cat "${AO_RUN}"/msgsvr.pid)  
	status_process "login server" $(cat "${AO_RUN}"/login_manager.pid) 
	status_process "animation server" $(cat "${AO_RUN}"/anim.pid) 
	status_process "combat server" $(cat "${AO_RUN}"/combat.pid)                
	status_process "object manager" $(cat "${AO_RUN}"/objmgr.pid)
	status_process "world manager 1" $(cat "${AO_RUN}"/wmgr1.pid)
  if [ $DUALWMGRS -gt 0 ]; then
	    status_process "world manager 2" $(cat "${AO_RUN}"/wmgr2.pid)
	fi	
	status_process "proxy server" $(cat "${AO_RUN}"/proxy.pid)
	status_process "world reader" $(cat "${AO_RUN}"/worldreader.pid)
	status_process "mob server" $(cat "${AO_RUN}"/mobserver.pid)
	exit ${down}
}

function test_server () {
	if [ $verbose -gt 0 ]; then
		java $JAVA_FLAGS atavism.simpleclient.SimpleClient -e ${AO_COMMON}/simpleclient.props
	else
		java $JAVA_FLAGS atavism.simpleclient.SimpleClient -e ${AO_COMMON}/simpleclient.props > /dev/null 2>&1
	fi
	result=$?
	if [ $verbose -gt 0 ]; then
		if [ $result == 0 ]; then
			echo "login test: PASS"
		else
			echo "login test: FAIL"
		fi
	fi
	exit ${result}
}

verbose=0
while getopts "hvw:p:" arg; do
    case "$arg" in
        h)
            echo "$0: usage: $0 [-hv] [-w worldname] [-p propertyfilename] (start|stop|status|test)"
            ;;
        v)
            let verbose++
            ;;
		w)
		    JAVA_FLAGS=-Datavism.worldname=$OPTARG
			AO_WORLDNAME=$OPTARG
			;;
		p)
		    AO_PROPERTYFILE=$OPTARG
			;;
	esac
done
shift $((OPTIND-1))

if [ $(uname -o) = "Cygwin" ]; then
    AO_HOME_UNIX=$(cygpath -u "${AO_HOME}")
else
    AO_HOME_UNIX="$AO_HOME"
fi

# where the local startup configs are stored, such as the port number
# and log level
AO_BIN=${AO_BIN:-"${AO_HOME}/bin"}

# where common config files are stored, such as plugin logic
AO_COMMON=${AO_COMMON:-"${AO_HOME}/config/common"}

AO_PROPERTYFILE=${AO_PROPERTYFILE:-"${AO_BIN}/${DEFAULT_AO_PROPERTYFILE}"}

# Determine if we should use .class files from the build hierarchy,
# or .jar files from the dist hierarchy.  To run the property getter
# before AO_JAR is set, we always use the dist version of the property
# getter.
echo "Property File: " ${AO_PROPERTYFILE}
echo "AO_HOME: " ${AO_HOME}

USE_CLASS_FILES=${USE_CLASS_FILES:-$(java -cp ${AO_HOME}/dist/lib/atavism.jar -Datavism.propertyfile=${AO_PROPERTYFILE} atavism.scripts.PropertyGetter atavism.use_class_files)}

RHINO=${RHINO:-"${AO_HOME}/other/rhino1_5R5/js.jar"}
GETOPT=${GETOPT:-"${AO_HOME}/other/java-getopt-1.0.11.jar"}
JYTHON=${JYTHON:-"${AO_HOME}/other/jython_2_1.jar"}
LOG4J=${LOG4J:-"${AO_HOME}/other/log4j-1.2.14.jar"}
BCEL=${BCEL:-"${AO_HOME}/other/bcel-5.2.jar"}

if [ $USE_CLASS_FILES = "true" ]; then
	AO_JAR=${AO_JAR:-"${AO_HOME}/build"}
	MARS_JAR=${MARS_JAR:-"${AO_HOME}/build"}
	INJECTED_JAR=${INJECTED_JAR:-"${AO_HOME}/inject"}
else
	AO_JAR=${AO_JAR:-"${AO_HOME}/dist/lib/atavism.jar"}
	MARS_JAR=${MARS_JAR:-"${AO_HOME}/dist/lib/mars.jar"}
	INJECTED_JAR=${INJECTED_JAR:-"${AO_HOME}/dist/lib/injected.jar"}
fi

JDBC=${JDBC:-$(java -cp $AO_JAR -Datavism.propertyfile=${AO_PROPERTYFILE} atavism.scripts.PropertyGetter atavism.jdbcJarPath)}

if [ $(uname -o) == "Cygwin" ]; then
    export PATH=$(cygpath "$JAVA_HOME"/bin):.:$PATH
    AO_CLASSPATH="$RHINO;$JDBC;$INJECTED_JAR;$AO_JAR;$MARS_JAR;$GETOPT;$JYTHON;$LOG4J;$BCEL;."
else
    AO_CLASSPATH="${RHINO}:${JDBC}:${INJECTED_JAR}:${AO_JAR}:${MARS_JAR}:${GETOPT}:${JYTHON}:${LOG4J}:${BCEL}:."
fi
        
JAVA_FLAGS="-cp ${AO_CLASSPATH} -Djava.system.class.loader=atavism.server.marshalling.MarshallingClassLoader -Datavism.propertyfile=${AO_PROPERTYFILE} ${JAVA_FLAGS}"

#Get world name from properties file, and construct path to world script dir if not set from env var.
AO_WORLDNAME=${AO_WORLDNAME:-mtest}
AO_WORLD=${AO_WORLD:-"$AO_HOME/config/$AO_WORLDNAME"}

# Get path to mvw file if set explicitly in atavism.mvwfile, otherwise, construct path to mvwfile.
AOW_FILENAME=$(java $JAVA_FLAGS atavism.scripts.PropertyGetter atavism.mvwfile)

if [ "$AOW_FILENAME" = "null" ]; then
    AOW_FILENAME=$AO_HOME/config/$AO_WORLDNAME/$AO_WORLDNAME.mvw
fi

# This is in local OS format
AO_LOGS=${AO_LOGS:-"${AO_HOME}/logs/${AO_WORLDNAME}"}
DELETE_LOGS_ON_STARTUP=${AO_DELETE_LOGS_ON_STARTUP:-$(java $JAVA_FLAGS atavism.scripts.PropertyGetter atavism.delete_logs_on_startup)}

# This should always be in "unix" format
if [ $(uname -o) = "Cygwin" ]; then
    AO_RUN=${AO_RUN:-$(cygpath -w ${AO_BIN}/run/${AO_WORLDNAME})}
else
    AO_RUN=${AO_RUN:-${AO_BIN}/run/${AO_WORLDNAME}}
fi

DUALWMGRS=${DUALWMGRS:-$(java $JAVA_FLAGS atavism.scripts.PropertyGetter atavism.dualworldmanagers 0)}

JAVA_FLAGS="${JAVA_FLAGS} -Datavism.logs=${AO_LOGS}"

start_server "$@"

 
