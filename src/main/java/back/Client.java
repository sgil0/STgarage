package back;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Client {

    // =========================================================================
    // Attributs
    // =========================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idClient;
    private String nom;
    private String prenom;
    private String mail;
    private String adresse;

    // Vehicule one to many
    @OneToMany(mappedBy = "proprietaire")
    private Collection<Vehicule> vehicules;

    // =========================================================================
    // Constructeurs
    // =========================================================================

    public Client(String nom, String prenom, String mail, String adresse) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.adresse = adresse;
        this.vehicules = new ArrayList<>();
    }

    public Client() {
    }

    public Client(String trim, String trim1, String trim2, String trim3, String trim4) {
    }

    // =========================================================================
    // Méthodes
    // =========================================================================



    // =========================================================================
    // Setters
    // =========================================================================

    public void setVehicules(Collection<Vehicule> vehicules) {
        this.vehicules = vehicules;
    }

    // =========================================================================
    // Getters
    // =========================================================================

    public long getIdClient() {
        return idClient;
    }
    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public String getMail() {
        return mail;
    }
    public String getAdresse() {
        return adresse;
    }
    public Collection<Vehicule> getVehicules() {
        return vehicules;
    }
    public String getIdentite() {
        return nom.toUpperCase() + " " + prenom;
    }

    // =========================================================================
    // Equals, Hashcode & toString
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return idClient == client.idClient;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idClient);
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient=" + idClient +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", mail='" + mail + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
