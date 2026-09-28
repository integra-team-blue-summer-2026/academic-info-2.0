package cloudflight.integra.backend.coffee.model;

import jakarta.persistence.*;

@Entity
public class Coffee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String origin;
    @Enumerated(EnumType.STRING)
    private BrewMethod brewMethod;

    public Coffee() {
    }

    public Coffee(Long id, String origin, BrewMethod brewMethod) {
        this.id = id;
        this.origin = origin;
        this.brewMethod = brewMethod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public BrewMethod getBrewMethod() {
        return brewMethod;
    }

    public void setBrewMethod(BrewMethod brewMethod) {
        this.brewMethod = brewMethod;
    }
}
