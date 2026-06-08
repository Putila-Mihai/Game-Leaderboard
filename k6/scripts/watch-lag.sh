echo "Watching consumer lag for leaderboard-service..."
echo "Press Ctrl+C to stop"
echo ""

while true; do
    echo "=== $(date '+%H:%M:%S') ==="
    docker exec redpanda-0 rpk group describe leaderboard-service | grep  "TOTAL-LAG"
    echo ""
    sleep 1
done