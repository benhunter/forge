package forge.gamesimulationtests;

import forge.ai.simulation.SimulationTest;
import forge.game.Game;
import forge.game.ability.AbilityFactory;
import forge.game.ability.AbilityKey;
import forge.game.card.Card;
import forge.game.player.Player;
import forge.game.replacement.ReplacementEffect;
import forge.game.replacement.ReplacementHandler;
import forge.game.spellability.SpellAbility;
import forge.game.zone.ZoneType;
import forge.item.PaperCard;
import forge.model.FModel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class ConniveRegressionTest extends SimulationTest {

    @Test
    public void conniveUsesConniveReplacementType() {
        final Game game = initAndCreateGame();
        final Player player = game.getPlayers().get(1);
        final Card conniver = addCard("Runeclaw Bear", player);
        addCardToZone("Forest", player, ZoneType.Library);

        final ReplacementEffect replacement = ReplacementHandler.parseReplacement(
                "Event$ Connive | ActiveZones$ Battlefield | ValidConniver$ Card.Self | Skip$ True",
                conniver,
                true);
        conniver.addReplacementEffect(replacement);

        final SpellAbility connive = AbilityFactory.getAbility("DB$ Connive | Defined$ Self", conniver);
        connive.setActivatingPlayer(player);
        connive.resolve();

        assertEquals(player.getCardsIn(ZoneType.Library).size(), 1,
                "A replaced connive must not draw a card");
        assertEquals(player.getCardsIn(ZoneType.Graveyard).size(), 0,
                "A replaced connive must not discard a card");
    }

    @Test
    public void conniveDoesNotUseExploreReplacementType() {
        final Game game = initAndCreateGame();
        final Player player = game.getPlayers().get(1);
        final Card conniver = addCard("Runeclaw Bear", player);
        addCardToZone("Forest", player, ZoneType.Library);

        final ReplacementEffect replacement = ReplacementHandler.parseReplacement(
                "Event$ Explore | ActiveZones$ Battlefield | ValidExplorer$ Card.Self | Skip$ True",
                conniver,
                true);
        conniver.addReplacementEffect(replacement);

        final SpellAbility connive = AbilityFactory.getAbility("DB$ Connive | Defined$ Self", conniver);
        connive.setActivatingPlayer(player);
        connive.resolve();

        assertEquals(player.getCardsIn(ZoneType.Library).size(), 0,
                "An explore replacement must not replace a connive event");
        assertEquals(player.getCardsIn(ZoneType.Graveyard).size(), 1,
                "Connive must still discard after an unrelated explore replacement");
    }

    @Test
    public void conniveReplacementHonorsValidConniver() {
        final Game game = initAndCreateGame();
        final Player controller = game.getPlayers().get(1);
        final Player opponent = game.getPlayers().get(0);
        final Card replacementHost = addCard("Runeclaw Bear", controller);
        final Card opponentCreature = addCard("Bear Cub", opponent);

        final ReplacementEffect replacement = ReplacementHandler.parseReplacement(
                "Event$ Connive | ActiveZones$ Battlefield | ValidConniver$ Creature.YouCtrl | Skip$ True",
                replacementHost,
                true);

        assertFalse(replacement.canReplace(AbilityKey.mapFromAffected(opponentCreature)),
                "ValidConniver$ Creature.YouCtrl must reject an opponent's creature");
    }

    @Test
    public void leaderSuperGeniusPrintResolvesToItsScript() {
        final PaperCard card = FModel.getMagicDb().getCommonCards().getCard("Leader, Super-Genius", "MSH");

        assertNotNull(card,
                "The MSH edition entry must resolve to the Leader, Super-Genius card script");
    }
}
