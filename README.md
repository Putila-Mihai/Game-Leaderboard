# Game Leaderboard Service

A real-time leaderboard built to learn Kafka in more depth.

## What it does
Players submit scores via a REST API. Scores flow through Kafka
and land in Redis sorted sets that power the leaderboard queries.

## Tech stack
- **Spring Boot** - REST API, Kafka consumer, Kafka Streams
- **Redpanda** - Kafka-compatible broker (no ZooKeeper)
- **Redis** - leaderboard state via sorted sets (ZADD/ZREVRANGE)
- **Avro + Schema Registry** - event serialization
- **OpenAPI** - API design

## Concepts explored
- Manual offset commit and at-least-once delivery guarantees

## Chaos scenarios
> To be documented after load testing

## Running locally
```bash
docker compose up -d
./gradlew bootRun
```

Redpanda Console: http://localhost:8080
API: http://localhost:8090

## reset redis state
`docker exec -it redis redis-cli DEL leaderboard:quake`

## Observations
used k6 to test load on producer handled 1000req/s
