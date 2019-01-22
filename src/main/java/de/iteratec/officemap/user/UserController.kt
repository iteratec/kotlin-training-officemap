package de.iteratec.officemap.user

import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api")
class UserController {

    @GetMapping("/user")
    @ApiOperation(value = "getUser", notes = "Returns the current logged-in user.")
    fun user(principal: Principal): UserDTO {
        return UserDTO(principal.name)
    }
}

data class UserDTO(val name: String)
