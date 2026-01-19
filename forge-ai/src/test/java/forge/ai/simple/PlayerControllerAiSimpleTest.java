package forge.ai.simple;

import forge.game.Game;
import forge.game.card.CardCollection;
import forge.game.card.CardCollectionView;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static forge.ai.simple.Helpers.createGame;
import static org.testng.Assert.*;

public class PlayerControllerAiSimpleTest {

    private PlayerControllerAiSimple controller;

    @BeforeMethod
    public void setUp() {
        Helpers.initializeLocalizer();

        Game game = createGame();
        controller = new PlayerControllerAiSimple(game, null, null);
    }

    @Test
    public void testIsAI() {
        assertTrue(controller.isAI());
    }

    @Test
    public void testGetAbilityToPlay() {
        assertNull(controller.getAbilityToPlay(null, null, null));
    }

    @Test
    public void testPlayTrigger() {
        assertFalse(controller.playTrigger(null, null, true));
    }

    @Test
    public void testPlaySaFromPlayEffect() {
        assertFalse(controller.playSaFromPlayEffect(null));
    }

    @Test
    public void testSideboard() {
        // TODO
    }

    @Test
    public void testChooseCardsYouWonToAddToDeck() {
        // TODO
    }

    @Test
    public void testAssignCombatDamage() {
        // TODO
    }

    @Test
    public void testDivideShield() {
        // TODO
    }

    @Test
    public void testSpecifyManaCombo() {
        // TODO
    }

    @Test
    public void testChoosePermanentsToSacrifice_shouldChoseZero_whenPossible() {
        CardCollectionView validTargets = new CardCollection();
        CardCollectionView cards = controller.choosePermanentsToSacrifice(null, 0, 1, validTargets, "Test");
        assertEquals(cards.size(), 0);
    }

    @Test
    public void testChoosePermanentsToSacrifice_shouldChoseFirst() {
        // TODO
    }

    @Test
    public void testChoosePermanentsToDestroy_shouldChoseZero_whenPossible() {
        CardCollectionView validTargets = new CardCollection();
        CardCollectionView cards = controller.choosePermanentsToSacrifice(null, 0, 1, validTargets, "Test");
        assertEquals(cards.size(), 0);
    }

    @Test
    public void testChoosePermanentsToDestroy_shouldChoseFirst() {
        // TODO
    }

    @Test
    public void testAnnounceRequirements() {
        assertEquals(controller.announceRequirements(null, null), 0);
    }

    @Test
    public void testChooseNewTargetsFor() {
        assertNull(controller.chooseNewTargetsFor(null, null, false));
    }

    @Test
    public void testChooseTargetsFor() {
        assertFalse(controller.chooseTargetsFor(null));
    }

    @Test
    public void testChooseTarget() {
        assertNull(controller.chooseTarget(null, null));
    }

    @Test
    public void testHelpPayForAssistSpell() {
        assertFalse(controller.helpPayForAssistSpell(null, null, 0, 0));
    }

    @Test
    public void testVote_shouldNull_whenOptional() {
        assertNull(controller.vote(null, null, null, null, null, true));
    }

    @Test
    public void testVote_shouldRejectNullOption_whenRequired() {
        assertThrows(IllegalArgumentException.class, () -> controller.vote(null, null, null, null, null, false));
    }

    @Test
    public void testVote_shouldChooseFirst_whenRequired() {
        List<Object> options = new ArrayList<>();
        options.add("Option1");
        options.add("Option2");
        assertNotNull(controller.vote(null, null, options, null, null, false));
    }

    @Test
    public void testTuckCardsViaMulligan() {
        assertEquals(controller.tuckCardsViaMulligan(controller.getPlayer(), 0).size(), 0);
    }
}