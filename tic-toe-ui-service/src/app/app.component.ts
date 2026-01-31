import { Component, OnDestroy } from "@angular/core";
import { CommonModule } from "@angular/common";
import { SessionResponse } from "./models/session-response.model";
import { MoveRecord } from "./models/move-record.model";
import { GameResponse } from "./models/game-response.model";
import { TicToeApiService } from "./services/tic-toe-api.service";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"]
})
export class AppComponent implements OnDestroy {
  sessionId = "";
  gameId = "";
  gameStatus = "";
  sessionStatus = "";
  board: string[][] = this.createEmptyBoard();
  moves: MoveRecord[] = [];
  lastMessage = "";
  errorMessage = "";
  isSimulating = false;

  private eventSource?: EventSource;

  constructor(private api: TicToeApiService) {}

  ngOnDestroy(): void {
    this.closeStream();
  }

  startSimulation(): void {
    this.resetState();
    this.api.createSession().subscribe({
      next: (session: SessionResponse) => {
        this.sessionId = session.sessionId;
        this.gameId = session.gameId;
        this.applySession(session);
        this.openStream();
        this.triggerSimulation();
      },
      error: (err: unknown) => {
        this.errorMessage = this.extractError(err);
      }
    });
  }

  private triggerSimulation(): void {
    if (!this.sessionId) {
      return;
    }
    this.isSimulating = true;
    this.api.simulateSession(this.sessionId).subscribe({
      next: (session: SessionResponse) => {
        this.applySession(session);
        this.isSimulating = false;
      },
      error: (err: unknown) => {
        this.errorMessage = this.extractError(err);
        this.isSimulating = false;
      }
    });
  }

  private openStream(): void {
    if (!this.gameId) {
      return;
    }
    this.closeStream();
    this.eventSource = new EventSource(this.api.getSseUrl(this.gameId));
    this.eventSource.onmessage = (event) => {
      const data = JSON.parse(event.data) as GameResponse;
      this.gameStatus = data.status;
      this.board = data.board ?? this.board;
      this.lastMessage = data.message ?? "";
      this.refreshSession();
    };
    this.eventSource.onerror = () => {
      this.errorMessage = "Live updates disconnected. Please refresh or retry.";
      this.closeStream();
    };
  }

  private refreshSession(): void {
    if (!this.sessionId) {
      return;
    }
    this.api.getSession(this.sessionId).subscribe({
      next: (session: SessionResponse) => this.applySession(session),
      error: () => {}
    });
  }

  private applySession(session: SessionResponse): void {
    this.sessionStatus = session.status;
    this.gameStatus = session.gameStatus || this.gameStatus;
    this.board = session.board ?? this.board;
    this.moves = session.moves ?? [];
    if (session.error) {
      this.errorMessage = session.error.message;
    }
  }

  private resetState(): void {
    this.sessionId = "";
    this.gameId = "";
    this.gameStatus = "";
    this.sessionStatus = "";
    this.board = this.createEmptyBoard();
    this.moves = [];
    this.lastMessage = "";
    this.errorMessage = "";
    this.isSimulating = false;
    this.closeStream();
  }

  private closeStream(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = undefined;
    }
  }

  private createEmptyBoard(): string[][] {
    return Array.from({ length: 3 }, () => Array.from({ length: 3 }, () => " "));
  }

  private extractError(err: any): string {
    return err?.error?.message || err?.message || "Unexpected error occurred";
  }
}
