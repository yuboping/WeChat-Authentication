#! /bin/bash

smsIP=172.16.51.35
servername="wxserver"
servernum=`ps -ef|grep target=${servername}|grep -v grep | wc -l`

echo ${servernum}
if [ ${servernum} -ge 1 ]
then
    echo "`date +'[%Y-%m-%d %H:%M:%S]'` ${servername} is running! "
else
	echo "`date +'[%Y-%m-%d %H:%M:%S]'` ${servername} is stop! "
	messagesend ${smsIP} "[`date "+%Y-%m-%d %H:%M:%S"`] [`hostname`] Service ${servername} is stop!restart"
	cd /lcims/support70/wxserver/sh
	sh restart.sh
fi