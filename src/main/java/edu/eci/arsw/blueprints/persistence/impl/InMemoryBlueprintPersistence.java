/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author hcadavid
 */
@Repository
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{

    private final Map<Tuple<String,String>,Blueprint> blueprints=new ConcurrentHashMap<>();

    public InMemoryBlueprintPersistence() {

        Point[] pts1 = new Point[]{new Point(140, 140), new Point(115, 115)};
        Blueprint bp1 = new Blueprint("Andres", "plano1", pts1);
        blueprints.put(new Tuple<>(bp1.getAuthor(), bp1.getName()), bp1);

        Point[] pts2 = new Point[]{new Point(50, 50), new Point(60, 60)};
        Blueprint bp2 = new Blueprint("Andres", "plano2", pts2);
        blueprints.put(new Tuple<>(bp2.getAuthor(), bp2.getName()), bp2);

        Point[] pts3 = new Point[]{new Point(200, 200), new Point(220, 220)};
        Blueprint bp3 = new Blueprint("Maria", "plano3", pts3);
        blueprints.put(new Tuple<>(bp3.getAuthor(), bp3.getName()), bp3);

        Point[] housePoints = new Point[]{
                new Point(100, 300),
                new Point(200, 300),
                new Point(200, 200),
                new Point(150, 150),
                new Point(100, 200),
                new Point(100, 300)
        };

        Blueprint house = new Blueprint("JohnConnor", "house", housePoints);
        blueprints.put(new Tuple<>(house.getAuthor(), house.getName()), house);
    }    
    
    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {


        if (blueprints.containsKey(new Tuple<>(bp.getAuthor(),bp.getName()))){
            throw new BlueprintPersistenceException("The given blueprint already exists: "+bp);
        }
        else{
            blueprints.putIfAbsent(new Tuple<>(bp.getAuthor(),bp.getName()), bp);
        }
    }

    @Override
    public Blueprint getBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        Blueprint bp = blueprints.get(new Tuple<>(author, bprintname));
        if (bp == null) {
            throw new BlueprintNotFoundException("Blueprint not found: " + author + " - " + bprintname);
        }
        return bp;
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> result = new HashSet<>();
        for (Tuple<String,String> key : blueprints.keySet()) {
            if (key.getElem1().equals(author)) {
                result.add(blueprints.get(key));
            }
        }
        if (result.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints found for author: " + author);
        }
        return result;
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(blueprints.values());
    }

    @Override
    public void updateBlueprint(String author, String bprintname, Blueprint updatedBp) throws BlueprintNotFoundException {
        Tuple<String, String> key = new Tuple<>(author, bprintname);
        if (!blueprints.containsKey(key)) {
            throw new BlueprintNotFoundException("Plano no encontrado: " + author + " - " + bprintname);
        }


        Blueprint newBp = new Blueprint(author, bprintname, updatedBp.getPoints().toArray(new Point[0]));
        blueprints.put(key, newBp);
    }

    @Override
    public void deleteBlueprint(String author, String bprintname) throws BlueprintNotFoundException {
        Tuple<String, String> key = new Tuple<>(author, bprintname);
        if (!blueprints.containsKey(key)) {
            throw new BlueprintNotFoundException("No existe el plano para eliminar: " + author + "/" + bprintname);
        }
        blueprints.remove(key);
    }


}
