public class Market {
    private Castle castle;
    private Position position;
    private int accessRange = 1;

    public Market(Castle castle) {
        this.castle = castle;
        this.position = castle.getPosition();
    }

    public Position getPosition() { return position; }
    public Castle getCastle() { return castle; }

    private void ensureAccess(Player player) throws MarketAccessException {
        if (player.getId() != castle.getOwner().getId()) {
            throw new MarketAccessException("Access to this market is not allowed!");
        }
        int dist = player.getPosition().manhattanTo(position);
        if (dist > accessRange) {
            throw new MarketAccessException("You have to be near the castle to use the market!");
        }
    }

    public void buy(Player player, ResourceType type, int amount)
            throws MarketAccessException, NotEnoughGoldException, InvalidTradeException {

        if (amount <= 0) return;
        if (!EconomyManager.isTradable(type)) {
            throw new InvalidTradeException("This resource cannot be purchased from the market!");
        }

        ensureAccess(player);

        int price = EconomyManager.getBuyPrice(type) * amount;
        ResourceBundle res = castle.getResources();

        if (!res.consume(ResourceType.GOLD, price)) {
            throw new NotEnoughGoldException("There is not enough gold to buy!");
        }
        res.add(type, amount);
    }

    public void sell(Player player, ResourceType type, int amount)
            throws MarketAccessException, NotEnoughResourceException, InvalidTradeException {

        if (amount <= 0) return;
        if (!EconomyManager.isTradable(type)) {
            throw new InvalidTradeException("This resource cannot be sold on the market!");
        }

        ensureAccess(player);

        ResourceBundle res = castle.getResources();
        if (res.get(type) < amount) {
            throw new NotEnoughResourceException("There is not enough of the resource available for sale!");
        }

        int income = EconomyManager.getSellPrice(type) * amount;
        res.consume(type, amount);
        res.add(ResourceType.GOLD, income);
    }
}