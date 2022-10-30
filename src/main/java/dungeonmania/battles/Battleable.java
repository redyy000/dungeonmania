package dungeonmania.battles;

/**
 * Entities implement this interface can do battles
 */
public interface Battleable {
    public BattleStatistics getBattleStatistics();
    public double getHealth();
}
