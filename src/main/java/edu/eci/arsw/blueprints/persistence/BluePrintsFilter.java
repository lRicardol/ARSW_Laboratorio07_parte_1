package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;

public interface BluePrintsFilter {
    Blueprint apply(Blueprint bp);
}
