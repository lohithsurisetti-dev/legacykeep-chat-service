# LegacyKeep Chat Service - API Documentation

## 📋 Overview

This document provides comprehensive API documentation for the LegacyKeep Chat Service. All endpoints return responses wrapped in the `ApiResponse<T>` format and support pagination where applicable.

**Base URL**: `http://localhost:8083/chat`  
**Content-Type**: `application/json`  
**Authentication**: JWT Bearer Token (where required)

## 🔐 Authentication

All API endpoints (except health checks and testing) require authentication via JWT Bearer token:

```bash
Authorization: Bearer <your-jwt-token>
```

## 📊 Response Format

### Success Response
```json
{
  "status": "success",
  "message": "Operation completed successfully",
  "data": { /* response data */ },
  "timestamp": "2024-01-01T00:00:00Z",
  "error": null,
  "statusCode": 200,
  "path": "/api/v1/endpoint"
}
```

### Error Response
```json
{
  "status": "error",
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-01T00:00:00Z",
  "error": {
    "code": "ERROR_CODE",
    "details": "Additional error details",
    "field": "fieldName"
  },
  "statusCode": 400,
  "path": "/api/v1/endpoint"
}
```

### Paginated Response
```json
{
  "status": "success",
  "message": "Data retrieved successfully",
  "data": {
    "content": [ /* array of items */ ],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 0,
    "size": 20,
    "first": true,
    "last": false
  },
  "timestamp": "2024-01-01T00:00:00Z",
  "error": null,
  "statusCode": 200,
  "path": "/api/v1/endpoint"
}
```

## 🏠 Health & Testing Endpoints

### Health Check
```http
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "mongo": { "status": "UP" },
    "redis": { "status": "UP" }
  }
}
```

### Database Test
```http
GET /api/v1/test/db
```

**Response:**
```json
{
  "status": "success",
  "message": "Database connection successful",
  "data": "Chat rooms count: 0",
  "timestamp": "2024-01-01T00:00:00Z",
  "error": null,
  "statusCode": 200,
  "path": "/api/v1/test/db"
}
```

## 💬 Chat Room Endpoints

