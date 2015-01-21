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

package radiation.agent;

import radiation.ChemitaxisSim;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

import java.awt.*;
import java.util.Iterator;

/**
 * Created by cbadenes on 12/12/14.
 */
public class RadioControlParticle extends Particle {

    protected RadioActiveParticle source;

    public RadioControlParticle(ChemitaxisSim sim, String id) {
        super(
                sim.space.stx((sim.random.nextDouble() * sim.width) - (sim.width * 0.5)),
                sim.space.sty((sim.random.nextDouble() * sim.height) - (sim.height * 0.5)),
                (sim.random.nextDouble() % sim.getMaxVelocity()),
                (sim.random.nextDouble() % sim.getMaxVelocity()),
                sim,
                id,
                -1,  // intensity
                1.0   // lambda response rate
        );
    }

    @Override
    public Color getColor() {
        // Attached Mode
//        if (this.source != null) return Color.green;
        // Exploration Mode
        return Color.blue;
    }

    @Override
    public void stepUpdateVelocity(){
        MutableDouble2D displacement = new MutableDouble2D(0.0,0.0);

        // Attached Force
        if (this.source != null){
            Double2D force = calculateDisplacementBy(this.source.position, 1);
            displacement.addIn(force);
        }

        Bag neighbors = sim.space.getNeighborsExactlyWithinDistance(new Double2D(position), sim.radiationRadius);
        if (neighbors.size() > 1){
            Iterator iterator = neighbors.iterator();
            while(iterator.hasNext()){
                Particle particle = (Particle) iterator.next();

                if (particle.id.equals(this.id)) continue;

                if ((this.source != null) && (this.source.id.equals(particle.id))) continue;

                if (particle instanceof RadioActiveParticle){
                    attach((RadioActiveParticle) particle);
                }
                double force = this.getForce() + particle.getForce();
                Double2D partialDisplacement = calculateDisplacementBy(particle.position, force);
                displacement.addIn(partialDisplacement);
            }
        }
        if (displacement.length() == 0) {
            // random movement
            MutableDouble2D endpoint = new MutableDouble2D();
            MutableDouble2D movement = new MutableDouble2D();
            do{
                endpoint.setTo(this.position);
                movement.setTo(randomMovement());
                endpoint.addIn(movement);
            }while(!moveFrom(endpoint, sim.getMaxVelocity()));
            displacement.addIn(movement);

        }


        MutableDouble2D partialVelocity = limitToMaxVelocity(displacement, sim.getMaxVelocity()*responseRate);
        this.velocity.addIn(partialVelocity);
    }




    @Override
    public void stepUpdatePosition(){

        MutableDouble2D startingPoint = this.position.dup();

        if (velocity.length() > 0 ){

            // Move
            position.addIn(velocity);

            // Adjust to toroidal space
            this.position.x = sim.space.stx(position.x);
            this.position.y = sim.space.sty(position.y);

            // Avoid collision moving backward
            Bag neighbours = sim.space.getNeighborsExactlyWithinDistance(new Double2D(this.position), sim.agentRadius, true);
            Double2D backward = new Double2D(-this.velocity.x/10,-this.velocity.y/10);
            while (neighbours.size() > 0){
                this.position.addIn(backward);
                if (neighbours.contains(this) && neighbours.size()==1) break;
                neighbours = sim.space.getNeighborsExactlyWithinDistance(new Double2D(this.position), sim.agentRadius, true);
            }
        }

        // Maintain a maximum distance to radioactive particle:: Gas Particle Model
        double distance = (this.source != null)? distance(this.position, this.source.position) : 0.0 ;
        if ((distance > sim.radiationRadius)
                && (distance > sim.radiationRadius*this.source.attached)
                && (distance < (this.source.attached*sim.radiationRadius+sim.getMaxVelocity()))
                && (source.velocity.length() <= 0.0)
                ){
            // undo movement
            this.position = startingPoint;
            // random movement
            this.velocity = randomMovement();
            // evaluate
            stepUpdatePosition();
            return;
        }

        sim.space.setObjectLocation(this, new Double2D(position));
        this.lastMovements.add(new Double2D(this.position));
        this.velocity.setTo(0.0,0.0);
    }


    @Override
    public void stepUpdateRadiation() {
        // maintain intensity constant
    }

    private synchronized void attach(RadioActiveParticle particle){
        // Attach to radioactive particles within the radiation radius
        if ((distance(particle.position, this.position) < sim.radiationRadius)){
            this.source = particle;
        }
    }
}
