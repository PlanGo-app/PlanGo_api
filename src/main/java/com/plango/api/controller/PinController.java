package com.plango.api.controller;

import com.plango.api.dto.pin.CreatePinDto;
import com.plango.api.dto.pin.GetPinDto;
import com.plango.api.dto.pin.UpdatePinDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/pin")
public interface PinController {
    /***
     * Recherche d'un pin par son id
     *
     * @param id : id du pin recherché
     * @return ResponseEntity<GetPinDto> le pin correspondant à l'id
     */
    @ApiOperation(value = "Récupérer un pin identifié par son id.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = GetPinDto.class,
                    message = "Le pin a bien été trouvé et renvoyé"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits d'accès au pin"),
            @ApiResponse(code = 404, message = "Aucun pin correspondant à cet id n'a pu être trouvé")})
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetPinDto> getPinById(@PathVariable Long id);

    /***
     * Recherche d'un pin par son l'id du voyage et ses coordonnées
     *
     * @param travelId : id du voyage dans lequel se trouve le pin
     * @param longitude : longitude du pin
     * @param latitude : latitude du pin
     * @return ResponseEntity<GetPinDto> le pin de trouvant aux coordonnées indiquées du voyage
     */
    @ApiOperation(value = "Récupérer un pin identifié par l'id du voyage et de ses coordonnées.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = GetPinDto.class,
                    message = "Le pin a bien été trouvé et renvoyé"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits d'accès au pin"),
            @ApiResponse(code = 404, message = "Aucun pin à ces coordonnées n'a pu être trouvé pour ce voyage")})
    @GetMapping(path = "/travel/{travelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GetPinDto> getPinByCoordinates(@PathVariable Long travelId, @RequestParam Float longitude, @RequestParam Float latitude);

    /***
     * Créer un pin associé à un travel et à l'utilisateur courant
     *
     * @param pin : le pin à créer
     * @return String Confirmation de la création du pin
     */
    @ApiOperation(value = "Créer un pin associé à un travel et à l'utilisateur courant.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Le pin a bien été créé"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour créer un pin sur le voyage indiqué"),
            @ApiResponse(code = 404, message = "Le voyage n'a pas été trouvé"),
            @ApiResponse(code = 409, message = "Un pin existe déjà à ces coordonnées pour le voyage indiqué")})
    @PostMapping(path = "", consumes="application/json")
    ResponseEntity<String> createPin(@RequestBody CreatePinDto pin);

    /***
     * Met à jour un pin
     *
     * @param pin : le pin avec les informations mise à jour
     * @return String Confirmation de la mise à jour du pin
     */
    @ApiOperation(value = "Met à jour un pin.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le pin a bien été mis à jour"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour mettre à jour le pin indiqué"),
            @ApiResponse(code = 404, message = "Aucun pin avec l'id fourni n'a été trouvé")})
    @PutMapping(path = "", consumes="application/json")
    ResponseEntity<String> updatePin(@RequestBody UpdatePinDto pin);

    /***
     * Supprime un pin avec l'id indiqué
     *
     * @param id : l'id du pin à supprimer
     * @return String Confirmation de la suppression du pin
     */
    @ApiOperation(value = "Supprime un pin avec l'id indiqué.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Le pin a bien été supprimé"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour supprimer le pin indiqué"),
            @ApiResponse(code = 404, message = "Aucun pin avec l'id fourni n'a été trouvé")})
    @DeleteMapping(path = "/{id}")
    ResponseEntity<String> deletePinById(@PathVariable Long id);

    /***
     * Delete pin par l'id du voyage et ses coordonnées
     *
     * @param travelId : id du voyage dans lequel se trouve le pin
     * @param longitude : longitude du pin
     * @param latitude : latitude du pin
     * @return String Confirmation de la suppression du pin
     */
    @ApiOperation(value = "Récupérer un pin identifié par l'id du voyage et de ses coordonnées.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = GetPinDto.class,
                    message = "Le pin a bien été trouvé et supprimé"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits d'accès au pin"),
            @ApiResponse(code = 404, message = "Aucun pin à ces coordonnées n'a pu être trouvé pour ce voyage")})
    @DeleteMapping(path = "/travel/{travelId}")
    ResponseEntity<String> deletePinByCoordinates(@PathVariable Long travelId, @RequestParam Float longitude, @RequestParam Float latitude);


}
