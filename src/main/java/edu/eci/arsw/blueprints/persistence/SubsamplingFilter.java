package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;

import edu.eci.arsw.blueprints.model.Point;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SubsamplingFilter implements BluePrintsFilter{
    @Override
    public Blueprint apply(Blueprint bp){
        List<Point> original = bp.getPoints();
        List<Point> filtered = new ArrayList<>();

        for (int i = 0; i < original.size(); i++) {
            if (i % 2 == 0) { // dejamos 1 de cada 2
                filtered.add(original.get(i));
            }
        }

        return new Blueprint(bp.getAuthor(), bp.getName(), filtered.toArray(new Point[0]));
    }
}
