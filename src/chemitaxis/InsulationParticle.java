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
import sim.util.gui.SimpleColorMap;

import java.awt.*;
import java.util.Iterator;

/**
 * Created by cbadenes on 12/12/14.
 */
public class InsulationParticle extends Particle {

    final SimpleColorMap map = new SimpleColorMap(
            0,
            sim.getInsulatingIntensity(),
            Color.white,
            Color.blue);

    protected InsulationParticle(ChemitaxisSim sim, String id) {
        super(
                sim.space.stx((sim.random.nextDouble() * sim.width) - (sim.width * 0.5)),
                sim.space.sty((sim.random.nextDouble() * sim.height) - (sim.height * 0.5)),
                (sim.random.nextDouble() % sim.getMaxVelocity()),
                (sim.random.nextDouble() % sim.getMaxVelocity()),
                sim,
                id,
                sim.getInsulatingIntensity()
        );
    }

    @Override
    public Color getColor() {
        return map.getColor(intensity);
    }

    @Override
    public void stepUpdateForce(){
        this.force.intensity = 0;
        this.force.source = id;
    }

    @Override
    public void stepUpdateVelocity(){
        this.velocity.setX(sim.random.nextDouble() % sim.getMaxVelocity());
        this.velocity.setY(sim.random.nextDouble() % sim.getMaxVelocity());

        Bag neighbors = sim.space.getNeighborsExactlyWithinDistance(new Double2D(position), sim.getRadiationRadius(), true);

        double minimumDistance = sim.getRadiationRadius();

        Iterator iterator = neighbors.iterator();
        while(iterator.hasNext()){
            Particle particle = (Particle) iterator.next();
            if (particle instanceof RadiationParticle){
                double distance = this.position.distance(particle.position);
                if (distance <= minimumDistance){
                    minimumDistance = distance;
                    this.velocity.setX(particle.position.x - this.position.x);
                    this.velocity.setY(particle.position.y - this.position.y);
                }
            }
        }
    }

    @Override
    public void stepUpdateRadiation() {

    }
}
