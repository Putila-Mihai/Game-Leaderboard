## Concepts explored
- Manual offset commit and at-least-once delivery guarantees
- Partitioning by `gameId` caused a partition to take all events. Fixed with composite key `gameId:playerId` for balanced
  distribution
- Consumer concurrency — matching thread count to partition count is the key
  scaling lever (see observations)
- Avro schema evolution — backward compatibility requires new fields to have
  defaults. Breaking changes require topic replay in dev or migration strategy
  in production
- Redis sorted sets — ZADD/ZREVRANGE for O(log N) leaderboard reads
- 
## Running locally
```bash
docker compose up -d
./gradlew bootRun
```

## Useful commands

Reset leaderboard state:
```bash
docker exec -it redis redis-cli DEL leaderboard:quake
```

Nuke container state and start fresh:
```bash
docker compose down -v && docker compose up -d
```

## Load test observations

Test setup: ~3700 req/s sustained, 50 players, single game (quake),
scores incrementing per player to simulate realistic gameplay.

| Partitions | Consumer threads | Peak lag | Producer p95 |
|------------|-----------------|----------|--------------|
| 4          | 1               | 121,102  | 1.17ms       |
| 4          | 4               | 18,006   | 1.41ms       |
| 4          | 8               | 113,479  | 1.00ms       |
| 8          | 8               | 524      | 1.98ms       |

**Key findings:**

Matching consumer thread count to partition count is the key scaling lever.
4 partitions + 1 thread caused lag peak at 121k messages. 4 partitions +
4 threads dropped it 85% to 18k.

4 partitions + 8 threads performed *worse* than 4+4 — extra threads idle probably. More threads

Doubling to 8 partitions + 8 threads reduced peak lag by 99.5% vs the baseline,
to just 524 messages at identical throughput.

## Chaos scenarios

> To be documented — kill consumer mid load test, Redis down scenario, poison
> event DLT routing