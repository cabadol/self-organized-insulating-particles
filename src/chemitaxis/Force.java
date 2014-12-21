package chemitaxis;

/**
 * Created by cbadenes on 17/12/14.
 */
public class Force {

    int intensity;

    String source;

    double desplacementOverX;

    double desplacementOverY;

    public Force(int intensity, String source, double desplacementOverX, double desplacementOverY) {
        this.intensity  = intensity;
        this.source     = source;
        this.desplacementOverX = desplacementOverX;
        this.desplacementOverY = desplacementOverY;
    }

    public int getIntensity() {
        return intensity;
    }

    public double getDesplacementOverX() {
        return desplacementOverX;
    }

    public double getDesplacementOverY() {
        return desplacementOverY;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDesplacementOverX(double desplacementOverX) {
        this.desplacementOverX = desplacementOverX;
    }

    public void setDesplacementOverY(double desplacementOverY) {
        this.desplacementOverY = desplacementOverY;
    }
}
