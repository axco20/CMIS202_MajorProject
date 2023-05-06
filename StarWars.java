import java.io.Serializable;

public class StarWars extends FunkoPop implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String movie;

    public StarWars(String name, String series, double price, int index) {
        super(name, series, price, index);
        
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }
}