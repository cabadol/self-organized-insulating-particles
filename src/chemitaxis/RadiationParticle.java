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
            sim.getRadiationIntensity()*100,
            Color.white,
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
        return map.getColor(intensity <= 0? 0 : sim.getRadiationIntensity()*100);
    }


    @Override
    public void stepUpdateVelocity(){
//        if (this.intensity == 0){
//            // Join to others radioactive particles without radiation
//            double displacementX = 0.0;
//            double displacementY = 0.0;
//            double x1 = this.position.x;
//            double y1 = this.position.y;
//            Bag neighbors = sim.space.getNeighborsExactlyWithinDistance(new Double2D(position), sim.getRadiationRadius()*4);
//            if (neighbors.size() > 1){
//                Iterator iterator = neighbors.iterator();
//                while(iterator.hasNext()){
//                    Particle particle = (Particle) iterator.next();
//                    if ((particle.id.equals(this.id)) || (particle instanceof InsulationParticle)) continue;
//                    // Force
//                    double distance = this.position.distance(particle.position);
//                    double force = 1 / (distance*2); // inverse to distance
//                    // Neighbour Particle
//                    double x2 = particle.position.x;
//                    double y2 = particle.position.y;
//                    // Distance
//                    double partialX = Math.abs(x2 - x1);
//                    double partialY = Math.abs(y2 - y1);
//                    // Orientation
//                    if (particle.intensity == 0){
//                        // Attractive Force
//                        // X-Axis
//                        if (x2 > x1){
//                            displacementX += force * (partialX/partialY);
//                        } else if (x2 < x1){
//                            displacementX -= force * (partialX/partialY);
//                        }
//                        // Y-Axis
//                        if (y2 > y1){
//                            displacementY += force * (partialY/partialX);
//                        } else if (y2 < y1){
//                            displacementY -= force * (partialY/partialX);
//                        }
//                    }
//                }
//            }
//
//            if ((displacementX == 0) && (displacementY == 0)) {
//                displacementX = (sim.random.nextDouble() * sim.width) - (sim.width * 0.5);
//                displacementY = (sim.random.nextDouble() * sim.height) - (sim.height * 0.5);
//            }
//
//            Double2D partialVelocity = adjustToMaxVelocity(new Double2D(displacementX, displacementY));
//            this.velocity.setTo(partialVelocity);
//        }
    }

    @Override
    public void stepUpdateRadiation() {
        Bag neighbors = sim.space.getNeighborsExactlyWithinDistance(new Double2D(position), sim.getRadiationRadius());

        if (this.intensity <= 0) return;

        Iterator iterator = neighbors.iterator();
        while(iterator.hasNext()){
            Particle particle = (Particle) iterator.next();
            if (particle instanceof InsulationParticle){
                ((InsulationParticle) particle).radiate(this);
            }
        }
    }

}
