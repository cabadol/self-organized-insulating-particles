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
 * Created by cbadenes on 05/12/14.
 */
public class RadiationParticle extends Particle {

    private double intensity = 2.0;

    final SimpleColorMap map = new SimpleColorMap(
            200,
            1000,
            Color.red,
            Color.green);

    protected RadiationParticle(ChemitaxisSim sim, int id) {
        super(
//                (sim.random.nextDouble() * sim.width) - (sim.width * 0.5),
//                (sim.random.nextDouble() * sim.height) - (sim.height * 0.5),
                0,
                0,
                0,
                0,
                sim,
                id
        );
    }

    @Override
    public Color getColor() {
        return map.getColor(intensity);
    }

    @Override
    public void stepUpdateIntensity() {

    }

}
