import java.io.Serializable;

public class FunkoPop implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
    private String series;
    private double price;
    private int index;

    public FunkoPop(String name, String series, double price, int index) {
        this.name = name;
        this.series = series;
        this.price = price;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getSeries() {
        return series;
    }

    public double getPrice() {
        return price;
    }

    public int getIndex() {
        return index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    public int compareTo(FunkoPop other) {
        return name.compareTo(other.getName());
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((series == null) ? 0 : series.hashCode());
        long temp;
        temp = Double.doubleToLongBits(price);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FunkoPop other = (FunkoPop) obj;
        if (index != other.index)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
            return false;
        if (series == null) {
            if (other.series != null)
                return false;
        } else if (!series.equals(other.series))
            return false;
        return true;
    }
}
