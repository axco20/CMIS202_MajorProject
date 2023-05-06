import java.util.Comparator;

public class FunkoPopComparator implements Comparator<FunkoPop> {
    @Override
    public int compare(FunkoPop o1, FunkoPop o2) {
        int nameCompare = o1.getName().compareTo(o2.getName());
        if (nameCompare != 0) {
            return nameCompare;
        }
        return o1.getSeries().compareTo(o2.getSeries());
    }
}
