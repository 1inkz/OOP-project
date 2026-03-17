package simscli.actions;

/**
 * Abstraction layer for UI operations needed by actions.
 * Allows actions to request user input without coupling to specific UI implementation.
 * 
 * This follows Dependency Inversion Principle:
 * - Actions depend on this abstraction
 * - ConsoleUI provides implementation via GameContext
 * - Makes actions testable without UI mocking
 * 
 * ActionUIAdapter
 * It's an interface we created for dependency injection of UI operations. 
 * Instead of actions doing new Input() and tight coupling to the Input class, 
 * actions now receive GameContext (which implements ActionUIAdapter) and call ui.intRange() and ui.line() through it. 
 * This decouples actions from UI implementation—so if you swap UI engines later, 
 * you just update GameContext, not 30+ action files.
 */
public interface ActionUIAdapter {
    /**
     * Get integer input from user within range [min, max].
     * @param prompt Message to display
     * @param min Minimum inclusive value
     * @param max Maximum inclusive value
     * @return User's selected integer
     */
    int intRange(String prompt, int min, int max);
    
    /**
     * Get a line of text input from user.
     * @param prompt Message to display  
     * @return User's input string
     */
    String line(String prompt);
}
