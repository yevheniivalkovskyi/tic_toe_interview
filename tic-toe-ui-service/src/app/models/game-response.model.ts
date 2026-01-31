export interface GameResponse {
  gameId: string;
  board: string[][];
  status: string;
  currentPlayer: string;
  message?: string;
}
