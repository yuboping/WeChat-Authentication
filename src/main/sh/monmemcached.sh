#! /bin/bash

smsIP=172.16.51.35
servername=memcached
portrnum=`netstat -an | grep 11211 | grep LISTEN | grep -v grep | wc -l`

echo "portrnum:${portrnum}"
if [ ${portrnum} -ge 1 ]
then
	servernum=`ps -ef|grep memcached|grep -v grep|grep 11211 | wc -l`
	echo servernum"${servernum}"
	if [ ${servernum} -ge 1 ]
	then
		echo "`date +'[%Y-%m-%d %H:%M:%S]'` ${servername} is running! "
	else
		echo "`date +'[%Y-%m-%d %H:%M:%S]'` ${servername} is stop! "
		messagesend ${smsIP} "[`date "+%Y-%m-%d %H:%M:%S"`] [`hostname`] Service ${servername} is stop!restart"
		/usr/local/bin/memcached -u root -p 11211 -m 1024m &
	fi
    
else
	echo "`date +'[%Y-%m-%d %H:%M:%S]'` ${servername} is stop! "
	messagesend ${smsIP} "[`date "+%Y-%m-%d %H:%M:%S"`] [`hostname`] Service ${servername} is stop!restart"
	/usr/local/bin/memcached -u root -p 11211 -m 1024m &
fi

