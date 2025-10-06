package edu.eci.arsw.blueprints.test.services;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BluePrintsFilter;
import edu.eci.arsw.blueprints.persistence.RedundancyFilter;
import org.junit.Assert;
import org.junit.Test;

public class RedundancyFilterTest {

    @Test
    public void testFilterRemovesRedundantPoints() {
        BluePrintsFilter filter = new RedundancyFilter();

        Point[] pts = new Point[]{
                new Point(10, 10),
                new Point(10, 10),
                new Point(20, 20)
        };

        Blueprint bp = new Blueprint("Andres", "plano1", pts);
        Blueprint filtered = filter.apply(bp);

        Assert.assertEquals(2, filtered.getPoints().size());
    }
}
