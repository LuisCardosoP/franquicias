package co.com.franchise.model;

import reactor.core.publisher.Mono;

public class Branch {

    private String id;
    private String franchiseId;
    private String name;

    private Branch() {
    }

    public static Mono<Branch> create(String name, String franchiseId) {
        if (name == null || name.isBlank()) {
            return Mono.error(new InvalidInputException("Branch name must not be empty"));
        }
        Branch branch = new Branch();
        branch.name = name.trim();
        branch.franchiseId = franchiseId;
        return Mono.just(branch);
    }

    public static Mono<Branch> updateName(Branch existing, String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.error(new InvalidInputException("Branch name must not be empty"));
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

    public String getFranchiseId() {
        return franchiseId;
    }

    public String getName() {
        return name;
    }
}
