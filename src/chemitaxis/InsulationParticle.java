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
import sim.util.gui.SimpleColorMap;

import java.awt.*;
import java.util.Iterator;

/**
 * Created by cbadenes on 12/12/14.
 */
public class InsulationParticle extends Particle {

    protected RadiationParticle source;
    protected int sourceRadiation = 0;


    final SimpleColorMap map = new SimpleColorMap(
            0,
            1,
            Color.green,
            Color.blue);

    protected InsulationParticle(ChemitaxisSim sim, String id) {
        super(
                sim.space.stx((sim.random.nextDouble() * sim.width) - (sim.width * 0.5)),
                sim.space.sty((sim.random.nextDouble() * sim.height) - (sim.height * 0.5)),
                (sim.random.nextDouble() % sim.getMaxVelocity()),
                (sim.random.nextDouble() % sim.getMaxVelocity()),
                sim,
                id,
                1
        );
    }

    @Override
    public Color getColor() {
        return map.getColor(intensity);
    }

    @Override
    public void stepUpdateVelocity(){
        double displacementX = 0.0;
        double displacementY = 0.0;
        double x1 = this.position.x;
        double y1 = this.position.y;
        Bag neighbors = sim.space.getNeighborsExactlyWithinDistance(new Double2D(position), sim.getRadiationRadius());
        if (neighbors.size() > 1){
            Iterator iterator = neighbors.iterator();
            while(iterator.hasNext()){
                Particle particle = (Particle) iterator.next();
                if (particle.id.equals(this.id)) continue;
                // Force
                double distance = distance(this.position, particle.position);
                double force = 1 / (distance*2); // inverse to distance
                // Neighbour Particle
                double x2 = particle.position.x;
                double y2 = particle.position.y;
                // Distance
                double partialX = Math.abs(x2 - x1);
                double partialY = Math.abs(y2 - y1);
                // Orientation
                if (particle instanceof RadiationParticle){
                    // Attractive Force
//                    force *= (particle.intensity)*2;
                    int attraction = this.sourceRadiation + particle.intensity;
                    force *= attraction*2;

                } else if ((particle instanceof InsulationParticle) && (!particle.id.equals(this.id))){
                    // Repulsive Force
                    force *= -0.5;
                }
                // X-Axis
                if (x2 > x1){
                    displacementX += force * (partialX/partialY);
                } else if (x2 < x1){
                    displacementX -= force * (partialX/partialY);
                }
                // Y-Axis
                if (y2 > y1){
                    displacementY += force * (partialY/partialX);
                } else if (y2 < y1){
                    displacementY -= force * (partialY/partialX);
                }
            }
        }

        if (displacementX == 0.0 && displacementY == 0.0) {
            displacementX = (sim.random.nextDouble() * sim.width) - (sim.width * 0.5);
            displacementY = (sim.random.nextDouble() * sim.height) - (sim.height * 0.5);
        }

        Double2D partialVelocity = adjustToMaxVelocity(new Double2D(displacementX, displacementY));
        this.velocity.setTo(partialVelocity);
    }



    @Override
    public void stepUpdatePosition(){

        if (velocity.length() > 0 ){

            MutableDouble2D startingPoint = (MutableDouble2D) this.position.clone();

            // Move
            position.addIn(velocity);

            // Adjust to toroidal space
            this.position.x = sim.space.stx(position.x);
            this.position.y = sim.space.sty(position.y);

            // Check neighbours
            Bag neighbours = sim.space.getNeighborsExactlyWithinDistance(new Double2D(this.position), sim.particleWidth, true);
            Double2D backward = new Double2D(-this.velocity.x/10,-this.velocity.y/10);
            while (neighbours.size() > 0) {
                this.position.addIn(backward);
                if (neighbours.contains(this) && neighbours.size()==1) break;
                neighbours = sim.space.getNeighborsExactlyWithinDistance(new Double2D(this.position), sim.particleWidth, true);
            }
        }


//        // Maintain a maximum distance to radioactive particle
//        if ((source != null) && ( distance(this.position, this.source.position) > sim.getRadiationRadius())){
//            double backwardX = this.velocity.x/10;
//            double backwardY = this.velocity.y/10;
//
//            do{
//                this.velocity.setX(this.velocity.getX()-backwardX);
//                this.velocity.setY(this.velocity.getY()-backwardY);
//
//                super.stepUpdatePosition();
//
//            }while(  distance(this.position, this.source.position) > sim.getRadiationRadius());
//
//            sim.space.setObjectLocation(this, new Double2D(position));
//        }

        sim.space.setObjectLocation(this, new Double2D(position));
    }


    @Override
    public void stepUpdateRadiation() {

    }

    public void radiate (RadiationParticle particle){
        if (this.intensity > 0){
            this.source = particle;
            this.sourceRadiation = particle.intensity;
            particle.intensity -= this.intensity;
            this.intensity = 0;
        }
    }
}
