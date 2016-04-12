@ echo off
:: This batch file runs the Atavism sever processes on Windows
:: You must have installed Java, a database, JDBC driver, and the Atavism servers
:: Copyright 2013 Neojac Entertainment, Inc.

:: Set DEFAULT_AO_PROPERTYFILE if you want to use a different defult property file
if defined DEFAULT_AO_PROPERTYFILE (
   echo DEFAULT_AO_PROPERTYFILE is %DEFAULT_AO_PROPERTYFILE%
) else (
   echo DEFAULT_AO_PROPERTYFILE is not defined using atavism.properties
   set DEFAULT_AO_PROPERTYFILE=world.properties
)

:: Set to true to enable JMX management and monitoring
if not defined ENABLE_MGMT set ENABLE_MGMT=false

:: Check that script is being run from AO_home\bin
if not defined AO_HOME (
  echo AO_HOME is not defined, using relative paths
  if exist .\start-world.bat (
    set AO_HOME=..
  ) else (
    echo Batch script must be run from AO_HOME\bin directory!
  )
) 

echo AO_HOME is %AO_HOME%
echo ENABLE_MGMT is %ENABLE_MGMT%

:: Change to "server" to use the server Java VM
set JVM_TYPE=client
set JVM_HEAP_FLAGS=-Xms32m -Xmx256m

set PROPFILE=%1
if %1x==x (
  set PROPFILE=%DEFAULT_AO_PROPERTYFILE%
)
echo Using properties file %PROPFILE%

if not defined AO_JAR (
  set AO_JAR=%AO_HOME%\dist\lib\atavism.jar
)
if not defined MARS_JAR (
  set MARS_JAR=%AO_HOME%\dist\lib\mars.jar
)
if not defined INJECTED_JAR (
  set INJECTED_JAR=%AO_HOME%\dist\lib\injected.jar
)

:: Set value of AO_WORLDNAME from atavism.worldname in property file
java -cp %AO_JAR% -Datavism.propertyfile=%PROPFILE% -Dwin_env_var=AO_WORLDNAME atavism.scripts.PropertyGetter atavism.worldname > tmp.bat
call tmp.bat
del tmp.bat

set JYTHON=%AO_HOME%\other\jython.jar
set RHINO=%AO_HOME%\other\rhino1_5R5\js.jar
set GETOPT=%AO_HOME%\other\java-getopt-1.0.11.jar
set LOG4J=%AO_HOME%\other\log4j-1.2.14.jar
set BCEL=%AO_HOME%\other\bcel-5.2.jar
set EXT_JAR=%AO_HOME%\dist\lib\%AO_WORLDNAME%.jar

:: Get path to JDBC JAR file from property file, unless set in env. var.
if not defined JDBC (
  java -cp %AO_JAR% -Datavism.propertyfile=%PROPFILE% -Dwin_env_var=JDBC atavism.scripts.PropertyGetter atavism.jdbcJarPath > tmp.bat
  call tmp.bat
  del tmp.bat
)
echo JDBC is %JDBC%

set AO_CLASSPATH=%INJECTED_JAR%;%AO_JAR%;%MARS_JAR%;%EXT_JAR%;%RHINO%;%GETOPT%;%JYTHON%;%JDBC%;%LOG4J%;%BCEL%

set CMDLINE_PROPS=
if defined AO_HOSTNAME (
  set CMDLINE_PROPS=-Patavism.hostname=%AO_HOSTNAME%
)

set JAVA_FLAGS=-%JVM_TYPE% %JVM_HEAP_FLAGS% -cp "%AO_CLASSPATH%" -Datavism.propertyfile=%PROPFILE%

:: Set value of MVW_FILENAME from atavism.mvwfile in property file if it is there, if not set based on AO_WORLDNAME
java -cp %AO_JAR% -Datavism.propertyfile=%PROPFILE% -Dwin_env_var=MVW_FILENAME atavism.scripts.PropertyGetter atavism.mvwfile > tmp.bat
call tmp.bat
del tmp.bat

if %MVW_FILENAME%==null (
    set MVW_FILENAME="%AO_HOME%\config\%AO_WORLDNAME%\%AO_WORLDNAME%.mvw"
)
echo Using world file %MVW_FILENAME%

