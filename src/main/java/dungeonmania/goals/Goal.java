package dungeonmania.goals;

import dungeonmania.Game;


public interface Goal {

    public boolean achieved(Game game);
    public String toString(Game game);

    /**
     * @return true if the goal has been achieved, false otherwise
     */
    // public boolean achieved(Game game) {
    //     if (game.getPlayer() == null) return false;
    //     switch (type) {
    //         case "exit":
    //             Player character = game.getPlayer();
    //             Position pos = character.getPosition();
    //             List<Exit> es = game.getMap().getEntities(Exit.class);
    //             if (es == null || es.size() == 0) return false;
    //             return es
    //                 .stream()
    //                 .map(Entity::getPosition)
    //                 .anyMatch(pos::equals);
    //         case "boulders":
    //             return game.getMap().getEntities(Switch.class).stream().allMatch(s -> s.isActivated());
    //         case "treasure":
    //             return game.getInitialTreasureCount() - game.getMap().getEntities(Treasure.class).size() >= target;
    //         case "AND":
    //             return goal1.achieved(game) && goal2.achieved(game);
    //         case "OR":
    //             return goal1.achieved(game) || goal2.achieved(game);
    //         default:
    //             break;
    //     }
    //     return false;
    // }

    // public String toString(Game game) {
    //     if (this.achieved(game)) return "";
    //     switch (type) {
    //         case "exit":
    //             return ":exit";
    //         case "boulders":
    //             return ":boulders";
    //         case "treasure":
    //             return ":treasure";
    //         case "AND":
    //             return "(" + goal1.toString(game) + " AND " + goal2.toString(game) + ")";
    //         case "OR":
    //             if (achieved(game)) return "";
    //             else return "(" + goal1.toString(game) + " OR " + goal2.toString(game) + ")";
    //         default:
    //             break;
    //     }
    //     return "";
    // }

}
