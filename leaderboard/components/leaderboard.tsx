"use client"

import { useState, useEffect } from "react"
import { motion, AnimatePresence } from "framer-motion"

interface Player {
  id: number
  name: string
  score: number
}

const RANK_STYLES = [
  { bg: "from-red-950/95 via-red-900/90 to-red-950/95", border: "#ff0a3c", glow: "rgba(255, 10, 60, 0.6)", text: "#ff0a3c" },
  { bg: "from-fuchsia-950/95 via-fuchsia-900/90 to-fuchsia-950/95", border: "#d946ef", glow: "rgba(217, 70, 239, 0.5)", text: "#d946ef" },
  { bg: "from-orange-950/95 via-orange-900/90 to-orange-950/95", border: "#f97316", glow: "rgba(249, 115, 22, 0.5)", text: "#f97316" },
  { bg: "from-cyan-950/95 via-cyan-900/90 to-cyan-950/95", border: "#00f0ff", glow: "rgba(0, 240, 255, 0.4)", text: "#00f0ff" },
  { bg: "from-blue-950/95 via-blue-900/90 to-blue-950/95", border: "#3b82f6", glow: "rgba(59, 130, 246, 0.35)", text: "#3b82f6" },
  { bg: "from-violet-950/95 via-violet-900/90 to-violet-950/95", border: "#8b5cf6", glow: "rgba(139, 92, 246, 0.3)", text: "#8b5cf6" },
  { bg: "from-slate-900/95 via-slate-800/90 to-slate-900/95", border: "#64748b", glow: "rgba(100, 116, 139, 0.25)", text: "#94a3b8" },
  { bg: "from-slate-900/95 via-slate-850/90 to-slate-900/95", border: "#475569", glow: "rgba(71, 85, 105, 0.2)", text: "#64748b" },
  { bg: "from-zinc-900/95 via-zinc-800/90 to-zinc-900/95", border: "#3f3f46", glow: "rgba(63, 63, 70, 0.15)", text: "#52525b" },
  { bg: "from-neutral-900/95 via-neutral-850/90 to-neutral-900/95", border: "#262626", glow: "rgba(38, 38, 38, 0.1)", text: "#404040" },
]

const PLAYER_NAMES = [
  "REAPER_X",
  "N30N_HUNT3R",
  "CYB3R_W0LF",
  "GHOST_PR0T0C0L",
  "BLAD3_RUNN3R",
  "SYST3M_SH0CK",
  "D4RK_SYNTH",
  "V0ID_W4LK3R",
  "N1GHT_C1TY",
  "R3D_QU33N",
]

function generatePlayers(): Player[] {
  return PLAYER_NAMES.map((name, i) => ({
    id: i + 1,
    name,
    score: Math.floor(Math.random() * 50000) + 10000,
  })).sort((a, b) => b.score - a.score)
}

function PlayerCard({ player, rank }: { player: Player; rank: number }) {
  const style = RANK_STYLES[rank - 1] || RANK_STYLES[9]
  const isTopThree = rank <= 3

  return (
    <motion.div
      layout
      initial={{ opacity: 0, x: -30, scale: 0.95 }}
      animate={{ opacity: 1, x: 0, scale: 1 }}
      exit={{ opacity: 0, x: 30, scale: 0.95 }}
      transition={{ duration: 0.4, ease: [0.4, 0, 0.2, 1] }}
      className="relative group"
    >
      {/* Outer glow */}
      <div
        className={`absolute -inset-1 rounded-lg blur-md opacity-60 ${isTopThree ? "animate-pulse" : ""}`}
        style={{ background: style.glow }}
      />

      {/* Main card container */}
      <div
        className={`
          relative overflow-hidden rounded-lg
          bg-gradient-to-r ${style.bg}
          backdrop-blur-xl
          border-2 border-solid
          transition-all duration-300
          group-hover:scale-[1.02] group-hover:translate-x-2
        `}
        style={{
          borderColor: style.border,
          boxShadow: `
            0 0 30px ${style.glow},
            inset 0 0 60px rgba(0, 0, 0, 0.5),
            inset 0 2px 0 rgba(255, 255, 255, 0.05)
          `,
        }}
      >
        {/* Metallic sheen overlay */}
        <div className="absolute inset-0 bg-gradient-to-br from-white/5 via-transparent to-black/20 pointer-events-none" />

        {/* Grid pattern overlay */}
        <div
          className="absolute inset-0 opacity-10 pointer-events-none"
          style={{
            backgroundImage: `
              linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
              linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px)
            `,
            backgroundSize: "20px 20px",
          }}
        />

        {/* Content */}
        <div className="relative flex items-center justify-between px-5 py-4">
          {/* Left side - Rank + Name */}
          <div className="flex items-center gap-4">
            {/* Rank badge */}
            <div
              className="relative flex items-center justify-center w-14 h-14 rounded-md font-mono text-2xl font-black"
              style={{
                background: `linear-gradient(135deg, ${style.border}22, ${style.border}11)`,
                border: `2px solid ${style.border}`,
                color: style.text,
                textShadow: isTopThree
                  ? `0 0 10px ${style.text}, 0 0 20px ${style.text}`
                  : `0 0 5px ${style.text}`,
                boxShadow: `inset 0 0 20px ${style.glow}`,
              }}
            >
              {rank}
              {/* Corner accents for top 3 */}
              {isTopThree && (
                <>
                  <span
                    className="absolute -top-px -left-px w-3 h-3 border-t-2 border-l-2"
                    style={{ borderColor: style.border }}
                  />
                  <span
                    className="absolute -bottom-px -right-px w-3 h-3 border-b-2 border-r-2"
                    style={{ borderColor: style.border }}
                  />
                </>
              )}
            </div>

            {/* Vertical separator */}
            <div
              className="w-0.5 h-10 rounded-full"
              style={{
                background: `linear-gradient(180deg, transparent, ${style.border}, transparent)`,
              }}
            />

            {/* Player name */}
            <div className="flex flex-col">
              <span
                className="text-lg md:text-xl font-bold tracking-wide"
                style={{
                  color: isTopThree ? style.text : "#e5e5e5",
                  textShadow: isTopThree ? `0 0 8px ${style.glow}` : "none",
                }}
              >
                {player.name}
              </span>
              <span className="text-xs text-neutral-500 font-mono tracking-wider">
                PLAYER #{player.id.toString().padStart(3, "0")}
              </span>
            </div>
          </div>

          {/* Right side - Score */}
          <div className="flex flex-col items-end">
            <span
              className="text-2xl md:text-3xl font-black font-mono tabular-nums tracking-tight"
              style={{
                color: style.text,
                textShadow: `0 0 10px ${style.glow}, 0 0 20px ${style.glow}`,
              }}
            >
              {player.score.toLocaleString()}
            </span>
            <span className="text-xs text-neutral-500 font-mono">POINTS</span>
          </div>
        </div>

        {/* Animated border sweep for top 3 */}
        {isTopThree && (
          <div
            className="absolute inset-0 pointer-events-none"
            style={{
              background: `linear-gradient(90deg, transparent, ${style.border}33, transparent)`,
              backgroundSize: "200% 100%",
              animation: "sweep 3s linear infinite",
            }}
          />
        )}
      </div>
    </motion.div>
  )
}