set AO_LOGS=%AO_HOME%\logs\%AO_WORLDNAME%
set JAVA_FLAGS=%JAVA_FLAGS% -Datavism.logs=%AO_LOGS%

if not exist %AO_LOGS% (
  mkdir %AO_LOGS%
)

if not defined DELETE_LOGS_ON_STARTUP (
  java -cp %AO_JAR% -Datavism.propertyfile=%PROPFILE% -Dwin_env_var=DELETE_LOGS_ON_STARTUP atavism.scripts.PropertyGetter atavism.delete_logs_on_startup > tmp.bat
  call tmp.bat
  del tmp.bat
)

if %DELETE_LOGS_ON_STARTUP%==true (
  echo Deleting existing log files
  del %AO_LOGS%\*.out*
)

if not exist run (
  echo Creating run directory
  mkdir run
)

if not exist run\%AO_WORLDNAME% (
  echo Creating run\%AO_WORLDNAME% directory
  mkdir run\%AO_WORLDNAME%
)
del run\%AO_WORLDNAME%\*.bat

set JAVA_FLAGS=-Datavism.rundir=run\%AO_WORLDNAME% %JAVA_FLAGS%

if %ENABLE_MGMT%==true (
  echo Enabling JMX mgmt and monitoring
  set JAVA_FLAGS=-Dcom.sun.management.jmxremote %JAVA_FLAGS%
) 

set AO_COMMON=%AO_HOME%\config\common
set AO_WORLD=%AO_HOME%\config\%AO_WORLDNAME%
set AGENT_NAMES=-a combat -a wmgr_1 -a mobserver -a objmgr -a login_manager -a proxy_1 -a instance -a voiceserver
set PLUGIN_TYPES=-p Login,1 -p Proxy,1 -p ObjectManager,1 -p WorldManager,1 -p Inventory,1 -p MobManager,1 -p Quest,1 -p Instance,1 -p Voice,1 -p Trainer,1 -p Group,1 -p Combat,1 -p ClassAbility,1 -p Domain,1

echo Using world script directory %AO_WORLD%
echo Using log directory %AO_LOGS%
echo Using common directory %AO_COMMON%
echo Java Flags are: %JAVA_FLAGS%

echo Starting message domain server
@ echo on
START /B java  %JAVA_FLAGS% ^
    -Datavism.loggername=domain ^
    atavism.msgsys.DomainServer ^
    %CMDLINE_PROPS% ^
    -t %AO_COMMON%\typenumbers.txt ^
    %AGENT_NAMES% ^
    %PLUGIN_TYPES%

@ echo off
echo Starting world manager
START /B java ^
    %JAVA_FLAGS% ^
    -Datavism.agenttype=wmgr ^
    -Datavism.loggername=wmgr_1 ^
    atavism.server.engine.Engine ^
    %CMDLINE_PROPS% ^
    -i wmgr_local1.py ^
    -i %AO_COMMON%\mvmessages.py ^
    -i %AO_WORLD%\worldmessages.py ^
    -t %AO_COMMON%\typenumbers.txt ^
    %AO_COMMON%\global_props.py ^
    %AO_WORLD%\global_props.py ^
    %AO_COMMON%\world_mgr1.py ^
    %AO_WORLD%\extensions_wmgr.py
        
echo Starting combat server
START /B java ^
    %JAVA_FLAGS% ^
    -Datavism.loggername=combat ^
    atavism.server.engine.Engine ^
    %CMDLINE_PROPS% ^
    -i wmgr_local1.py ^
    -i %AO_COMMON%\mvmessages.py ^
    -i %AO_WORLD%\worldmessages.py ^
    -t %AO_COMMON%\typenumbers.txt ^
    %AO_COMMON%\global_props.py ^
    %AO_WORLD%\global_props.py ^
    %AO_COMMON%\skill_db.py ^
    %AO_WORLD%\skill_db.py ^
    %AO_COMMON%\ability_db.py ^
    %AO_WORLD%\ability_db.py ^
    %AO_WORLD%\classabilityplugin.py ^
    %AO_WORLD%\combat.py ^
    %AO_WORLD%\extensions_combat.py ^
    %AO_COMMON%\profession_db.py ^
    %AO_WORLD%\profession_db.py ^
    %AO_COMMON%\groupplugin.py ^
    %AO_WORLD%\group.py

