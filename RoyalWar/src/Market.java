public class Market {
    private Castle castle;           // مارکت وابسته به یک قلعه
    private Position position;       // محل مارکت (روی همان خانه قلعه برای سادگی)
    private int accessRange = 1;     // فاصله منهتن مجاز برای دسترسی به مارکت

    public Market(Castle castle) {
        this.castle = castle;
        this.position = castle.getPosition();
    }

    public Position getPosition() { return position; }
    public Castle getCastle() { return castle; }

    private void ensureAccess(Player player) throws MarketAccessException {
        // بازیکن باید مالک همین قلعه باشد و نزدیک مارکت
        if (player.getId() != castle.getOwner().getId()) {
            throw new MarketAccessException("دسترسی به این مارکت مجاز نیست.");
        }
        int dist = player.getPosition().manhattanTo(position);
        if (dist > accessRange) {
            throw new MarketAccessException("برای استفاده از مارکت باید نزدیک قلعه باشی.");
        }
    }

    public void buy(Player player, ResourceType type, int amount)
            throws MarketAccessException, NotEnoughGoldException, InvalidTradeException {

        if (amount <= 0) return;
        if (!EconomyManager.isTradable(type)) {
            throw new InvalidTradeException("این منبع قابل خرید از مارکت نیست.");
        }

        ensureAccess(player);

        int price = EconomyManager.getBuyPrice(type) * amount;
        ResourceBundle res = castle.getResources();

        if (!res.consume(ResourceType.GOLD, price)) {
            throw new NotEnoughGoldException("طلای کافی برای خرید وجود ندارد.");
        }
        res.add(type, amount);
    }

    public void sell(Player player, ResourceType type, int amount)
            throws MarketAccessException, NotEnoughResourceException, InvalidTradeException {

        if (amount <= 0) return;
        if (!EconomyManager.isTradable(type)) {
            throw new InvalidTradeException("این منبع قابل فروش به مارکت نیست.");
        }

        ensureAccess(player);

        ResourceBundle res = castle.getResources();
        if (res.get(type) < amount) {
            throw new NotEnoughResourceException("مقدار کافی از منبع برای فروش موجود نیست.");
        }

        int income = EconomyManager.getSellPrice(type) * amount;
        res.consume(type, amount);
        res.add(ResourceType.GOLD, income);
    }
}