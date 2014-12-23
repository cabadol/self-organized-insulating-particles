/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package chemitaxis;

import sim.util.Bag;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

import java.util.Iterator;

/**
 * Created by cbadenes on 12/12/14.
 */
public abstract class Particle {

    protected MutableDouble2D position = new MutableDouble2D();
    protected MutableDouble2D velocity = new MutableDouble2D();

    protected ChemitaxisSim sim;
    protected String id;
    protected int intensity;

    protected Particle(double x, double y, double vx, double vy, ChemitaxisSim sim, String id, int intensity) {
        this.id = id;
        this.sim = sim;
        this.position.setTo(x, y);
        this.velocity.setTo(vx, vy);
        this.intensity = intensity;
        sim.space.setObjectLocation(this,new Double2D(position));
    }

    public abstract java.awt.Color getColor();

    public abstract void stepUpdateRadiation();

    public abstract void stepUpdateVelocity();

    public abstract void stepUpdatePosition();

    protected Double2D adjustToMaxVelocity(Double2D displacement){
        if ((Math.abs(displacement.getY()) < sim.getMaxVelocity())
                && (Math.abs(displacement.getX()) < sim.getMaxVelocity())) return displacement;

        double absX = Math.abs(displacement.x);
        double absY = Math.abs(displacement.y);

        double valueX = (displacement.x < 0)? -1 : 1;
        double valueY = (displacement.y < 0)? -1 : 1;

        if (absY >= absX){
            valueX *= (absX*sim.getMaxVelocity())/absY;
            valueY *= sim.getMaxVelocity();

        }else{
            valueY *= (absY*sim.getMaxVelocity())/absX;
            valueX *= sim.getMaxVelocity();
        }
        return new Double2D(valueX, valueY);
    }

    protected double distance (MutableDouble2D p1, MutableDouble2D p2){
        // Handle toroidal space
        return Math.sqrt(Math.pow( Math.min( Math.abs(p1.x - p2.x),  sim.space.width - Math.abs(p1.x - p2.x)), 2) +
                                    Math.pow( Math.min( Math.abs(p1.y - p2.y),  sim.space.height - Math.abs(p1.y - p2.y)), 2));

    }

}
