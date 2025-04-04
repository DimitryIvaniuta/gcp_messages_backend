package com.messages.engine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a conversation (chat) that can be private or group.
 */
@Entity
@Table(name = "conversations")
public class Conversation {

    /**
     * Unique identifier for the conversation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GP_UNIQUE_ID")
    @SequenceGenerator(name = "GP_UNIQUE_ID", sequenceName = "GP_UNIQUE_ID", allocationSize = 1)
    private Long id;

    /**
     * Name of the conversation. For private chats, this can be null.
     */
    @Column
    private String name;

    /**
     * Participants in the conversation.
     */
    @ManyToMany
    @JoinTable(
            name = "conversation_participants",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    /**
     * Default constructor.
     */
    public Conversation() {
    }

    /**
     * Constructor with conversation name.
     *
     * @param name the name of the conversation.
     */
    public Conversation(String name) {
        this.name = name;
    }

    /**
     * Gets the unique identifier.
     *
     * @return the conversation id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier.
     *
     * @param id the conversation id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the conversation name.
     *
     * @return the name of the conversation.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the conversation name.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the participants in the conversation.
     *
     * @return the set of participants.
     */
    public Set<User> getParticipants() {
        return participants;
    }

    /**
     * Sets the participants in the conversation.
     *
     * @param participants the set of participants to set.
     */
    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }
}
