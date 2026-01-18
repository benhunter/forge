package forge.game.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import forge.card.MagicColor;
import forge.card.mana.ManaAtom;
import forge.game.GameType;
import forge.game.GameView;
import forge.game.card.CardView;
import forge.game.card.CounterType;
import forge.game.card.CardView.CardStateView;
import forge.game.phase.PhaseType;
import forge.game.player.PlayerView;
import forge.game.spellability.StackItemView;
import forge.game.zone.ZoneType;
import forge.util.collect.FCollectionView;

public final class ViewDtoMapper {
    private static final List<ZoneType> ZONE_TYPES = List.of(
            ZoneType.Ante,
            ZoneType.Battlefield,
            ZoneType.Command,
            ZoneType.Exile,
            ZoneType.Graveyard,
            ZoneType.Hand,
            ZoneType.Library,
            ZoneType.Flashback,
            ZoneType.Sideboard,
            ZoneType.PlanarDeck,
            ZoneType.SchemeDeck,
            ZoneType.AttractionDeck,
            ZoneType.ContraptionDeck,
            ZoneType.Junkyard
    );

    private ViewDtoMapper() {
    }

    public static GameStateDto toGameState(GameView view) {
        if (view == null) {
            return null;
        }
        List<PlayerStateDto> players = toPlayerStates(view.getPlayers());
        List<StackItemDto> stack = toStackItems(view.getStack());
        return new GameStateDto(
                DtoSchemaVersion.CURRENT,
                view.getId(),
                view.getTitle(),
                enumName(view.getGameType()),
                view.isCommander(),
                view.getTurn(),
                enumName(view.getPhase()),
                id(view.getPlayerTurn()),
                id(view.getPlanarPlayer()),
                view.getStormCount(),
                view.isMulligan(),
                view.isGameOver(),
                view.isMatchOver(),
                view.getWinningPlayerName(),
                view.getWinningTeam(),
                players,
                stack
        );
    }

    public static StackStateDto toStackState(GameView view) {
        if (view == null) {
            return null;
        }
        return new StackStateDto(
                DtoSchemaVersion.CURRENT,
                view.getId(),
                toStackItems(view.getStack())
        );
    }

    public static PlayerStateDto toPlayerState(PlayerView view) {
        if (view == null) {
            return null;
        }
        return new PlayerStateDto(
                DtoSchemaVersion.CURRENT,
                view.getId(),
                view.getName(),
                view.getLobbyPlayerName(),
                view.isAI(),
                view.getLife(),
                view.getIsExtraTurn(),
                view.hasDelirium(),
                view.getCurrentPlaneName(),
                toManaPool(view),
                toCounters(view.getCounters()),
                toOpponentIds(view.getOpponents()),
                toZones(view)
        );
    }

    public static ZoneStateDto toZoneState(PlayerView view) {
        if (view == null) {
            return null;
        }
        return new ZoneStateDto(
                DtoSchemaVersion.CURRENT,
                view.getId(),
                toZones(view)
        );
    }

    public static ZoneDto toZone(PlayerView view, ZoneType zoneType) {
        if (view == null || zoneType == null) {
            return null;
        }
        FCollectionView<CardView> cards = view.getCards(zoneType);
        List<CardDto> cardDtos = toCards(cards);
        return new ZoneDto(zoneType.name(), view.getZoneSize(zoneType), cardDtos);
    }

    public static StackItemDto toStackItem(StackItemView view) {
        if (view == null) {
            return null;
        }
        return new StackItemDto(
                view.getId(),
                view.getKey(),
                view.getText(),
                toCard(view.getSourceCard()),
                id(view.getActivatingPlayer()),
                toCardIds(view.getTargetCards()),
                toPlayerIds(view.getTargetPlayers()),
                view.isAbility(),
                view.isOptionalTrigger(),
                view.getOptionalCostString(),
                toStackItem(view.getSubInstance())
        );
    }

    public static CardDto toCard(CardView view) {
        if (view == null) {
            return null;
        }
        CardStateView state = view.getCurrentState();
        return new CardDto(
                view.getId(),
                state == null ? null : state.getDisplayId(),
                view.getName(),
                view.getOracleName(),
                id(view.getOwner()),
                id(view.getController()),
                enumName(view.getZone()),
                state == null ? null : state.getImageKey(),
                view.isFaceDown(),
                view.isTapped(),
                view.isToken(),
                view.isAttacking(),
                view.isBlocking(),
                toCardState(state)
        );
    }

