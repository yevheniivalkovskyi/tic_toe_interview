import { ErrorResponse } from "./error-response.model";
import { MoveRecord } from "./move-record.model";

export interface SessionResponse {
  sessionId: string;
  gameId: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  board: string[][];
  gameStatus: string;
  error?: ErrorResponse;
  moves: MoveRecord[];
}
