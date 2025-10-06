package edu.eci.arsw.blueprints.test.services;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BluePrintsFilter;
import edu.eci.arsw.blueprints.persistence.SubsamplingFilter;
import org.junit.Assert;
import org.junit.Test;

public class SubsamplingFilterTest {

    @Test
    public void testFilterSubsamplesPoints() {
        BluePrintsFilter filter = new SubsamplingFilter();

        Point[] pts = new Point[]{
                new Point(0, 0),
                new Point(10, 10),
                new Point(20, 20),
                new Point(30, 30)
        };

        Blueprint bp = new Blueprint("Maria", "plano2", pts);
        Blueprint filtered = filter.apply(bp);

        Assert.assertEquals(2, filtered.getPoints().size());
    }
}