echo Starting instance server
START /B java ^
    %JAVA_FLAGS% ^
    -Datavism.loggername=instance ^
    atavism.server.engine.Engine ^
    %CMDLINE_PROPS% ^
    -i %AO_COMMON%\mvmessages.py ^
    -i %AO_WORLD%\worldmessages.py ^
    -t %AO_COMMON%\typenumbers.txt ^
    %AO_COMMON%\global_props.py ^
    %AO_WORLD%\global_props.py ^
    %AO_COMMON%\instance.py ^
    %AO_WORLD%\startup_instance.py

echo Starting object manager
START /B java ^
    %JAVA_FLAGS% ^
    -Datavism.loggername=objmgr ^
    atavism.server.engine.Engine ^
    %CMDLINE_PROPS% ^
    -i wmgr_local1.py ^
    -i %AO_COMMON%\mvmessages.py ^
    -i %AO_WORLD%\worldmessages.py ^
    -t %AO_COMMON%\typenumbers.txt ^
    %AO_COMMON%\global_props.py ^
    %AO_WORLD%\global_props.py ^
    %AO_WORLD%\templates.py ^
    %AO_COMMON%\obj_manager.py ^
    %AO_WORLD%\mobs_db.py ^
    %AO_WORLD%\items_db.py ^
    %AO_WORLD%\extensions_objmgr.py

echo Starting login manager
START /B java ^
    %JAVA_FLAGS% ^
    -Datavism.loggername=login_manager ^
    atavism.server.engine.Engine ^
    %CMDLINE_PROPS% ^
    -i %AO_COMMON%\mvmessages.py ^
    -i %AO_WORLD%\worldmessages.py ^
    -t %AO_COMMON%\typenumbers.txt ^
    login_manager.py ^
    %AO_COMMON%\login_manager.py ^
    %AO_COMMON%\character_factory.py ^
    %AO_WORLD%\character_factory.py ^
    %AO_WORLD%\extensions_login.py

echo Starting proxy server
START /B java  ^
    %JAVA_FLAGS% ^
    -Datavism.loggername=proxy_1 ^
    -Datavism.agenttype=proxy ^
    atavism.server.engine.Engine ^
    %CMDLINE_PROPS% ^
    -i proxy.py ^
    -i %AO_COMMON%\events.py ^
    -i %AO_COMMON%\mvmessages.py ^
    -i %AO_WORLD%\worldmessages.py ^
    -t %AO_COMMON%\typenumbers.txt ^
    %AO_COMMON%\proxy.py ^
    %AO_COMMON%\global_props.py ^
    %AO_WORLD%\global_props.py ^
    %AO_WORLD%\extensions_proxy.py

echo Starting mob server
START /B java ^
    %JAVA_FLAGS% ^
    -Datavism.loggername=mobserver ^
    atavism.server.engine.Engine ^
    %CMDLINE_PROPS% ^
    -i mobserver_local.py ^
    -i %AO_COMMON%\mvmessages.py ^
    -i %AO_WORLD%\worldmessages.py ^
    -t %AO_COMMON%\typenumbers.txt ^
    %AO_COMMON%\global_props.py ^
    %AO_WORLD%\global_props.py ^
    %AO_COMMON%\mobserver_init.py ^
    %AO_WORLD%\mobserver_init.py ^
    %AO_COMMON%\questplugin.py ^
    %AO_COMMON%\trainerplugin.py ^
    %AO_COMMON%\mobserver.py ^
    %AO_WORLD%\mobserver.py ^
    %AO_WORLD%\extensions_mobserver.py

echo Starting voice server
START /B java ^
    %JAVA_FLAGS% ^
    -Datavism.loggername=voiceserver ^
    atavism.server.engine.Engine ^
    %CMDLINE_PROPS% ^
    -i %AO_COMMON%\mvmessages.py ^
    -i %AO_WORLD%\worldmessages.py ^
    -t %AO_COMMON%\typenumbers.txt ^
    %AO_COMMON%\voice.py ^
    %AO_WORLD%\voice.py 
     
echo Wait for finished initializing msg... 
