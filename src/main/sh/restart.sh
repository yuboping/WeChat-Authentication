target="target=wxserver"
PID=`ps -ef | grep "${target}" | grep -v "grep" | awk ' { print $2 } '`
if [ 'x' != "x${PID}" ]; then
   kill -9 $PID
else
    echo "server not running !"
fi

sleep 2
sh ../shell/startserver.sh