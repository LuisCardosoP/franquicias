package co.com.franchise.model;

import reactor.core.publisher.Mono;

public class Franchise {

    private String id;
    private String name;

    private Franchise() {
    }

    public static Mono<Franchise> create(String name) {
        if (name == null || name.isBlank()) {
            return Mono.error(new InvalidInputException("Franchise name must not be empty"));
        }
        Franchise franchise = new Franchise();
        franchise.name = name.trim();
        return Mono.just(franchise);
    }

    public static Mono<Franchise> updateName(Franchise existing, String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.error(new InvalidInputException("Franchise name must not be empty"));
        }
        existing.name = newName.trim();
        return Mono.just(existing);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}
