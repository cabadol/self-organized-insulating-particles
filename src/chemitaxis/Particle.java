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

    public enum State{
        FREE, INSULATE, ATTACHED;
    }

    protected MutableDouble2D position = new MutableDouble2D();
    protected MutableDouble2D velocity = new MutableDouble2D();

    protected ChemitaxisSim sim;
    protected String id;
    protected int intensity;
    protected Force force;
    protected State status;

    protected Particle(double x, double y, double vx, double vy, ChemitaxisSim sim, String id, int intensity) {
        this.id = id;
        this.sim = sim;
        this.position.setTo(x, y);
        this.velocity.setTo(vx, vy);
        this.intensity = intensity;
        this.status = State.FREE;
        this.force = new Force(0,id,0.0,0.0);
        sim.space.setObjectLocation(this,new Double2D(position));
    }

    public abstract java.awt.Color getColor();

    public abstract void stepUpdateRadiation();

    public abstract void stepUpdateVelocity();

    public abstract void stepUpdateForce();

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
                this.position.setTo(startingPoint);
                return;
            }
        }

        sim.space.setObjectLocation(this, new Double2D(position));
    }

    private void avoidCollision(Double2D startingPoint){

        // Check neighbours
        Bag neighbours = sim.space.getNeighborsExactlyWithinDistance(new Double2D(this.position), sim.particleWidth, true);
        if ((neighbours.size() == 0)) return;

        double displacementX = 0.0;
        double displacementY = 0.0;

        Iterator iterator = neighbours.iterator();
        double x1 = startingPoint.x;
        double y1 = startingPoint.y;
        double x2 = this.position.x;
        double y2 = this.position.y;

        while(iterator.hasNext()){
            double newX;
            double newY;
            Particle neighbour = (Particle) iterator.next();
            if (neighbour.id.equals(this.id)) continue;

            // X-Axis
            double displacement = sim.particleWidth - Math.abs(this.position.x - neighbour.position.x);
            if (velocity.x > 0){
                newX = position.x - displacement;
            } else if (velocity.x < 0) {
                newX = position.x + displacement;
            } else{
                newX = position.x;
            }

            //Y-Axis
            newY = ((y2-y1)/(x2-x1))*(newX-x1)+y1;

            displacementX += newX - this.position.x;
            displacementY += newY - this.position.y;
        }
        if (displacementX == 0.0 && displacementY == 0.0) {
            // not move
            return;
        }

        this.velocity.setX(displacementX);
        this.velocity.setY(displacementY);
//        move();
    }

    protected void calculateForce (){
        Bag neighbors = sim.space.getNeighborsExactlyWithinDistance(new Double2D(position), 1, true);
        Iterator iterator = neighbors.iterator();
        while(iterator.hasNext()){
            Object neighbor = iterator.next();
            if (neighbor instanceof Particle){
                Particle particle = (Particle) neighbor;
                if ((this.force.source.equals(particle.force.source))
                        &(this.force.getIntensity() <  particle.force.getIntensity())){
                    this.force = particle.force;
                }
            }
        }

    }


}
