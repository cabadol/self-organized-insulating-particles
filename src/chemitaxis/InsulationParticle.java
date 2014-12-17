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

import sim.util.gui.SimpleColorMap;

import java.awt.*;

/**
 * Created by cbadenes on 12/12/14.
 */
public class InsulationParticle extends Particle {

    final SimpleColorMap map = new SimpleColorMap(
            0,
            sim.getInsulatingIntensity(),
            Color.white,
            Color.blue);

    protected InsulationParticle(ChemitaxisSim sim, int id) {
        super(
                sim.space.stx((sim.random.nextDouble() * sim.width) - (sim.width * 0.5)),
                sim.space.sty((sim.random.nextDouble() * sim.height) - (sim.height * 0.5)),
                (sim.random.nextDouble() % sim.getMaxVelocity()),
                (sim.random.nextDouble() % sim.
                        getMaxVelocity()),
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
    public void stepUpdateRadiation() {

    }

    @Override
    public void stepUpdateVelocity(){

    }
}
