import { useState, useEffect, useRef } from "react";
import { SessionResponse } from "./models/session-response.model";
import { MoveRecord } from "./models/move-record.model";
import { TicToeApiService } from "./services/tic-toe-api.service";
import "./App.css";

const createEmptyBoard = (): string[][] => {
  return Array.from({ length: 3 }, () => Array.from({ length: 3 }, () => " "));
};

function App() {
  const [sessionId, setSessionId] = useState("");
  const [gameId, setGameId] = useState("");
  const [gameStatus, setGameStatus] = useState("");
  const [sessionStatus, setSessionStatus] = useState("");
  const [board, setBoard] = useState<string[][]>(createEmptyBoard());
  const [moves, setMoves] = useState<MoveRecord[]>([]);
  const [displayedMoveCount, setDisplayedMoveCount] = useState(0);
  const [lastMessage, setLastMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isSimulating, setIsSimulating] = useState(false);

  const socketRef = useRef<WebSocket | null>(null);
  const playbackTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const displayedMoveCountRef = useRef(0);
  const apiService = useRef(new TicToeApiService());

  const closeSocket = () => {
    if (socketRef.current) {
      socketRef.current.close();
      socketRef.current = null;
    }
  };

  const stopPlayback = () => {
    if (playbackTimeoutRef.current) {
      clearTimeout(playbackTimeoutRef.current);
      playbackTimeoutRef.current = null;
    }
  };

  useEffect(() => {
    return () => {
      stopPlayback();
      closeSocket();
    };
  }, []);

  useEffect(() => {
    displayedMoveCountRef.current = displayedMoveCount;
  }, [displayedMoveCount]);

  const openSocket = (sessionIdToUse: string) => {
    if (!sessionIdToUse) {
      return;
    }
    closeSocket();
    const wsUrl = `ws://localhost:8081/ws/session?sessionId=${sessionIdToUse}`;
    const socket = new WebSocket(wsUrl);
    socketRef.current = socket;

    socket.onopen = () => {
      setLastMessage("Connected to live updates");
      setErrorMessage("");
    };

    socket.onmessage = (event) => {
      try {
        const session = JSON.parse(event.data) as SessionResponse;
        applySession(session);
        setLastMessage("Update received");
      } catch (error) {
        console.error("Error parsing WebSocket message:", error);
      }
    };

    socket.onerror = () => {
      setErrorMessage("Live updates connection error");
    };

    socket.onclose = () => {
      setLastMessage("Live updates disconnected");
    };
  };

  const reconstructBoardFromMoves = (moves: MoveRecord[], upTo?: number): string[][] => {
    const board = createEmptyBoard();
    const limit = typeof upTo === "number" ? Math.min(upTo, moves.length) : moves.length;
    for (let i = 0; i < limit; i += 1) {
      const move = moves[i];
      const playerSymbol = move.player === 'X' || move.player === 'O' ? move.player : move.player.toUpperCase();
      if (move.row >= 0 && move.row < 3 && move.column >= 0 && move.column < 3) {
        board[move.row][move.column] = playerSymbol;
      }
    }
    return board;
  };

  const startPlayback = (moves: MoveRecord[], startIndex: number) => {
    stopPlayback();
    const initialBoard = reconstructBoardFromMoves(moves, startIndex);
    setBoard(initialBoard);

    if (startIndex >= moves.length) {
      setDisplayedMoveCount(moves.length);
      return;
    }

    const playStep = (index: number, currentBoard: string[][]) => {
      if (index >= moves.length) {
        setDisplayedMoveCount(moves.length);
        return;
      }

      const nextBoard = currentBoard.map((row) => [...row]);
      const move = moves[index];
      const symbol = move.player === 'X' || move.player === 'O' ? move.player : move.player.toUpperCase();
      if (move.row >= 0 && move.row < 3 && move.column >= 0 && move.column < 3) {
        nextBoard[move.row][move.column] = symbol;
      }
      setBoard(nextBoard);
      setDisplayedMoveCount(index + 1);
      setLastMessage(`Move #${index + 1} applied`);

      playbackTimeoutRef.current = setTimeout(() => {
        playStep(index + 1, nextBoard);
      }, 500);
    };

    playStep(startIndex, initialBoard);
  };

  const applySession = (session: SessionResponse) => {
    try {
      // Handle status - it might be an object or string
      const statusValue = typeof session.status === 'string' 
        ? session.status 
        : (session.status as any)?.toString() || String(session.status) || '';
      setSessionStatus(statusValue);
      
      if (session.gameStatus) {
        setGameStatus(session.gameStatus);
      }
      
      const incomingMoves = session.moves ?? [];
      setMoves(incomingMoves);

      if (incomingMoves.length === 0) {
        setBoard(createEmptyBoard());
        setDisplayedMoveCount(0);
      } else {
        const currentCount = displayedMoveCountRef.current;
        if (incomingMoves.length > currentCount) {
          startPlayback(incomingMoves, currentCount);
        }
      }
      
      if (session.error) {
        setErrorMessage(session.error.message);
      } else {
        // Clear error if no error in response
        setErrorMessage("");
      }
    } catch (error) {
      console.error("Error applying session:", error, session);
      setErrorMessage("Failed to process session data");
    }
  };

  const resetState = () => {
    setSessionId("");
    setGameId("");
    setGameStatus("");
    setSessionStatus("");
    setBoard(createEmptyBoard());
    setMoves([]);
    setLastMessage("");
    setErrorMessage("");
    setIsSimulating(false);
    setDisplayedMoveCount(0);
    stopPlayback();
    closeSocket();
  };

  const extractError = (err: any): string => {
    return err?.error?.message || err?.message || "Unexpected error occurred";
  };

  const triggerSimulation = async (sessionIdToUse: string) => {
    if (!sessionIdToUse) {
      return;
    }
    setIsSimulating(true);
    try {
      const session = await apiService.current.simulateSession(sessionIdToUse);
      applySession(session);
      setIsSimulating(false);
    } catch (err: unknown) {
      setErrorMessage(extractError(err));
      setIsSimulating(false);
    }
  };

  const startSimulation = async () => {
    resetState();
    try {
      const session = await apiService.current.createSession();
      if (!session || !session.sessionId) {
        throw new Error("Invalid session response");
      }
      setSessionId(session.sessionId);
      setGameId(session.gameId || "");
      applySession(session);
      openSocket(session.sessionId);
      // Pass sessionId directly instead of relying on state
      await triggerSimulation(session.sessionId);
    } catch (err: unknown) {
      console.error("Error starting simulation:", err);
      setErrorMessage(extractError(err));
      setIsSimulating(false);
    }
  };

  return (
    <main className="page">
      <header className="header">
        <div>
          <h1>Tic Tac Toe</h1>
          <p>Automated simulation via microservices</p>
        </div>
        <button
          className="primary"
          onClick={startSimulation}
          disabled={isSimulating}
        >
          {isSimulating ? "Simulating..." : "Start Simulation"}
        </button>
      </header>

      <section className="status">
        <div className="status-item">
          <span className="label">Session</span>
          <span className="value">{sessionStatus || "-"}</span>
        </div>
        <div className="status-item">
          <span className="label">Game</span>
          <span className="value">{gameStatus || "-"}</span>
        </div>
        <div className="status-item">
          <span className="label">Session ID</span>
          <span className="value mono">{sessionId || "-"}</span>
        </div>
        <div className="status-item">
          <span className="label">Game ID</span>
          <span className="value mono">{gameId || "-"}</span>
        </div>
      </section>

      <section className="board">
        <div className="grid">
          {board.map((row, rowIndex) => (
            <div key={rowIndex} className="row">
              {row.map((cell, colIndex) => (
                <div key={colIndex} className="cell-item">
                  {cell}
                </div>
              ))}
            </div>
          ))}
        </div>
      </section>

      <section className="info">
        <div className="panel">
          <h2>Move History</h2>
          <ul>
            {moves.length === 0 ? (
              <li>No moves yet.</li>
            ) : (
              moves.map((move, i) => (
                <li key={i}>
                  #{i + 1} - Player {move.player} → ({move.row}, {move.column})
                </li>
              ))
            )}
          </ul>
        </div>

        <div className="panel">
          <h2>Status</h2>
          <p>{lastMessage || "Waiting for live updates..."}</p>

          {errorMessage && <div className="error">{errorMessage}</div>}
        </div>
      </section>
    </main>
  );
}

export default App;
