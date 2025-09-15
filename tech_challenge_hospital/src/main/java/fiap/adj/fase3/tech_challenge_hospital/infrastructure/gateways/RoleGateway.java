package fiap.adj.fase3.tech_challenge_hospital.infrastructure.gateways;

import fiap.adj.fase3.tech_challenge_hospital.application.dtos.internal.RoleDto;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.ports.output.RoleOutputPort;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.presenters.RolePresenter;
import fiap.adj.fase3.tech_challenge_hospital.infrastructure.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleGateway implements RoleOutputPort {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<RoleDto> consultarPorNome(String nome) {
        return roleRepository.findByName(nome)
                .map(RolePresenter::converterDaoParaDto);
    }
}
