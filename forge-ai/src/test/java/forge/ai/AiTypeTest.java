package forge.ai;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AiTypeTest {
    @Test
    public void fromPreference_shouldMatchDisplayName() {
        assertEquals(AiType.fromPreference("MCTS"), AiType.MCTS);
        assertEquals(AiType.fromPreference("Advanced (FSM)"), AiType.ADVANCED_FSM);
    }

    @Test
    public void fromPreference_shouldMatchEnumName() {
        assertEquals(AiType.fromPreference("NEURAL"), AiType.NEURAL);
        assertEquals(AiType.fromPreference("simple"), AiType.SIMPLE);
    }

    @Test
    public void getDisplayNames_shouldIncludeMcts() {
        String[] displayNames = AiType.getDisplayNames();
        boolean hasMcts = false;
        for (String name : displayNames) {
            if ("MCTS".equals(name)) {
                hasMcts = true;
                break;
            }
        }
        assertTrue(hasMcts);
    }
}
