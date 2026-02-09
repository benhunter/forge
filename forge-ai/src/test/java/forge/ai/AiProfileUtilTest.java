package forge.ai;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class AiProfileUtilTest {
    @Test
    public void profilesDisplayList_includesEngineProfiles() {
        List<String> profiles = AiProfileUtil.getProfilesDisplayList();
        assertTrue(profiles.contains(AiProfileUtil.AI_PROFILE_SIMPLE));
        assertTrue(profiles.contains(AiProfileUtil.AI_PROFILE_MCTS));
        assertTrue(profiles.contains(AiProfileUtil.AI_PROFILE_NEURAL));
    }
}
