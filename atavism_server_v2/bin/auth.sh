#!/bin/sh
(shopt -s igncr) 2>/dev/null && shopt -s igncr; # Workaround Cygwin line-ending issue

# This batch file runs the atavism sever processes on Linux in bash shell or on Windows/Cygwin
# You must have installed Java, a database, JDBC driver, and the atavism servers
# Copyright 2012 Neojac Entertainment, Inc.
# Thanks to Judd-MGT for contributions.

# Optional: Set AO_HOME env. variable to be able to run this script from an arbitrary directory.
# Otherwise, this script assumes it is being run from AO_HOME/bin working directory
# NOTE: Doesn't work if you set AO_HOME.
export AO_HOME=${AO_HOME:-".."}

# Optional: Set DEFAULT_AUTH_PROPERTYFILE env. variable to set the default property file, otherwise use world.properties
# when no property file is specified as final argument on command line.
DEFAULT_AUTH_PROPERTYFILE=${DEFAULT_AUTH_PROPERTYFILE:-"auth.properties"}

### Set to true to enable JMX management and monitoring (either here or in env. variable.
ENABLE_MGMT=${ENABLE_MGMT:-"false"}

function kill_process () {
    if [ $verbose -gt 0 ]; then
        echo -en "stopping $1 "
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
    ps -e ho pid | grep $1 > /dev/null 2>&1
    result=$?
    if [ $result = 1 ]; then
        echo FAILED
    else
        echo SUCCESS
    fi
}

function status_process () {
    if [ x$2 == x ]; then
	return
    fi
    ps -e ho pid | grep $2 > /dev/null 2>&1
    result=$?
    if [ $result = 0 ]; then
        echo -e "$1" RUNNING
    else
        echo -e "$1" NOT RUNNING
    fi
}

function archive_log_dir () {
    rm -rf "${AO_LOGS}.old"
    mv "${AO_LOGS}" "${AO_LOGS}.old"
}