    private static CardStateDto toCardState(CardStateView state) {
        if (state == null) {
            return null;
        }
        String manaCost = state.getManaCost() == null ? null : state.getManaCost().toString();
        String types = state.getType() == null ? null : state.getType().toString();
        return new CardStateDto(
                enumName(state.getState()),
                state.getName(),
                state.getOracleName(),
                types,
                manaCost,
                state.getPower(),
                state.getToughness(),
                state.getLoyalty(),
                state.getOracleText(),
                state.getRulesText()
        );
    }

    private static List<PlayerStateDto> toPlayerStates(FCollectionView<PlayerView> players) {
        if (players == null) {
            return List.of();
        }
        List<PlayerStateDto> dtos = new ArrayList<>(players.size());
        for (PlayerView view : players) {
            dtos.add(toPlayerState(view));
        }
        return List.copyOf(dtos);
    }

    private static List<StackItemDto> toStackItems(FCollectionView<StackItemView> stack) {
        if (stack == null) {
            return List.of();
        }
        List<StackItemDto> dtos = new ArrayList<>(stack.size());
        for (StackItemView view : stack) {
            dtos.add(toStackItem(view));
        }
        return List.copyOf(dtos);
    }

    private static List<ZoneDto> toZones(PlayerView view) {
        if (view == null) {
            return List.of();
        }
        List<ZoneDto> zones = new ArrayList<>(ZONE_TYPES.size());
        for (ZoneType zoneType : ZONE_TYPES) {
            zones.add(toZone(view, zoneType));
        }
        return List.copyOf(zones);
    }

    private static List<CardDto> toCards(Iterable<CardView> cards) {
        if (cards == null) {
            return List.of();
        }
        List<CardDto> dtos = new ArrayList<>();
        for (CardView card : cards) {
            dtos.add(toCard(card));
        }
        return List.copyOf(dtos);
    }

    private static List<Integer> toPlayerIds(Iterable<PlayerView> players) {
        if (players == null) {
            return List.of();
        }
        List<Integer> ids = new ArrayList<>();
        for (PlayerView player : players) {
            ids.add(player.getId());
        }
        return List.copyOf(ids);
    }

    private static List<Integer> toOpponentIds(Iterable<PlayerView> players) {
        return toPlayerIds(players);
    }

    private static List<Integer> toCardIds(Iterable<CardView> cards) {
        if (cards == null) {
            return List.of();
        }
        List<Integer> ids = new ArrayList<>();
        for (CardView card : cards) {
            ids.add(card.getId());
        }
        return List.copyOf(ids);
    }

    private static Integer id(PlayerView player) {
        return player == null ? null : player.getId();
    }

    private static String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private static String enumName(GameType value) {
        return value == null ? null : value.name();
    }

    private static String enumName(PhaseType value) {
        return value == null ? null : value.name();
    }

    private static String enumName(ZoneType value) {
        return value == null ? null : value.name();
    }

    private static Map<String, Integer> toCounters(Map<CounterType, Integer> counters) {
        if (counters == null || counters.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> sorted = new TreeMap<>();
        for (Map.Entry<CounterType, Integer> entry : counters.entrySet()) {
            CounterType type = entry.getKey();
            if (type != null) {
                sorted.put(type.getName(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(sorted);
    }

    private static Map<String, Integer> toManaPool(PlayerView view) {
        if (view == null) {
            return Collections.emptyMap();
        }
        Map<String, Integer> mana = new LinkedHashMap<>();
        for (byte manaType : ManaAtom.MANATYPES) {
            mana.put(manaTypeName(manaType), view.getMana(manaType));
        }
        return Collections.unmodifiableMap(mana);
    }

    private static String manaTypeName(byte manaType) {
        if (manaType == ManaAtom.WHITE) {
            return MagicColor.Constant.WHITE;
        }
        if (manaType == ManaAtom.BLUE) {
            return MagicColor.Constant.BLUE;
        }
        if (manaType == ManaAtom.BLACK) {
            return MagicColor.Constant.BLACK;
        }
        if (manaType == ManaAtom.RED) {
            return MagicColor.Constant.RED;
        }
        if (manaType == ManaAtom.GREEN) {
            return MagicColor.Constant.GREEN;
        }
        if (manaType == ManaAtom.COLORLESS) {
            return MagicColor.Constant.COLORLESS;
        }
        return "unknown";
    }
}
