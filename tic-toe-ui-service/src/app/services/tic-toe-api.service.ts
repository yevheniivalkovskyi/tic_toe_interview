import { SessionResponse } from "../models/session-response.model";

export class TicToeApiService {
  private readonly sessionBaseUrl = "http://localhost:8081";

  async createSession(): Promise<SessionResponse> {
    const response = await fetch(`${this.sessionBaseUrl}/sessions`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({}),
    });
    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: "Failed to create session" }));
      throw error;
    }
    return response.json();
  }

  async simulateSession(sessionId: string): Promise<SessionResponse> {
    const response = await fetch(`${this.sessionBaseUrl}/sessions/${sessionId}/simulate`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({}),
    });
    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: "Failed to simulate session" }));
      throw error;
    }
    return response.json();
  }
}
