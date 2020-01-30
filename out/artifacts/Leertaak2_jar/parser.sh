#!/bin/bash
# parser
#
# description: this script handles starting, stopping and restarting the parser&server java program to handle weatherdata

case $1 in
    start)
        /bin/bash /usr/local/bin/parser-start.sh
    ;;
    stop)
        /bin/bash /usr/local/bin/parser-stop.sh
    ;;
    restart)
        /bin/bash /usr/local/bin/parser-stop.sh
        /bin/bash /usr/local/bin/parser-start.sh
    ;;
esac
exit 0