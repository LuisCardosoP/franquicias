package co.com.franchise.mongo.config;

public class MongoDBSecret {
    private final String uri;

    public MongoDBSecret(String uri) {
    this.uri = uri;
    }

    public String getUri() {
    return uri;
    }

    @Override
    public String toString() {
    return "MongoDBSecret{" +
    "uri='" + uri + '\'' +
    '}';
    }

}
