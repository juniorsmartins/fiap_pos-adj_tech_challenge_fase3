package fiap.adj.fase3.tech_challenge_hospital.application.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class EnfermeiroRequestDto {

    private String nome;

    private UserRequestDto user;
}
