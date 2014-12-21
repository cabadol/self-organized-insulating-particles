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

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Network;

/**
 * Created by cbadenes on 05/12/14.
 */
public class ChemitaxisSim extends SimState {

    public double width             = 2.0;
    public double height            = 2.0;
    public double particleWidth     = 0.06; // 75% width

    public Continuous2D space;

    private RadiationParticle[] radiationParticles;
    private InsulationParticle[] insulationParticles;

    private int numRadioctiveParticles = 1;
    private int numInsulationParticles = 1;

    private int radiationIntensity  = 2;
    private double radiationRadius  = 0.5;
    private int insulatingIntensity = 1;
    private double maxVelocity      = 0.2;


    // Properties
    public double getRadiationRadius() {
        return radiationRadius;
    }
    public void setRadiationRadius(double radiationRadius) {
        this.radiationRadius = radiationRadius;
    }
    public double getMaxVelocity() {
        return maxVelocity;
    }
    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }
    public int getNumRadioctiveParticles() {
        return numRadioctiveParticles;
    }
    public void setNumRadioctiveParticles(int numRadioctiveParticles) {
        this.numRadioctiveParticles = numRadioctiveParticles;
    }
    public int getNumInsulationParticles() {
        return numInsulationParticles;
    }
    public void setNumInsulationParticles(int numInsulationParticles) {
        this.numInsulationParticles = numInsulationParticles;
    }
    public int getInsulatingIntensity() {
        return insulatingIntensity;
    }
    public void setInsulatingIntensity(int insulatingIntensity) {
        this.insulatingIntensity = insulatingIntensity;
    }
    public int getRadiationIntensity() {
        return radiationIntensity;
    }
    public void setRadiationIntensity(int radiationIntensity) {
        this.radiationIntensity = radiationIntensity;
    }

    public ChemitaxisSim(long seed) {
        super(seed);
    }

    private Particle initializeParticle(Particle particle){
        schedule.scheduleRepeating(Schedule.EPOCH, 1, new Steppable() {
            public void step(SimState state) {
                particle.stepUpdateForce();
            }
        });

        schedule.scheduleRepeating(Schedule.EPOCH, 2, new Steppable() {
            public void step(SimState state) {
                particle.stepUpdateVelocity();
            }
        });

        schedule.scheduleRepeating(Schedule.EPOCH, 3, new Steppable() {
            public void step(SimState state) {
                particle.stepUpdatePosition();
            }
        });

        schedule.scheduleRepeating(Schedule.EPOCH, 4, new Steppable() {
            public void step(SimState state) {
                particle.stepUpdateRadiation();
            }
        });
        return particle;
    }

    public void start() {
        super.start();
        space = new Continuous2D(particleWidth, width, height);

        radiationParticles = new RadiationParticle[numRadioctiveParticles];
        for (int i = 0; i < numRadioctiveParticles; i++) {
            radiationParticles[i] = (RadiationParticle) initializeParticle(new RadiationParticle(this, "r-"+i));
        }

        insulationParticles = new InsulationParticle[numInsulationParticles];
        for (int i = 0; i < numInsulationParticles; i++) {
            insulationParticles[i] = (InsulationParticle) initializeParticle(new InsulationParticle(this, "i-"+i));
        }



        // Function to stop the process
//        schedule.scheduleRepeating(Schedule.EPOCH, 4, new Steppable() {
//            public void step(SimState state) {
//                int count = 0;
//                for (int i = 0; i < space.allObjects.numObjs; i++) {
//                    Object o = space.allObjects.get(i);
//
//                    if (o instanceof Particle) {
//                        Particle p = (Particle) o;
//                        if (Math.abs(p.getFitness() - FUNCTION_SCALE -
//                                functionProperties[_fFunction]
//                                        [FUNCTION_GLOBALMIN])
//                                <= _fSuccessThreshold)
//                            count++;
//                    }
//                }
//                if (count == _fNumParticles)
//                    state.kill();
//            }
//        });

    }

    public static void main(String[] args) {
        doLoop(ChemitaxisSim.class, args);
        System.exit(0);
    }

}
