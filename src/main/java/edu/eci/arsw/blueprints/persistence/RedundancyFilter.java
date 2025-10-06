package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class RedundancyFilter implements BluePrintsFilter{
    @Override
    public Blueprint apply(Blueprint bp) {
        List<Point> original = bp.getPoints();
        List<Point> filtered = new ArrayList<>();

        Point prev = null;
        for (Point p : original) {
            if (prev == null || !p.equals(prev)) {
                filtered.add(p);
            }
            prev = p;
        }

        return new Blueprint(bp.getAuthor(), bp.getName(), filtered.toArray(new Point[0]));
    }
}
