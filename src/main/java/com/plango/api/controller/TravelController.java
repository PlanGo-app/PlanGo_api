package com.plango.api.controller;

import com.plango.api.dto.travel.CreateTravelDto;
import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.travel.TravelPinsDto;
import com.plango.api.dto.travel.TravelPlanningEventsDto;
import com.plango.api.dto.member.TravelMembersDto;
import com.plango.api.entity.Travel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/travel")
public interface TravelController {

    /***
     * Créer un voyage avec l'utilisateur courant comme Admin (ADMIN)
     *
     * @param newTravel : le voyage à créer
     * @return ResponseEntity<GetTravelDto> Le voyage créé
     */
    @ApiOperation(value = "Créer un voyage avec l'utilisateur courant comme Admin")
    @ApiResponses(value = {
            @ApiResponse(code = 201, response = GetTravelDto.class, message = "Le voyage a bien été créé"),
            @ApiResponse(code = 400, message = "Les informations données ne permettent pas de créer un voyage"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 404, message = "L'utilisateur courant n'a pas pu être identifié")})
    @PostMapping(path = "",
            consumes= MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetTravelDto> createTravel(@RequestBody CreateTravelDto newTravel);

    /***
     * Supprime un voyage à partir de son Id
     *
     * @param id : l'id du voyage à supprimer
     * @return ResponseEntity<String> Confirmation de la suppression du voyage
     */
    @ApiOperation(value = "Supprime un voyage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = GetTravelDto.class, message = "Le voyage a bien été créé"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour supprimer ce voyage'"),
            @ApiResponse(code = 404, message = "Le voyage à supprimer n'a pas pu être trouvé")})
    @DeleteMapping(path = "/{id}")
    ResponseEntity<String> deleteTravelById(@PathVariable Long id);

    /***
     * Ajoute un utilisateur (membre) à un voyage avec un status donné
     *
     * @param travelId : l'id du voyage auquel on souhaite ajouter un utilisateur
     * @param userId : l'id de l'utilisateur à ajouter au voyage
     * @param role : le rôle à donner à l'utilisateur pour le voyage
     * @return String Confirmation de l'ajout de l'utilisateur au voyage
     */
    @ApiOperation(value = "Ajoute un utilisateur (membre) à un voyage avec un status donné")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "L'utilisateur a bien été ajouté au voyage"),
            @ApiResponse(code = 400, message = "Les informations données ne permettent pas d'ajouter un utilisateur au voyage"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour ajouter un utilisateur à ce voyage'"),
            @ApiResponse(code = 404, message = "L'utilisateur à ajouter ou le voyage n'ont pas été trouvé"),
            @ApiResponse(code = 409, message = "L'utilisateur est déjà membre du voyage")})
    @PostMapping(path = "/{travelId}/member/{userId}")
    ResponseEntity<String> addMemberToTravel(@PathVariable Long travelId, @PathVariable Long userId, @RequestParam String role);

    /***
     * Met à jour le rôle d'un membre d'un voyage
     *
     * @param travelId : l'id du voyage où se trouve le membre
     * @param userId : l'id utilisateur du membre
     * @param role : le nouveau rôle à donner au membre
     * @return String Confirmation de la mise à jour du rôle du membre
     */
    @ApiOperation(value = "Met à jour le rôle d'un membre d'un voyage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le rôle du membre a bien été mis à jour pour le voyage"),
            @ApiResponse(code = 400, message = "Les informations données ne permettent pas de mettre à jour le rôle du membre"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour mettre à jour le rôle d'un membre de ce voyage'"),
            @ApiResponse(code = 404, message = "L'utilisateur membre à mettre à jour ou le voyage n'ont pas été trouvé")})
    @PutMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> updateMemberOfTravel(@PathVariable Long travelId, @PathVariable Long userId, @RequestParam String role);

    /***
     * Supprime un utilisateur (membre) d'un voyage
     *
     * @param travelId : l'id du voyage où se trouve le membre
     * @param userId : l'id utilisateur du membre
     * @return String Confirmation de la suppression du membre
     */
    @ApiOperation(value = "Supprime un utilisateur (membre) d'un voyage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'utilisateur a bien été supprimé de ce voyage"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour supprimer un membre de ce voyage'"),
            @ApiResponse(code = 404, message = "L'utilisateur membre à supprimer ou le voyage n'ont pas été trouvé")})
    @DeleteMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> deleteMemberOfTravel(@PathVariable Long travelId, @PathVariable Long userId);

    /***
     * Supprime l'utilisateur courant (membre) d'un voyage
     *
     * @param travelId : l'id du voyage où se trouve le membre
     * @return String Confirmation de la suppression de l'utilisateur courant du voyage
     */
    @ApiOperation(value = "Supprime l'utilisateur courant (membre) d'un voyage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'utilisateur courant a bien été supprimé de ce voyage"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 404, message = "Le voyage n'a pas été trouvé")})
    @DeleteMapping("/{travelId}/me")
    ResponseEntity<String> deleteCurrentUserOfTravel(@PathVariable Long travelId);

    /***
     * Renvoi la liste des membres d'un voyage
     *
     * @param travelId : l'id du voyage
     * @return ResponseEntity<TravelMembersDto> la liste des membres du voyage
     */
    @ApiOperation(value = "Renvoi la liste des membres d'un voyage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = TravelPinsDto.class,
                    message = "La liste des membres a bien été retournée"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour consulter la liste des membres de ce voyage'"),
            @ApiResponse(code = 404, message = "Au moins l'un des membres ou le voyage n'ont pas été trouvé")})
    @GetMapping(path = "/{travelId}/members", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TravelMembersDto> getTravelMembers(@PathVariable Long travelId);

    /***
     * Permet à un utilisateur de rejoindre un voyage (devenir membre), via un code d'invitation, avec le rôle d'ORGANISER
     *
     * @param code : le code d'invitation au voyage
     * @return ResponseEntity<GetTravelDto> le voyage que l'utilisateur à rejoint
     */
    @ApiOperation(value = "Permet à un utilisateur de rejoindre un voyage (devenir membre), via un code d'invitation, avec le rôle d'ORGANISER.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = GetTravelDto.class,
                    message = "L'utilisateur a bien rejoint le vayage"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas pu être identifié"),
            @ApiResponse(code = 404, message = "Le voyage n'a pas été trouvé"),
            @ApiResponse(code = 409, message = "L'utilisateur est déjà membre du voyage")})
    @PostMapping(path = "/invitation", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetTravelDto> addMemberToTravelWithInvitation(@RequestParam String code);

    /***
     * Renvoi la liste des planning events d'un voyage
     *
     * @param travelId : l'id du voyage
     * @return ResponseEntity<TravelPlanningEventsDto> la liste des planning events du voyage
     */
    @ApiOperation(value = "Renvoi la liste des planning events d'un voyage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = TravelPlanningEventsDto.class,
                    message = "La liste des planning events a bien été retournée"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour consulter la liste des planning events de ce voyage'"),
            @ApiResponse(code = 404, message = "Le voyage n'a pas été trouvé")})
    @GetMapping(path = "{travelId}/planningEvents", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TravelPlanningEventsDto> getTravelPlanningEvents(@PathVariable Long travelId);

    /***
     * Renvoi la liste des pins d'un voyage
     *
     * @param travelId : l'id du voyage
     * @return ResponseEntity<TravelPinsDto> la liste des pins du voyage
     */
    @ApiOperation(value = "Renvoi la liste des pins d'un voyage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Travel.class,
                    message = "La liste des pins a bien été retournée"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour consulter la liste des pins de ce voyage'"),
            @ApiResponse(code = 404, message = "Le voyage n'a pas été trouvé")})
    @GetMapping(path = "{travelId}/pins", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TravelPinsDto> getTravelPins(@PathVariable Long travelId);


}
