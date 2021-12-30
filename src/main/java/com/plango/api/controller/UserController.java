package com.plango.api.controller;

import com.plango.api.dto.user.UserDto;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.dto.travel.UserTravelsDto;
import com.plango.api.dto.user.UserUpdateDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RequestMapping("/user")
public interface UserController {
    /***
     * Retourne les informations de l'utilisateur courant
     *
     * @return ResponseEntity<UserDto> Les informations de l'utilisateur courant
     */
    @ApiOperation(value = "Retourne les informations de l'utilisateur courant.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = UserDto.class,
                    message = "Les informations de l'utilisateur courant ont bien été trouvées et renvoyées"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas pu être identifié")})
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserDto> getCurrentUser() throws UserNotFoundException;

    /***
     * Créer un utilisateur avec les informations indiquées
     *
     * @return String La confirmation que l'utilisateur a bien été créé
     */
    @ApiOperation(value = "Créer un utilisateur avec les informations indiquées.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "L'utilisateur a bien été créé"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 409, message = "Un utilisateur avec ce pseudo et/ou email existe déjà.")})
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> createUser(@RequestBody UserDto userDto);

    /***
     * Met à jour les informations d'un utilisateur
     *
     * @return String La confirmation que les informations de l'utilisateur ont bien été mises à jour
     */
    @ApiOperation(value = "Met à jour les informations d'un utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Les informations de l'utilisateur ont bien été mises à jour"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas les droits pour mettre à jour cet utilisateur.")})
    @PutMapping(path = "", consumes="application/json")
    ResponseEntity<String> updateCurrentUser(@RequestBody UserUpdateDto userUpdateDto);

    /***
     * Supprime l'utilisateur courant
     *
     * @return String La confirmation que l'utilisateur courant a bien été supprimé
     */
    @ApiOperation(value = "Supprime l'utilisateur courant.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "L'utilisateur courant a bien été supprimé'"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas pu être identifié.")})
    @DeleteMapping(path = "")
    ResponseEntity<String> deleteCurrentUser();

    /***
     * Retourne la liste des voyages de l'utilisateur courant
     *
     * @return ResponseEntity<UserTravelDto> La liste des voyages de l'utilisateur courant
     */
    @ApiOperation(value = "Retourne la liste des voyages de l'utilisateur courant.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = UserTravelsDto.class,
                    message = "La liste des voyages de l'utilisateur courant bien été trouvée et renvoyée"),
            @ApiResponse(code = 401, message = "Pas de token d'identification valide fourni"),
            @ApiResponse(code = 403, message = "L'utilisateur courant n'a pas pu être identifié")})
    @GetMapping(path = "/travels", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserTravelsDto> getTravelsOfCurrentUser();
}
