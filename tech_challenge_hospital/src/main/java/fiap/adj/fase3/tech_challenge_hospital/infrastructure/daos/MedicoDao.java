package fiap.adj.fase3.tech_challenge_hospital.infrastructure.daos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class MedicoDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = UserDao.class, optional = false)
    private UserDao user;
}