### List Chat Rooms
```http
GET /api/v1/chat-rooms?page=0&size=20&sort=createdAt,desc
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort criteria (default: createdAt,desc)

**Response:**
```json
{
  "status": "success",
  "message": "Chat rooms retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "roomUuid": "550e8400-e29b-41d4-a716-446655440000",
        "roomName": "Family Chat",
        "roomDescription": "Main family group chat",
        "roomType": "FAMILY",
        "familyId": 123,
        "status": "ACTIVE",
        "isEncrypted": false,
        "isArchived": false,
        "isMuted": false,
        "messageCount": 150,
        "participantCount": 5,
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T00:00:00Z"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "size": 20,
    "first": true,
    "last": true
  }
}
```

### Create Chat Room
```http
POST /api/v1/chat-rooms
```

**Request Body:**
```json
{
  "createdByUserId": 123,
  "roomName": "Family Reunion Chat",
  "roomDescription": "Chat for the upcoming family reunion",
  "roomType": "FAMILY",
  "familyId": 456,
  "roomPhotoUrl": "https://example.com/photo.jpg",
  "isEncrypted": false,
  "roomSettings": {
    "allowMedia": true,
    "allowReactions": true,
    "maxParticipants": 50
  },
  "privacySettings": {
    "isPrivate": false,
    "allowInvites": true
  },
  "notificationSettings": {
    "muteNotifications": false,
    "soundEnabled": true
  },
  "metadata": {
    "createdFor": "family_reunion_2024"
  }
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Chat room created successfully",
  "data": {
    "id": 2,
    "roomUuid": "550e8400-e29b-41d4-a716-446655440001",
    "roomName": "Family Reunion Chat",
    "roomDescription": "Chat for the upcoming family reunion",
    "roomType": "FAMILY",
    "familyId": 456,
    "status": "ACTIVE",
    "isEncrypted": false,
    "isArchived": false,
    "isMuted": false,
    "messageCount": 0,
    "participantCount": 1,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### Get Chat Room by ID
```http
GET /api/v1/chat-rooms/{id}
```

**Path Parameters:**
- `id`: Chat room ID

**Response:**
```json
{
  "status": "success",
  "message": "Chat room retrieved successfully",
  "data": {
    "id": 1,
    "roomUuid": "550e8400-e29b-41d4-a716-446655440000",
    "roomName": "Family Chat",
    "roomDescription": "Main family group chat",
    "roomType": "FAMILY",
    "familyId": 123,
    "status": "ACTIVE",
    "isEncrypted": false,
    "isArchived": false,
    "isMuted": false,
    "messageCount": 150,
    "participantCount": 5,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### Get Chat Room by UUID
```http
GET /api/v1/chat-rooms/uuid/{uuid}
```

**Path Parameters:**
- `uuid`: Chat room UUID

### Update Chat Room
```http
PUT /api/v1/chat-rooms/{id}
```

**Request Body:**
```json
{
  "roomName": "Updated Family Chat",
  "roomDescription": "Updated description",
  "roomPhotoUrl": "https://example.com/new-photo.jpg",
  "roomSettings": {
    "allowMedia": true,
    "allowReactions": true,
    "maxParticipants": 100
  }
}
```

### Delete Chat Room
```http
DELETE /api/v1/chat-rooms/{id}
```

### Get Family Chat Rooms
```http
GET /api/v1/chat-rooms/family/{familyId}?page=0&size=20
```

### Get User Chat Rooms
```http
GET /api/v1/chat-rooms/user/{userId}?page=0&size=20
```

### Get Chat Room Statistics
```http
GET /api/v1/chat-rooms/stats
```

**Response:**
```json
{
  "status": "success",
  "message": "Chat room statistics retrieved successfully",
  "data": {
    "totalChatRooms": 25,
    "activeChatRooms": 20,
    "archivedChatRooms": 5,
    "totalMessages": 1500,
    "totalParticipants": 100,
    "averageMessagesPerRoom": 60,
    "mostActiveRoom": {
      "id": 1,
      "roomName": "Family Chat",
      "messageCount": 300
    }
  }
}
```

### Add Participant
```http
POST /api/v1/chat-rooms/{id}/participants
```

**Request Body:**
```json
{
  "userId": 789,
  "role": "MEMBER"
}
```

### Remove Participant
```http
DELETE /api/v1/chat-rooms/{id}/participants/{userId}
```

### Archive Chat Room
```http
POST /api/v1/chat-rooms/{id}/archive
```

**Request Body:**
```json
{
  "reason": "Chat room no longer needed"
}
```

### Mute Chat Room
```http
POST /api/v1/chat-rooms/{id}/mute
```

**Request Body:**
```json
{
  "muteUntil": "2024-01-02T00:00:00Z",
  "reason": "Focusing on work"
}
```

### Check Individual Chat
```http
GET /api/v1/chat-rooms/individual/{userId1}/{userId2}
```

**Response:**
```json
{
  "status": "success",
  "message": "Individual chat status retrieved",
  "data": {
    "exists": true,
    "chatRoomId": 5,
    "chatRoomUuid": "550e8400-e29b-41d4-a716-446655440005"
  }
}
```

## 📨 Message Endpoints

### Get Room Messages
```http
GET /api/v1/messages/room/{roomId}?page=0&size=50&sort=createdAt,desc
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 50)
- `sort` (optional): Sort criteria (default: createdAt,desc)

**Response:**
```json
{
  "status": "success",
  "message": "Messages retrieved successfully",
  "data": {
    "content": [
      {
        "messageId": "msg_123",
        "chatRoomId": 1,
        "senderId": 456,
        "messageType": "TEXT",
        "content": "Hello everyone!",
        "mediaUrl": null,
        "mediaMetadata": null,
        "replyToMessageId": null,
        "reactions": [
          {
            "userId": 789,
            "emoji": "👍",
            "timestamp": "2024-01-01T00:01:00Z"
          }
        ],
        "isEdited": false,
        "editedAt": null,
        "isDeleted": false,
        "deletedAt": null,
        "createdAt": "2024-01-01T00:00:00Z",
        "updatedAt": "2024-01-01T00:00:00Z"
      }
    ],
    "totalElements": 150,
    "totalPages": 3,
    "currentPage": 0,
    "size": 50,
    "first": true,
    "last": false
  }
}
```

### Send Message
```http
POST /api/v1/messages
```

**Request Body:**
```json
{
  "chatRoomId": 1,
  "senderId": 456,
  "messageType": "TEXT",
  "content": "Hello everyone!",
  "replyToMessageId": null,
  "forwardedFrom": null,
  "metadata": {
    "clientTimestamp": "2024-01-01T00:00:00Z"
  }
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Message sent successfully",
  "data": {
    "messageId": "msg_124",
    "chatRoomId": 1,
    "senderId": 456,
    "messageType": "TEXT",
    "content": "Hello everyone!",
    "mediaUrl": null,
    "mediaMetadata": null,
    "replyToMessageId": null,
    "reactions": [],
    "isEdited": false,
    "editedAt": null,
    "isDeleted": false,
    "deletedAt": null,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
}
```

### Edit Message
```http
PUT /api/v1/messages/{messageId}
```

**Request Body:**
```json
{
  "content": "Hello everyone! (edited)",
  "metadata": {
    "editReason": "Typo correction"
  }
}
```

### Delete Message
```http
DELETE /api/v1/messages/{messageId}
```

**Request Body:**
```json
{
  "reason": "Message no longer relevant"
}
```

### Add Reaction
```http
POST /api/v1/messages/{messageId}/reactions
```

**Request Body:**
```json
{
  "userId": 789,
  "emoji": "❤️"
}
```

### Remove Reaction
```http
DELETE /api/v1/messages/{messageId}/reactions/{userId}
```

### Forward Message
```http
POST /api/v1/messages/forward
```

**Request Body:**
```json
{
  "messageId": "msg_123",
  "targetChatRoomId": 2,
  "forwardedBy": 456,
  "comment": "Check this out!"
}
```

### Search Messages
```http
GET /api/v1/messages/search?query=hello&chatRoomId=1&page=0&size=20
```

**Query Parameters:**
- `query`: Search term
- `chatRoomId` (optional): Limit search to specific room
- `page` (optional): Page number
- `size` (optional): Page size

### Get User Message Statistics
```http
GET /api/v1/messages/stats/user/{userId}
```

**Response:**
```json
{
  "status": "success",
  "message": "User message statistics retrieved successfully",
  "data": {
    "totalMessages": 500,
    "messagesByType": {
      "TEXT": 400,
      "IMAGE": 50,
      "VIDEO": 30,
      "AUDIO": 20
    },
    "averageMessagesPerDay": 25,
    "mostActiveChatRoom": {
      "id": 1,
      "roomName": "Family Chat",
      "messageCount": 200
    }
  }
}
```

### Get Room Message Statistics
```http
GET /api/v1/messages/stats/room/{roomId}
```

## 🔌 WebSocket Endpoints

### Get WebSocket Statistics
```http
GET /api/v1/websocket/stats
```

**Response:**
```json
{
  "status": "success",
  "message": "WebSocket statistics retrieved successfully",
  "data": {
    "totalConnections": 150,
    "activeConnections": 75,
    "familySubscriptions": 25,
    "storySubscriptions": 10,
    "eventSubscriptions": 5,
    "chatRoomSubscriptions": 100,
    "messagesSent": 1000,
    "notificationsSent": 200,
    "errorsOccurred": 5
  }
}
```

### Subscribe to Topics
```http
POST /api/v1/websocket/subscribe
```

**Request Body:**
```json
{
  "userId": 456,
  "topics": [
    "/topic/chat.1",
    "/topic/family.123",
    "/queue/notifications.456"
  ]
}
```

### Unsubscribe from Topics
```http
POST /api/v1/websocket/unsubscribe
```

**Request Body:**
```json
{
  "userId": 456,
  "topics": [
    "/topic/chat.1",
    "/topic/family.123"
  ]
}
```

### Get Active Connections
```http
GET /api/v1/websocket/connections?userId=456
```

## 📊 Analytics Endpoints

### Get Family Analytics
```http
GET /api/v1/analytics/family/{familyId}
```

**Response:**
```json
{
  "status": "success",
  "message": "Family analytics retrieved successfully",
  "data": {
    "familyId": 123,
    "totalMembers": 10,
    "activeMembers": 8,
    "totalChatRooms": 5,
    "totalMessages": 2000,
    "messagesThisWeek": 150,
    "mostActiveMember": {
      "userId": 456,
      "messageCount": 300
    },
    "communicationTrends": {
      "dailyAverage": 25,
      "weeklyGrowth": 15.5,
      "peakHours": ["19:00", "20:00", "21:00"]
    }
  }
}
```

### Get User Analytics
```http
GET /api/v1/analytics/user/{userId}
```

### Get Chat Room Analytics
```http
GET /api/v1/analytics/chat-room/{chatRoomId}
```

### Get Communication Trends
```http
GET /api/v1/analytics/trends?period=week&familyId=123
```

**Query Parameters:**
- `period`: day, week, month, year
- `familyId` (optional): Filter by family

### Get AI Features Usage
```http
GET /api/v1/analytics/ai-features
```

### Get Security Metrics
```http
GET /api/v1/analytics/security
```

### Get Media Usage Statistics
```http
GET /api/v1/analytics/media
```

### Get Real-time Metrics
```http
GET /api/v1/analytics/real-time
```

### Get Family Engagement
```http
GET /api/v1/analytics/engagement/family/{familyId}
```

### Get Analytics Health
```http
GET /api/v1/analytics/health
```

## 🚨 Error Codes

| Code | Description | HTTP Status |
|------|-------------|-------------|
| `CHAT_ROOM_NOT_FOUND` | Chat room does not exist | 404 |
| `MESSAGE_NOT_FOUND` | Message does not exist | 404 |
| `UNAUTHORIZED_ACCESS` | Insufficient permissions | 403 |
| `INVALID_MESSAGE_TYPE` | Invalid message type | 400 |
| `ROOM_ARCHIVED` | Chat room is archived | 409 |
| `USER_NOT_PARTICIPANT` | User not in chat room | 403 |
| `INVALID_REQUEST` | Request validation failed | 400 |
| `DATABASE_ERROR` | Database operation failed | 500 |
| `WEBSOCKET_ERROR` | WebSocket operation failed | 500 |

## 📝 Request/Response Examples

### Complete Chat Room Creation Flow

1. **Create Chat Room**
```bash
curl -X POST http://localhost:8083/chat/api/v1/chat-rooms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "createdByUserId": 123,
    "roomName": "Family Reunion",
    "roomType": "FAMILY",
    "familyId": 456
  }'
```

2. **Add Participants**
```bash
curl -X POST http://localhost:8083/chat/api/v1/chat-rooms/1/participants \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "userId": 789,
    "role": "MEMBER"
  }'
```

3. **Send First Message**
```bash
curl -X POST http://localhost:8083/chat/api/v1/messages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "chatRoomId": 1,
    "senderId": 123,
    "messageType": "TEXT",
    "content": "Welcome to our family reunion chat!"
  }'
```

### WebSocket Connection Example

```javascript
// Connect to WebSocket
const socket = new SockJS('http://localhost:8083/chat/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to chat room messages
    stompClient.subscribe('/topic/chat.1', function(message) {
        const chatMessage = JSON.parse(message.body);
        console.log('Received message:', chatMessage);
    });
    
    // Send a message
    stompClient.send('/app/chat.send', {}, JSON.stringify({
        chatRoomId: 1,
        senderId: 123,
        messageType: 'TEXT',
        content: 'Hello from WebSocket!'
    }));
});
```

---

This API documentation provides comprehensive information about all available endpoints in the LegacyKeep Chat Service. For additional examples or questions, please refer to the source code or contact the development team.
