<img width="1358" height="906" alt="Screenshot 2026-06-18 at 00 36 36" src="https://github.com/user-attachments/assets/eb24d502-6179-47f1-9cb6-3535c9fa2da2" />

## I created this small project to test and learn more Kafka, and also to create a future playground for testing things

**FE Part is AI-generated**

## Concepts explored
- Manual offset commit and at-least-once delivery guarantees
- Partitioning by `gameId` caused a partition to take all events. Fixed with composite key `gameId:playerId` for balanced
  distribution
- Consumer concurrency — matching thread count to partition count is the key
  scaling lever (see observations)
- Avro schema evolution — backward compatibility requires new fields to have
  defaults. Breaking changes require topic replay in dev or migration strategy
  in production
- Redis sorted sets
- Kstreams
- SSE

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

**Added kstreams**

Increased lag to 64645 locally , but probably on production where kstreams they scale much better. 

different config => 1,630 max lag

- linger.ms=5 + batch-size=65536 — producer batching
- cache.max.bytes.buffering=10MB — fewer RocksDB flushes in Kafka Streams
- commit.interval.ms=100 — consistent forward progress instead of 30s bursts

**Added SSE for real time App view** 

## TODO:
> kill consumer mid load test, Redis down scenario, poison event