export default function Leaderboard() {
  const [players, setPlayers] = useState<Player[]>([])

  useEffect(() => {
    setPlayers(generatePlayers())

    const interval = setInterval(() => {
      setPlayers((prev) =>
        prev
          .map((p) => ({
            ...p,
            score: p.score + Math.floor(Math.random() * 500) - 100,
          }))
          .sort((a, b) => b.score - a.score)
      )
    }, 3000)

    return () => clearInterval(interval)
  }, [])

  return (
    <div className="relative min-h-screen w-full overflow-hidden">
      {/* Background image */}
      <div
        className="fixed inset-0 bg-cover bg-center bg-no-repeat"
        style={{
          backgroundImage: "url(/images/cyberpunk-bg.jpg)",
          transform: "scale(1.1)",
        }}
      />

      {/* Dark overlay */}
      <div className="fixed inset-0 bg-black/50" />

      {/* Gradient overlays */}
      <div className="fixed inset-0 bg-gradient-to-t from-black/70 via-transparent to-black/50" />
      <div className="fixed inset-0 bg-gradient-to-r from-black/30 via-transparent to-black/30" />

      {/* Scanlines */}
      <div className="fixed inset-0 pointer-events-none z-50 opacity-30">
        <div
          className="w-full h-full"
          style={{
            background: "repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,0,0,0.1) 2px, rgba(0,0,0,0.1) 4px)",
          }}
        />
      </div>

      {/* Vignette */}
      <div
        className="fixed inset-0 pointer-events-none z-40"
        style={{
          background: "radial-gradient(ellipse at center, transparent 0%, transparent 40%, rgba(0,0,0,0.8) 100%)",
        }}
      />

      {/* Content */}
      <div className="relative z-30 flex flex-col items-center justify-center min-h-screen p-4 md:p-8">
        {/* Header */}
        <div className="mb-8 text-center">
          <h1
            className="text-4xl md:text-6xl font-black tracking-widest mb-2"
            style={{
              color: "#ff0a3c",
              textShadow: "0 0 10px #ff0a3c, 0 0 30px #ff0a3c, 0 0 50px #ff0a3c",
            }}
          >
            LEADERBOARD
          </h1>
          <div className="flex items-center justify-center gap-4">
            <div className="h-px w-16 md:w-24 bg-gradient-to-r from-transparent to-red-500" />
            <span className="text-xs md:text-sm font-mono text-red-400/70 tracking-[0.3em] uppercase">
              Top 10 Players
            </span>
            <div className="h-px w-16 md:w-24 bg-gradient-to-l from-transparent to-red-500" />
          </div>
        </div>

        {/* Leaderboard container */}
        <div
          className="w-full max-w-2xl rounded-xl p-4 md:p-6"
          style={{
            background: "linear-gradient(135deg, rgba(0,0,0,0.8) 0%, rgba(10,0,5,0.85) 50%, rgba(0,0,0,0.8) 100%)",
            backdropFilter: "blur(20px)",
            border: "1px solid rgba(255, 10, 60, 0.2)",
            boxShadow: "0 0 60px rgba(255, 10, 60, 0.15), inset 0 0 80px rgba(0,0,0,0.5)",
          }}
        >
          <div className="flex flex-col gap-3">
            <AnimatePresence mode="popLayout">
              {players.map((player, index) => (
                <PlayerCard key={player.id} player={player} rank={index + 1} />
              ))}
            </AnimatePresence>
          </div>
        </div>

        {/* Live indicator */}
        <div className="mt-6 flex items-center gap-2">
          <div className="w-2 h-2 rounded-full bg-red-500 animate-pulse" />
          <span className="text-xs font-mono text-red-400/60 tracking-widest">LIVE</span>
        </div>
      </div>

      {/* CSS for sweep animation */}
      <style jsx>{`
        @keyframes sweep {
          0% { background-position: -200% 0; }
          100% { background-position: 200% 0; }
        }
      `}</style>
    </div>
  )
}
