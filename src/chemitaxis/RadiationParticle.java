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

import java.awt.*;
import java.util.Iterator;

/**
 * Created by cbadenes on 05/12/14.
 */
public class RadiationParticle extends Particle {

    public enum Mode{
        RADIATE, BLOCKED;
    }

    int attached = 0;
    Mode mode = Mode.RADIATE;

    protected RadiationParticle(ChemitaxisSim sim, String id) {
        super(
                sim.space.stx((sim.random.nextDouble() * sim.width) - (sim.width * 0.5)),
                sim.space.sty((sim.random.nextDouble() * sim.height) - (sim.height * 0.5)),
                0,
                0,
                sim,
                id,
                sim.getRadiationIntensity(),    // intensity
                0.15                            // response rate
        );
        sim.area.setObjectLocation(this,new Double2D(position));
    }

    @Override
    public Color getColor() {
//        if (this.mode.equals(Mode.BLOCKED)) return Color.white;
        return Color.red;
    }


    @Override
    public void stepUpdateVelocity(){
        if (mode.equals(Mode.BLOCKED)){
            // Join to others radioactive particles without radiation
            attached = 0;
            MutableDouble2D displacement = new MutableDouble2D(0.0,0.0);

            Bag neighbors = sim.area.getNeighborsExactlyWithinDistance(new Double2D(position), sim.getJoiningRadius());
            if (neighbors.size() > 1){
                Iterator iterator = neighbors.iterator();
                while(iterator.hasNext()){
                    Particle particle = (Particle) iterator.next();
                    if ((particle.id.equals(this.id))
                            || (particle instanceof InsulationParticle)
                            || ((RadiationParticle)particle).mode.equals(Mode.RADIATE)) continue;
                    attached++;
                    if (distance(particle.position, this.position) <= sim.particleWidth) continue;

                    Double2D force = calculateDisplacementBy(particle.position, particle.getForce());
                    displacement.addIn(force);
                }
            }
            MutableDouble2D limitedVelocity = limitToMaxVelocity(displacement, sim.getMaxVelocity()*responseRate);
            this.velocity.setTo(limitedVelocity);
        }
    }

    public void stepUpdatePosition(){
        if (velocity.length() > 0 ){

            Double2D startingPoint =  new Double2D(this.position);

            // Move
            position.addIn(velocity);

            // Adjust to toroidal space
            this.position.x = sim.space.stx(position.x);
            this.position.y = sim.space.sty(position.y);

            // Check neighbours
            Bag neighbours = sim.space.getNeighborsExactlyWithinDistance(new Double2D(this.position), sim.particleWidth, true);
            if ((neighbours.size() > 0)) {
                Iterator iterator = neighbours.iterator();
                while(iterator.hasNext()){
                    Particle particle = (Particle) iterator.next();
                    if (particle.id.equals(this.id)) continue;
                    if ((particle instanceof RadiationParticle)){
                        // Maybe creation of isolated groups of radioactive particles
                        this.position.setTo(startingPoint);
                        this.velocity.setTo(new Double2D(0.0,0.0));
                        return;
                    }else if (particle instanceof InsulationParticle){
                        // Generate repulsive force to neighbours
                        particle.velocity.addIn(this.velocity);
                    }
                }
            }
        }

        sim.space.setObjectLocation(this, new Double2D(position));
        sim.area.setObjectLocation(this,new Double2D(position));
    }


    @Override
    public void stepUpdateRadiation() {
        if (this.mode.equals(Mode.RADIATE)){
            // Update radiation based on neighbours
            this.intensity = sim.getRadiationIntensity();
            Bag neighbors = sim.space.getNeighborsExactlyWithinDistance(new Double2D(position), sim.getRadiationRadius(),true);
            Iterator iterator = neighbors.iterator();
            while(iterator.hasNext()){
                Particle particle = (Particle) iterator.next();
                if (particle.id.equals(this.id)) continue;
                if (particle instanceof InsulationParticle){
                    InsulationParticle insulating = (InsulationParticle) particle;
                    this.intensity -= ((insulating.source != null) && (insulating.source.id.equals(this.id)))?  Math.abs(particle.intensity) : 0 ;
                }
            }
            if (this.intensity <= 0){
                // Change to BLOCKED Mode
                this.mode = Mode.BLOCKED;
            }
        }
    }


}
