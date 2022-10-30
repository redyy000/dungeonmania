// package dungeonmania.entities.playerState;

// import dungeonmania.entities.Player;

// public class InvisibleState extends PlayerState {
//     public InvisibleState(Player player) {
//         super(player, false, true);
//     }

//     @Override
//     public void transitionBase() {
//         Player player = getPlayer();
//         player.changeState(new BaseState(player));
//     }

//     @Override
//     public void transitionInvincible() {
//         Player player = getPlayer();
//         player.changeState(new InvincibleState(player));
//     }

//     @Override
//     public void transitionInvisible() {
//         Player player = getPlayer();
//         player.changeState(new InvisibleState(player));
//     }
// }
