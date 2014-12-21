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
 * Created by cbadenes on 05/12/14.
 */
public class RadiationParticle extends Particle {

    final SimpleColorMap map = new SimpleColorMap(
            0,
            sim.getRadiationIntensity(),
            Color.green,
            Color.red);

    protected RadiationParticle(ChemitaxisSim sim, String id) {
        super(
                sim.space.stx((sim.random.nextDouble() * sim.width) - (sim.width * 0.5)),
                sim.space.sty((sim.random.nextDouble() * sim.height) - (sim.height * 0.5)),
                0,
                0,
                sim,
                id,
                sim.getRadiationIntensity()
        );
    }

    @Override
    public Color getColor() {
        return map.getColor(intensity < 0? 0 : intensity);
    }

    @Override
    public void stepUpdateForce(){
        this.force.source = this.id;
        this.force.intensity = 0;
    }

    @Override
    public void stepUpdateVelocity(){
        calculateForce();
        velocity.setY(force.getDesplacementOverY());
        velocity.setX(force.getDesplacementOverX());

    }

    @Override
    public void stepUpdateRadiation() {
        Bag neighbors = sim.space.getNeighborsExactlyWithinDistance(new Double2D(position), sim.getRadiationRadius());

        int insulatingLevel = 0;
        Iterator iterator = neighbors.iterator();
        while(iterator.hasNext()){
            Particle particle = (Particle) iterator.next();
            if (particle instanceof InsulationParticle){
                insulatingLevel += ((InsulationParticle) particle).intensity;
            }
        }
        
        intensity = sim.getRadiationIntensity() - insulatingLevel;
    }

}
