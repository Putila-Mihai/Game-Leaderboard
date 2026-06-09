#!/bin/bash

echo "Watching consumer lag for leaderboard-service..."
echo "Press Ctrl+C to stop"
echo ""

max_lag=0

while true; do
    current_lag=$(docker exec redpanda-0 rpk group describe leaderboard-service 2>/dev/null | \
        grep "TOTAL-LAG" | awk '{print $2}')

    if [ -n "$current_lag" ] && [ "$current_lag" -gt "$max_lag" ]; then
        max_lag=$current_lag
    fi

    echo "=== $(date '+%H:%M:%S') === current: $current_lag | max: $max_lag"
    sleep 1
done