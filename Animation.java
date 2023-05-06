import java.io.Serializable;

public class Animation extends FunkoPop implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String character;

    public Animation(String name, String series, double price, int index) {
        super(name, series, price, index);
      
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }
}

