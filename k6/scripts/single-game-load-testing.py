import http from "k6/http"
import { check, sleep } from "k6"
import { Rate, Trend } from "k6/metrics"

const errorRate = new Rate("errors")
const publishDuration = new Trend("publish_duration")

const GAME_ID = "quake"
const PLAYERS = 50

export const options = {
  stages: [
    { duration: "30s", target: 400 },
    { duration: "30s", target: 700 },
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"],
    errors: ["rate<0.01"],
  },
}

const playerScores = {}


export default function () {
  const playerIndex = Math.floor(Math.random() * PLAYERS)
  const playerId = `player_${playerIndex}`

  if (!playerScores[playerId]) {
    playerScores[playerId] = Math.floor(Math.random() * 1000) + 500
  }

  playerScores[playerId] += Math.floor(Math.random() * 200) + 1

  const payload = JSON.stringify({
    playerId: playerId,
    gameId: GAME_ID,
    score: playerScores[playerId]
  })

  const res = http.post(
    "http://localhost:8090/scores",
    payload,
    { headers: { "Content-Type": "application/json" } }
  )

  const success = check(res, {
    "status is 202": (r) => r.status === 202,
    "response time < 200ms": (r) => r.timings.duration < 200,
  })

  errorRate.add(!success)
  publishDuration.add(res.timings.duration)

  sleep(0.1)
}