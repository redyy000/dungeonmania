package dungeonmania.response.models;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.battles.BattleRound;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.util.NameConverter;

public class ResponseBuilder {
    public static DungeonResponse getDungeonResponse(Game game) {
        return game.getDungeonResponse();
    }

    public static List<ItemResponse> getInventoryResponse(Inventory inventory) {
        return inventory.getEntities()
                        .stream()
                        .map(ResponseBuilder::getItemResponse)
                        .collect(Collectors.toList());
    }

    public static ItemResponse getItemResponse(Entity entity) {
        return new ItemResponse(entity.getId(), NameConverter.toSnakeCase(entity));
    }

    public static EntityResponse getEntityResponse(Game game, Entity entity) {
        return new EntityResponse(
                entity.getId(),
                NameConverter.toSnakeCase(entity),
                entity.getPosition(),
                (entity instanceof Interactable) && ((Interactable) entity).isInteractable(game.getPlayer()));
    }

    public static RoundResponse getRoundResponse(BattleRound round) {
        return new RoundResponse(
                round.getDeltaSelfHealth(),
                round.getDeltaTargetHealth());
    }
}
