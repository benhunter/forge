# Game State JSON Schema (DTOs)

This document describes the stable JSON schemas for the view-model DTOs exposed by Forge for external clients. These DTOs are immutable and versioned to make it safe to evolve the payloads over time.

## Versioning

All top-level payloads include a `schemaVersion` field. Clients **must** use this field for compatibility checks before consuming the payload. Current version: `1`.

## Game State Endpoint

**DTO:** `forge.game.dto.GameStateDto`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://forge.example/schema/game-state.json",
  "title": "GameState",
  "type": "object",
  "required": ["schemaVersion", "id", "title", "gameType", "isCommander", "turn", "phase", "stormCount", "isMulligan", "isGameOver", "isMatchOver", "winningTeam", "players", "stack"],
  "properties": {
    "schemaVersion": { "type": "string", "enum": ["1"] },
    "id": { "type": "integer" },
    "title": { "type": "string" },
    "gameType": { "type": ["string", "null"] },
    "isCommander": { "type": "boolean" },
    "turn": { "type": "integer" },
    "phase": { "type": ["string", "null"] },
    "playerTurnId": { "type": ["integer", "null"] },
    "planarPlayerId": { "type": ["integer", "null"] },
    "stormCount": { "type": "integer" },
    "isMulligan": { "type": "boolean" },
    "isGameOver": { "type": "boolean" },
    "isMatchOver": { "type": "boolean" },
    "winningPlayerName": { "type": ["string", "null"] },
    "winningTeam": { "type": "integer" },
    "players": { "type": "array", "items": { "$ref": "#/$defs/playerState" } },
    "stack": { "type": "array", "items": { "$ref": "#/$defs/stackItem" } }
  },
  "$defs": {
    "playerState": {
      "type": "object",
      "required": ["schemaVersion", "id", "name", "isAi", "life", "isExtraTurn", "hasDelirium", "manaPool", "counters", "opponentIds", "zones"],
      "properties": {
        "schemaVersion": { "type": "string", "enum": ["1"] },
        "id": { "type": "integer" },
        "name": { "type": "string" },
        "lobbyPlayerName": { "type": ["string", "null"] },
        "isAi": { "type": "boolean" },
        "life": { "type": "integer" },
        "isExtraTurn": { "type": "boolean" },
        "hasDelirium": { "type": "boolean" },
        "currentPlane": { "type": ["string", "null"] },
        "manaPool": { "type": "object", "additionalProperties": { "type": "integer" } },
        "counters": { "type": "object", "additionalProperties": { "type": "integer" } },
        "opponentIds": { "type": "array", "items": { "type": "integer" } },
        "zones": { "type": "array", "items": { "$ref": "#/$defs/zone" } }
      }
    },
    "zone": {
      "type": "object",
      "required": ["zoneType", "size", "cards"],
      "properties": {
        "zoneType": { "type": "string" },
        "size": { "type": "integer" },
        "cards": { "type": "array", "items": { "$ref": "#/$defs/card" } }
      }
    },
    "stackItem": {
      "type": "object",
      "required": ["id", "key", "text", "isAbility", "isOptionalTrigger", "targetCardIds", "targetPlayerIds"],
      "properties": {
        "id": { "type": "integer" },
        "key": { "type": ["string", "null"] },
        "text": { "type": ["string", "null"] },
        "sourceCard": { "$ref": "#/$defs/card" },
        "activatingPlayerId": { "type": ["integer", "null"] },
        "targetCardIds": { "type": "array", "items": { "type": "integer" } },
        "targetPlayerIds": { "type": "array", "items": { "type": "integer" } },
        "isAbility": { "type": "boolean" },
        "isOptionalTrigger": { "type": "boolean" },
        "optionalCosts": { "type": ["string", "null"] },
        "subInstance": { "$ref": "#/$defs/stackItem" }
      }
    },
    "card": {
      "type": "object",
      "required": ["id", "displayId", "name", "oracleName", "zone", "isFaceDown", "isTapped", "isToken", "isAttacking", "isBlocking", "currentState"],
      "properties": {
        "id": { "type": "integer" },
        "displayId": { "type": ["string", "null"] },
        "name": { "type": ["string", "null"] },
        "oracleName": { "type": ["string", "null"] },
        "ownerId": { "type": ["integer", "null"] },
        "controllerId": { "type": ["integer", "null"] },
        "zone": { "type": ["string", "null"] },
        "imageKey": { "type": ["string", "null"] },
        "isFaceDown": { "type": "boolean" },
        "isTapped": { "type": "boolean" },
        "isToken": { "type": "boolean" },
        "isAttacking": { "type": "boolean" },
        "isBlocking": { "type": "boolean" },
        "currentState": { "$ref": "#/$defs/cardState" }
      }
    },
    "cardState": {
      "type": "object",
      "required": ["state", "name", "oracleName", "types", "manaCost", "power", "toughness", "loyalty", "oracleText", "rulesText"],
      "properties": {
        "state": { "type": ["string", "null"] },
        "name": { "type": ["string", "null"] },
        "oracleName": { "type": ["string", "null"] },
        "types": { "type": ["string", "null"] },
        "manaCost": { "type": ["string", "null"] },
        "power": { "type": ["integer", "null"] },
        "toughness": { "type": ["integer", "null"] },
        "loyalty": { "type": ["string", "null"] },
        "oracleText": { "type": ["string", "null"] },
        "rulesText": { "type": ["string", "null"] }
      }
    }
  }
}
```

## Player State Endpoint

**DTO:** `forge.game.dto.PlayerStateDto`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://forge.example/schema/player-state.json",
  "title": "PlayerState",
  "type": "object",
  "required": ["schemaVersion", "id", "name", "isAi", "life", "isExtraTurn", "hasDelirium", "manaPool", "counters", "opponentIds", "zones"],
  "properties": {
    "schemaVersion": { "type": "string", "enum": ["1"] },
    "id": { "type": "integer" },
    "name": { "type": "string" },
    "lobbyPlayerName": { "type": ["string", "null"] },
    "isAi": { "type": "boolean" },
    "life": { "type": "integer" },
    "isExtraTurn": { "type": "boolean" },
    "hasDelirium": { "type": "boolean" },
    "currentPlane": { "type": ["string", "null"] },
    "manaPool": { "type": "object", "additionalProperties": { "type": "integer" } },
    "counters": { "type": "object", "additionalProperties": { "type": "integer" } },
    "opponentIds": { "type": "array", "items": { "type": "integer" } },
    "zones": { "type": "array", "items": { "$ref": "#/$defs/zone" } }
  },
  "$defs": {
    "zone": {
      "type": "object",
      "required": ["zoneType", "size", "cards"],
      "properties": {
        "zoneType": { "type": "string" },
        "size": { "type": "integer" },
        "cards": { "type": "array", "items": { "$ref": "#/$defs/card" } }
      }
    },
    "card": {
      "type": "object",
      "required": ["id", "displayId", "name", "oracleName", "zone", "isFaceDown", "isTapped", "isToken", "isAttacking", "isBlocking", "currentState"],
      "properties": {
        "id": { "type": "integer" },
        "displayId": { "type": ["string", "null"] },
        "name": { "type": ["string", "null"] },
        "oracleName": { "type": ["string", "null"] },
        "ownerId": { "type": ["integer", "null"] },
        "controllerId": { "type": ["integer", "null"] },
        "zone": { "type": ["string", "null"] },
        "imageKey": { "type": ["string", "null"] },
        "isFaceDown": { "type": "boolean" },
        "isTapped": { "type": "boolean" },
        "isToken": { "type": "boolean" },
        "isAttacking": { "type": "boolean" },
        "isBlocking": { "type": "boolean" },
        "currentState": { "$ref": "#/$defs/cardState" }
      }
    },
    "cardState": {
      "type": "object",
      "required": ["state", "name", "oracleName", "types", "manaCost", "power", "toughness", "loyalty", "oracleText", "rulesText"],
      "properties": {
        "state": { "type": ["string", "null"] },
        "name": { "type": ["string", "null"] },
        "oracleName": { "type": ["string", "null"] },
        "types": { "type": ["string", "null"] },
        "manaCost": { "type": ["string", "null"] },
        "power": { "type": ["integer", "null"] },
        "toughness": { "type": ["integer", "null"] },
        "loyalty": { "type": ["string", "null"] },
        "oracleText": { "type": ["string", "null"] },
        "rulesText": { "type": ["string", "null"] }
      }
    }
  }
}
```

