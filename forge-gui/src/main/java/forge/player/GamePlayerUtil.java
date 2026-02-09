package forge.player;

import forge.LobbyPlayer;
import forge.ai.AIOption;
import forge.ai.AiProfileUtil;
import forge.ai.AiType;
import forge.ai.mcts.LobbyPlayerAiMctsFactory;
import forge.ai.mcts.PlayerControllerAiMcts;
import forge.ai.neural.LobbyPlayerAiNeuralFactory;
import forge.ai.simple.LobbyPlayerAiSimpleFactory;
import forge.ai.stateMachine.LobbyPlayerAiAdvancedFsmFactory;
import forge.gui.GuiBase;
import forge.gui.util.SOptionPane;
import forge.localinstance.properties.ForgeNetPreferences;
import forge.localinstance.properties.ForgePreferences.FPref;
import forge.model.FModel;
import forge.util.GuiDisplayUtil;
import forge.util.Localizer;
import forge.util.MyRandom;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public final class GamePlayerUtil {
    private GamePlayerUtil() { }
    private static Localizer localizer = Localizer.getInstance();
    private static final LobbyPlayer guiPlayer = new LobbyPlayerHuman("Human");
    public static LobbyPlayer getGuiPlayer() {
        return guiPlayer;
    }
    public static LobbyPlayer getGuiPlayer(final String name, final int avatarIndex, final int sleeveIndex, final boolean writePref) {
        if (writePref) {
            if (!name.equals(guiPlayer.getName())) {
                guiPlayer.setName(name);
                FModel.getPreferences().setPref(FPref.PLAYER_NAME, name);
                FModel.getPreferences().save();
            }

            guiPlayer.setAvatarIndex(avatarIndex);
            guiPlayer.setSleeveIndex(sleeveIndex);
            return guiPlayer;
        }
        //use separate LobbyPlayerHuman instance for human players beyond first
        return new LobbyPlayerHuman(name, avatarIndex, sleeveIndex);
    }

    public static LobbyPlayer getQuestPlayer() {
        return guiPlayer; //TODO: Make this a separate player
    }

    public static LobbyPlayer createAiPlayer() {
        return createAiPlayer(GuiDisplayUtil.getRandomAiName());
    }
    public static LobbyPlayer createAiPlayer(final String name) {
        final int avatarCount = GuiBase.getInterface().getAvatarCount();
        final int sleeveCount = GuiBase.getInterface().getSleevesCount();
        return createAiPlayer(name, avatarCount == 0 ? 0 : MyRandom.getRandom().nextInt(avatarCount), sleeveCount == 0 ? 0 : MyRandom.getRandom().nextInt(sleeveCount));
    }
    public static LobbyPlayer createAiPlayer(final String name, final String profileOverride) {
        final int avatarCount = GuiBase.getInterface().getAvatarCount();
        final int sleeveCount = GuiBase.getInterface().getSleevesCount();
        return createAiPlayer(name, avatarCount == 0 ? 0 : MyRandom.getRandom().nextInt(avatarCount), sleeveCount == 0 ? 0 : MyRandom.getRandom().nextInt(sleeveCount), null, profileOverride);
    }
    public static LobbyPlayer createAiPlayer(final String name, final int avatarIndex) {
        final int sleeveCount = GuiBase.getInterface().getSleevesCount();
        return createAiPlayer(name, avatarIndex, sleeveCount == 0 ? 0 : MyRandom.getRandom().nextInt(sleeveCount), null, "");
    }
    public static LobbyPlayer createAiPlayer(final String name, final int avatarIndex, final int sleeveIndex) {
        return createAiPlayer(name, avatarIndex, sleeveIndex, null, "");
    }
    public static LobbyPlayer createAiPlayer(final String name, final int avatarIndex, final int sleeveIndex, final Set<AIOption> options) {
        return createAiPlayer(name, avatarIndex, sleeveIndex, options, "");
    }
    public static LobbyPlayer createAiPlayer(final String name, final int avatarIndex, final int sleeveIndex, final Set<AIOption> options, final String profileOverride) {
        final AiType aiType = getPreferredAiType();
        final LobbyPlayer player = createAiPlayerByType(aiType, name, options);

        if (player instanceof LobbyPlayerAiAdvancedFsmFactory || player instanceof LobbyPlayerAiSimpleFactory) {
            // TODO: implement specific AI profiles for quest mode.
            String profile = "";
            if (profileOverride == null || profileOverride.isEmpty()) {
                String lastProfileChosen = FModel.getPreferences().getPref(FPref.UI_CURRENT_AI_PROFILE);
                if (!AiProfileUtil.getProfilesDisplayList().contains(lastProfileChosen)) {
                    System.out.println("[AI Preferences] Unknown profile " + lastProfileChosen + " was requested, resetting to default.");
                    lastProfileChosen = "Default";
                    FModel.getPreferences().setPref(FPref.UI_CURRENT_AI_PROFILE, "Default");
                    FModel.getPreferences().save();
                }
                boolean rotateProfile = lastProfileChosen.equals(AiProfileUtil.AI_PROFILE_RANDOM_DUEL);
                if (player instanceof LobbyPlayerAiAdvancedFsmFactory advancedPlayer) {
                    advancedPlayer.setRotateProfileEachGame(rotateProfile);
                } else {
                    ((LobbyPlayerAiSimpleFactory) player).setRotateProfileEachGame(rotateProfile);
                }
                if (lastProfileChosen.equals(AiProfileUtil.AI_PROFILE_RANDOM_MATCH)) {
                    lastProfileChosen = AiProfileUtil.getRandomProfile();
                }
                profile = lastProfileChosen;
            } else {
                profile = profileOverride;
            }

            assert (!profile.isEmpty()); // TODO test instead of assert

            System.out.println("[AI Preferences] using profile " + profile);
            if (player instanceof LobbyPlayerAiAdvancedFsmFactory advancedPlayer) {
                advancedPlayer.setAiProfile(profile);
            } else {
                ((LobbyPlayerAiSimpleFactory) player).setAiProfile(profile);
            }
        }

        player.setAvatarIndex(avatarIndex);
        player.setSleeveIndex(sleeveIndex);
        return player;
    }

    private static AiType getPreferredAiType() {
        String aiTypePref = FModel.getPreferences().getPref(FPref.UI_AI_TYPE);
        if (!AiType.isKnownPreference(aiTypePref)) {
            System.out.println("[AI Preferences] Unknown AI type " + aiTypePref + " was requested, resetting to default.");
            aiTypePref = AiType.SIMPLE.getDisplayName();
            FModel.getPreferences().setPref(FPref.UI_AI_TYPE, aiTypePref);
            FModel.getPreferences().save();
        }
        return AiType.fromPreference(aiTypePref);
    }

    private static LobbyPlayer createAiPlayerByType(AiType aiType, String name, Set<AIOption> options) {
        return switch (aiType) {
            case ADVANCED_FSM -> new LobbyPlayerAiAdvancedFsmFactory(name, options);
            case MCTS -> createMctsAiPlayer(name, options);
            case NEURAL -> new LobbyPlayerAiNeuralFactory(name, options);
            case SIMPLE -> new LobbyPlayerAiSimpleFactory(name, options);
        };
    }

    private static LobbyPlayerAiMctsFactory createMctsAiPlayer(String name, Set<AIOption> options) {
        int iterationBudget = FModel.getPreferences().getPrefInt(FPref.AI_MCTS_ITERATION_BUDGET);
        long timeLimitMs = FModel.getPreferences().getPrefInt(FPref.AI_MCTS_TIME_LIMIT_MS);
        int rolloutDepth = FModel.getPreferences().getPrefInt(FPref.AI_MCTS_ROLLOUT_DEPTH);
        double explorationConstant = parseDoublePref(
                FModel.getPreferences().getPref(FPref.AI_MCTS_EXPLORATION_CONSTANT),
                PlayerControllerAiMcts.DEFAULT_EXPLORATION_CONSTANT);
        return new LobbyPlayerAiMctsFactory(name, options, iterationBudget, timeLimitMs, rolloutDepth, explorationConstant);
    }

    private static double parseDoublePref(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void setPlayerName() {
        final String oldPlayerName = FModel.getPreferences().getPref(FPref.PLAYER_NAME);

        String newPlayerName;
        try {
            if (StringUtils.isBlank(oldPlayerName)) {
                newPlayerName = getVerifiedPlayerName(getPlayerNameUsingFirstTimePrompt(), oldPlayerName);
            } else {
                newPlayerName = getVerifiedPlayerName(getPlayerNameUsingStandardPrompt(oldPlayerName), oldPlayerName);
            }
        } catch (final IllegalStateException ise){
            //now is not a good time for this...
            newPlayerName = StringUtils.isBlank(oldPlayerName) ? "Human" : oldPlayerName;
        }

        FModel.getPreferences().setPref(FPref.PLAYER_NAME, newPlayerName);
        FModel.getPreferences().save();

        if (StringUtils.isBlank(oldPlayerName) && !newPlayerName.equals("Human")) {
            showThankYouPrompt(newPlayerName);
        }
    }

    public static void setServerPort() {
        final int oldPort = FModel.getNetPreferences().getPrefInt(ForgeNetPreferences.FNetPref.NET_PORT);
        int newPort = getServerPortPrompt(oldPort);
        FModel.getNetPreferences().setPref(ForgeNetPreferences.FNetPref.NET_PORT, String.valueOf(newPort));
        FModel.getNetPreferences().save();
    }

    private static void showThankYouPrompt(final String playerName) {
        SOptionPane.showMessageDialog("Thank you, " + playerName + ". "
                + "You will not be prompted again but you can change\n"
                + "your name at any time using the \"Player Name\" setting in Preferences\n"
                + "or via the constructed match setup screen\n");
    }

    private static String getPlayerNameUsingFirstTimePrompt() {
        return SOptionPane.showInputDialog(
                "By default, Forge will refer to you as the \"Human\" during gameplay.\n" +
                        "If you would prefer a different name please enter it now.",
                        "Personalize Forge Gameplay",
                        SOptionPane.QUESTION_ICON);
    }

    private static String getPlayerNameUsingStandardPrompt(final String playerName) {
        return SOptionPane.showInputDialog(
                "Please enter a new name. (alpha-numeric only)",
                "Personalize Forge Gameplay",
                null,
                playerName);
    }

    private static Integer getServerPortPrompt(final Integer serverPort) {
        String input = SOptionPane.showInputDialog(
                localizer.getMessage("sOPServerPromptMessage"),
                localizer.getMessage("sOPServerPromptTitle"),
                null,
                serverPort.toString(),
                null,
                true
        );
        Integer port;
        try {
             port = Integer.parseInt(input);
        } catch (NumberFormatException nfe) {
            SOptionPane.showErrorDialog(localizer.getMessage("sOPServerPromptError", input));
            return serverPort;
        }
        if(port < 0 || port > 65535) {
            SOptionPane.showErrorDialog(localizer.getMessage("sOPServerPromptError", input));
            return serverPort;
        }
        return  port;
    }

    private static String getVerifiedPlayerName(String newName, final String oldName) {
        if (newName == null || !StringUtils.isAlphanumericSpace(newName)) {
            newName = (StringUtils.isBlank(oldName) ? "Human" : oldName);
        } else if (StringUtils.isWhitespace(newName)) {
            newName = "Human";
        } else {
            newName = newName.trim();
        }
        return newName;
    }


}
