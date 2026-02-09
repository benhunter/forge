package forge.ai.mcts;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import forge.LobbyPlayer;
import forge.card.ColorSet;
import forge.card.ICardFace;
import forge.card.mana.ManaCost;
import forge.card.mana.ManaCostShard;
import forge.deck.Deck;
import forge.deck.DeckSection;
import forge.game.*;
import forge.game.ability.effects.RollDiceEffect;
import forge.game.card.*;
import forge.game.combat.Combat;
import forge.game.cost.Cost;
import forge.game.cost.CostPart;
import forge.game.cost.CostPartMana;
import forge.game.keyword.KeywordInterface;
import forge.game.mana.Mana;
import forge.game.mana.ManaConversionMatrix;
import forge.game.mana.ManaCostBeingPaid;
import forge.game.player.*;
import forge.game.replacement.ReplacementEffect;
import forge.game.spellability.*;
import forge.game.staticability.StaticAbility;
import forge.game.trigger.WrappedAbility;
import forge.game.zone.PlayerZone;
import forge.game.zone.ZoneType;
import forge.item.PaperCard;
import forge.util.ITriggerEvent;
import forge.util.collect.FCollectionView;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PlayerControllerAiMcts extends PlayerController {
    public static final int DEFAULT_ITERATION_BUDGET = 100;
    public static final long DEFAULT_TIME_LIMIT_MS = 50;
    public static final double DEFAULT_EXPLORATION_CONSTANT = 1.4;
    public static final int DEFAULT_ROLLOUT_DEPTH = 2;

    private final MctsSearch search;
    private int iterationBudget = DEFAULT_ITERATION_BUDGET;
    private long timeLimitMs = DEFAULT_TIME_LIMIT_MS;
    private double explorationConstant = DEFAULT_EXPLORATION_CONSTANT;
    private int rolloutDepth = DEFAULT_ROLLOUT_DEPTH;

    public PlayerControllerAiMcts(Game game, Player player, LobbyPlayer lobbyPlayer) {
        super(game, player, lobbyPlayer);
        this.search = new MctsSearch(game, player);
        applySearchSettings();
    }

    @Override
    public boolean isAI() {
        return true;
    }

    @Override
    public SpellAbility getAbilityToPlay(Card hostCard, List<SpellAbility> abilities, ITriggerEvent triggerEvent) {
        return search.chooseAbilityToPlay(hostCard, abilities, triggerEvent);
    }

    @Override
    public void playSpellAbilityNoStack(SpellAbility effectSA, boolean mayChoseNewTargets) {

    }

    @Override
    public void orderAndPlaySimultaneousSa(List<SpellAbility> activePlayerSAs) {

    }

    @Override
    public boolean playTrigger(Card host, WrappedAbility wrapperAbility, boolean isMandatory) {
        return false;
    }

    @Override
    public boolean playSaFromPlayEffect(SpellAbility tgtSA) {
        return false;
    }

    @Override
    public List<PaperCard> sideboard(Deck deck, GameType gameType, String message) {
        return List.of();
    }

    @Override
    public List<PaperCard> chooseCardsYouWonToAddToDeck(List<PaperCard> losses) {
        return List.of();
    }

    @Override
    public Map<Card, Integer> assignCombatDamage(Card attacker, CardCollectionView blockers, CardCollectionView remaining, int damageDealt, GameEntity defender, boolean overrideOrder) {
        return search.assignCombatDamage(attacker, blockers, remaining, damageDealt, defender, overrideOrder);
    }

    @Override
    public Map<GameEntity, Integer> divideShield(Card effectSource, Map<GameEntity, Integer> affected, int shieldAmount) {
        return Map.of();
    }

    @Override
    public Map<Byte, Integer> specifyManaCombo(SpellAbility sa, ColorSet colorSet, int manaAmount, boolean different) {
        return Map.of();
    }

    @Override
    public CardCollectionView choosePermanentsToSacrifice(SpellAbility sa, int min, int max, CardCollectionView validTargets, String message) {
        return (CardCollectionView) validTargets.subList(0, Math.min(min, validTargets.size()));
    }

    @Override
    public CardCollectionView choosePermanentsToDestroy(SpellAbility sa, int min, int max, CardCollectionView validTargets, String message) {
        return (CardCollectionView) validTargets.subList(0, Math.min(min, validTargets.size()));
    }

    @Override
    public Integer announceRequirements(SpellAbility ability, String announce) {
        return 0;
    }

    @Override
    public TargetChoices chooseNewTargetsFor(SpellAbility ability, Predicate<GameObject> filter, boolean optional) {
        return null;
    }

    @Override
    public boolean chooseTargetsFor(SpellAbility currentAbility) {
        return search.chooseTargetsFor(currentAbility);
    }

    @Override
    public Pair<SpellAbilityStackInstance, GameObject> chooseTarget(SpellAbility sa, List<Pair<SpellAbilityStackInstance, GameObject>> allTargets) {
        return search.chooseTarget(sa, allTargets);
    }

    @Override
    public boolean helpPayForAssistSpell(ManaCostBeingPaid cost, SpellAbility sa, int max, int requested) {
        return false;
    }

    @Override
    public Player choosePlayerToAssistPayment(FCollectionView<Player> optionList, SpellAbility sa, String title, int max) {
        return null;
    }

    @Override
    public CardCollectionView chooseCardsForEffect(CardCollectionView sourceList, SpellAbility sa, String title, int min, int max, boolean isOptional, Map<String, Object> params) {
        return null;
    }

    @Override
    public CardCollection chooseCardsForEffectMultiple(Map<String, CardCollection> validMap, SpellAbility sa, String title, boolean isOptional) {
        return null;
    }

    @Override
    public <T extends GameEntity> T chooseSingleEntityForEffect(FCollectionView<T> optionList, DelayedReveal delayedReveal, SpellAbility sa, String title, boolean isOptional, Player relatedPlayer, Map<String, Object> params) {
        return null;
    }

    @Override
    public <T extends GameEntity> List<T> chooseEntitiesForEffect(FCollectionView<T> optionList, int min, int max, DelayedReveal delayedReveal, SpellAbility sa, String title, Player relatedPlayer, Map<String, Object> params) {
        return List.of();
    }

    @Override
    public List<SpellAbility> chooseSpellAbilitiesForEffect(List<SpellAbility> spells, SpellAbility sa, String title, int num, Map<String, Object> params) {
        return List.of();
    }

    @Override
    public SpellAbility chooseSingleSpellForEffect(List<SpellAbility> spells, SpellAbility sa, String title, Map<String, Object> params) {
        return null;
    }

    @Override
    public boolean confirmAction(SpellAbility sa, PlayerActionConfirmMode mode, String message, List<String> options, Card cardToShow, Map<String, Object> params) {
        return false;
    }

    @Override
    public boolean confirmBidAction(SpellAbility sa, PlayerActionConfirmMode bidlife, String string, int bid, Player winner) {
        return false;
    }

    @Override
    public boolean confirmReplacementEffect(ReplacementEffect replacementEffect, SpellAbility effectSA, GameEntity affected, String question) {
        return false;
    }

    @Override
    public boolean confirmStaticApplication(Card hostCard, PlayerActionConfirmMode mode, String message, String logic) {
        return false;
    }

    @Override
    public boolean confirmTrigger(WrappedAbility sa) {
        return false;
    }

    @Override
    public List<Card> exertAttackers(List<Card> attackers) {
        return List.of();
    }

    @Override
    public List<Card> enlistAttackers(List<Card> attackers) {
        return List.of();
    }

    @Override
    public void declareAttackers(Player attacker, Combat combat) {
        search.declareAttackers(attacker, combat);
    }

    @Override
    public void declareBlockers(Player defender, Combat combat) {
        search.declareBlockers(defender, combat);
    }

    @Override
    public CardCollection orderBlockers(Card attacker, CardCollection blockers) {
        return search.orderBlockers(attacker, blockers);
    }

    @Override
    public CardCollection orderBlocker(Card attacker, Card blocker, CardCollection oldBlockers) {
        return search.orderBlocker(attacker, blocker, oldBlockers);
    }

    @Override
    public CardCollection orderAttackers(Card blocker, CardCollection attackers) {
        return search.orderAttackers(blocker, attackers);
    }

    @Override
    public void reveal(CardCollectionView cards, ZoneType zone, Player owner, String messagePrefix, boolean addMsgSuffix) {
    }

    @Override
    public void reveal(List<CardView> cards, ZoneType zone, PlayerView owner, String messagePrefix, boolean addMsgSuffix) {
    }

    @Override
    public void notifyOfValue(SpellAbility saSource, GameObject realtedTarget, String value) {
    }

    @Override
    public ImmutablePair<CardCollection, CardCollection> arrangeForScry(CardCollection topN) {
        return null;
    }

    @Override
    public ImmutablePair<CardCollection, CardCollection> arrangeForSurveil(CardCollection topN) {
        return null;
    }

    @Override
    public boolean willPutCardOnTop(Card c) {
        return false;
    }

    @Override
    public CardCollectionView orderMoveToZoneList(CardCollectionView cards, ZoneType destinationZone, SpellAbility source) {
        return null;
    }

    @Override
    public CardCollectionView chooseCardsToDiscardFrom(Player playerDiscard, SpellAbility sa, CardCollection validCards, int min, int max) {
        return null;
    }

    @Override
    public CardCollectionView chooseCardsToDiscardUnlessType(int min, CardCollectionView hand, String param, SpellAbility sa) {
        return null;
    }

    @Override
    public CardCollection chooseCardsToDiscardToMaximumHandSize(int numDiscard) {
        CardCollectionView hand = this.player.getCardsIn(ZoneType.Hand);
        CardCollection discard = new CardCollection();
        for (int i = 0; i < numDiscard && i < hand.size(); i++) {
            discard.add(hand.get(i));
        }
        return discard;
    }

    @Override
    public CardCollectionView chooseCardsToDelve(int genericAmount, CardCollection grave) {
        return null;
    }

    @Override
    public Map<Card, ManaCostShard> chooseCardsForConvokeOrImprovise(SpellAbility sa, ManaCost manaCost, CardCollectionView untappedCards, boolean artifacts, boolean creatures, Integer maxReduction) {
        return Map.of();
    }

    @Override
    public List<Card> chooseCardsForSplice(SpellAbility sa, List<Card> cards) {
        return List.of();
    }

    @Override
    public CardCollectionView chooseCardsToRevealFromHand(int min, int max, CardCollectionView valid) {
        return null;
    }

    @Override
    public List<SpellAbility> chooseSaToActivateFromOpeningHand(List<SpellAbility> usableFromOpeningHand) {
        return List.of();
    }

    @Override
    public Player chooseStartingPlayer(boolean isFirstGame) {
        return this.player;
    }

    @Override
    public PlayerZone chooseStartingHand(List<PlayerZone> zones) {
        return null;
    }

    @Override
    public Mana chooseManaFromPool(List<Mana> manaChoices) {
        return null;
    }

    @Override
    public String chooseSomeType(String kindOfType, SpellAbility sa, Collection<String> validTypes, boolean isOptional) {
        return "";
    }

    @Override
    public String chooseSector(Card assignee, String ai, List<String> sectors) {
        return "";
    }

    @Override
    public List<Card> chooseContraptionsToCrank(List<Card> contraptions) {
        return List.of();
    }

    @Override
    public int chooseSprocket(Card assignee, boolean forceDifferent) {
        return 0;
    }

    @Override
    public PlanarDice choosePDRollToIgnore(List<PlanarDice> rolls) {
        return null;
    }

    @Override
    public Integer chooseRollToIgnore(List<Integer> rolls) {
        return 0;
    }

    @Override
    public List<Integer> chooseDiceToReroll(List<Integer> rolls) {
        return List.of();
    }

    @Override
    public Integer chooseRollToModify(List<Integer> rolls) {
        return 0;
    }

    @Override
    public RollDiceEffect.DieRollResult chooseRollToSwap(List<RollDiceEffect.DieRollResult> rolls) {
        return null;
    }

    @Override
    public String chooseRollSwapValue(List<String> swapChoices, Integer currentResult, int power, int toughness) {
        return "";
    }

    @Override
    public Object vote(SpellAbility sa, String prompt, List<Object> options, ListMultimap<Object, Player> votes, Player forPlayer, boolean optional) {
        if (optional) {
            return null;
        }
        if (options == null) {
            throw new IllegalArgumentException("options cannot be null");
        }
        if (!options.isEmpty()) {
            return options.get(0);
        }
        return null;
    }

    @Override
    public boolean mulliganKeepHand(Player player, int cardsToReturn) {
        return true;
    }

    @Override
    public CardCollectionView tuckCardsViaMulligan(Player mulliganingPlayer, int cardsToReturn) {
        return new CardCollection();
    }

    @Override
    public boolean confirmMulliganScry(Player p) {
        return true;
    }

    @Override
    public List<SpellAbility> chooseSpellAbilityToPlay() {
        return null;
    }

    @Override
    public boolean playChosenSpellAbility(SpellAbility sa) {
        return false;
    }

    @Override
    public List<AbilitySub> chooseModeForAbility(SpellAbility sa, List<AbilitySub> possible, int min, int num, boolean allowRepeat) {
        return List.of();
    }

    @Override
    public int chooseNumberForCostReduction(SpellAbility sa, int min, int max) {
        return 0;
    }

    @Override
    public int chooseNumberForKeywordCost(SpellAbility sa, Cost cost, KeywordInterface keyword, String prompt, int max) {
        return 0;
    }

    @Override
    public int chooseNumber(SpellAbility sa, String title, int min, int max) {
        return 0;
    }

    @Override
    public int chooseNumber(SpellAbility sa, String title, List<Integer> values, Player relatedPlayer) {
        return 0;
    }

    @Override
    public boolean chooseBinary(SpellAbility sa, String question, BinaryChoiceType kindOfChoice, Boolean defaultChoice) {
        return false;
    }

    @Override
    public boolean chooseFlipResult(SpellAbility sa, Player flipper, boolean[] results, boolean call) {
        return false;
    }

    @Override
    public byte chooseColor(String message, SpellAbility sa, ColorSet colors) {
        return 0;
    }

    @Override
    public byte chooseColorAllowColorless(String message, Card c, ColorSet colors) {
        return 0;
    }

    @Override
    public ColorSet chooseColors(String message, SpellAbility sa, int min, int max, ColorSet options) {
        return null;
    }

    @Override
    public ICardFace chooseSingleCardFace(SpellAbility sa, String message, Predicate<ICardFace> cpp, String name) {
        return null;
    }

    @Override
    public ICardFace chooseSingleCardFace(SpellAbility sa, List<ICardFace> faces, String message) {
        return null;
    }

    @Override
    public CardState chooseSingleCardState(SpellAbility sa, List<CardState> states, String message, Map<String, Object> params) {
        return null;
    }

    @Override
    public boolean chooseCardsPile(SpellAbility sa, CardCollectionView pile1, CardCollectionView pile2, String faceUp) {
        return false;
    }

    @Override
    public CounterType chooseCounterType(List<CounterType> options, SpellAbility sa, String prompt, Map<String, Object> params) {
        return null;
    }

    @Override
    public String chooseKeywordForPump(List<String> options, SpellAbility sa, String prompt, Card tgtCard) {
        return "";
    }

    @Override
    public boolean confirmPayment(CostPart costPart, String string, SpellAbility sa) {
        return false;
    }

    @Override
    public ReplacementEffect chooseSingleReplacementEffect(List<ReplacementEffect> possibleReplacers) {
        return null;
    }

    @Override
    public StaticAbility chooseSingleStaticAbility(String prompt, List<StaticAbility> possibleReplacers) {
        return null;
    }

    @Override
    public String chooseProtectionType(String string, SpellAbility sa, List<String> choices) {
        return "";
    }

    @Override
    public void revealAnte(String message, Multimap<Player, PaperCard> removedAnteCards) {

    }

    @Override
    public void revealAISkipCards(String message, Map<Player, Map<DeckSection, List<? extends PaperCard>>> deckCards) {

    }

    @Override
    public void revealUnsupported(Map<Player, List<PaperCard>> unsupported) {
    }

    @Override
    public void resetAtEndOfTurn() {

    }

    @Override
    public List<OptionalCostValue> chooseOptionalCosts(SpellAbility choosen, List<OptionalCostValue> optionalCostValues) {
        return List.of();
    }

    @Override
    public List<CostPart> orderCosts(List<CostPart> costs) {
        return List.of();
    }

    @Override
    public boolean payCostToPreventEffect(Cost cost, SpellAbility sa, boolean alreadyPaid, FCollectionView<Player> allPayers) {
        return false;
    }

    @Override
    public boolean payCostDuringRoll(Cost cost, SpellAbility sa, FCollectionView<Player> allPayers) {
        return false;
    }

    @Override
    public boolean payCombatCost(Card card, Cost cost, SpellAbility sa, String prompt) {
        return false;
    }

    @Override
    public boolean payManaCost(ManaCost toPay, CostPartMana costPartMana, SpellAbility sa, String prompt, ManaConversionMatrix matrix, boolean effect) {
        return false;
    }

    @Override
    public String chooseCardName(SpellAbility sa, Predicate<ICardFace> cpp, String valid, String message) {
        return "";
    }

    @Override
    public String chooseCardName(SpellAbility sa, List<ICardFace> faces, String message) {
        return "";
    }

    @Override
    public Card chooseSingleCardForZoneChange(ZoneType destination, List<ZoneType> origin, SpellAbility sa, CardCollection fetchList, DelayedReveal delayedReveal, String selectPrompt, boolean isOptional, Player decider) {
        return null;
    }

    @Override
    public List<Card> chooseCardsForZoneChange(ZoneType destination, List<ZoneType> origin, SpellAbility sa, CardCollection fetchList, int min, int max, DelayedReveal delayedReveal, String selectPrompt, Player decider) {
        return List.of();
    }

    @Override
    public void autoPassCancel() {

    }

    @Override
    public void awaitNextInput() {

    }

    @Override
    public void cancelAwaitNextInput() {

    }

    public void setIterationBudget(int iterationBudget) {
        this.iterationBudget = iterationBudget;
        applySearchSettings();
    }

    public int getIterationBudget() {
        return iterationBudget;
    }

    public void setTimeLimitMs(long timeLimitMs) {
        this.timeLimitMs = timeLimitMs;
        applySearchSettings();
    }

    public long getTimeLimitMs() {
        return timeLimitMs;
    }

    public void setExplorationConstant(double explorationConstant) {
        this.explorationConstant = explorationConstant;
        applySearchSettings();
    }

    public double getExplorationConstant() {
        return explorationConstant;
    }

    public void setRolloutDepth(int rolloutDepth) {
        this.rolloutDepth = rolloutDepth;
        applySearchSettings();
    }

    public int getRolloutDepth() {
        return rolloutDepth;
    }

    private void applySearchSettings() {
        search.setIterationBudget(iterationBudget);
        search.setTimeLimitMs(timeLimitMs);
        search.setExplorationConstant(explorationConstant);
        search.setRolloutDepth(rolloutDepth);
    }
}
