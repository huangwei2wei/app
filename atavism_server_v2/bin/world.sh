#!/bin/sh
(shopt -s igncr) 2>/dev/null && shopt -s igncr; # Workaround Cygwin line-ending issue

# This batch file runs the atavism sever processes on Linux in bash shell
# or on Windows/Cygwin.  You must have installed Java, a database, JDBC driver,
# and the Atavism servers.

# Variable catalog:
# AO_HOME (exported)
# AO_BIN
# AO_COMMON_CONFIG
# AO_WORLD_CONFIG (exported)
# AO_WORLD_NAME
# AO_RUN
# AO_LOGS

# Optional: Set AO_HOME env. variable to be able to run this script from an
# arbitrary directory.  Otherwise, this script assumes it is being run from
# AO_HOME/bin working directory
export AO_HOME=${AO_HOME:-".."}

# Optional: Set DEFAULT_AO_PROPERTY_FILE env. variable to set the default property file, otherwise use world.properties
# when no property file is specified as final argument on command line.
DEFAULT_AO_PROPERTY_FILE=${DEFAULT_AO_PROPERTY_FILE:-"world.properties"}

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
	return 0
    fi
    ps -e ho pid | grep $2 > /dev/null 2>&1
    result=$?
    if [ $result = 0 ]; then
        echo -e "$1" RUNNING
    else
        echo -e "$1" NOT RUNNING
    fi
    return $result
}

function write_pid () {
    agent_name=$1
    pid=$2

    echo $pid > "${AO_RUN}"/${agent_name}.pid
    if [ $(uname -o) == "Cygwin" ]; then
	winpid=`ps -eW | grep ' \+ '$pid' \+[0-9]\+ \+[0-9]\+ \+\([0-9]\+\) \+.*' | sed -e s/' \+ '$pid' \+[0-9]\+ \+[0-9]\+ \+\([0-9]\+\) \+.*'/\\\\1/`
	echo $winpid > "${AO_RUN}"/${agent_name}.winpid
    fi
}

function alloc_domain_name () {

    type=$1
    name=$2

    if [ $(uname -o) == "Cygwin" ]; then
	CMD_CLASSPATH="$AO_JAR;$GETOPT;$LOG4J;$BCEL;."
    else
	CMD_CLASSPATH="${AO_JAR}:${GETOPT}:${LOG4J}:${BCEL}:."
    fi

    # not currently using CMD_CLASSPATH

    java -client ${JAVA_FLAGS} \
	    -Datavism.disable_logs=true \
	    -Datavism.log_level=3 \
	    atavism.msgsys.DomainCommand \
	    $CMDLINE_PROPS \
	    -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
	    -n ${type},${name}

    if [ $? -ne 0 ]; then
	echo "alloc_domain_name failed" 1>&2
	exit 1
    fi
}

function start_world_manager () {

    AGENT_TYPE=wmgr
    #AGENT_NAME=$(alloc_domain_name AGENT ${AGENT_TYPE}_# )
    AGENT_NAME=wmgr_1

    if [ $verbose -gt 0 ]; then
        echo -en "Starting $AGENT_NAME ...     \t"
    fi

    java \
        $JAVA_FLAGS \
	$JMX_FLAGS \
        -Datavism.agenttype=${AGENT_TYPE} \
        -Datavism.loggername=${AGENT_NAME} \
        atavism.server.engine.Engine \
	$CMDLINE_PROPS \
        -i "${AO_BIN}"/wmgr_local1.py \
        -i "${AO_COMMON_CONFIG}"/aomessages.py \
        -i "${AO_WORLD_CONFIG}"/worldmessages.py \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
	"${AO_COMMON_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/global_props.py \
        "${AO_COMMON_CONFIG}"/world_mgr1.py \
        "${AO_WORLD_CONFIG}"/extensions_wmgr.py \
        &

    PID=$!
    write_pid ${AGENT_NAME} $PID

    if [ $verbose -gt 0 ]; then
        check_process $PID
    fi
}

