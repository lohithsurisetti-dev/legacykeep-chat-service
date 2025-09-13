-- Chat Service Database Schema
-- PostgreSQL tables for chat room metadata and user management
-- Version: 1.0.0

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create chat_rooms table
CREATE TABLE IF NOT EXISTS chat_rooms (
    id BIGSERIAL PRIMARY KEY,
    room_uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
    room_name VARCHAR(255),
    room_description TEXT,
    room_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by_user_id BIGINT NOT NULL,
    family_id BIGINT,
    story_id BIGINT,
    event_id BIGINT,
    room_photo_url VARCHAR(500),
    is_encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    is_muted BOOLEAN NOT NULL DEFAULT FALSE,
    last_message_id BIGINT,
    last_message_at TIMESTAMP,
    last_message_by_user_id BIGINT,
    message_count BIGINT NOT NULL DEFAULT 0,
    participant_count INTEGER NOT NULL DEFAULT 0,
    room_settings JSONB,
    privacy_settings JSONB,
    notification_settings JSONB,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_room_type CHECK (room_type IN ('INDIVIDUAL', 'GROUP', 'FAMILY_GROUP', 'STORY_CHAT', 'EVENT_CHAT')),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'DELETED')),
    CONSTRAINT chk_participant_count CHECK (participant_count >= 0),
    CONSTRAINT chk_message_count CHECK (message_count >= 0)
);

-- Create chat_participants table
CREATE TABLE IF NOT EXISTS chat_participants (
    id BIGSERIAL PRIMARY KEY,
    chat_room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP,
    last_read_at TIMESTAMP,
    last_read_message_id BIGINT,
    is_muted BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    notification_settings JSONB,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_participant_role CHECK (role IN ('ADMIN', 'MODERATOR', 'MEMBER', 'VIEWER')),
    CONSTRAINT chk_participant_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LEFT', 'REMOVED', 'BANNED')),
    CONSTRAINT fk_chat_participants_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(id) ON DELETE CASCADE,
    CONSTRAINT uk_chat_participants_room_user UNIQUE (chat_room_id, user_id)
);

-- Create chat_audits table
CREATE TABLE IF NOT EXISTS chat_audits (
    id BIGSERIAL PRIMARY KEY,
    audit_uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    performed_by_user_id BIGINT NOT NULL,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_audit_entity_type CHECK (entity_type IN ('CHAT_ROOM', 'MESSAGE', 'PARTICIPANT', 'REACTION', 'FAMILY_PASSWORD')),
    CONSTRAINT chk_audit_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'JOIN', 'LEAVE', 'MUTE', 'UNMUTE', 'ARCHIVE', 'UNARCHIVE', 'REACT', 'UNREACT'))
);

-- Create family_passwords table
CREATE TABLE IF NOT EXISTS family_passwords (
    id BIGSERIAL PRIMARY KEY,
    password_uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
    family_id BIGINT NOT NULL,
    password_type VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_by_user_id BIGINT NOT NULL,
    expires_at TIMESTAMP,
    last_used_at TIMESTAMP,
    usage_count INTEGER NOT NULL DEFAULT 0,
    max_usage_count INTEGER,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_password_type CHECK (password_type IN ('FAMILY_SHARED', 'INDIVIDUAL', 'MESSAGE_PROTECTION', 'ROOM_ACCESS')),
    CONSTRAINT chk_password_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'EXPIRED', 'REVOKED')),
    CONSTRAINT chk_usage_count CHECK (usage_count >= 0),
    CONSTRAINT chk_max_usage_count CHECK (max_usage_count IS NULL OR max_usage_count > 0)
);

