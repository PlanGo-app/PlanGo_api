package com.plango.api.controller;

import com.plango.api.dto.authentication.AuthDto;
import com.plango.api.dto.authentication.CredentialDto;
import com.plango.api.dto.user.UserDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
public interface AuthController {

    /***
     * Identification de l'utilisateur
     *
     * @param credentials les informations permettant à l'utilisateur de se connecter
     * @return ResponseEntity<AuthDto> les informations d'identification de l'utilisateur
     */
    @ApiOperation(value = "Identification de l'utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = AuthDto.class,
                    message = "L'utilisateur a bien réussi à se connecter"),
            @ApiResponse(code = 401, message = "Les informations fournies ne permettent pas d'identifier l'utilisateur")})
    @PostMapping("/login")
    ResponseEntity<AuthDto> login(@RequestBody CredentialDto credentials);

    /***
     * Création d'un compte utilisateur
     *
     * @param userDto les informations permettant de créer un utilisateur
     * @return ResponseEntity<AuthDto> les informations d'identification de l'utilisateur
     */
    @ApiOperation(value = "Création d'un compte utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, response = AuthDto.class,
                    message = "L'utilisateur a bien réussi à se créer un compte et est connecté"),
            @ApiResponse(code = 400, message = "Les informations fournies ne permettent pas de créer un compte utilisateur"),
            @ApiResponse(code = 409, message = "Un compte utilisateur avec ce pseudo et/ou cet email existe déjà")})
    @PostMapping("/signup")
    ResponseEntity<AuthDto> signup(@RequestBody UserDto userDto);
}
