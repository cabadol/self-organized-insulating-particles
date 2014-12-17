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

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.gui.SimpleColorMap;

import javax.swing.*;
import java.awt.*;

/**
 * Created by cbadenes on 05/12/14.
 */
public class ChemitaxisSimWithUI extends GUIState {

    public Display2D display;
    public JFrame displayFrame;
    private ContinuousPortrayal2D swarmPortrayal = new ContinuousPortrayal2D();

    public ChemitaxisSimWithUI()
    {
        super(new ChemitaxisSim(System.currentTimeMillis()));
    }

    public ChemitaxisSimWithUI(SimState state) {
        super(state);
    }

    public static String getName()
    {
        return "Chemitaxis-based Self-Organization";
    }

    public Object getSimulationInspectedObject() {
        return state; // non-volatile
    }

    public void start()
    {
        super.start();
        setupPortrayals();
    }

    public void load(SimState state)
    {
        super.load(state);
        setupPortrayals();
    }

    public void init(Controller c) {
        super.init(c);

        // make the displayer
        display = new Display2D(750, 750, this);
        display.setBackdrop(Color.black);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Chemitaxis-based Self-Organization");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(swarmPortrayal, "Behold the Swarm!",
                (display.insideDisplay.width * 0.5),
                (display.insideDisplay.height * 0.5), true);
    }

    public void setupPortrayals() {
        ChemitaxisSim swarm = (ChemitaxisSim) state;

        swarmPortrayal.setField(swarm.space);

        for (int x = 0; x < swarm.space.allObjects.numObjs; x++) {
            Object o = swarm.space.allObjects.objs[x];
            if (o instanceof Particle) {
                final Particle p = (Particle) o;
                swarmPortrayal.setPortrayalForObject(
                        p,
                        new RectanglePortrayal2D(Color.green, 0.05) {
                            public void draw(Object object, Graphics2D graphics,DrawInfo2D info) {
                                paint = p.getColor();
                                super.draw(object, graphics, info);
                            }
                        });
            }
        }
        // update the size of the display appropriately.
        double w = swarm.space.getWidth();
        double h = swarm.space.getHeight();
        if (w == h)
        {
            display.insideDisplay.width = display.insideDisplay.height = 750;
        }
        else if (w > h)
        {
            display.insideDisplay.width = 750;
            display.insideDisplay.height = 750 * (h/w);
        }
        else if (w < h)
        {
            display.insideDisplay.height = 750;
            display.insideDisplay.width = 750 * (w/h);
        }

        // reschedule the displayer
        display.reset();

        // redraw the display
        display.repaint();
    }


    public void quit() {
        super.quit();

        if (displayFrame != null) displayFrame.dispose();
        displayFrame = null;
        display = null;
    }

    public static void main(String[] args)
    {
        new ChemitaxisSimWithUI().createController();  // randomizes by currentTimeMillis
    }
}
