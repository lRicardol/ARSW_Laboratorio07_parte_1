package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/blueprints")
public class BlueprintAPIController {

    private static final Logger logger = Logger.getLogger(BlueprintAPIController.class.getName());

    @Autowired
    private BlueprintsServices blueprintServices;

    /**
     * GET /blueprints
     * Devuelve todos los planos.
     */
    @GetMapping
    public ResponseEntity<Set<Blueprint>> getAllBlueprints() {
        Set<Blueprint> all = blueprintServices.getAllBlueprints();
        return new ResponseEntity<>(all, HttpStatus.ACCEPTED);
    }

    /**
     * GET /blueprints/{author}
     * Devuelve todos los planos de un autor.
     */
    @GetMapping("/{author}")
    public ResponseEntity<?> getBlueprintsByAuthor(@PathVariable String author) {
        try {
            Set<Blueprint> bps = blueprintServices.getBlueprintsByAuthor(author);
            return new ResponseEntity<>(bps, HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException ex) {
            logger.log(Level.WARNING, "Autor no encontrado: " + author, ex);
            return new ResponseEntity<>(
                    "Autor no encontrado: " + author,
                    HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * GET /blueprints/{author}/{bpname}
     * Devuelve un plano específico por autor y nombre.
     */
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<Blueprint> getBlueprintByAuthorAndName(
            @PathVariable String author,
            @PathVariable String bpname) {
        try {
            Blueprint bp = blueprintServices.getBlueprint(author, bpname);
            return new ResponseEntity<>(bp, HttpStatus.OK);
        } catch (BlueprintNotFoundException ex) {
            logger.log(Level.WARNING,
                    "Plano no encontrado: " + author + "/" + bpname, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST /blueprints
     * Registra un nuevo plano.
     */
    @PostMapping
    public ResponseEntity<?> addNewBlueprint(@RequestBody Blueprint bp) {
        try {
            blueprintServices.addNewBlueprint(bp);
            return new ResponseEntity<>(bp, HttpStatus.CREATED);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error al agregar el plano!", ex);
            return new ResponseEntity<>(
                    "No se pudo registrar el plano!",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    /**
     * PUT /blueprints/{author}/{bpname}
     * Actualiza un plano existente.
     */
    @PutMapping("/{author}/{bpname}")
    public ResponseEntity<?> updateBlueprint(
            @PathVariable String author,
            @PathVariable String bpname,
            @RequestBody Blueprint updatedBp) {
        try {
            blueprintServices.updateBlueprint(author, bpname, updatedBp);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BlueprintNotFoundException ex) {
            logger.log(Level.WARNING, "Plano no encontrado para actualizar: " + author + "/" + bpname, ex);
            return new ResponseEntity<>("Plano no encontrado", HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error al actualizar el plano!", ex);
            return new ResponseEntity<>("Error al actualizar el plano", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{author}/{bpname}")
    public ResponseEntity<?> deleteBlueprint(
            @PathVariable String author,
            @PathVariable String bpname) {
        try {
            blueprintServices.deleteBlueprint(author, bpname);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BlueprintNotFoundException ex) {
            return new ResponseEntity<>("Blueprint no encontrado", HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>("Error al eliminar el blueprint", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}