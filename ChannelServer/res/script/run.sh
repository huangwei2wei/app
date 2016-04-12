LIB_PATH=.
for i in ../lib/*.jar; do
LIB_PATH=${LIB_PATH}:$i
done


$JAVA_HOME/bin/java -server -Xmx1024m -XX:MaxDirectMemorySize=256M -verbose:gc -Djava.awt.headless=true -classpath .:$CLASSPATH:$LIB_PATH:ChannelServer.jar com.wyd.channel.Server >> stdout.log 2>&1 &

echo $! > ChannelServer.pid
