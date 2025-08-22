import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Barracks extends Building {
    private List<Unit> trainedUnits;

    public Barracks(Castle owner) {
        super("Barracks", 50, owner);
        this.trainedUnits = new ArrayList<>();
    }

    public List<Unit> getTrainedUnits() {
        return trainedUnits;
    }

    public void trainUnit(UnitType type) throws NotEnoughGoldException, UnitNotAvailableException {
        Unit unit = createUnit(type);
        if (!owner.getResources().consume(ResourceType.GOLD, unit.getCostGold())) {
            throw new NotEnoughGoldException("طلای کافی برای آموزش " + unit.getName() + " وجود ندارد.");
        }
        trainedUnits.add(unit);
    }

    public int totalAttackPower() {
        int sum = 0;
        for (Unit u : trainedUnits) sum += u.getAttackPower();
        return sum;
    }

    public int count(UnitType type) {
        int c = 0;
        for (Unit u : trainedUnits) {
            if (matches(u, type)) c++;
        }
        return c;
    }

    // برداشت n واحد از نوع مشخص برای تشکیل نیروی حمله
    public List<Unit> takeUnits(UnitType type, int n) throws UnitNotAvailableException {
        if (n <= 0) return new ArrayList<>();
        if (count(type) < n) throw new UnitNotAvailableException("تعداد کافی از " + type + " موجود نیست.");
        List<Unit> out = new ArrayList<>();
        Iterator<Unit> it = trainedUnits.iterator();
        while (it.hasNext() && n > 0) {
            Unit u = it.next();
            if (matches(u, type)) {
                out.add(u);
                it.remove();
                n--;
            }
        }
        return out;
    }

    // بازگرداندن نیروهای بازمانده پس از نبرد
    public void addUnits(List<Unit> units) {
        if (units == null) return;
        trainedUnits.addAll(units);
    }

    private Unit createUnit(UnitType type) throws UnitNotAvailableException {
        switch (type) {
            case SOLDIER: return new Soldier();
            case ARCHER: return new Archer();
            case CAVALRY: return new Cavalry();
            case SPY: return new Spy();
            case MERCHANT: return new Merchant();
            default: throw new UnitNotAvailableException("نوع نیروی درخواستی موجود نیست.");
        }
    }

    private boolean matches(Unit u, UnitType t) {
        if (t == UnitType.SOLDIER && u instanceof Soldier) return true;
        if (t == UnitType.ARCHER  && u instanceof Archer) return true;
        if (t == UnitType.CAVALRY && u instanceof Cavalry) return true;
        if (t == UnitType.SPY     && u instanceof Spy) return true;
        if (t == UnitType.MERCHANT&& u instanceof Merchant) return true;
        return false;
    }

    @Override
    protected void onUpgrade() {
        // ظرفیت/سرعت آموزش می‌تواند در آینده افزایش یابد
    }
}