package com.messages.engine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "messages")
@NoArgsConstructor
public class Message {

    /** Unique identifier for the message. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCP_UNIQUE_ID")
    @SequenceGenerator(name = "GCP_UNIQUE_ID", sequenceName = "GCP_UNIQUE_ID", allocationSize = 1)
    private Long id;

    /** Content of the message. */
    @Column(nullable = false)
    private String content;

    /**
     * The conversation this message belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /**
     * Many-to-one relation to the User entity.
     * Each message is associated with one user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
