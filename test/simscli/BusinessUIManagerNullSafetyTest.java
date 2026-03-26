package simscli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import simscli.game.Game;
import simscli.ui.BusinessUIManager;
import simscli.ui.Input;
import simscli.ui.UIHelper;

/**
 * Regression tests for UI null-safety around active Sim state.
 */
public class BusinessUIManagerNullSafetyTest {

    @Test
    public void showDoLocationActionsMenuShouldNotCrashWhenNoActiveSim() {
        Game game = new Game();
        Input input = new Input();
        UIHelper uiHelper = new UIHelper(input);
        BusinessUIManager manager = new BusinessUIManager(game, input, uiHelper);

        game.setActiveSim(-1);

        assertDoesNotThrow(() -> manager.showDoLocationActionsMenu(game.location().get("hospital")));
        game.shutdown();
    }
}
