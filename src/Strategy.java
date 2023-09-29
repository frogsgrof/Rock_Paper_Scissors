public interface Strategy {

    /**
     * Decides what move to use against the player. This may or may not take the player's move into account.
     * @param player move chosen by the player
     * @return move to be played against the player
     */
    Move determineMove(Move player);
}