## Zones Endpoint

**DTO:** `forge.game.dto.ZoneStateDto`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://forge.example/schema/zone-state.json",
  "title": "ZoneState",
  "type": "object",
  "required": ["schemaVersion", "playerId", "zones"],
  "properties": {
    "schemaVersion": { "type": "string", "enum": ["1"] },
    "playerId": { "type": "integer" },
    "zones": { "type": "array", "items": { "$ref": "#/$defs/zone" } }
  },
  "$defs": {
    "zone": {
      "type": "object",
      "required": ["zoneType", "size", "cards"],
      "properties": {
        "zoneType": { "type": "string" },
        "size": { "type": "integer" },
        "cards": { "type": "array", "items": { "$ref": "#/$defs/card" } }
      }
    },
    "card": {
      "type": "object",
      "required": ["id", "displayId", "name", "oracleName", "zone", "isFaceDown", "isTapped", "isToken", "isAttacking", "isBlocking", "currentState"],
      "properties": {
        "id": { "type": "integer" },
        "displayId": { "type": ["string", "null"] },
        "name": { "type": ["string", "null"] },
        "oracleName": { "type": ["string", "null"] },
        "ownerId": { "type": ["integer", "null"] },
        "controllerId": { "type": ["integer", "null"] },
        "zone": { "type": ["string", "null"] },
        "imageKey": { "type": ["string", "null"] },
        "isFaceDown": { "type": "boolean" },
        "isTapped": { "type": "boolean" },
        "isToken": { "type": "boolean" },
        "isAttacking": { "type": "boolean" },
        "isBlocking": { "type": "boolean" },
        "currentState": { "$ref": "#/$defs/cardState" }
      }
    },
    "cardState": {
      "type": "object",
      "required": ["state", "name", "oracleName", "types", "manaCost", "power", "toughness", "loyalty", "oracleText", "rulesText"],
      "properties": {
        "state": { "type": ["string", "null"] },
        "name": { "type": ["string", "null"] },
        "oracleName": { "type": ["string", "null"] },
        "types": { "type": ["string", "null"] },
        "manaCost": { "type": ["string", "null"] },
        "power": { "type": ["integer", "null"] },
        "toughness": { "type": ["integer", "null"] },
        "loyalty": { "type": ["string", "null"] },
        "oracleText": { "type": ["string", "null"] },
        "rulesText": { "type": ["string", "null"] }
      }
    }
  }
}
```

## Stack Items Endpoint

**DTO:** `forge.game.dto.StackStateDto`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://forge.example/schema/stack-state.json",
  "title": "StackState",
  "type": "object",
  "required": ["schemaVersion", "gameId", "items"],
  "properties": {
    "schemaVersion": { "type": "string", "enum": ["1"] },
    "gameId": { "type": "integer" },
    "items": { "type": "array", "items": { "$ref": "#/$defs/stackItem" } }
  },
  "$defs": {
    "stackItem": {
      "type": "object",
      "required": ["id", "key", "text", "isAbility", "isOptionalTrigger", "targetCardIds", "targetPlayerIds"],
      "properties": {
        "id": { "type": "integer" },
        "key": { "type": ["string", "null"] },
        "text": { "type": ["string", "null"] },
        "sourceCard": { "$ref": "#/$defs/card" },
        "activatingPlayerId": { "type": ["integer", "null"] },
        "targetCardIds": { "type": "array", "items": { "type": "integer" } },
        "targetPlayerIds": { "type": "array", "items": { "type": "integer" } },
        "isAbility": { "type": "boolean" },
        "isOptionalTrigger": { "type": "boolean" },
        "optionalCosts": { "type": ["string", "null"] },
        "subInstance": { "$ref": "#/$defs/stackItem" }
      }
    },
    "card": {
      "type": "object",
      "required": ["id", "displayId", "name", "oracleName", "zone", "isFaceDown", "isTapped", "isToken", "isAttacking", "isBlocking", "currentState"],
      "properties": {
        "id": { "type": "integer" },
        "displayId": { "type": ["string", "null"] },
        "name": { "type": ["string", "null"] },
        "oracleName": { "type": ["string", "null"] },
        "ownerId": { "type": ["integer", "null"] },
        "controllerId": { "type": ["integer", "null"] },
        "zone": { "type": ["string", "null"] },
        "imageKey": { "type": ["string", "null"] },
        "isFaceDown": { "type": "boolean" },
        "isTapped": { "type": "boolean" },
        "isToken": { "type": "boolean" },
        "isAttacking": { "type": "boolean" },
        "isBlocking": { "type": "boolean" },
        "currentState": { "$ref": "#/$defs/cardState" }
      }
    },
    "cardState": {
      "type": "object",
      "required": ["state", "name", "oracleName", "types", "manaCost", "power", "toughness", "loyalty", "oracleText", "rulesText"],
      "properties": {
        "state": { "type": ["string", "null"] },
        "name": { "type": ["string", "null"] },
        "oracleName": { "type": ["string", "null"] },
        "types": { "type": ["string", "null"] },
        "manaCost": { "type": ["string", "null"] },
        "power": { "type": ["integer", "null"] },
        "toughness": { "type": ["integer", "null"] },
        "loyalty": { "type": ["string", "null"] },
        "oracleText": { "type": ["string", "null"] },
        "rulesText": { "type": ["string", "null"] }
      }
    }
  }
}
```
