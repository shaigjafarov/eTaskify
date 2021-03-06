package az.etaskify.controller;

import az.etaskify.dto.UserDto;
import az.etaskify.model.Organization;
import az.etaskify.model.User;
import az.etaskify.service.OrganizationService;
import az.etaskify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private  final UserService userService;

    @PostMapping(value = "/save")
    public ResponseEntity<String> signUpOrganization(@Valid @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.saveOrUpdateUser(userDto), HttpStatus.CREATED) ;
    }


    @GetMapping(value = "/organization/users")
    public ResponseEntity<List<UserDto>> organizationUsers (){
        return new ResponseEntity<>( userService.organizationUsers(), HttpStatus.OK);
    }



}