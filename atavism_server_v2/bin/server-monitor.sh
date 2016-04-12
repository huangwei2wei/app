#!/bin/sh

if [ $(uname -o) = "Cygwin" ]; then
    AO_HOME_UNIX=$(cygpath -u "${AO_HOME}")
else
    AO_HOME_UNIX="$AO_HOME"
fi

AO_BIN=${AO_BIN:-"${AO_HOME}/bin"}
AO_MAILCMD=${AO_MAILCMD:-"/usr/sbin/sendmail"}

function health_check() {
    local rv
    ${AO_BIN}/world.sh status > /dev/null
    rv=$?
    if [ $verbose -gt 1 ]; then
	if [ $rv -eq 0 ]; then
	    echo "process test: PASS"
	else
	    echo "process test: FAIL"
	fi
    fi
    if [ $rv -ne 0 ]; then
	if [ $verbose -gt 0 ]; then
	    echo "server health check failed"
	fi
	return $rv
    fi

    if [ $verbose -gt 1 ]; then
	${AO_BIN}/world.sh -v test
    else
	${AO_BIN}/world.sh test
    fi
    rv=$?
    if [ $rv -ne 0 ]; then
	if [ $verbose -gt 0 ]; then
	    echo "server health check failed"
	fi
	return $rv
    fi

    if [ $verbose -gt 0 ]; then
	echo "server health check passed"
    fi
    return 0
}

function restart_server() {
    if [ $verbose -gt 0 ]; then
	echo -n "stopping server: "
    fi
    ${AO_BIN}/world.sh stop
    if [ $verbose -gt 0 ]; then
	echo "DONE"
	echo -n "rotating logs: "
    fi
    ${AO_BIN}/rotate-logs.sh
    if [ $verbose -gt 0 ]; then
	echo "DONE"
	echo -n "restarting: "
    fi
    if [ $verbose -gt 1 ]; then
	echo
	${AO_BIN}/world.sh -v start
    else
	${AO_BIN}/world.sh start
    fi
    if [ $verbose -gt 0 ]; then
	echo "DONE"
    fi
}

function send_alert() {
    if [ -n "$AO_MAILTO" ]; then
	$AO_MAILCMD $AO_MAILTO <<EOF
Subject: Atavism Online Server restart

$(date)
The Atavism Online Server is being restarted because it failed a health check.

Current status:
$(${AO_BIN}/world.sh status)
EOF
    fi
}

verbose=0
while getopts "hv" arg; do
    case "$arg" in
	h)
	    echo "$0: usage: $0 [-hv]"
	    ;;
	v)
	    let verbose++
	    ;;
    esac
done
shift $((OPTIND-1))

if [ $verbose -gt 0 ]; then
    date
fi

health_check
if [ $? -ne 0 ]; then
    send_alert
    restart_server
fi
