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

import sim.util.Double2D;
import sim.util.MutableDouble2D;

/**
 * Created by cbadenes on 12/12/14.
 */
public abstract class Particle {

    protected MutableDouble2D position = new MutableDouble2D();
    protected MutableDouble2D velocity = new MutableDouble2D();

    protected int id;
    protected ChemitaxisSim sim;
    protected int intensity;

    protected Particle(double x, double y, double vx, double vy, ChemitaxisSim sim, int id, int intensity) {
        this.id = id;
        this.sim = sim;
        this.position.setTo(x, y);
        this.velocity.setTo(vx, vy);
        this.intensity = intensity;
        sim.space.setObjectLocation(this,new Double2D(position));
    }

    public abstract java.awt.Color getColor();

    public abstract void stepUpdateRadiation();

    public void stepUpdateVelocity(){
        double x = position.x;
        double y = position.y;
    }

    public void stepUpdatePosition(){
        if (velocity.length() > 0){

            position.addIn(velocity);

            // Toroidal space
            position.x = sim.space.stx(position.x);
            position.y = sim.space.sty(position.y);

            sim.space.setObjectLocation(this, new Double2D(position));

        }
    }


}
