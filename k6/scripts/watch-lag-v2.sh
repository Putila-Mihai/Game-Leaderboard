#!/bin/bash

echo "Watching full pipeline lag..."
echo "Press Ctrl+C to stop"
echo ""

max_lag_streams=0
max_lag_computed=0

while true; do
    echo "=== $(date '+%H:%M:%S') ==="

    lag_streams=$(docker exec redpanda-0 rpk group describe leaderboard-streams \
        2>/dev/null | grep "TOTAL-LAG" | awk '{print $2}')

    lag_computed=$(docker exec redpanda-0 rpk group describe leaderboard-computed-service \
        2>/dev/null | grep "TOTAL-LAG" | awk '{print $2}')

    if [ -n "$lag_streams" ] && [ "$lag_streams" -gt "$max_lag_streams" ]; then
        max_lag_streams=$lag_streams
    fi

    if [ -n "$lag_computed" ] && [ "$lag_computed" -gt "$max_lag_computed" ]; then
        max_lag_computed=$lag_computed
    fi

    echo "Streams  (raw→computed):    current=$lag_streams  max=$max_lag_streams"
    echo "Consumer (computed→Redis):  current=$lag_computed  max=$max_lag_computed"
    echo ""

    sleep 1
done