function start_proxy () {

    AGENT_TYPE=proxy
    #AGENT_NAME=$(alloc_domain_name AGENT ${AGENT_TYPE}_# )
    AGENT_NAME=proxy_1

    if [ $verbose -gt 0 ]; then
        echo -en "Starting $AGENT_NAME ...     \t"
    fi

    java  \
        $JAVA_FLAGS \
	$JMX_FLAGS \
        -Datavism.agenttype=$AGENT_TYPE \
        -Datavism.loggername=$AGENT_NAME \
        atavism.server.engine.Engine \
	$CMDLINE_PROPS \
        -i "${AO_BIN}"/proxy.py \
        -i "${AO_COMMON_CONFIG}"/events.py \
        -i "${AO_COMMON_CONFIG}"/aomessages.py \
        -i "${AO_WORLD_CONFIG}"/worldmessages.py \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
        "${AO_COMMON_CONFIG}"/proxy.py \
        "${AO_COMMON_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/extensions_proxy.py \
        &

    PID=$!
    write_pid ${AGENT_NAME} $PID

    if [ $verbose -gt 0 ]; then
        check_process $PID
    fi
}

function archive_log_dir () {
    rm -rf "${AO_LOGS}.old"/*
    mkdir -p "${AO_LOGS}".old
    mv "${AO_LOGS}"/* "${AO_LOGS}".old
}

function start_server () {

    if [ X$ARCHIVE_LOG_DIR = X"true" ]; then
	if [ -d "${AO_LOGS}" ]; then
	    echo "*** Archiving logs ***"
	    archive_log_dir
	fi
    fi

    # Do marshalling code injection if USE_CLASS_FILES is true
    if [ X$USE_CLASS_FILES = X"true" ]; then
        echo "*** Performing injection for $AO_WORLD_NAME ***"
        ./performinjection.sh $AO_WORLD_NAME
    fi
	
    # Use these flags for profiling
    HPROF_FLAGS="-agentlib:hprof=heap=sites,depth=8"
    GCDETAILS_FLAGS="-XX:+PrintGC -XX:+PrintGCDetails"

    # Linux: Use strace with the following flags to monitor one of the
    # atavism server processes.
    #    strace -f -e trace=\!futex,gettimeofday,clock_gettime java \

    echo "*** Starting world $AO_WORLD_NAME ***"
    if [ ! -d "${AO_RUN}" ]
        then
        mkdir -p "${AO_RUN}"
    fi

    if [ ! -d "${AO_LOGS}" ]
        then
        mkdir -p "${AO_LOGS}"
    fi

    rm -f ${AO_RUN}/*.pid
    rm -f ${AO_RUN}/*.winpid

    if [ X$DELETE_LOGS_ON_STARTUP = X"true" ]; then
        rm "${AO_LOGS}"/*.out*
    fi

    if [ X$ENABLE_MGMT = X"true" ]; then
        echo "Enabling JMX mgmt & monitoring"
        JAVA_FLAGS="${JAVA_FLAGS} $JMX_FLAGS"
    fi        

    if [ $verbose -gt 0 ]; then
        echo AO_HOME is $AO_HOME
        if [ X$USE_CLASS_FILES = X"true" ]; then
            echo "Using .class files from the /build hierarchy"
        else
            echo "Using .jar files from the /dist hierarchy"
        fi
        echo Using property file $AO_PROPERTY_FILE  
        echo Using world file $AOW_FILENAME
        echo Using world script directory $AO_WORLD
        echo Using log directory $AO_LOGS
        echo Using common directory $AO_COMMON_CONFIG, bin directory $AO_BIN     
        echo "JAVA_FLAGS=\"${JAVA_FLAGS}\""
    fi

    # Increase the file descriptor limit up to the hard limit
    # Linux: Use /etc/security/limits.conf to set the hard limit
    ulimit -n hard

    AGENT_NAMES="-a combat -a wmgr_1 -a mobserver -a objmgr -a login_manager -a proxy_1 -a instance -a arena -a builder"
#    PLUGIN_TYPES="-p Login,1 -p Proxy,1 -p ObjectManager,1 -p WorldManager,1 -p Inventory,1 -p MobManager,1 -p Quest,1 -p Instance,1 -p Group,1 -p Combat,1 -p ClassAbility,1 -p Domain,1 -p DataLogger,1"
    PLUGIN_TYPES="-p Login,1 -p Proxy,1 -p ObjectManager,1 -p WorldManager,1 -p Inventory,1 -p MobManager,1 -p Quest,1 -p Faction,1 -p Instance,1 -p Group,1 -p Combat,1 -p ClassAbility,1 -p Domain,1 -p DataLogger,1 -p Arena,1 -p Social,1 -p Crafting,1 -p Voxel,1"

    if [ $verbose -gt 0 ]; then
        echo -en "Starting domain server: \t"
    fi

    java ${JAVA_FLAGS} \
        -Datavism.loggername=domain \
        atavism.msgsys.DomainServer \
	$CMDLINE_PROPS \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
        ${AGENT_NAMES} ${PLUGIN_TYPES} \
        &

    write_pid domain $!

    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/domain.pid)
    fi
    
    sleep 3

    start_world_manager

    if [ $verbose -gt 0 ]; then
        echo -en "Starting combat server: \t"
    fi
    java \
        $JAVA_FLAGS \
        -Datavism.loggername=combat \
        atavism.server.engine.Engine \
	$CMDLINE_PROPS \
        -i "${AO_BIN}"/wmgr_local1.py \
        -i "${AO_COMMON_CONFIG}"/aomessages.py \
        -i "${AO_WORLD_CONFIG}"/worldmessages.py \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
        "${AO_COMMON_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/global_props.py \
        "${AO_COMMON_CONFIG}"/skill_db.py \
        "${AO_WORLD_CONFIG}"/skill_db.py \
        "${AO_COMMON_CONFIG}"/ability_db.py \
        "${AO_WORLD_CONFIG}"/ability_db.py \
        "${AO_WORLD_CONFIG}"/classabilityplugin.py \
        "${AO_WORLD_CONFIG}"/combat.py \
        "${AO_WORLD_CONFIG}"/extensions_combat.py \
        "${AO_COMMON_CONFIG}"/profession_db.py \
        "${AO_WORLD_CONFIG}"/profession_db.py \
        "${AO_COMMON_CONFIG}"/groupplugin.py \
        "${AO_WORLD_CONFIG}"/group.py \
        &

    write_pid combat $!

    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/combat.pid)
        echo -en "Starting instance server: \t"
    fi
    java \
        $JAVA_FLAGS \
        -Datavism.loggername=instance \
        atavism.server.engine.Engine \
	$CMDLINE_PROPS \
        -i "${AO_COMMON_CONFIG}"/aomessages.py \
        -i "${AO_WORLD_CONFIG}"/worldmessages.py \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
        "${AO_COMMON_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/global_props.py \
        "${AO_COMMON_CONFIG}"/instance.py \
        "${AO_WORLD_CONFIG}"/startup_instance.py \
        &

    write_pid instance $!

    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/instance.pid)
        echo -en "Starting object manager: \t"
    fi
    java \
        ${JAVA_FLAGS} \
        -Datavism.loggername=objmgr \
        atavism.server.engine.Engine \
	$CMDLINE_PROPS \
        -i "${AO_COMMON_CONFIG}"/aomessages.py \
        -i "${AO_WORLD_CONFIG}"/worldmessages.py \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
        "${AO_COMMON_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/templates.py \
        "${AO_COMMON_CONFIG}"/obj_manager.py \
        "${AO_WORLD_CONFIG}"/mobs_db.py \
        "${AO_WORLD_CONFIG}"/items_db.py \
        "${AO_WORLD_CONFIG}"/craftingplugin.py \
        "${AO_WORLD_CONFIG}"/extensions_objmgr.py \
        "${AO_COMMON_CONFIG}"/datalogger.py \
        &
#        "${AO_COMMON_CONFIG}"/billing.py \

    write_pid objmgr $!

    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/objmgr.pid)
        echo -en "Starting login manager: \t"
    fi

    if [ -f "${AO_WORLD_CONFIG}/login_manager.py" ]; then
	java \
            ${JAVA_FLAGS} \
            -Datavism.loggername=login_manager \
            atavism.server.engine.Engine \
	    $CMDLINE_PROPS \
            -i "${AO_COMMON_CONFIG}"/aomessages.py \
            -i "${AO_WORLD_CONFIG}"/worldmessages.py \
            -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
            "${AO_BIN}"/login_manager.py \
            "${AO_COMMON_CONFIG}"/global_props.py \
            "${AO_WORLD_CONFIG}"/global_props.py \
            "${AO_WORLD_CONFIG}"/login_manager.py \
            "${AO_COMMON_CONFIG}"/character_factory.py \
            "${AO_WORLD_CONFIG}"/character_factory.py \
            "${AO_WORLD_CONFIG}"/extensions_login.py \
            &
    else
	java \
            ${JAVA_FLAGS} \
            -Datavism.loggername=login_manager \
            atavism.server.engine.Engine \
	    $CMDLINE_PROPS \
            -i "${AO_COMMON_CONFIG}"/aomessages.py \
            -i "${AO_WORLD_CONFIG}"/worldmessages.py \
            -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
            "${AO_BIN}"/login_manager.py \
            "${AO_COMMON_CONFIG}"/global_props.py \
            "${AO_WORLD_CONFIG}"/global_props.py \
	    "${AO_COMMON_CONFIG}"/login_manager.py \
            "${AO_COMMON_CONFIG}"/character_factory.py \
            "${AO_WORLD_CONFIG}"/character_factory.py \
            "${AO_WORLD_CONFIG}"/extensions_login.py \
            &
    fi

    write_pid login_manager $!

    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/login_manager.pid)
    fi

    start_proxy

    if [ $verbose -gt 0 ]; then
        echo -en "Starting mob server:    \t"
    fi

    java \
        ${JAVA_FLAGS} \
        -Datavism.loggername=mobserver \
        atavism.server.engine.Engine \
	$CMDLINE_PROPS \
        -i "${AO_BIN}"/mobserver_local.py \
        -i "${AO_COMMON_CONFIG}"/aomessages.py \
        -i "${AO_WORLD_CONFIG}"/worldmessages.py \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
        "${AO_COMMON_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/global_props.py \
        "${AO_COMMON_CONFIG}"/mobserver_init.py \
        "${AO_WORLD_CONFIG}"/mobserver_init.py \
        "${AO_COMMON_CONFIG}"/questplugin.py \
        "${AO_WORLD_CONFIG}"/factionplugin.py \
        "${AO_WORLD_CONFIG}"/socialplugin.py \
        "${AO_COMMON_CONFIG}"/mobserver.py \
        "${AO_WORLD_CONFIG}"/mobserver.py \
        "${AO_WORLD_CONFIG}"/extensions_mobserver.py \
        &

    write_pid mobserver $!
    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/mobserver.pid)
        echo -en "Starting arena server:    \t"
    fi

    java \
        ${JAVA_FLAGS} \
        -Datavism.loggername=arena \
        atavism.server.engine.Engine \
        -i "${AO_COMMON_CONFIG}"/aomessages.py \
        -i "${AO_WORLD_CONFIG}"/worldmessages.py \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
        "${AO_COMMON_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/arenaplugin.py \
        &

    echo $! > ${AO_RUN}/arena.pid

    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/arena.pid)
        echo -en "Starting builder server:    \t"
    fi

    java \
        ${JAVA_FLAGS} \
        -Datavism.loggername=builder \
        atavism.server.engine.Engine \
        -i "${AO_COMMON_CONFIG}"/aomessages.py \
        -i "${AO_WORLD_CONFIG}"/worldmessages.py \
        -t "${AO_COMMON_CONFIG}"/typenumbers.txt \
        "${AO_COMMON_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/global_props.py \
        "${AO_WORLD_CONFIG}"/voxelplugin.py \
        &

    echo $! > ${AO_RUN}/builder.pid

    if [ $verbose -gt 0 ]; then
        check_process $(cat "${AO_RUN}"/builder.pid)
    fi

    echo "Wait for finished initializing msg... "
}

function stop_server () {
    echo "*** Stopping world $AO_WORLD_NAME ***"
    kill_process "login server   " $(cat "${AO_RUN}"/login_manager.pid)
    kill_process "combat server  " $(cat "${AO_RUN}"/combat.pid)
    kill_process "instance       " $(cat "${AO_RUN}"/instance.pid)
    kill_process "object manager " $(cat "${AO_RUN}"/objmgr.pid)
    kill_process "world manager  " $(cat "${AO_RUN}"/wmgr_1.pid)
    kill_process "proxy server   " $(cat "${AO_RUN}"/proxy_1.pid)
    kill_process "mob server     " $(cat "${AO_RUN}"/mobserver.pid)
    kill_process "arena server   " $(cat "${AO_RUN}"/arena.pid)
    kill_process "builder server " $(cat "${AO_RUN}"/builder.pid)
    kill_process "domain server  " $(cat "${AO_RUN}"/domain.pid)
}

function status_server () {
    down=0
    status_process "domain server  " $(cat "${AO_RUN}"/domain.pid)  
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "login server   " $(cat "${AO_RUN}"/login_manager.pid) 
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "combat server  " $(cat "${AO_RUN}"/combat.pid)
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "instance       " $(cat "${AO_RUN}"/instance.pid)
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "object manager " $(cat "${AO_RUN}"/objmgr.pid)
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "world manager  " $(cat "${AO_RUN}"/wmgr_1.pid)
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "proxy server   " $(cat "${AO_RUN}"/proxy_1.pid)
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "mob server     " $(cat "${AO_RUN}"/mobserver.pid)
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "arena server   " $(cat "${AO_RUN}"/arena.pid)
    if [ $? -ne 0 ]; then down=1 ; fi
    status_process "builder server " $(cat "${AO_RUN}"/builder.pid)
    if [ $? -ne 0 ]; then down=1 ; fi
    exit ${down}
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
rm -f _cmdline_props_
while getopts "hvw:p:CMP:Aac:" arg; do
    case "$arg" in
        h)
            echo "$0: usage: $0 [-hvCM] [-w worldname] [-p propertyfilename] (start|stop|status|test)"
            ;;
        v)
            let verbose++
            ;;
	w)
	    AO_WORLD_NAME=$OPTARG
	    if [ -z "$AO_WORLD_CONFIG" ]; then
		export AO_WORLD_CONFIG="$AO_HOME/config/$AO_WORLD_NAME"
	    fi
	    ;;
	p)
	    if [ -z $AO_PROPERTY_FILE ]; then
		AO_PROPERTY_FILE=$OPTARG
	    else
		CMDLINE_PROPS="$CMDLINE_PROPS -p $OPTARG"
		PROPERTY_FILES="$PROPERTY_FILES $OPTARG"
	    fi
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
	    echo $OPTARG >> _cmdline_props_
	    ;;
	A)
	    AGGRESIVE="-XX:CompileThreshold=200 -Xnoclassgc -XX:+RelaxAccessControlCheck"
	    ;;
	a)
	    ARCHIVE_LOG_DIR=true
	    ;;
	c)
	    AO_WORLD_CONFIG="$OPTARG"
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
AO_COMMON_CONFIG=${AO_COMMON_CONFIG:-"${AO_HOME}/config/common"}

AO_PROPERTY_FILE=${AO_PROPERTY_FILE:-"${AO_BIN}/${DEFAULT_AO_PROPERTY_FILE}"}

import_property_file $AO_PROPERTY_FILE
import_property_file "$AO_WORLD_CONFIG/world.properties" $PROPERTY_FILES _cmdline_props_
rm -f _cmdline_props_

if [ -n "$AO_WORLD_NAME" ] ; then
    atavism_worldname=$AO_WORLD_NAME
fi

JAVA_FLAGS="$JAVA_FLAGS -Datavism.worldname=$atavism_worldname"
CMDLINE_PROPS="$CMDLINE_PROPS -Patavism.worldname=$atavism_worldname"

if [ -z "$ARCHIVE_LOG_DIR" -a -n "$atavism_archive_logs_on_startup" ] ; then
    ARCHIVE_LOG_DIR=$atavism_archive_logs_on_startup
fi

# Determine if we should use .class files from the build hierarchy,
# or .jar files from the dist hierarchy.  To run the property getter
# before AO_JAR is set, we always use the dist version of the property
# getter.
USE_CLASS_FILES=${USE_CLASS_FILES:-$atavism_use_class_files}

RHINO=${RHINO:-"${AO_HOME}/other/rhino1_5R5/js.jar"}
GETOPT=${GETOPT:-"${AO_HOME}/other/java-getopt-1.0.11.jar"}
JYTHON=${JYTHON:-"${AO_HOME}/other/jython.jar"}
LOG4J=${LOG4J:-"${AO_HOME}/other/log4j-1.2.15.jar"}
BCEL=${BCEL:-"${AO_HOME}/other/bcel-5.2.jar"}
MBS=${MBS:-"${AO_HOME}/other/mbsclient.jar"}
SHIRO=${SHIRO:-"${AO_HOME}/other/shiro-core-1.0-incubating-SNAPSHOT.jar"}
SLF4J=${SLF4J:-"${AO_HOME}/other/slf4j-api-1.5.6.jar"}
SLF4J_LOG4J=${SLF4J_LOG4J:-"${AO_HOME}/other/slf4j-log4j12-1.5.6.jar"}
#JDBC=${JDBC:-"${AO_HOME}/other/mysql-connector-java-3.1.14-binjar"}
JSON=${JSON:-"${AO_HOME}/other/json_simple-1.1.jar"}

if  [ X$USE_CLASS_FILES = X"true" ]; then
    AO_JAR=${AO_JAR:-"${AO_HOME}/build"}
    AGIS_JAR=${AGIS_JAR:-"${AO_HOME}/build"}
    INJECTED_JAR=${INJECTED_JAR:-"${AO_HOME}/inject"}
else
    AO_JAR=${AO_JAR:-"${AO_HOME}/dist/lib/atavism.jar"}
    AGIS_JAR=${AGIS_JAR:-"${AO_HOME}/dist/lib/agis.jar"}
    INJECTED_JAR=${INJECTED_JAR:-"${AO_HOME}/dist/lib/injected.jar"}
fi

#Get world name from properties file, and construct path to world script dir if not set from env var.
AO_WORLD_NAME=${AO_WORLD_NAME:-$atavism_worldname}
export AO_WORLD_CONFIG=${AO_WORLD_CONFIG:-"$AO_HOME/config/$AO_WORLD_NAME"}

EXT_JAR=${EXT_JAR:="${AO_WORLD_CONFIG}/${AO_WORLD_NAME}.jar"}
JDBC=${JDBC:-$atavism_jdbcJarPath}

if [ $(uname -o) == "Cygwin" ]; then
    export PATH=$(cygpath "$JAVA_HOME"/bin):.:$PATH
    AO_CLASSPATH="$RHINO;$JDBC;$INJECTED_JAR;$AO_JAR;$AGIS_JAR;$EXT_JAR;$GETOPT;$JYTHON;$LOG4J;$BCEL;$MBS;$SLF4J;$SLF4J_LOG4J;$SHIRO;$JSON;."
else
    AO_CLASSPATH="${RHINO}:${JDBC}:${INJECTED_JAR}:${AO_JAR}:${AGIS_JAR}:${EXT_JAR}:${GETOPT}:${JYTHON}:${LOG4J}:${BCEL}:${MBS}:${SLF4J}:${SLF4J_LOG4J}:${SHIRO}:${JSON}:."
fi
        
# HotSpot tracking flags: -XX:+PrintCompilation -XX:+CITime
JVM_FLAG="${JVM_FLAG:-"-server"} $AGGRESIVE"
JAVA_FLAGS="-cp ${AO_CLASSPATH} -Datavism.propertyfile=${AO_PROPERTY_FILE} ${JAVA_FLAGS} -noverify"
JAVA_FLAGS="${JVM_FLAG} ${JAVA_FLAGS}"

# Get path to aow file if set explicitly in atavism.aow file, otherwise, construct path to aow file.
AOW_FILENAME=${AOW_FILENAME:-$atavism_aowfile}

if [ "$AOW_FILENAME" = "null" ]; then
    AOW_FILENAME=$AO_HOME/config/$AO_WORLD_NAME/$AO_WORLD_NAME.aow
fi

if [ X"$AO_HOSTNAME" != "X" ]; then
    CMDLINE_PROPS="$CMDLINE_PROPS -Patavism.hostname=${AO_HOSTNAME}"
    JAVA_FLAGS="$JAVA_FLAGS -Datavism.hostname=${AO_HOSTNAME}"
fi

# This is in local OS format
AO_LOGS=${AO_LOGS:-"${AO_HOME}/logs/${AO_WORLD_NAME}"}
DELETE_LOGS_ON_STARTUP=${DELETE_LOGS_ON_STARTUP:-$atavism_delete_logs_on_startup}

# This should always be in "unix" format
if [ $(uname -o) = "Cygwin" ]; then
    AO_RUN=${AO_RUN:-$(cygpath -w ${AO_BIN}/run/${AO_WORLD_NAME})}
else
    AO_RUN=${AO_RUN:-${AO_BIN}/run/${AO_WORLD_NAME}}
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

    proxy)
        start_proxy
        ;;

    wmgr)
        start_world_manager
        ;;

esac 
 
