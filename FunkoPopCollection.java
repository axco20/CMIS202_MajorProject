import java.util.ArrayList;
import java.util.Collections;

public class FunkoPopCollection {
    private ArrayList<FunkoPop> items;

    public FunkoPopCollection() {
        items = new ArrayList<>();
    }

    public void addItem(FunkoPop item) {
        items.add(item);
    }

    public void removeItem(FunkoPop itemToRemove) {
        items.remove(itemToRemove);
    }

    public void sortItems() {
        Collections.sort(items, new FunkoPopComparator());
    }
    
    public ArrayList<FunkoPop> getItems() {
        return items;
    }

	
}