function start_server () {

    if [ X$ARCHIVE_LOG_DIR = X"true" ]; then
	if [ -d "${AO_LOGS}" ]; then
	    archive_log_dir
	fi
    fi

    # Use these flags for profiling
    HPROF_FLAGS="-agentlib:hprof=heap=sites,depth=8"
    GCDETAILS_FLAGS="-XX:+PrintGC -XX:+PrintGCDetails"

    # Linux: Use strace with the following flags to monitor one of the
    # atavism server processes.
    #    strace -f -e trace=\!futex,gettimeofday,clock_gettime java \

    echo "*** Starting master server ***"
    if [ ! -d "${AO_RUN}" ]
        then
        mkdir -p "${AO_RUN}"
    fi

    if [ ! -d "${AO_LOGS}" ]
        then
        mkdir -p "${AO_LOGS}"
    fi

    rm -f ${AO_RUN}/*.pid

    if [ $DELETE_LOGS_ON_STARTUP = "true" ]; then
        rm "${AO_LOGS}"/*.out*
    fi

    if $ENABLE_MGMT = "true"; then        
        echo "Enabling JMX mgmt & monitoring"
        JAVA_FLAGS="${JAVA_FLAGS} $JMX_FLAGS"
    fi        

    if [ $verbose -gt 0 ]; then
        echo AO_HOME is $AO_HOME
        if [ $USE_CLASS_FILES = "true" ]; then
            echo "Using .class files from the /build hierarchy"
        else
            echo "Using .jar files from the /dist hierarchy"
        fi
        echo Using property file $AUTH_PROPERTYFILE  
        echo Using log directory $AO_LOGS
        echo Using common directory $AO_COMMON, bin directory $AO_BIN     
        echo "JAVA_FLAGS=\"${JAVA_FLAGS}\""
    fi

    # Increase the file descriptor limit up to the hard limit
    # Linux: Use /etc/security/limits.conf to set the hard limit
    ulimit -n hard

    if [ $verbose -gt 0 ]; then
        echo -en "Starting authentication server: \t"
    fi

    java ${JAVA_FLAGS} \
        -Datavism.loggername=authentication \
        atavism.server.engine.MasterServer \
	$CMDLINE_PROPS \
        $AO_BIN/auth_server.py \
        &

    echo $! > "${AO_RUN}"/auth.pid

    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/auth.pid)
    fi

    echo "Wait for finished initializing msg... "
}

function stop_server () {
    echo "*** Stopping authnetication server ***"
    kill_process "authnetication server  " $(cat "${AO_RUN}"/auth.pid)
}

function status_server () {
    down=0
    status_process "authnetication server  " $(cat "${AO_RUN}"/auth.pid)  
    exit ${down}
}

function test_server () {
    echo "JAVA_FLAGS=\"${JAVA_FLAGS}\""
    java $JAVA_FLAGS atavism.server.util.SecureTokenManager
    exit 0
    if [ $verbose -gt 0 ]; then
        java $JAVA_FLAGS atavism.simpleclient.SimpleClient -e ${AO_COMMON}/simpleclient.props -s $AO_COMMON/simpleclient.py --exit-after-login
    else
        java $JAVA_FLAGS atavism.simpleclient.SimpleClient -e ${AO_COMMON}/simpleclient.props -s ${AO_COMMON}/simpleclient.py --exit-after-login > /dev/null 2>&1
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

function import_property_file () {
    for file
    do
        if [ -f $file ]; then
            files="$files $file"
        fi
    done
    if [ -n "$files" ]; then
        awk -f "$AO_BIN/prop2sh.awk" $files > "$AO_BIN/_javaprops_"
        . "$AO_BIN/_javaprops_"
        rm -f "$AO_BIN/_javaprops_"
    fi
}

verbose=0
while getopts "hvw:p:CMP:Aa" arg; do
    case "$arg" in
        h)
            echo "$0: usage: $0 [-hvCM] [-p propertyfilename] (start|stop|status|test)"
            ;;
        v)
            let verbose++
            ;;
	p)
	    AUTH_PROPERTYFILE=$OPTARG
	    ;;
	C)
	    JVM_FLAG=-client
	    ;;
	M)
	    ENABLE_MGMT=true
	    JMX_FLAGS="-Dcom.sun.management.jmxremote"
	    ;;
	P)
	    CMDLINE_PROPS="$CMDLINE_PROPS -P$OPTARG"
	    ;;
	A)
	    AGGRESIVE="-XX:CompileThreshold=200 -Xnoclassgc -XX:+RelaxAccessControlCheck"
	    ;;
	a)
	    ARCHIVE_LOG_DIR=true
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

AUTH_PROPERTYFILE=${AUTH_PROPERTYFILE:-"${AO_BIN}/${DEFAULT_AUTH_PROPERTYFILE}"}

import_property_file $AUTH_PROPERTYFILE

if [ -z "$ARCHIVE_LOG_DIR" -a -n "$atavism_archive_logs_on_startup" ] ; then
    ARCHIVE_LOG_DIR=$atavism_archive_logs_on_startup
fi

# Determine if we should use .class files from the build hierarchy,
# or .jar files from the dist hierarchy.  To run the property getter
# before AO_JAR is set, we always use the dist version of the property
# getter.
USE_CLASS_FILES=${USE_CLASS_FILES:-$(java -cp ${AO_HOME}/dist/lib/atavism.jar -Datavism.propertyfile=${AUTH_PROPERTYFILE} atavism.scripts.PropertyGetter atavism.use_class_files)}

RHINO=${RHINO:-"${AO_HOME}/other/rhino1_5R5/js.jar"}
GETOPT=${GETOPT:-"${AO_HOME}/other/java-getopt-1.0.11.jar"}
JYTHON=${JYTHON:-"${AO_HOME}/other/jython.jar"}
LOG4J=${LOG4J:-"${AO_HOME}/other/log4j-1.2.15.jar"}
BCEL=${BCEL:-"${AO_HOME}/other/bcel-5.2.jar"}
#XMLRPC=${XMLRPC:-"${AO_HOME}/other/org-apache-xmlrpc.jar"}

if  [ $USE_CLASS_FILES = "true" ]; then
    AO_JAR=${AO_JAR:-"${AO_HOME}/build"}
    AGIS_JAR=${AGIS_JAR:-"${AO_HOME}/build"}
    INJECTED_JAR=${INJECTED_JAR:-"${AO_HOME}/inject"}
else
    AO_JAR=${AO_JAR:-"${AO_HOME}/dist/lib/atavism.jar"}
    AGIS_JAR=${AGIS_JAR:-"${AO_HOME}/dist/lib/agis.jar"}
    INJECTED_JAR=${INJECTED_JAR:-"${AO_HOME}/dist/lib/injected.jar"}
fi

JDBC=${JDBC:-$(java -cp $AO_JAR -Datavism.propertyfile=${AUTH_PROPERTYFILE} atavism.scripts.PropertyGetter atavism.jdbcJarPath)}

if [ $(uname -o) == "Cygwin" ]; then
    export PATH=$(cygpath "$JAVA_HOME"/bin):.:$PATH
    AO_CLASSPATH="$RHINO;$JDBC;$INJECTED_JAR;$AO_JAR;$AGIS_JAR;$GETOPT;$JYTHON;$LOG4J;$BCEL;."
else
    AO_CLASSPATH="${RHINO}:${JDBC}:${INJECTED_JAR}:${AO_JAR}:${AGIS_JAR}:${GETOPT}:${JYTHON}:${LOG4J}:${BCEL}:."
fi
        
# HotSpot tracking flags: -XX:+PrintCompilation -XX:+CITime
JVM_FLAG="${JVM_FLAG:-"-server"} $AGGRESIVE"
JAVA_FLAGS="-cp ${AO_CLASSPATH} -Datavism.propertyfile=${AUTH_PROPERTYFILE} ${JAVA_FLAGS}"
JAVA_FLAGS="${JVM_FLAG} ${JAVA_FLAGS}"

if [ X"$AO_HOSTNAME" != "X" ]; then
    CMDLINE_PROPS="$CMDLINE_PROPS -Patavism.hostname=${AO_HOSTNAME}"
fi

# This is in local OS format
AO_LOGS=${AO_LOGS:-"${AO_HOME}/logs/master"}
DELETE_LOGS_ON_STARTUP=${AO_DELETE_LOGS_ON_STARTUP:-$(java $JAVA_FLAGS atavism.scripts.PropertyGetter atavism.delete_logs_on_startup)}

# This should always be in "unix" format
if [ $(uname -o) = "Cygwin" ]; then
    AO_RUN=${AO_RUN:-$(cygpath -w ${AO_BIN}/run/master)}
else
    AO_RUN=${AO_RUN:-${AO_BIN}/run/master}
fi

JAVA_FLAGS="${JAVA_FLAGS} -Datavism.logs=${AO_LOGS}"

case "$1" in

    start)
        start_server
        ;;

    stop)
        stop_server
        ;;

    status)
        status_server
        ;;

    restart)
        stop_server
        start_server
        ;;

    test)
        test_server
        ;;
esac 
 
