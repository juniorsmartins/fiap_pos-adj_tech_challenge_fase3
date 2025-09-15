package fiap.adj.fase3.tech_challenge_hospital.infrastructure.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityEndpoint {

    @GetMapping("/admin")
    public String adminPage() {
        return "Rota Admin";
    }

    @GetMapping("/user")
    public String userPage() {
        return "Rota User";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "Rota Login";
    }
}
