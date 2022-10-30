package dungeonmania.goals;

import dungeonmania.Game;

public class OrGoal implements Goal {
    private Goal goal1;
    private Goal goal2;

    public OrGoal(Goal goal1, Goal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    @Override
    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;

        return goal1.achieved(game) || goal2.achieved(game);
    }

    @Override
    public String toString(Game game) {
        if (achieved(game)) return "";
        else return "(" + goal1.toString(game) + " OR " + goal2.toString(game) + ")";
    }
}
