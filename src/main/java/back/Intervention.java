package back;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.Collection;

@Entity
public class Intervention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date date;
    private float kilometrage;
    private float prix;

    // Vehicule many to one
    @ManyToOne
    @JoinColumn(name = "immatVehicule")
    private Vehicule immatVehicule;

    // TypeIntervention Many to one
    @ManyToOne
    @JoinColumn(name = "idTypeIntervention")
    private TypeIntervention typeIntervention;
}

