import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { SessionResponse } from "../models/session-response.model";

@Injectable({ providedIn: "root" })
export class TicToeApiService {
  private readonly gatewayBaseUrl = "http://localhost:8082";
  private readonly sessionBaseUrl = `${this.gatewayBaseUrl}/session`;
  private readonly engineBaseUrl = `${this.gatewayBaseUrl}/engine`;

  constructor(private http: HttpClient) {}

  createSession(): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${this.sessionBaseUrl}/sessions`, {});
  }

  simulateSession(sessionId: string): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${this.sessionBaseUrl}/sessions/${sessionId}/simulate`, {});
  }

  getSession(sessionId: string): Observable<SessionResponse> {
    return this.http.get<SessionResponse>(`${this.sessionBaseUrl}/sessions/${sessionId}`);
  }

  getSseUrl(gameId: string): string {
    return `${this.engineBaseUrl}/games/${gameId}/stream`;
  }
}
