CREATE TABLE conversation_participants
(
    conversation_id BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    PRIMARY KEY (conversation_id, user_id),
    CONSTRAINT fk_conversation FOREIGN KEY (conversation_id) REFERENCES conversations (id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id)
);