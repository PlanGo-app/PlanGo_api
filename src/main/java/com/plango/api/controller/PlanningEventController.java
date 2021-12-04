package com.plango.api.controller;

import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.dto.planningevent.UpdatePlanningEventDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/planning_event")
public interface PlanningEventController {
    /***
     * Recherche d'un planning event par son id
     *
     * @param id : id du planning event recherché
     * @return ResponseEntity<GetPlanningEventDto> le planning event correspondant à l'id
     */
    @ApiOperation(value = "Récupérer un planning event identifié par son id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = GetPlanningEventDto.class,
                    message = "Le planning event a bien été trouvé et renvoyé"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits d'accès au planning event"),
            @ApiResponse(code = 404, message = "Aucun planning event correspondant à cet id n'a pu être trouvé")})
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetPlanningEventDto> getPlanningEventById(@PathVariable Long id);

    /***
     * Met à jour un planning event
     *
     * @param updatePlanningEventDto : le planning event avec les informations mise à jour
     * @return String Confirmation de la mise à jour du planning event
     */
    @ApiOperation(value = "Met à jour un planning event.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le planning event a bien été mis à jour"),
            @ApiResponse(code = 400, message = "Les informations fournies pour mettre à jour le planning event ne sont pas valides"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour mettre à jour le planning event indiqué"),
            @ApiResponse(code = 404, message = "Aucun planning event avec l'id fourni n'a été trouvé")})
    @PutMapping(path = "", consumes="application/json")
    ResponseEntity<String> updatePlanningEvent(@RequestBody UpdatePlanningEventDto updatePlanningEventDto);
}