-- Create message_reactions table
CREATE TABLE IF NOT EXISTS message_reactions (
    id BIGSERIAL PRIMARY KEY,
    reaction_uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
    message_id VARCHAR(255) NOT NULL, -- MongoDB ObjectId as string
    user_id BIGINT NOT NULL,
    reaction_type VARCHAR(20) NOT NULL,
    emoji VARCHAR(10),
    reaction_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_reaction_type CHECK (reaction_type IN ('EMOJI', 'CUSTOM', 'STICKER', 'GIF', 'ANIMATION')),
    CONSTRAINT uk_message_reactions_user UNIQUE (message_id, user_id, reaction_type)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_chat_rooms_room_uuid ON chat_rooms(room_uuid);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_room_type ON chat_rooms(room_type);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_status ON chat_rooms(status);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_created_by ON chat_rooms(created_by_user_id);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_family_id ON chat_rooms(family_id);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_story_id ON chat_rooms(story_id);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_event_id ON chat_rooms(event_id);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_last_message_at ON chat_rooms(last_message_at);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_created_at ON chat_rooms(created_at);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_updated_at ON chat_rooms(updated_at);

CREATE INDEX IF NOT EXISTS idx_chat_participants_room_id ON chat_participants(chat_room_id);
CREATE INDEX IF NOT EXISTS idx_chat_participants_user_id ON chat_participants(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_participants_role ON chat_participants(role);
CREATE INDEX IF NOT EXISTS idx_chat_participants_status ON chat_participants(status);
CREATE INDEX IF NOT EXISTS idx_chat_participants_joined_at ON chat_participants(joined_at);
CREATE INDEX IF NOT EXISTS idx_chat_participants_last_read_at ON chat_participants(last_read_at);

CREATE INDEX IF NOT EXISTS idx_chat_audits_entity_type_id ON chat_audits(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_chat_audits_action ON chat_audits(action);
CREATE INDEX IF NOT EXISTS idx_chat_audits_performed_by ON chat_audits(performed_by_user_id);
CREATE INDEX IF NOT EXISTS idx_chat_audits_created_at ON chat_audits(created_at);

CREATE INDEX IF NOT EXISTS idx_family_passwords_family_id ON family_passwords(family_id);
CREATE INDEX IF NOT EXISTS idx_family_passwords_type ON family_passwords(password_type);
CREATE INDEX IF NOT EXISTS idx_family_passwords_status ON family_passwords(status);
CREATE INDEX IF NOT EXISTS idx_family_passwords_created_by ON family_passwords(created_by_user_id);
CREATE INDEX IF NOT EXISTS idx_family_passwords_expires_at ON family_passwords(expires_at);

CREATE INDEX IF NOT EXISTS idx_message_reactions_message_id ON message_reactions(message_id);
CREATE INDEX IF NOT EXISTS idx_message_reactions_user_id ON message_reactions(user_id);
CREATE INDEX IF NOT EXISTS idx_message_reactions_type ON message_reactions(reaction_type);
CREATE INDEX IF NOT EXISTS idx_message_reactions_created_at ON message_reactions(created_at);

-- Create composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_chat_rooms_type_status ON chat_rooms(room_type, status);
CREATE INDEX IF NOT EXISTS idx_chat_rooms_family_type ON chat_rooms(family_id, room_type);
CREATE INDEX IF NOT EXISTS idx_chat_participants_room_status ON chat_participants(chat_room_id, status);
CREATE INDEX IF NOT EXISTS idx_chat_participants_user_status ON chat_participants(user_id, status);

-- Create triggers for updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_chat_rooms_updated_at BEFORE UPDATE ON chat_rooms
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_chat_participants_updated_at BEFORE UPDATE ON chat_participants
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_family_passwords_updated_at BEFORE UPDATE ON family_passwords
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_message_reactions_updated_at BEFORE UPDATE ON message_reactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert initial data (if needed)
-- This can be used for default chat rooms or system configurations

COMMENT ON TABLE chat_rooms IS 'Stores chat room metadata and configuration';
COMMENT ON TABLE chat_participants IS 'Stores user participation in chat rooms';
COMMENT ON TABLE chat_audits IS 'Stores audit trail for all chat-related actions';
COMMENT ON TABLE family_passwords IS 'Stores family-specific password configurations';
COMMENT ON TABLE message_reactions IS 'Stores message reactions and interactions';

COMMENT ON COLUMN chat_rooms.room_uuid IS 'Unique identifier for the chat room';
COMMENT ON COLUMN chat_rooms.room_type IS 'Type of chat room: INDIVIDUAL, GROUP, FAMILY_GROUP, STORY_CHAT, EVENT_CHAT';
COMMENT ON COLUMN chat_rooms.status IS 'Current status of the chat room';
COMMENT ON COLUMN chat_rooms.room_settings IS 'JSON object with room-specific settings';
COMMENT ON COLUMN chat_rooms.privacy_settings IS 'JSON object with privacy settings';
COMMENT ON COLUMN chat_rooms.notification_settings IS 'JSON object with notification preferences';

COMMENT ON COLUMN chat_participants.role IS 'User role in the chat room: ADMIN, MODERATOR, MEMBER, VIEWER';
COMMENT ON COLUMN chat_participants.status IS 'Current participation status';
COMMENT ON COLUMN chat_participants.last_read_at IS 'Timestamp when user last read messages';
COMMENT ON COLUMN chat_participants.last_read_message_id IS 'ID of the last message read by user';

COMMENT ON COLUMN chat_audits.entity_type IS 'Type of entity being audited';
COMMENT ON COLUMN chat_audits.entity_id IS 'ID of the entity being audited';
COMMENT ON COLUMN chat_audits.action IS 'Action performed on the entity';
COMMENT ON COLUMN chat_audits.old_values IS 'Previous values before the action';
COMMENT ON COLUMN chat_audits.new_values IS 'New values after the action';

COMMENT ON COLUMN family_passwords.password_type IS 'Type of password: FAMILY_SHARED, INDIVIDUAL, MESSAGE_PROTECTION, ROOM_ACCESS';
COMMENT ON COLUMN family_passwords.password_hash IS 'Hashed password value';
COMMENT ON COLUMN family_passwords.salt IS 'Salt used for password hashing';
COMMENT ON COLUMN family_passwords.usage_count IS 'Number of times password has been used';
COMMENT ON COLUMN family_passwords.max_usage_count IS 'Maximum allowed usage count (NULL for unlimited)';

COMMENT ON COLUMN message_reactions.message_id IS 'MongoDB ObjectId of the message as string';
COMMENT ON COLUMN message_reactions.reaction_type IS 'Type of reaction: EMOJI, CUSTOM, STICKER, GIF, ANIMATION';
COMMENT ON COLUMN message_reactions.emoji IS 'Emoji character for emoji reactions';
COMMENT ON COLUMN message_reactions.reaction_data IS 'Additional reaction data in JSON format';
