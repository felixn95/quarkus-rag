package com.tech11.rag;

import com.tech11.rag.entity.ConversationSession;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ConversationSessionService {

	// In-memory storage for sessions. TODO: Persist conversations to db
	private final ConcurrentHashMap<String, ConversationSession> sessions = new ConcurrentHashMap<>();

	public ConversationSession getSession(String sessionId) {
		if (sessionId != null && sessions.containsKey(sessionId)) {
			return sessions.get(sessionId);
		} else {
			ConversationSession newSession = new ConversationSession();
			sessions.put(newSession.getSessionId(), newSession);
			return newSession;
		}
	}

	public void saveSession(ConversationSession session) {
		sessions.put(session.getSessionId(), session);
	}
}
