CREATE TABLE messages
(
    id              BIGINT NOT NULL primary key,
    content         TEXT   NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id         BIGINT NOT NULL references users (id) on delete cascade,
    conversation_id BIGINT NOT NULL references conversations (id) on delete cascade
);