LIB_PATH=.
for i in ../lib/*.jar; do
LIB_PATH=${LIB_PATH}:$i
done

for i in ../serverLib/*.jar; do
LIB_PATH=${LIB_PATH}:$i
done


$JAVA_HOME/bin/java -server -Xmx512m -XX:MaxDirectMemorySize=128M -verbose:gc -Djava.awt.headless=true -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider -classpath .:$CLASSPATH:$LIB_PATH:dddDispatchServer.jar com.app.dispatch.ToolServer >> stdout.log 2>&1 &

echo $! > ddddispatchserver.pid
