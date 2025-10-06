/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.services;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BluePrintsFilter;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hcadavid
 */
@Service
public class BlueprintsServices {
   
    @Autowired
    BlueprintsPersistence bpp=null;

    @Autowired
    BluePrintsFilter bpf;

    public void addNewBlueprint(Blueprint bp) throws Exception {
        bpp.saveBlueprint(bp);
    }

    public Set<Blueprint> getAllBlueprints(){
        Set<Blueprint> blueprints = bpp.getAllBlueprints();
        return blueprints.stream()
                .map(bpf::apply)
                .collect(java.util.stream.Collectors.toSet());
    }
    
    /**
     * 
     * @param author blueprint's author
     * @param name blueprint's name
     * @return the blueprint of the given name created by the given author
     * @throws BlueprintNotFoundException if there is no such blueprint
     */
    public Blueprint getBlueprint(String author,String name) throws BlueprintNotFoundException{
        Blueprint bp = bpp.getBlueprint(author, name);
        return bpf.apply(bp);
    }

    /**
     * 
     * @param author blueprint's author
     * @return all the blueprints of the given author
     * @throws BlueprintNotFoundException if the given author doesn't exist
     */
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException{
        Set<Blueprint> bps = bpp.getBlueprintsByAuthor(author);
        Set<Blueprint> filtered = new HashSet<>();
        for (Blueprint bp : bps) {
            filtered.add(bpf.apply(bp));
        }
        return filtered;
    }

    public void updateBlueprint(String author, String name, Blueprint updatedBp) throws BlueprintNotFoundException {
        bpp.updateBlueprint(author, name, updatedBp);
    }

}
